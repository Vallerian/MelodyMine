"use client"
import {IOnlineUsers, IReceiveControl, IVolume} from "@/interfaces";
import {useEffect, useRef, useState} from "react";
import {useStreamStore} from "@/store/StreamStore";
import {BsFillMicFill, BsFillMicMuteFill} from "react-icons/bs";
import {useUserStore} from "@/store/UserStore";
import {useControlStore} from "@/store/ControlStore";
import {BiPhoneCall, BiSolidRightArrow, BiSolidUserVoice, BiSolidVolumeMute} from "react-icons/bi";
import {RiVoiceprintFill} from "react-icons/ri";
import {ImUserTie} from "react-icons/im";
import {useSocketStore} from "@/store/SocketStore";
import {decrypt} from "@/utils";
import {MediaConnection} from "peerjs";
import {useValidateStore} from "@/store/ValidateStore";
import {MdOutlinePhoneCallback} from "react-icons/md";
import {useSoundStore} from "@/store/SoundStore";
import UserHead from "@/components/UserHead";
import UserVolumeLine from "@/components/UserVolumeLine";

const SingleUser = ({user}: { user: IOnlineUsers }) => {
    const {socket, peer} = useSocketStore(state => state)
    const {uuid, server, serverIsOnline, isActiveVoice} = useUserStore(state => state)
    const {setUserMute, muteUsers} = useControlStore(state => state)
    const {isValidate} = useValidateStore(state => state)
    const {soundList} = useSoundStore(state => state)
    const {stream} = useStreamStore(state => state)
    const {soundIsActive} = useStreamStore(state => state)
    const [userStream, setUserStream] = useState<MediaStream>()
    const [isUserMute, setIsUserMute] = useState<boolean>(false)
    const [voiceBack, setVoiceBack] = useState<boolean>(false)
    const [isSelfMute, setIsSelfMute] = useState<boolean>(true)
    const [isDeafen, setIsDeafen] = useState<boolean>(true)
    const [userIsAdminMode, setUserIsAdminMode] = useState<boolean>(false)
    const [call, setCall] = useState<MediaConnection | undefined>()
    const [isInCall, setIsInCall] = useState<boolean>(false)
    const [isPendingCall, setIsPendingCall] = useState<boolean>(false)
    const [callingSound, setCallingSound] = useState<Howl>()
    const [callingSound2, setCallingSound2] = useState<Howl>()
    const [endCallSound, setEndCallSound] = useState<Howl>()
    const [pannerNode, setPannerNode] = useState<PannerNode>()
    const [gain, setGain] = useState<GainNode>()
    const [audioContext, setAudioContext] = useState<AudioContext>()
    const [enableVoice, setEnableVoice] = useState<boolean>(false)

    const audioRef = useRef<HTMLAudioElement>(null)

    useEffect(() => {
        setCallingSound(soundList.find(sound => sound.name == "calling")?.howl)
        setCallingSound2(soundList.find(sound => sound.name == "calling2")?.howl)
        setEndCallSound(soundList.find(sound => sound.name == "hangUp")?.howl)
    }, [soundList])

    const connectPeerCall = (peerUuid: string) => {
        if (peerUuid == uuid || !peer) return
        call?.peerConnection.close()
        call?.close()
        setCall(undefined)
        const peerCall = peer.call(peerUuid!!, stream!!, {
            metadata: {
                uuid: uuid
            }
        })
        setCall(peerCall)
        peerCall.on("stream", remoteStream => {
            if (audioRef.current) {
                audioRef.current.srcObject = remoteStream
                setUserStream(remoteStream)
            }
        })

        peerCall.peerConnection.oniceconnectionstatechange = () => {
            if (peerCall.peerConnection.iceConnectionState === "disconnected") {
                connectPeerCall(peerUuid)
            }
        }

    }


    const onEnableVoiceReceive = (token: string) => {
        const onlineUser = decrypt(token) as IOnlineUsers
        if (onlineUser.uuid != user.uuid) return
        if (call) return
        connectPeerCall(onlineUser.uuid!!)
    }

    const onDisableVoiceReceive = (token: string) => {
        const onlineUser = decrypt(token) as IOnlineUsers
        if (onlineUser.uuid != user.uuid) return
        if (!call) return
        call?.close()
        setCall(undefined)
    }

    const onPlayerInitAdminModeReceive = (token: string) => {
        const data = decrypt(token) as IOnlineUsers
        if (data.uuid == user.uuid && data.uuid != uuid && isValidate) {
            setUserIsAdminMode(true)
        }
    }


    const onAdminModeEnableReceive = (token: string) => {
        const data = decrypt(token) as {
            uuid: string,
            server: string
        }
        if (data.uuid != user.uuid && isValidate) return
        setUserIsAdminMode(true)
        if (data.uuid == uuid || !peer) return

        connectPeerCall(data.uuid)
    }

    const onAdminModeDisableReceive = (token: string) => {
        const data = decrypt(token) as {
            uuid: string
        }
        if (data.uuid != user.uuid) return
        setUserIsAdminMode(false)
        call?.close()
        setCall(undefined)

    }

    const onNewPlayerLeave = (token: string) => {
        const data = decrypt(token) as IOnlineUsers
        if (data.uuid != user.uuid || data.uuid == uuid) return
        call?.close()
        setCall(undefined)
    }

    const onPlayerLeaveReceivePlugin = (token: string) => {
        const data = decrypt(token) as IOnlineUsers
        if (data.uuid != user.uuid || data.uuid == uuid) return
        call?.close()
        setCall(undefined)

    }

    const onPlayerChangeServer = (token: string) => {
        const data = decrypt(token) as {
            name: string,
            uuid: string,
            server: string
        }
        if (data.uuid != user.uuid && data.uuid != uuid) return
        setUserIsAdminMode(false)
        call?.close()
        setCall(undefined)
    }

    const onSetVolumeReceive = (data: IVolume) => {
        if (data.uuid != user.uuid) return
        updateListenerPosition(data)
        updatePannerPosition(data)
    }

    const onPlayerChangeControlReceive = (token: string) => {
        const data = decrypt(token) as IReceiveControl
        if (data.uuid == user.uuid) {
            if (data.type == "mic") {
                setIsSelfMute(data.value)
            } else {
                setIsDeafen(data.value)
            }
        }
    }

    const onSetControlPluginReceive = (token: string) => {
        const data = decrypt(token) as IReceiveControl
        if (data.uuid == user.uuid && data.uuid != uuid) {
            if (data.type == "mic") {
                setIsSelfMute(data.value)
            } else {
                setIsDeafen(data.value)
            }
        }
    }

    const onStartCallSelfPluginReceive = (token: string) => {
        const data = decrypt(token) as { name: string, uuid: string }
        if (data.uuid == user.uuid && data.uuid != uuid) {
            setIsPendingCall(true)
            callingSound?.play()

        }
    }

    const onStartCallTargetPluginReceive = (token: string) => {
        const data = decrypt(token) as { name: string, uuid: string }
        if (data.uuid == user.uuid && data.uuid != uuid) {
            setIsPendingCall(true)
            callingSound2?.play()

        }
    }

    const onAcceptCallSelfPluginReceive = (token: string) => {
        const data = decrypt(token) as { name: string, uuid: string }
        if (data.uuid == user.uuid && data.uuid != uuid) {
            setIsPendingCall(false)
            setIsInCall(true)
            callingSound2?.stop()

        }
    }

    const onAcceptCallTargetPluginReceive = (token: string) => {
        const data = decrypt(token) as { name: string, uuid: string }
        if (data.uuid == user.uuid && data.uuid != uuid) {
            connectPeerCall(data.uuid)
            setIsPendingCall(false)
            setIsInCall(true)
            callingSound?.stop()
        }

    }

    const onEndCallSelfPluginReceive = (token: string) => {
        const data = decrypt(token) as { name: string, uuid: string }
        if (data.uuid == user.uuid && data.uuid != uuid) {
            setIsInCall(false)
            call?.close()
            setCall(undefined)
            endCallSound?.play()
        }
    }

    const onEndCallTargetPluginReceive = (token: string) => {
        const data = decrypt(token) as { name: string, uuid: string }
        if (data.uuid == user.uuid && data.uuid != uuid) {
            setIsInCall(false)
            call?.close()
            setCall(undefined)
            endCallSound?.play()
        }
    }

    const onPendingCallEndSelfPluginReceive = (token: string) => {
        const data = decrypt(token) as { name: string, uuid: string }
        if (data.uuid == user.uuid && data.uuid != uuid) {
            setIsPendingCall(false)
            callingSound?.stop()
            endCallSound?.play()
        }
    }

    const onPendingCallEndTargetPluginReceive = (token: string) => {
        const data = decrypt(token) as { name: string, uuid: string }
        if (data.uuid == user.uuid && data.uuid != uuid) {
            setIsPendingCall(false)
            callingSound2?.stop()
            endCallSound?.play()
        }
    }

    const onDenyCallSelfPluginReceive = (token: string) => {
        const data = decrypt(token) as { name: string, uuid: string }
        if (data.uuid == user.uuid && data.uuid != uuid) {
            setIsPendingCall(false)
            callingSound?.stop()
            callingSound2?.stop()
            endCallSound?.play()
        }
    }

    const onDenyCallTargetPluginReceive = (token: string) => {
        const data = decrypt(token) as { name: string, uuid: string }
        if (data.uuid == user.uuid && data.uuid != uuid) {
            setIsPendingCall(false)
            callingSound2?.stop()
            callingSound?.stop()
            endCallSound?.play()
        }
    }

    useEffect(() => {
            if (!uuid || !stream) return

            socket?.on("onEnableVoiceReceive", onEnableVoiceReceive)
            socket?.on("onDisableVoiceReceive", onDisableVoiceReceive)
            socket?.on("onPlayerInitAdminModeReceive", onPlayerInitAdminModeReceive)
            socket?.on("onAdminModeEnableReceive", onAdminModeEnableReceive)
            socket?.on("onAdminModeDisableReceive", onAdminModeDisableReceive)
            socket?.on("onNewPlayerLeave", onNewPlayerLeave)
            socket?.on("onPlayerLeaveReceivePlugin", onPlayerLeaveReceivePlugin)
            socket?.on("onPlayerChangeServer", onPlayerChangeServer)
            socket?.on("onSetVolumeReceive", onSetVolumeReceive)

            socket?.on("onStartCallSelfPluginReceive", onStartCallSelfPluginReceive)
            socket?.on("onStartCallTargetPluginReceive", onStartCallTargetPluginReceive)

            socket?.on("onEndCallSelfPluginReceive", onEndCallSelfPluginReceive)
            socket?.on("onEndCallTargetPluginReceive", onEndCallTargetPluginReceive)

            socket?.on("onPendingCallEndSelfPluginReceive", onPendingCallEndSelfPluginReceive)
            socket?.on("onPendingCallEndTargetPluginReceive", onPendingCallEndTargetPluginReceive)

            socket?.on("onAcceptCallSelfPluginReceive", onAcceptCallSelfPluginReceive)
            socket?.on("onAcceptCallTargetPluginReceive", onAcceptCallTargetPluginReceive)

            socket?.on("onDenyCallSelfPluginReceive", onDenyCallSelfPluginReceive)
            socket?.on("onDenyCallTargetPluginReceive", onDenyCallTargetPluginReceive)

            return () => {
                socket?.off("onEnableVoiceReceive", onEnableVoiceReceive)
                socket?.off("onDisableVoiceReceive", onDisableVoiceReceive)
                socket?.off("onPlayerInitAdminModeReceive", onPlayerInitAdminModeReceive)
                socket?.off("onAdminModeEnableReceive", onAdminModeEnableReceive)
                socket?.off("onAdminModeDisableReceive", onAdminModeDisableReceive)
                socket?.off("onNewPlayerLeave", onNewPlayerLeave)
                socket?.off("onPlayerLeaveReceivePlugin", onPlayerLeaveReceivePlugin)
                socket?.off("onPlayerChangeServer", onPlayerChangeServer)
                socket?.off("onSetVolumeReceive", onSetVolumeReceive)

                socket?.off("onStartCallSelfPluginReceive", onStartCallSelfPluginReceive)
                socket?.off("onStartCallTargetPluginReceive", onStartCallTargetPluginReceive)
                socket?.off("onEndCallSelfPluginReceive", onEndCallSelfPluginReceive)
                socket?.off("onEndCallTargetPluginReceive", onEndCallTargetPluginReceive)
                socket?.off("onPendingCallEndSelfPluginReceive", onPendingCallEndSelfPluginReceive)
                socket?.off("onPendingCallEndTargetPluginReceive", onPendingCallEndTargetPluginReceive)
                socket?.off("onAcceptCallSelfPluginReceive", onAcceptCallSelfPluginReceive)
                socket?.off("onAcceptCallTargetPluginReceive", onAcceptCallTargetPluginReceive)
                socket?.off("onDenyCallSelfPluginReceive", onDenyCallSelfPluginReceive)
                socket?.off("onDenyCallTargetPluginReceive", onDenyCallTargetPluginReceive)
            }

        }, [socket, uuid, stream, isValidate, call, callingSound, callingSound2, endCallSound, pannerNode, soundIsActive, isUserMute, user, voiceBack, gain]
    )

    useEffect(() => {
        if (isValidate) return
        call?.close()
        setCall(undefined)
        setUserIsAdminMode(false)
    }, [isValidate, call])

    useEffect(() => {
            if (!uuid) return
            socket?.on("onPlayerChangeControlReceive", onPlayerChangeControlReceive)
            socket?.on("onSetControlPluginReceive", onSetControlPluginReceive)

            return () => {
                socket?.off("onPlayerChangeControlReceive", onPlayerChangeControlReceive)
                socket?.off("onSetControlPluginReceive", onSetControlPluginReceive)
            }

        }, [socket, uuid]
    )

    useEffect(() => {
        if (!peer || !stream) return
        peer.on("call", call => {
            if (call.metadata.uuid != user.uuid) return
            call.answer(stream!!)
            call.on("stream", remoteStream => {
                if (audioRef.current) {
                    audioRef.current.srcObject = remoteStream
                    setUserStream(remoteStream)
                }
            })
        })
        return () => {
            peer?.off("call")
        }
    }, [peer, stream])


    useEffect(() => {
        if (user.uuid == uuid) {
            if (audioRef.current) {
                audioRef.current.srcObject = stream
                setUserStream(stream!)
            }
        }
    }, [user])


    const updatePannerPosition = (data: IVolume) => {
        if (!pannerNode || !audioContext || !gain) return
        const {playerLocation, playerDirection, settings, distance} = data
        if (distance > settings.maxDistance || checkPlayerCanTalk()) {
            gain.gain.value = 0
            setEnableVoice(false)
            return
        } else {
            gain.gain.value = 1
            setEnableVoice(true)
        }
        if (settings.sound3D) {
            const {x, y, z} = playerLocation
            const {x: Dx, y: Dy, z: Dz} = playerDirection
            pannerNode.panningModel = "HRTF"
            pannerNode.distanceModel = "inverse"
            pannerNode.maxDistance = settings.maxDistance
            pannerNode.refDistance = settings.refDistance
            pannerNode.rolloffFactor = 1
            pannerNode.coneInnerAngle = settings.innerAngle
            pannerNode.coneOuterAngle = settings.outerAngle
            pannerNode.coneOuterGain = settings.outerVolume
            pannerNode.setPosition(x, y, z)
            pannerNode.setOrientation(Dx, Dy, Dz)

        } else {
            if (!settings.lazyHear) return
            let volume = (settings.maxDistance - distance) / settings.maxDistance
            gain.gain.value = volume
            if (userIsAdminMode) gain.gain.value = 1
            if (checkPlayerCanTalk()) {
                gain.gain.value = 0
            } else {
                gain.gain.value = volume
            }
        }


    }

    const updateListenerPosition = (data: IVolume) => {
        const listener = audioContext?.listener
        if (!audioContext || !listener) return
        const {targetLocation, targetDirection, settings} = data
        if (!settings.sound3D) return
        const {x, y, z} = targetLocation
        const {x: Dx, y: Dy, z: Dz} = targetDirection
        listener.setPosition(x, y, z)
        listener.setOrientation(Dx, Dy, Dz, 0, 1, 0)

    }


    useEffect(() => {
        if (userStream) {
            const AC = new AudioContext()
            setAudioContext(AC)
            const AS = AC.createMediaStreamSource(userStream)
            const PN = AC.createPanner()
            const GN = AC.createGain()

            if (user.uuid == uuid) {
                setEnableVoice(true)
            }
            GN.gain.value = 0
            AS.connect(PN)
            PN.connect(GN)
            GN.connect(AC.destination)

            setGain(GN)
            setPannerNode(PN)
        }
    }, [userStream, uuid])

    useEffect(() => {
        const isMute = muteUsers.find(item => item.uuid == user.uuid)
        if (isMute) {
            setIsUserMute(isMute.isSelfMute)
        }
    }, [muteUsers])

    const checkPlayerCanTalk = () => {
        return (!soundIsActive || (isUserMute || user.isMute && user.uuid != uuid) || (!voiceBack && user.uuid == uuid))
    }


    useEffect(() => {
        if (!gain) return
        if (checkPlayerCanTalk()) {
            gain.gain.value = 0
        } else {
            gain.gain.value = 1
        }
    }, [soundIsActive, isUserMute, user, voiceBack, uuid, gain])

    return (
        <>
            <audio
                ref={audioRef}
                muted
                autoPlay
                playsInline
            >
            </audio>

            <div
                className={`${isPendingCall ? "shake" : ""} flex items-center bg-neutral-800 px-1 py-1 rounded-xl shadow-xl`}>
                <div className="flex justify-center items-center w-2/12">
                    <UserHead
                        key={user.name}
                        stream={userStream}
                        audioContext={audioContext}
                        name={user.name}
                        volume={audioRef?.current?.volume}
                        soundIsActive={soundIsActive}
                        isMute={user.isMute}
                        isSelfMute={isUserMute}
                        enableVoice={enableVoice}
                    />
                </div>
                <div className="flex flex-col ml-3 w-10/12">
                    <h3 className="text-white text-xl flex">
                        <span className="flex items-center gap-1">
                            <span className="text-2xl">
                                <BiSolidUserVoice/>
                            </span>
                            {user.name}
                        </span>
                        <div className="flex items-center justify-between overflow-clip flex-wrap gap-1">
                            {user.isMute ? (
                                <div className="ms-2 self-center">
                                <span
                                    className="whitespace-nowrap ring-1 ring-red-900 text-xs font-medium mr-2 px-1.5 py-0.5 rounded dark:bg-red-500 dark:text-white flex">
                                    <span className="me-1 self-center hidden sm:block">
                                        <BsFillMicMuteFill/>
                                    </span>
                                    Server Mute
                                </span>
                                </div>
                            ) : ""}
                            {userIsAdminMode ? (
                                <div className="ms-2 self-center">
                                    <span
                                        className="ring-1 ring-cyan-900 text-xs font-medium mr-2 px-1.5 py-0.5 rounded dark:bg-cyan-500 dark:text-white flex">
                                        <span className="me-1 self-center hidden sm:block">
                                            <ImUserTie/>
                                        </span>
                                        Admin
                                    </span>
                                </div>
                            ) : ""}

                            {isInCall ? (
                                <div className="ms-2 self-center">
                                <span
                                    className="whitespace-nowrap ring-1 ring-fuchsia-900 text-xs font-medium mr-2 px-1.5 py-0.5 rounded dark:bg-fuchsia-500 dark:text-white flex">
                                    <span className="me-1 self-center hidden sm:block">
                                        <BiPhoneCall/>
                                    </span>
                                    Call
                                </span>
                                </div>
                            ) : ""}

                            {isPendingCall ? (
                                <div className="ms-2 self-center">
                                <span
                                    className="whitespace-nowrap ring-1 ring-yellow-900 text-xs font-medium mr-2 px-1.5 py-0.5 rounded dark:bg-yellow-500 dark:text-white flex">
                                    <span className="me-1 self-center hidden sm:block">
                                        <MdOutlinePhoneCallback/>
                                    </span>
                                    Calling...
                                </span>
                                </div>
                            ) : ""}
                        </div>
                    </h3>
                    <UserVolumeLine
                        key={user.name + "volume"}
                        stream={userStream}
                        audioContext={audioContext}
                        name={user.name}
                        volume={audioRef?.current?.volume}
                        soundIsActive={soundIsActive}
                        isMute={user.isMute}
                        isSelfMute={isUserMute}
                        enableVoice={enableVoice}
                    />
                    <div className="flex items-center justify-between w-full">
                        <span
                            className={`${user.server == server && serverIsOnline && isActiveVoice ? "text-green-500" : "text-neutral-400"} font-bold flex items-center gap-1`}>
                            <BiSolidRightArrow/>
                            {user.server}
                        </span>
                        <div className="flex items-center gap-1">
                            <div className="flex items-center">
                                {!isSelfMute ? (
                                    <div className="self-center">
                                        <span
                                            className="whitespace-nowrap text-sm font-medium mr-2 rounded dark:text-neutral-400 flex">
                                            <span className="self-center hidden sm:block">
                                                <BsFillMicMuteFill/>
                                            </span>
                                        </span>
                                    </div>
                                ) : ""}

                                {!isDeafen ? (
                                    <div className="self-center">
                                        <span
                                            className="whitespace-nowrap text-sm font-medium mr-2 rounded dark:text-neutral-400 flex">
                                            <span className="self-center hidden sm:block">
                                                <BiSolidVolumeMute/>
                                            </span>
                                        </span>
                                    </div>
                                ) : ""}
                            </div>
                            {user.uuid != uuid ? (
                                <span
                                    className={`cursor-pointer ${isUserMute ? "text-red-500" : "text-neutral-400"} flex gap-1 justify-center text-sm items-center px-1 rounded ring-1 ${isUserMute ? "ring-red-700" : "ring-neutral-700"} shadow ${isUserMute ? "shadow-red-600" : "shadow-neutral-600"}`}
                                    onClick={() => {
                                        if (uuid != user.uuid) setUserMute(user.uuid!, !isUserMute)
                                    }}
                                >
                                {isUserMute ? (<>
                                    <BsFillMicFill/>
                                    UnMute
                                </>) : (<>
                                    <BsFillMicMuteFill/>
                                    Mute
                                </>)}
                        </span>
                            ) : ""}
                            {user.uuid == uuid ? (
                                <span
                                    className={`cursor-pointer ${voiceBack ? "text-green-400" : "text-neutral-400"} flex gap-1 justify-center text-sm items-center px-1 rounded ring-1 ${voiceBack ? "ring-green-700" : "ring-neutral-700"} shadow ${voiceBack ? "shadow-green-600" : "shadow-neutral-600"}`}
                                    onClick={() => setVoiceBack(!voiceBack)}
                                >
                            <RiVoiceprintFill/>
                            Voice Back
                        </span>
                            ) : ""}
                        </div>
                    </div>
                </div>
                <div className="relative ml-auto mb-auto">
                    <span className="relative flex h-3 w-3">
                      <span
                          className="animate-ping absolute inline-flex h-full w-full rounded-full bg-green-400 opacity-75"></span>
                      <span className="relative inline-flex rounded-full h-3 w-3 bg-green-500"></span>
                    </span>
                </div>
            </div>
        </>
    )
}
export default SingleUser
"use client"
import {IOnlineUsers, IReceiveControl, IVolume} from "@/interfaces";
import Image from "next/image";
import {useEffect, useRef, useState} from "react";
import {SoundMeter} from "@/utils/SoundMeter";
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

const SingleUser = ({user}: { user: IOnlineUsers }) => {
    const {socket, peer} = useSocketStore(state => state)
    const {uuid, server, serverIsOnline, isActiveVoice} = useUserStore(state => state)
    const {setUserMute, muteUsers} = useControlStore(state => state)
    const {isValidate} = useValidateStore(state => state)
    const {callingSound, callingSound2, endCallSound} = useSoundStore(state => state)
    const {stream} = useStreamStore(state => state)
    const {soundIsActive} = useStreamStore(state => state)
    const [userStream, setUserStream] = useState<MediaStream>()
    const [instant, setInstant] = useState(0.00)
    const [isUserMute, setIsUserMute] = useState<boolean>(false)
    const [voiceBack, setVoiceBack] = useState<boolean>(false)
    const [isSelfMute, setIsSelfMute] = useState<boolean>(true)
    const [isDeafen, setIsDeafen] = useState<boolean>(true)
    const [userVolume, setUserVolume] = useState<string>("1.0")
    const [userIsAdminMode, setUserIsAdminMode] = useState<boolean>(false)
    const [call, setCall] = useState<MediaConnection | undefined>()
    const [isInCall, setIsInCall] = useState<boolean>(false)
    const [isPendingCall, setIsPendingCall] = useState<boolean>(false)

    const audioRef = useRef<HTMLAudioElement>(null)


    const onEnableVoiceReceive = (token: string) => {
        const onlineUser = decrypt(token) as IOnlineUsers
        if (onlineUser.uuid != user.uuid) return

        const peerCall = peer?.call(onlineUser.uuid!!, stream!!, {
            metadata: {
                uuid: uuid
            }
        })

        setCall(peerCall)
        peerCall?.on("stream", remoteStream => {
            setUserStream(remoteStream)
            if (audioRef.current) {
                audioRef.current.srcObject = remoteStream
                setUserStream(remoteStream)
            }
        })
    }

    const onDisableVoiceReceive = (token: string) => {
        const onlineUser = decrypt(token) as IOnlineUsers
        if (onlineUser.uuid != user.uuid) return
        call?.close()
    }

    const onPlayerInitAdminModeReceive = (token: string) => {
        const data = decrypt(token) as IOnlineUsers
        if (data.uuid == user.uuid && data.uuid != uuid && isValidate) {
            setUserVolume("1.0")
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
        if (data.uuid == uuid) return
        const peerCall = peer?.call(data.uuid!!, stream!!, {
            metadata: {
                uuid: uuid
            }
        })
        setCall(peerCall)
        peerCall?.on("stream", remoteStream => {
            if (audioRef.current) {
                audioRef.current.srcObject = remoteStream
                setUserStream(remoteStream)
            }
        })

        setUserVolume("1.0")
    }

    const onAdminModeDisableReceive = (token: string) => {
        const data = decrypt(token) as {
            uuid: string
        }
        if (data.uuid != user.uuid) return
        setUserIsAdminMode(false)
        call?.close()
    }

    const onNewPlayerLeave = (token: string) => {
        const data = decrypt(token) as IOnlineUsers
        if (data.uuid != user.uuid && data.uuid == uuid) return
        call?.close()
    }

    const onPlayerLeaveReceivePlugin = (token: string) => {
        const data = decrypt(token) as IOnlineUsers
        if (data.uuid != user.uuid && data.uuid == uuid) return
        call?.close()
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
    }

    const onSetVolumeReceive = (data: IVolume) => {
        if (data.uuid != user.uuid) return
        setUserVolume(data.volume)
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

            callingSound2?.pause()

        }
    }

    const onAcceptCallTargetPluginReceive = (token: string) => {
        const data = decrypt(token) as { name: string, uuid: string }
        if (data.uuid == user.uuid && data.uuid != uuid) {
            const peerCall = peer?.call(data.uuid!!, stream!!, {
                metadata: {
                    uuid: uuid
                }
            })
            setCall(peerCall)
            peerCall?.on("stream", remoteStream => {
                if (audioRef.current) {
                    audioRef.current.srcObject = remoteStream
                    setUserStream(remoteStream)
                }
            })
            setUserVolume("1.0")
            setIsPendingCall(false)
            setIsInCall(true)

            callingSound?.pause()
        }

    }

    const onEndCallSelfPluginReceive = (token: string) => {
        const data = decrypt(token) as { name: string, uuid: string }
        if (data.uuid == user.uuid && data.uuid != uuid) {
            setIsInCall(false)
            call?.close()


            endCallSound?.play()
        }
    }

    const onEndCallTargetPluginReceive = (token: string) => {
        const data = decrypt(token) as { name: string, uuid: string }
        if (data.uuid == user.uuid && data.uuid != uuid) {
            setIsInCall(false)
            call?.close()

            endCallSound?.play()
        }
    }

    const onPendingCallEndSelfPluginReceive = (token: string) => {
        const data = decrypt(token) as { name: string, uuid: string }
        if (data.uuid == user.uuid && data.uuid != uuid) {
            setIsPendingCall(false)
            callingSound?.pause()
            endCallSound?.play()
        }
    }

    const onPendingCallEndTargetPluginReceive = (token: string) => {
        const data = decrypt(token) as { name: string, uuid: string }
        if (data.uuid == user.uuid && data.uuid != uuid) {
            setIsPendingCall(false)
            callingSound2?.pause()
            endCallSound?.play()
        }
    }

    const onDenyCallSelfPluginReceive = (token: string) => {
        const data = decrypt(token) as { name: string, uuid: string }
        if (data.uuid == user.uuid && data.uuid != uuid) {
            setIsPendingCall(false)
            callingSound?.pause()
            callingSound2?.pause()
            endCallSound?.play()
        }
    }

    const onDenyCallTargetPluginReceive = (token: string) => {
        const data = decrypt(token) as { name: string, uuid: string }
        if (data.uuid == user.uuid && data.uuid != uuid) {
            setIsPendingCall(false)
            callingSound2?.pause()
            callingSound?.pause()
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

        }, [socket, uuid, stream, isValidate, call]
    )

    useEffect(() => {
        if (isValidate) return
        call?.close()
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
        if (audioRef.current) {
            if (parseFloat(userVolume) > 1.0 || parseFloat(userVolume) < 0.0) {
                audioRef.current.volume = 1.0
            } else {
                if (isInCall || user.isAdminMode) {
                    audioRef.current.volume = 1.0
                } else {
                    audioRef.current.volume = parseFloat(userVolume)
                }
            }
        }
    }, [userVolume, isInCall, user])


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

    useEffect(() => {
        let interval: any
        let soundMaster: any
        const audioContext = new AudioContext()
        if (userStream) {
            soundMaster = new SoundMeter(audioContext)
            soundMaster.connectToSource(userStream, (event: any) => {
                if (!event) {
                    interval = setInterval(() => {
                        setInstant(soundMaster.instant.toFixed(2))
                    }, 10)
                }
            })
        }

        return () => {
            if (soundMaster) soundMaster.stop()
            clearInterval(interval)
        }
    }, [userStream])

    useEffect(() => {
        const isMute = muteUsers.find(item => item.uuid == user.uuid)
        if (isMute) setIsUserMute(isMute.isSelfMute)
    }, [muteUsers])


    return (
        <>
            <audio
                muted={!soundIsActive || (isUserMute || user.isMute && user.uuid != uuid) || (!voiceBack && user.uuid == uuid)}
                ref={audioRef}
                autoPlay
                playsInline
            >
            </audio>


            <div
                className={`${isPendingCall ? "shake" : ""} flex items-center bg-neutral-800 px-1 py-1 rounded-xl shadow-xl`}>
                <div className="flex justify-center items-center w-2/12">
                    <Image
                        src={`https://mc-heads.net/avatar/${user.name}`}
                        alt={`${user.name} avatar`} width={50} height={50}
                        className={` opacity-30 rounded ${instant > 0.00 && audioRef?.current?.volume != 0 && soundIsActive && !user.isMute && !isUserMute ? "soundAnimationSingle" : ""}`}
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
                    <div
                        className="bg-neutral-700 rounded-2xl h-[2px] my-1 relative w-full flex items-center">
                        <div className="btn-gradient h-[2px] rounded-2xl absolute shadow-2xl shadow-white" style={{
                            maxWidth: "100%",
                            width: instant > 0.00 && audioRef?.current?.volume != 0 && soundIsActive && !user.isMute && !isUserMute ? `${instant * 500}%` : "0px",
                        }}/>
                    </div>
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
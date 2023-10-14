"use client"
import {IOnlineUsers} from "@/interfaces/User";
import Image from "next/image";
import {usePeersStore} from "@/store/PeersStore";
import {useEffect, useRef, useState} from "react";
import {SoundMeter} from "@/utils/SoundMeter";
import {useStreamStore} from "@/store/StreamStore";
import {useVolumeStore} from "@/store/VolumeStore";
import {BsFillMicFill, BsFillMicMuteFill} from "react-icons/bs";
import {useUserStore} from "@/store/UserStore";
import {useControlStore} from "@/store/ControlStore";
import {BiSolidRightArrow, BiSolidUserVoice} from "react-icons/bi";
import {RiVoiceprintFill} from "react-icons/ri";
import {ImUserTie} from "react-icons/im";


const SingleUser = ({user}: { user: IOnlineUsers }) => {

    const {isAdminMode, uuid, server, serverIsOnline, isActiveVoice} = useUserStore(state => state)
    const {peers} = usePeersStore(state => state)
    const {setUserMute, muteUsers} = useControlStore(state => state)
    const {stream} = useStreamStore(state => state)
    const {soundIsActive} = useStreamStore(state => state)
    const {volumes} = useVolumeStore(state => state)
    const audioRef = useRef<HTMLAudioElement>(null)
    const [userStream, setUserStream] = useState<MediaStream>()
    const [instant, setInstant] = useState(0.00)
    const [isUserMute, setIsUserMute] = useState<boolean>(false)
    const [voiceBack, setVoiceBack] = useState<boolean>(false)

    useEffect(() => {
        if (user.uuid != uuid) {
            const item = peers.find(item => item.uuid == user.uuid)
            if (item) {
                item.peer.ontrack = event => {
                    if (audioRef.current) {
                        const stream = event.streams[0]
                        audioRef.current.srcObject = stream
                        setUserStream(stream)
                    }
                }
            }
        } else {
            if (audioRef.current) {
                audioRef.current.srcObject = stream
                setUserStream(stream!)
            }
        }

    }, [user, peers])

    useEffect(() => {
        let interval: any
        let soundMaster: any
        let audioContext = new AudioContext()

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
            if (soundMaster) {
                soundMaster.stop()
            }
            audioContext.close()
            clearInterval(interval)
        }
    }, [userStream])

    useEffect(() => {
        const userVolume = volumes.find(item => item.uuid == user.uuid)
        if (audioRef.current) {
            if (userVolume?.volume) {
                if (parseFloat(userVolume.volume) > 1.0 || parseFloat(userVolume.volume) < 0.0) {
                    audioRef.current.volume = 1.0
                } else {
                    audioRef.current.volume = parseFloat(userVolume.volume)
                }
            }
        }
    }, [volumes])

    useEffect(() => {
        if (audioRef.current) {
            if (isAdminMode) {
                audioRef.current.volume = 1.0
            }
        }
    }, [isAdminMode])

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
                playsInline>
            </audio>
            <div className="flex items-center bg-neutral-800 px-1 py-1 rounded-xl shadow-xl">
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
                            {user.isAdminMode ? (
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
                                className={`cursor-pointer ${voiceBack ? "text-cyan-400" : "text-neutral-400"} flex gap-1 justify-center text-sm items-center px-1 rounded ring-1 ${voiceBack ? "ring-cyan-700" : "ring-neutral-700"} shadow ${voiceBack ? "shadow-cyan-600" : "shadow-neutral-600"}`}
                                onClick={() => setVoiceBack(!voiceBack)}
                            >
                            <RiVoiceprintFill/>
                            Voice Back
                        </span>
                        ) : ""}
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
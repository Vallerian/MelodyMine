"use client"
import {BsFillMicFill, BsFillMicMuteFill} from "react-icons/bs";
import {useEffect, useState} from "react";
import {BiSolidVolumeFull, BiSolidVolumeMute} from "react-icons/bi";
import {VscDebugDisconnect} from "react-icons/vsc";
import {useValidateStore} from "@/store/ValidateStore";
import {useUserStore} from "@/store/UserStore";
import {useSocketStore} from "@/store/SocketStore";
import {useStreamStore} from "@/store/StreamStore";
import {SoundMeter} from "@/utils/SoundMeter";
import {useOnlineUsersStore} from "@/store/OnlineUsersStore";
import {useLoadingStore} from "@/store/LoadingStore";
import Progress from "@/components/Porgress/Progress";
import {decrypt, encrypt} from "@/utils";
import {IReceiveControl} from "@/interfaces";

const SoundControl = () => {

    const {uuid, name, server, changeUserAdminMode, changeActiveVoice, setServer, isMute} = useUserStore(state => state)
    const {socket} = useSocketStore(state => state)
    const {removeUser, setAdminModeAll} = useOnlineUsersStore(state => state)
    const {setStartButton, disconnectButton} = useLoadingStore(state => state)


    const {
        micIsActive,
        soundIsActive,
        setMicActive,
        setSoundActive,
        stream,
        closeStream
    } = useStreamStore(state => state)
    const {isValidate, setValidate} = useValidateStore(state => state)
    const [instant, setInstant] = useState(0.00)

    const onSetControlPluginReceive = (token: string) => {
        const data = decrypt(token) as IReceiveControl
        if (data.uuid == uuid) {
            if (data.type == "mic") {
                setMicActive(data.value, true)
            } else {
                setSoundActive(data.value, true)
            }
        }
    }


    useEffect(() => {
        let interval: any
        let soundMaster: any
        let audioContext = new AudioContext()

        if (stream) {
            soundMaster = new SoundMeter(audioContext)

            soundMaster.connectToSource(stream, (event: any) => {
                if (!event) {
                    interval = setInterval(() => {
                        setInstant(soundMaster.instant.toFixed(2))
                    }, 200)
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

    }, [stream])

    useEffect(() => {
        if (!uuid) return
        socket?.on("onSetControlPluginReceive", onSetControlPluginReceive)

        return () => {
            socket?.off("onSetControlPluginReceive", onSetControlPluginReceive)

        }

    }, [socket, uuid])

    const handleToggle = (type: "mic" | "sound") => {
        type === "mic" ? setMicActive(!micIsActive) : setSoundActive(!soundIsActive)
    }

    const handleCloseConnection = () => {
        setValidate(false)
        closeStream()
        removeUser(uuid)
        setAdminModeAll()
        changeUserAdminMode(false)
        changeActiveVoice(false)
        socket?.emit("onPlayerEndVoice", encrypt({
            name: name,
            uuid: uuid,
            server: server,
        }))
        setServer("")
        setStartButton()
    }

    if (!isValidate) return

    return (
        <div
            className="fixed md:fixed text-white bottom-5 bg-neutral-700 sm:bg-transparent sm:px-0 px-3 sm:py-0 py-2 rounded-xl sm:shadow-none shadow-xl z-10 bg-opacity-60">
            <ul className="flex gap-20 text-2xl text-[#1AA392]">
                <li className={`${!soundIsActive && 'text-red-500'} cursor-pointer`}
                    onClick={() => handleToggle("sound")}>
                    {!soundIsActive ? <BiSolidVolumeMute/> : <BiSolidVolumeFull/>}
                </li>
                <li className={`${!micIsActive && 'text-red-500'} cursor-pointer flex justify-center items-center`}
                    onClick={() => handleToggle("mic")}>
                    {instant > 0.00 && micIsActive && !isMute ? (<>
                        <div className="soundAnimation">
                        </div>
                    </>) : ''}
                    {!micIsActive ? <BsFillMicMuteFill/> : <BsFillMicFill/>}
                </li>
                <button disabled={disconnectButton} onClick={handleCloseConnection}
                        className="text-amber-300 flex items-center justify-center">
                    {disconnectButton ? (
                        <span className="pr-5 pl-2 self-center absolute mt-2">
                            <Progress/>
                        </span>
                    ) : ""}
                    <span className={`${disconnectButton ? "invisible" : ""}`}>
                        <VscDebugDisconnect/>
                    </span>
                </button>
            </ul>
        </div>
    )
}
export default SoundControl

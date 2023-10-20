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
import {usePeersStore} from "@/store/PeersStore";
import {useLoadingStore} from "@/store/LoadingStore";
import Progress from "@/components/Porgress/Progress";
import {encrypt} from "@/utils";
import {MdOutlineNoiseAware} from "react-icons/md";
import {useControlStore} from "@/store/ControlStore";

const SoundControl = () => {

    const user = useUserStore(state => state)
    const {socket} = useSocketStore(state => state)
    const {removeUser, setAdminModeAll} = useOnlineUsersStore(state => state)
    const {removeAll} = usePeersStore(state => state)
    const {setStartButton, disconnectButton} = useLoadingStore(state => state)
    const {noiseSuppression, setNoiseSuppression} = useControlStore(state => state)

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


    const handleToggle = (type: "mic" | "sound") => {
        type === "mic" ? setMicActive(!micIsActive) : setSoundActive(!soundIsActive)
    }

    const handleCloseConnection = () => {
        setValidate(false)
        closeStream()
        removeUser(user.uuid)
        removeAll()
        setAdminModeAll()
        user.changeUserAdminMode(false)
        user.changeActiveVoice(false)
        socket?.emit("onPlayerEndVoice", encrypt({
            name: user.name,
            uuid: user.uuid,
            server: user.server,
        }))

        setStartButton()
    }


    if (!isValidate) return (
        <div className="fixed md:fixed text-white bottom-5 bg-neutral-700 sm:bg-transparent sm:px-0 px-3 sm:py-0 py-2 rounded-xl sm:shadow-none shadow-xl z-10 bg-opacity-60">
            <button
                className={`cursor-pointer text-sm ${noiseSuppression ? "text-green-500" : "text-neutral-400"} flex gap-1 justify-center items-center p-1 rounded ring-1 ${noiseSuppression ? "ring-green-700" : "ring-neutral-700"} shadow ${noiseSuppression ? "shadow-green-600" : "shadow-neutral-600"}`}
                onClick={() => setNoiseSuppression(!noiseSuppression)}
            >
                <span className="text-xl hidden sm:block">
                    <MdOutlineNoiseAware/>
                </span>
                Noise Suppression
            </button>
        </div>
    )

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
                    {instant > 0.00 && micIsActive ? (<>
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

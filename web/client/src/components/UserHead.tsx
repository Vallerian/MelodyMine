import {useEffect, useState} from "react";
import {SoundMeter} from "@/utils/SoundMeter";
import {IOnlineUsers} from "@/interfaces";
import {useSocketStore} from "@/store/SocketStore";
import {encrypt} from "@/utils";
import {Image} from "@nextui-org/image";
interface props {
    stream?: MediaStream
    audioContext?: AudioContext
    name?: string
    uuid?: string
    user: IOnlineUsers
    volume?: number
    soundIsActive: boolean
    isMute?: boolean
    isSelfMute: boolean
    enableVoice: boolean
}

const UserHead = ({
                      stream,
                      audioContext,
                      isSelfMute,
                      isMute,
                      volume,
                      soundIsActive,
                      name,
                      uuid,
                      user,
                      enableVoice
                  }: props) => {

    const {socket} = useSocketStore(state => state)
    const [instant, setInstant] = useState(0.00)
    const [isTalk, setIsTalk] = useState<boolean>(false)

    useEffect(() => {
        let interval: any
        let soundMaster: any
        if (stream && audioContext) {
            soundMaster = new SoundMeter(audioContext)
            soundMaster.connectToSource(stream, (event: any) => {
                if (!event) {
                    interval = setInterval(() => {
                        setInstant(soundMaster.instant.toFixed(2))
                    }, 250)
                }
            })
        }
        return () => {
            if (soundMaster) soundMaster.stop()
            clearInterval(interval)
        }
    }, [stream, audioContext, isSelfMute, isMute, volume, soundIsActive, name])

    useEffect(() => {
        if (!uuid) return
        if (uuid != user.uuid) return
        if (isSelfMute || isMute) return

        if (instant == 0) {
            setIsTalk(false)
            socket?.emit("onPlayerTalk", encrypt({
                uuid,
                server: user.server,
                isTalk: false,
            }))
        } else {
            if (!isTalk) {
                setIsTalk(true)
                socket?.emit("onPlayerTalk", encrypt({
                    uuid,
                    server: user.server,
                    isTalk: true,
                }))
            }
        }


    }, [instant, uuid, isMute, isMute])


    return (
        <>
            <Image
                src={`https://mc-heads.net/avatar/${name}`}
                alt={`${name} avatar`} width={50} height={50}
                className={`!z-0 !opacity-30 rounded ${instant > 0.00 && volume != 0 && soundIsActive && !isMute && !isSelfMute && enableVoice ? "!opacity-100" : ""}`}
            />
        </>
    )
}

export default UserHead
import {useEffect, useState} from "react";
import {SoundMeter} from "@/utils/SoundMeter";

interface props {
    stream?: MediaStream
    audioContext?: AudioContext
    name?: string
    volume?: number
    soundIsActive: boolean
    isMute?: boolean
    isSelfMute: boolean
    enableVoice: boolean
}

const UserVolumeLine = ({stream, audioContext, isSelfMute, isMute, volume, soundIsActive, name,enableVoice}: props) => {
    const [instant, setInstant] = useState(0.00)
    useEffect(() => {
        let interval: any
        let soundMaster: any
        if (stream && audioContext) {
            soundMaster = new SoundMeter(audioContext)
            soundMaster.connectToSource(stream, (event: any) => {
                if (!event) {
                    interval = setInterval(() => {
                        setInstant(soundMaster.instant.toFixed(2))
                    }, 50)
                }
            })
        }
        return () => {
            if (soundMaster) soundMaster.stop()
            clearInterval(interval)
        }
    }, [stream, audioContext, isSelfMute, isMute, volume, soundIsActive, name])
    return (
        <>
            <div
                className="bg-neutral-700 rounded-2xl h-[2px] my-1 relative w-full flex items-center">
                <div className="btn-gradient h-[2px] rounded-2xl absolute shadow-2xl shadow-white" style={{
                    maxWidth: "100%",
                    width: instant > 0.00 && volume != 0 && soundIsActive && !isMute && !isSelfMute && enableVoice ? `${instant * 500}%` : "0px",
                }}/>
            </div>
        </>
    )
}

export default UserVolumeLine
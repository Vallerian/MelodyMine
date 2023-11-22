import Image from "next/image";
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
}

const UserHead = ({stream, audioContext, isSelfMute, isMute, volume, soundIsActive, name}: props) => {
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
                    }, 200)
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
            <Image
                src={`https://mc-heads.net/avatar/${name}`}
                alt={`${name} avatar`} width={50} height={50}
                className={` opacity-30 rounded ${instant > 0.00 && volume != 0 && soundIsActive && !isMute && !isSelfMute ? "soundAnimationSingle" : ""}`}
            />
        </>
    )
}

export default UserHead
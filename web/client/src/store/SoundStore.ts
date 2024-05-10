import {createWithEqualityFn} from "zustand/traditional";
import {shallow} from "zustand/shallow";
import {Howl} from "howler";
import sounds from "@/sounds";
import {IPlayerStatus, ISoundSettings} from "@/interfaces";

export interface ISound {
    name: string
    howl: Howl
    loop?: boolean
    volume?: number
}

interface State {
    soundList: ISound[]
    soundSettings: ISoundSettings
    playerStatus: IPlayerStatus[]
}

interface Actions {
    initSounds: () => void
    setSoundSettings: (soundSettings: ISoundSettings) => void
    setPlayerStatus: (playerStatus: IPlayerStatus[]) => void
}

export const useSoundStore = createWithEqualityFn<State & Actions>((setState) => ({
    soundSettings: {
        lazyHear: true,
        maxDistance: 15,
        refDistance: 5,
        rolloffFactor: 1,
        innerAngle: 120,
        outerAngle: 180,
        outerVolume: 0.3
    },
    playerStatus: [],
    soundList: [],
    initSounds: () => {
        const tempSounds: ISound[] = []
        sounds.forEach(sound => {
            tempSounds.push({
                name: sound.name,
                howl: new Howl({src: sound.url, volume: sound.volume, loop: sound.loop, html5: true, format: ["mp3"]})
            })
        })
        tempSounds.forEach(sound => sound.howl.load())
        setState(() => ({soundList: tempSounds}))
    },
    setSoundSettings: (soundSettings) => setState(() => ({soundSettings})),
    setPlayerStatus: (playerStatus) => setState(() => ({playerStatus}))


}), shallow)


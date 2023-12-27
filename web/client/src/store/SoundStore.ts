import {createWithEqualityFn} from "zustand/traditional";
import {shallow} from "zustand/shallow";
import {Howl} from "howler";
import sounds from "@/sounds";

export interface sound {
    name: string
    howl: Howl
    loop?: boolean
    volume?: number
}

interface State {
    soundList: sound[]
}

interface Actions {
    initSounds: () => void
}

export const useSoundStore = createWithEqualityFn<State & Actions>((setState) => ({
    soundList: [],
    initSounds: () => {
        const tempSounds: sound[] = []
        sounds.forEach(sound => {
            tempSounds.push({
                name: sound.name,
                howl: new Howl({src: sound.url, volume: sound.volume, loop: sound.loop, html5: true, format: ["mp3"]})
            })
        })
        setState(() => ({soundList: tempSounds}))
    }

}), shallow)


import {createWithEqualityFn} from "zustand/traditional";
import {shallow} from "zustand/shallow";
import {Howl} from "howler";


interface State {
    callingSound: Howl
    callingSound2: Howl
    endCallSound: Howl
}


export const useSoundStore = createWithEqualityFn<State>(() => ({
    callingSound: new Howl({src: "calling-sound.mp3", volume: 0.5, loop: true, html5: true}),
    callingSound2: new Howl({src: "calling-sound_2.mp3", volume: 0.5, loop: true, html5: true}),
    endCallSound: new Howl({src: "hang-up-sound.mp3", volume: 0.5, html5: true}),
}), shallow)


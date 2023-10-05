import {createWithEqualityFn} from "zustand/traditional";
import {shallow} from "zustand/shallow";


interface State {
    stream: MediaStream | null
    micIsActive: boolean
    soundIsActive: boolean
}

interface Actions {
    initStream: (stream: MediaStream) => void
    setMicActive: (micIsActive: boolean) => void
    setSoundActive: (soundIsActive: boolean) => void
    closeStream: () => void
}

export const useStreamStore = createWithEqualityFn<State & Actions>((setState, getState) => ({
    stream: null,
    micIsActive: true,
    soundIsActive: true,
    initStream: (stream) => {
        setState({
            stream,
            micIsActive: true,
            soundIsActive: true
        })
    },
    setMicActive: micIsActive => {
        const stream = getState().stream
        stream?.getAudioTracks().forEach(track => {
            track.enabled = micIsActive
        })
        setState({micIsActive})

    },
    setSoundActive: soundIsActive => {
        setState({soundIsActive})
    },
    closeStream: () => {
        const stream = getState().stream
        stream?.getTracks().forEach(track => {
            track.stop()
        })
    }
}), shallow)


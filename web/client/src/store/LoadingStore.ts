import {createWithEqualityFn} from "zustand/traditional";
import {shallow} from "zustand/shallow";


interface State {
    startButton: boolean
    disconnectButton: boolean
}

interface Actions {
    setStartButton: () => void
    setDisconnectButton: () => void
}

export const useLoadingStore = createWithEqualityFn<State & Actions>((setState, getState) => ({
    startButton: false,
    disconnectButton: false,
    setStartButton: () => {
        setState(() => ({startButton: true}))
        setTimeout(() => {
            setState(() => ({startButton: false}))
        }, 5000)
    },
    setDisconnectButton: () => {
        setState(() => ({disconnectButton: true}))
        setTimeout(() => {
            setState(() => ({disconnectButton: false}))
        }, 5000)
    }
}), shallow)


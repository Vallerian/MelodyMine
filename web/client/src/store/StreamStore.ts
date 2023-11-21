import {createWithEqualityFn} from "zustand/traditional";
import {shallow} from "zustand/shallow";
import {useSocketStore} from "@/store/SocketStore";
import {encrypt} from "@/utils";
import {useUserStore} from "@/store/UserStore";


interface State {
    stream: MediaStream | null
    micIsActive: boolean
    soundIsActive: boolean
}

interface Actions {
    initStream: (stream: MediaStream) => void
    setMicActive: (micIsActive: boolean, plugin?: boolean) => void
    setSoundActive: (soundIsActive: boolean, plugin?: boolean) => void
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
    setMicActive: (micIsActive, plugin = false) => {
        const stream = getState().stream
        const {socket} = useSocketStore.getState()
        const {name, uuid, server} = useUserStore.getState()
        stream?.getAudioTracks().forEach(track => {
            track.enabled = micIsActive
        })
        setState({micIsActive})
        if (!plugin && socket) {
            socket.emit("onPlayerChangeControl", encrypt({
                name: name,
                uuid: uuid,
                server: server,
                type: "mic",
                value:
                micIsActive
            }))
        }

    },
    setSoundActive: (soundIsActive, plugin = false) => {
        setState({soundIsActive})
        const {socket} = useSocketStore.getState()
        const {name, uuid, server} = useUserStore.getState()

        if (!plugin && socket) {
            socket.emit("onPlayerChangeControl", encrypt({
                name: name,
                uuid: uuid,
                server: server,
                type: "sound",
                value: soundIsActive
            }))
        }
    },
    closeStream: () => {
        const stream = getState().stream
        stream?.getTracks().forEach(track => {
            track.stop()
        })
        setState(() => ({stream: null}))
    }
}), shallow)


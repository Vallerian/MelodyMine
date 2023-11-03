import {createWithEqualityFn} from "zustand/traditional";
import {shallow} from "zustand/shallow";
import {Peer} from "peerjs";

interface State {
    socket: any
    peer: Peer | null
}

interface Actions {
    setSocket: (socket: any) => void
    setPeer: (peer: Peer) => void
    disconnectSocket: () => void
}

export const useSocketStore = createWithEqualityFn<State & Actions>((setState, getState) => ({
    socket: null,
    peer: null,
    setSocket: socket => setState(() => ({socket})),
    setPeer: peer => setState(() => ({peer})),
    disconnectSocket: () => {
        getState().socket?.disconnect()
    },
}), shallow)


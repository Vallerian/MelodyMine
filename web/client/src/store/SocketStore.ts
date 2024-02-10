import {createWithEqualityFn} from "zustand/traditional";
import {shallow} from "zustand/shallow";
import {iceServer} from "@/interfaces";


interface State {
    socket: any
    iceServers: iceServer[]
}

interface Actions {
    setSocket: (socket: any) => void
    disconnectSocket: () => void
    addIceServer: (iceServer: iceServer) => void
}

export const useSocketStore = createWithEqualityFn<State & Actions>((setState, getState) => ({
    iceServers: [],
    socket: null,
    peer: null,
    addIceServer: (iceServer: iceServer) => setState(() => ({iceServers: [...getState().iceServers, iceServer]})),
    setSocket: socket => setState(() => ({socket})),
    disconnectSocket: () => {
        getState().socket?.disconnect()
    },
}), shallow)


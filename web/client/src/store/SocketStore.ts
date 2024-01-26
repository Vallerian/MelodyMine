import {createWithEqualityFn} from "zustand/traditional";
import {shallow} from "zustand/shallow";
import {Peer} from "peerjs";
import {iceServer} from "@/interfaces";


interface State {
    socket: any
    peer: Peer | null
    iceServers: iceServer[]
}

interface Actions {
    setSocket: (socket: any) => void
    setPeer: (peer: Peer) => void
    disconnectSocket: () => void
    disconnectPeer: () => void
    addIceServer: (iceServer: iceServer) => void
}

export const useSocketStore = createWithEqualityFn<State & Actions>((setState, getState) => ({
    iceServers: [{
        urls: 'turn:melodymine.taher7.ir:3477',
        username: 'melodymine',
        credential: 'melodymine'
    }],
    socket: null,
    peer: null,
    addIceServer: (iceServer: iceServer) => setState(() => ({iceServers: [...getState().iceServers, iceServer]})),
    setSocket: socket => setState(() => ({socket})),
    setPeer: peer => setState(() => ({peer})),
    disconnectSocket: () => {
        getState().socket?.disconnect()
    },
    disconnectPeer: () => {
        getState().peer?.destroy()
        getState().peer?.disconnect()
    }
}), shallow)


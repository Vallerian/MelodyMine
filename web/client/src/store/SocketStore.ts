import {createWithEqualityFn} from "zustand/traditional";
import {shallow} from "zustand/shallow";

interface State {
    socket: any
}

interface Actions {
    setSocket: (socket: any) => void
    disconnectSocket: () => void
}

export const useSocketStore = createWithEqualityFn<State & Actions>((setState, getState) => ({
    socket: null,
    setSocket: socket => setState(() => ({socket})),
    disconnectSocket: () => {
        getState().socket?.disconnect()
    },
}), shallow)


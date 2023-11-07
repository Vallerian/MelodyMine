import {Socket} from "socket.io"

export interface IClient {
    from: "plugin" | "web"
    name?: string | null | undefined
    uuid?: string | null | undefined
    server?: string | null | undefined
    key?: string | null | undefined
}

export interface CustomSocket extends Socket {
    melodyClient: IClient
}

export interface callData {
    player: {
        name: string
        uuid: string
        socketID: string
    },
    target: {
        name: string
        uuid: string
        socketID: string
    }
}
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


export interface Location {
    x: number
    y: number
    z: number
}

export interface SoundSettings {
    lazyHear: boolean,
    maxDistance: number,
    refDistance: number,
    rolloffFactor: number,
    innerAngle: number,
    outerAngle: number,
    outerVolume: number,
}

export interface RenewPlayer {
    id: number
    l: [number, number, number]
    d: [number, number, number]
}

export interface RenewData {
    p: RenewPlayer[]
    c?: [number, number]
    d?: [number, number]
    v?: [number, number]
}
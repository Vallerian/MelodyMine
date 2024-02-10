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



export interface RenewData {
    name: string
    uuid: string
    server: string
    enableVoice: EnableVoiceTask[]
    disableVoice: DisableVoiceTask[]
    volume: VolumeTask[]
}

export interface EnableVoiceTask {
    socketID: string
}

export interface DisableVoiceTask {
    socketID: string
}

export interface Location {
    x: number
    y: number
    z: number
}

export interface VolumeTask {
    socketID: string
    distance: number
    settings:SoundSettings
    playerLocation: Location
    targetLocation: Location
    playerDirection: Location
    targetDirection: Location
}

export interface SoundSettings {
    sound3D: boolean,
    lazyHear: boolean,
    maxDistance: number,
    refDistance: number,
    innerAngle: number,
    outerAngle: number,
    outerVolume: number,
}
export interface IUser {
    name?: string | null | undefined
    uuid?: string | null | undefined
}

export interface IMelodyPlayer {
    uuid: string
    name: string
    server: string
    isMute: boolean
    isAdminMode?: boolean
    isActiveVoice: boolean
    serverIsOnline: boolean
    webIsOnline: boolean
}

export interface IOnlineUsers {
    name?: string
    uuid?: string
    server?: string
    isMute?: boolean
    isAdminMode?: boolean
}

export interface IPeer {
    uuid: string
    server?: string
    peer: RTCPeerConnection
}

export interface ISoundSettings {
    lazyHear: boolean
    maxDistance: number
    refDistance: number
    rolloffFactor: number
    innerAngle: number
    outerAngle: number
    outerVolume: number
}

export interface IPlayerStatus {
    uuid: string
    isMute: boolean
    isDeafen: boolean
}

export interface ILocation {
    x: number
    y: number
    z: number
}

export interface IDirection {
    x: number
    y: number
    z: number
}

export interface IVolume {
    uuid: string
    playerLocation: ILocation
    targetLocation: ILocation
    playerDirection: IDirection
    targetDirection: IDirection
}


export interface IReceiveControl {
    name: string
    uuid: string
    server: string
    type: "mic" | "sound"
    value: boolean
}

export interface iceServer {
    urls: string
    username: string
    credential: string
}
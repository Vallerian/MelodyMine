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
    serverLastLogin?: Date | null
    serverLastLogout?: Date | null
    webIsOnline: boolean
    webLastLogin?: Date | null
    webLastLogout?: Date | null
}

export interface IOnlineUsers {
    name?: string
    uuid?: string
    server?: string
    isMute?: boolean
    isAdminMode?: boolean
}

export interface IVolume {
    uuid: string
    volume: string
}

export interface IPeer {
    uuid: string
    server?: string
    peer: RTCPeerConnection
}
import {IMelodyPlayer, IOnlineUsers} from "@/interfaces";
import {createWithEqualityFn} from "zustand/traditional";
import {shallow} from "zustand/shallow";


interface State extends IMelodyPlayer {
    secretKey: string
    iceServers: string
}

interface Actions {
    initUser: (user: IMelodyPlayer) => void
    changeUserServer: (user: IOnlineUsers) => void
    changeUserIsMute: (value: boolean) => void
    changeUserAdminMode: (value: boolean) => void
    setSecretKey: (key: string) => void
    setServer: (server: string) => void
    changeActiveVoice: (value: boolean) => void
}

export const useUserStore = createWithEqualityFn<State & Actions>((setState) => ({
    name: "",
    uuid: "",
    server: "",
    secretKey: "",
    iceServers: "",
    isActiveVoice: false,
    serverIsOnline: false,
    isMute: false,
    isAdminMode: false,
    serverLastLogin: null,
    serverLastLogout: null,
    webIsOnline: false,
    webLastLogin: null,
    webLastLogout: null,
    peer: null,
    initUser: user => setState(() => ({...user})),
    changeUserServer: user => {
        setState(() => ({
            server: user.server
        }))
    },
    changeUserIsMute: value => setState(() => ({isMute: value})),
    changeUserAdminMode: value => setState(() => ({isAdminMode: value})),
    setSecretKey: key => setState(() => ({secretKey: key})),
    setServer: server => setState(() => ({server})),
    changeActiveVoice: value => setState(() => ({isActiveVoice: value}))

}), shallow)


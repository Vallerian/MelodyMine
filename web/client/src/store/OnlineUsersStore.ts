import {IOnlineUsers} from "@/interfaces/User";
import {createWithEqualityFn} from "zustand/traditional";
import {shallow} from "zustand/shallow";
import {useUserStore} from "@/store/UserStore";

interface State {
    users: IOnlineUsers[]
}

interface Actions {
    initUsers: (users: IOnlineUsers[]) => void
    addUser: (user: IOnlineUsers) => void
    removeUser: (uuid: string) => void
    updateUser: (user: IOnlineUsers) => void
    removeAllOnline: (server?: string) => void
    setMute: (uuid: string, value: boolean) => void
    setAdminMode: (uuid: string, value: boolean) => void
    setAdminModeAll: (checkAdmin?: boolean) => void
}

export const useOnlineUsersStore = createWithEqualityFn<State & Actions>((setState, getState) => ({
    users: [],
    initUsers: users => {
        const filteredUser = users.filter(item => item.uuid)
        setState(() => ({users: filteredUser}))
    },
    addUser: user => {
        const users = [...getState().users]
        const hasUser = users.find(item => item.uuid == user.uuid)
        if (!hasUser) {
            users.push(user)
            setState(() => ({users}))
        }
    },
    removeUser: uuid => {
        let users = [...getState().users]
        users = users.filter(item => item.uuid != uuid)
        setState({users})
    },
    updateUser: user => {
        const users = [...getState().users]
        const index = users.findIndex(item => item.uuid == user.uuid)
        users[index] = user
        setState({users})
    },
    removeAllOnline: server => {
        if (server) {
            let users = [...getState().users]
            users = users.filter(item => item.server != server)
            setState(() => ({users}))
        } else {
            setState(() => ({users: []}))
        }
    },
    setMute: (uuid, value) => {
        const users = [...getState().users]
        const index = users.findIndex(item => item.uuid == uuid)
        const user = users[index]
        if (user) {
            user.isMute = value
            users[index] = user
            setState(() => ({users}))
        }
    },
    setAdminMode: (uuid, value) => {
        const users = [...getState().users]
        const index = users.findIndex(item => item.uuid == uuid)
        const user = users[index]
        if (user) {
            user.isAdminMode = value
            users[index] = user
            setState(() => ({users}))
        }
    },
    setAdminModeAll: (checkAdmin) => {
        let server = useUserStore.getState().server
        let users = [...getState().users]
        if (!checkAdmin) {
            users = users.map(item => ({...item, isAdminMode: false}))
            setState(() => ({users}))
        } else {
            users = users.map(item => {
                if (item.server != server) {
                    return {...item, isAdminMode: false}
                } else {
                    return {...item}
                }
            })
            setState(() => ({users}))
        }
    }
}), shallow)


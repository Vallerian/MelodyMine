import {createJSONStorage, persist} from "zustand/middleware";
import {createWithEqualityFn} from "zustand/traditional";
import {shallow} from "zustand/shallow";

interface userMute {
    uuid: string
    isSelfMute: boolean
}

interface State {
    users: userMute[]
}

interface Actions extends State {
    setSelfMute: (uuid: string, value: boolean) => void
}


export const useMuteStore = createWithEqualityFn(
    persist<Actions>(
        (setState, getState) => ({
            users: [],
            setSelfMute: (uuid, value) => {
                const users = [...getState().users]
                const index = users.findIndex(item => item.uuid == uuid)
                const user = users[index]
                if (user) {
                    user.isSelfMute = value
                    users[index] = user
                    setState(() => ({users}))
                } else {
                    users.push({
                        uuid: uuid,
                        isSelfMute: value
                    })
                    setState(() => ({users}))
                }
            }
        }),
        {
            name: 'selfMute-storage',
            storage: createJSONStorage(() => sessionStorage),
        },
    ), shallow
)
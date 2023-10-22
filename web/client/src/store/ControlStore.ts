import {createJSONStorage, persist} from "zustand/middleware";
import {createWithEqualityFn} from "zustand/traditional";
import {shallow} from "zustand/shallow";

interface userMute {
    uuid: string
    isSelfMute: boolean
}

interface State {
    muteUsers: userMute[]
    noiseSuppression: boolean
}

interface Actions extends State {
    setUserMute: (uuid: string, value: boolean) => void
    setNoiseSuppression: (value: boolean) => void
}


export const useControlStore = createWithEqualityFn(
    persist<Actions>(
        (setState, getState) => ({
            muteUsers: [],
            noiseSuppression: true,
            setUserMute: (uuid, value) => {
                const users = [...getState().muteUsers]
                const index = users.findIndex(item => item.uuid == uuid)
                const user = users[index]
                if (user) {
                    user.isSelfMute = value
                    users[index] = user
                    setState(() => ({muteUsers: users}))
                } else {
                    users.push({
                        uuid: uuid,
                        isSelfMute: value
                    })
                    setState(() => ({muteUsers: users}))
                }
            },
            setNoiseSuppression: async value => setState(() => ({noiseSuppression: value}))

        }),
        {
            name: 'selfMute-storage',
            storage: createJSONStorage(() => sessionStorage),
        },
    ), shallow
)
import {createJSONStorage, persist} from "zustand/middleware";
import {createWithEqualityFn} from "zustand/traditional";
import {shallow} from "zustand/shallow";
import {useStreamStore} from "@/store/StreamStore";

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
            noiseSuppression: false,
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
            setNoiseSuppression: async value => {
                const stream = useStreamStore.getState().stream
                const {micIsActive} = useStreamStore.getState()
                if (stream) {
                    stream.getAudioTracks().forEach(track => {
                        stream.removeTrack(track)
                    })
                    const newStream = await navigator.mediaDevices.getUserMedia({
                        audio: {
                            noiseSuppression: value,
                        }, video: false
                    })
                    newStream.getAudioTracks().forEach(track => {
                        stream.addTrack(track)
                        track.enabled = micIsActive

                    })
                    setState(() => ({noiseSuppression: value}))
                }
            }
        }),
        {
            name: 'selfMute-storage',
            storage: createJSONStorage(() => sessionStorage),
        },
    ), shallow
)
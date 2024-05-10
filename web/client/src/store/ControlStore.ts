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
    echoCancellation: boolean
    sound3D: boolean
    sound3DModel: "inverse" | "linear" | "exponential"
    mediaDevices: MediaDeviceInfo[]
    audioInput?: MediaDeviceInfo
    audioOutput?: MediaDeviceInfo
}

interface Actions extends State {
    setUserMute: (uuid: string, value: boolean) => void
    setNoiseSuppression: (value: boolean) => void
    setEchoCancellation: (value: boolean) => void
    setAudioInput: (value: MediaDeviceInfo) => void
    setAudioOutput: (value: MediaDeviceInfo) => void
    setSound3D: (value: boolean) => void
    setSound3DModel: (value: "inverse" | "linear" | "exponential") => void
    initMediaDevices: () => void
}


export const useControlStore = createWithEqualityFn(
    persist<Actions>(
        (setState, getState) => ({
            muteUsers: [],
            mediaDevices: [],
            noiseSuppression: true,
            echoCancellation: true,
            sound3D: true,
            sound3DModel: "linear",
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
            setNoiseSuppression: noiseSuppression => setState(() => ({noiseSuppression})),
            setEchoCancellation: echoCancellation => setState(() => ({echoCancellation})),
            setSound3D: sound3D => setState(() => ({sound3D})),
            setSound3DModel: sound3DModel => setState(() => ({sound3DModel})),
            initMediaDevices: async () => {

                // @ts-ignore
                const permission = await navigator.permissions.query({name: 'microphone'})
                if (permission.state != "granted") return

                let devices = await navigator.mediaDevices.enumerateDevices()

                setState(() => ({mediaDevices: devices.filter(device => device.kind != "videoinput")}))

                if (!getState().audioOutput && !getState().audioInput) {
                    devices.forEach(device => {
                        if (device.deviceId == "default") {
                            if (device.kind == "audioinput") {
                                setState(() => ({audioInput: device}))
                            }

                            if (device.kind == "audiooutput") {
                                setState(() => ({audioOutput: device}))
                            }
                        }
                    })
                    return
                }

                if (!devices.find(device => device.kind == "audioinput" && device.deviceId == getState().audioInput?.deviceId)) {
                    setState(() => ({audioInput: devices.find(device => device.kind == "audioinput" && device.deviceId == "default")}))
                }

                if (!devices.find(device => device.kind == "audiooutput" && device.deviceId == getState().audioOutput?.deviceId)) {
                    setState(() => ({audioOutput: devices.find(device => device.kind == "audiooutput" && device.deviceId == "default")}))
                }
            },
            setAudioInput: audioInput => setState(() => ({audioInput})),
            setAudioOutput: audioOutput => setState(() => ({audioOutput})),

        }),
        {
            name: 'control-storage',
            storage: createJSONStorage(() => sessionStorage),
        },
    ), shallow
)
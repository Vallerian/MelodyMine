import {createWithEqualityFn} from "zustand/traditional";
import {shallow} from "zustand/shallow";
import {IVolume} from "@/interfaces/User";
import {useUserStore} from "@/store/UserStore";


interface State {
    volumes: IVolume[]
}

interface Actions {
    setVolume: (uuid: string, volume: string) => void
}

export const useVolumeStore = createWithEqualityFn<State & Actions>((setState, getState) => ({
    volumes: [],
    setVolume: async (uuid, volume) => {
        const isAdminMode = useUserStore.getState().isAdminMode
        if (!isAdminMode) {
            const volumes = [...getState().volumes]
            const index = volumes.findIndex(item => item.uuid == uuid)
            if (volumes[index]) {
                volumes[index].volume = volume
            } else {
                volumes.push({uuid, volume})
            }
            setState(() => ({volumes}))
        }
    },
}), shallow)


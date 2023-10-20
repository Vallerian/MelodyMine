import {createWithEqualityFn} from "zustand/traditional";
import {shallow} from "zustand/shallow";
import {IVolume} from "@/interfaces";
import {useUserStore} from "@/store/UserStore";


interface State {
    volumes: IVolume[]
}

interface Actions {
    setVolume: (data: IVolume) => void
}

export const useVolumeStore = createWithEqualityFn<State & Actions>((setState, getState) => ({
    volumes: [],
    setVolume: async (data: IVolume) => {
        const {volume, uuid, selfLocation, targetLocation} = data
        const isAdminMode = useUserStore.getState().isAdminMode
        if (!isAdminMode) {
            const volumes = [...getState().volumes]
            const index = volumes.findIndex(item => item.uuid == uuid)
            if (volumes[index]) {
                volumes[index].volume = volume
                volumes[index].selfLocation = selfLocation
                volumes[index].targetLocation = targetLocation
            } else {
                volumes.push(data)
            }
            setState(() => ({volumes}))
        }
    },
}), shallow)


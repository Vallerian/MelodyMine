"use client"
import {IOnlineUsers} from "@/interfaces/User";
import {useOnlineUsersStore} from "@/store/OnlineUsersStore";
import {useEffect, useState} from "react";
import {useSocketStore} from "@/store/SocketStore";
import {usePeersStore} from "@/store/PeersStore";
import {useSession} from "next-auth/react";
import {useValidateStore} from "@/store/ValidateStore";
import {useStreamStore} from "@/store/StreamStore";
import {useVolumeStore} from "@/store/VolumeStore";
import {useUserStore} from "@/store/UserStore";
import {decrypt} from "@/utils";
import SingleUser from "@/components/SingleUser";


const UserList = () => {
    const {
        initUsers,
        users,
        addUser,
        removeUser,
        removeAllOnline,
        updateUser,
        setMute,
        setAdminMode,
        setAdminModeAll
    } = useOnlineUsersStore(state => state)
    const {socket} = useSocketStore(state => state)
    const {uuid, server} = useUserStore(state => state)
    const {changeUserServer, changeUserIsMute, changeUserAdminMode} = useUserStore(state => state)
    const {setVolume} = useVolumeStore(state => state)
    const {setValidate, setError} = useValidateStore(state => state)
    const {closeStream} = useStreamStore(state => state)
    const {
        createOffer,
        createAnswer,
        removePeer,
        setDescription,
        setIcecandidate,
        removeAll
    } = usePeersStore(state => state)
    const [userOnline, setUserOnline] = useState<IOnlineUsers[]>()
    const {data: session} = useSession()

    useEffect(() => {

        socket?.on("onPlayerJoinReceive", (token: string) => {
            const data = decrypt(token) as IOnlineUsers[]
            initUsers(data)
        })

        socket?.on("onPlayerStartVoiceReceive", (token: string) => {
            const user = decrypt(token) as IOnlineUsers
            addUser(user)
        })

        socket?.on("onPlayerInDistanceReceive", (token: string) => {
            const user = decrypt(token) as IOnlineUsers
            createOffer(user.uuid!, user.server!)
        })

        socket?.on("onPlayerOutDistanceReceive", (token: string) => {
            const user = decrypt(token) as IOnlineUsers
            removePeer(user.uuid!)
        })

        socket?.on("onReceiveOffer", (token: string) => {
            const data = decrypt(token) as {
                uuid: string,
                server: string,
                offer: RTCSessionDescription
            }
            createAnswer(data.uuid, data.server!, data.offer)
        })

        socket?.on("onReceiveAnswer", (token: string) => {
            const data = decrypt(token) as {
                uuid: string,
                answer: RTCSessionDescription
            }
            setDescription(data.uuid, data.answer)
        })

        socket?.on("onReceiveCandidate", (token: string) => {
            const data = decrypt(token) as {
                uuid: string,
                candidate: RTCIceCandidate
            }
            setIcecandidate(data.uuid, data.candidate)
        })

        socket?.on("onNewPlayerLeave", (token: string) => {
            const user = decrypt(token) as IOnlineUsers
            removeUser(user.uuid!)
            removePeer(user.uuid!)
        })

        socket?.on("onPlayerLeaveReceivePlugin", (token: string) => {
            const user = decrypt(token) as IOnlineUsers
            if (user.uuid != session?.user.uuid) {
                removeUser(user.uuid!)
                removePeer(user.uuid!)
            } else {
                setError("playerLeaveServer")
                setValidate(false)
                closeStream()
                removeUser(user.uuid!)
                removeAll()
            }
        })

        socket?.on("onPlayerVolumeReceivePlugin", (data: {
            uuid: string,
            volume: string
        }) => {
            setVolume(data.uuid, data.volume)
        })

        socket?.on("onPluginDisabled", (token: string) => {
            const data = decrypt(token) as {
                server: string
            }
            setError("pluginDisabled")
            setValidate(false)
            closeStream()
            removeAllOnline(data.server)
            removeAll()
        })

        socket?.on("onPlayerChangeServer", (token: string) => {
            const data = decrypt(token) as {
                name: string,
                uuid: string,
                server: string
            }
            if (data.uuid == session?.user.uuid) {
                updateUser(data)
                changeUserServer(data)
                removeAll(true)
                setAdminModeAll(true)
                changeUserAdminMode(false)
            } else {
                updateUser(data)
                removePeer(data.uuid, true)
                setAdminMode(data.uuid, false)
            }
        })

        socket?.on("onAdminModeEnableReceive", (token: string) => {
            const data = decrypt(token) as {
                uuid: string,
                server: string
            }
            if (data.uuid != session?.user.uuid) {
                removePeer(data.uuid)
                createOffer(data.uuid!, data.server)
                setAdminMode(data.uuid, true)
                setVolume(data.uuid, "1.0")
            } else {
                changeUserAdminMode(true)
                setAdminMode(data.uuid, true)
                removeAll()
            }
        })

        socket?.on("onAdminModeDisableReceive", (token: string) => {
            const data = decrypt(token) as {
                uuid: string
            }
            if (data.uuid == session?.user.uuid) {
                changeUserAdminMode(false)
                setAdminMode(data.uuid, false)
                removeAll()
            } else {
                setAdminMode(data.uuid, false)
                removePeer(data.uuid)
            }
        })

        socket?.on("onPlayerInitAdminModeReceive", (token: string) => {
            const data = decrypt(token) as IOnlineUsers
            createOffer(data.uuid!, data.server!)
            setAdminMode(data.uuid!, true)
            setVolume(data.uuid!, "1.0")
        })

        socket?.on("onPlayerMuteReceive", (token: string) => {
            const data = decrypt(token) as {
                uuid: string
            }
            if (data.uuid == session?.user.uuid) {
                changeUserIsMute(true)
                setMute(data.uuid, true)
            } else {
                setMute(data.uuid, true)
            }
        })

        socket?.on("onPlayerUnmuteReceive", (token: string) => {
            const data = decrypt(token) as {
                uuid: string
            }
            if (data.uuid == session?.user.uuid) {
                changeUserIsMute(false)
                setMute(data.uuid, false)
            } else {
                setMute(data.uuid, false)
            }
        })

    }, [socket])

    useEffect(() => {
        setUserOnline(users)
    }, [users])

    return (
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 w-full mt-3">
            {userOnline?.sort((a, b) => {
                const uuidComparison = (a.uuid === uuid && b.uuid !== uuid) ? -1 :
                    (a.uuid !== uuid && b.uuid === uuid) ? 1 : 0;

                const serverComparison = (a.server === server && b.server !== server) ? -1 :
                    (a.server !== server && b.server === server) ? 1 : 0;

                const adminComparison = (a.isAdminMode && !b.isAdminMode) ? -1 :
                    (!a.isAdminMode && !b.isAdminMode) ? 1 : 0;

                return serverComparison || uuidComparison || adminComparison;
            })?.map(user => (
                <SingleUser key={user.uuid} user={user}/>
            ))}
        </div>
    )
}
export default UserList

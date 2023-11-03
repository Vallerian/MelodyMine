"use client"
import {IOnlineUsers} from "@/interfaces";
import {useOnlineUsersStore} from "@/store/OnlineUsersStore";
import {useEffect, useState} from "react";
import {useSocketStore} from "@/store/SocketStore";
import {useValidateStore} from "@/store/ValidateStore";
import {useStreamStore} from "@/store/StreamStore";
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
        setAdminModeAll
    } = useOnlineUsersStore(state => state)
    const {socket} = useSocketStore(state => state)
    const {setMicActive, setSoundActive} = useStreamStore(state => state)
    const {
        uuid,
        server,
        changeUserServer,
        changeUserIsMute,
        changeUserAdminMode
    } = useUserStore(state => state)
    const {setValidate, setError} = useValidateStore(state => state)
    const {closeStream} = useStreamStore(state => state)
    const [userOnline, setUserOnline] = useState<IOnlineUsers[]>()


    useEffect(() => {
        socket?.on("onPlayerJoinReceive", (token: string) => {
            const data = decrypt(token) as IOnlineUsers[]
            initUsers(data)
        })

        socket?.on("onPlayerStartVoiceReceive", (token: string) => {
            const user = decrypt(token) as IOnlineUsers
            addUser(user)
        })

        socket?.on("onNewPlayerLeave", (token: string) => {
            const user = decrypt(token) as IOnlineUsers
            removeUser(user.uuid!)
        })

        socket?.on("onPlayerLeaveReceivePlugin", (token: string) => {
            const user = decrypt(token) as IOnlineUsers
            if (user.uuid != uuid) {
                removeUser(user.uuid!)
            } else {
                setValidate(false)
                setError("playerLeaveServer")
                closeStream()
                removeUser(user.uuid!)
                changeUserAdminMode(false)
            }
        })

        socket?.on("onPluginDisabled", (token: string) => {
            const data = decrypt(token) as {
                server: string
            }
            setError("pluginDisabled")
            setValidate(false)
            closeStream()
            removeAllOnline(data.server)
            changeUserAdminMode(false)
        })

        socket?.on("onPlayerChangeServer", (token: string) => {
            const data = decrypt(token) as {
                name: string,
                uuid: string,
                server: string
            }
            if (data.uuid == uuid) {
                updateUser(data)
                changeUserServer(data)
                setAdminModeAll(true)
                changeUserAdminMode(false)
                setMicActive(true)
                setSoundActive(true)
            } else {
                updateUser(data)
            }
        })

        socket?.on("onPlayerMuteReceive", (token: string) => {
            const data = decrypt(token) as {
                uuid: string
            }
            if (data.uuid == uuid) {
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
            if (data.uuid == uuid) {
                changeUserIsMute(false)
                setMute(data.uuid, false)
            } else {
                setMute(data.uuid, false)
            }
        })

        return () => {
            socket?.off("onPlayerJoinReceive")
            socket?.off("onPlayerStartVoiceReceive")
            socket?.off("onNewPlayerLeave")
            socket?.off("onPlayerLeaveReceivePlugin")
            socket?.off("onPluginDisabled")
            socket?.off("onPlayerChangeServer")
            socket?.off("onPlayerMuteReceive")
            socket?.off("onPlayerUnmuteReceive")
        }

    }, [socket, uuid])

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
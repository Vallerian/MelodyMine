"use client"
import Image from "next/image";
import {FaUserAlt} from "react-icons/fa";
import {TbArrowBigRightLineFilled, TbPlugConnected, TbPlugConnectedX} from "react-icons/tb";
import {RxExit} from "react-icons/rx";
import StartButton from "@/components/StartButton";
import {signOut, useSession} from "next-auth/react";
import {useUserStore} from "@/store/UserStore";
import {useValidateStore} from "@/store/ValidateStore";
import {IUser} from "@/interfaces";
import {BsFillMicMuteFill, BsFillPeopleFill} from "react-icons/bs";
import {useOnlineUsersStore} from "@/store/OnlineUsersStore";
import {useEffect, useState} from "react";
import {ImUserTie} from "react-icons/im";
import {useSocketStore} from "@/store/SocketStore";
import {decrypt} from "@/utils";
import {useSoundStore} from "@/store/SoundStore";


interface UserInfoProps {
    user: IUser,
    websocketKey?: string
}

const UserInfo = ({user, websocketKey}: UserInfoProps) => {
    const {server, setSecretKey, isMute} = useUserStore(state => state)
    const {isValidate} = useValidateStore(state => state)
    const {initSounds, soundList} = useSoundStore(state => state)
    const {socket} = useSocketStore(state => state)
    const {users} = useOnlineUsersStore(state => state)
    const {status} = useSession()
    const [userIsAdminMode, setUserIsAdminMode] = useState<boolean>(false)

    const onAdminModeEnableReceive = (token: string) => {
        const data = decrypt(token) as {
            uuid: string,
            server: string
        }
        if (data.uuid != user.uuid) return
        setUserIsAdminMode(true)
    }

    const onAdminModeDisableReceive = (token: string) => {
        const data = decrypt(token) as {
            uuid: string
        }
        if (data.uuid != user.uuid) return
        setUserIsAdminMode(false)
    }

    const onPlaySoundReceive = (token: string) => {
        const data = decrypt(token) as {
            sound: string
        }
        const sound = soundList.find(item => item.name == data.sound)?.howl
        sound?.play()
    }

    const onPauseSoundReceive = (token: string) => {
        const data = decrypt(token) as {
            sound: string
        }
        const sound = soundList.find(item => item.name == data.sound)?.howl
        sound?.pause()
    }

    const onStopSoundReceive = (token: string) => {
        const data = decrypt(token) as {
            sound: string
        }
        const sound = soundList.find(item => item.name == data.sound)?.howl
        sound?.stop()
    }


    useEffect(() => {
        setSecretKey(websocketKey!!)
        initSounds()
    }, [])

    useEffect(() => {
        socket?.on("onAdminModeEnableReceive", onAdminModeEnableReceive)
        socket?.on("onAdminModeDisableReceive", onAdminModeDisableReceive)
        socket?.on("onPlaySoundReceive", onPlaySoundReceive)
        socket?.on("onPauseSoundReceive", onPauseSoundReceive)
        socket?.on("onStopSoundReceive", onStopSoundReceive)

        return () => {
            socket?.off("onAdminModeEnableReceive", onAdminModeEnableReceive)
            socket?.off("onAdminModeDisableReceive", onAdminModeDisableReceive)
            socket?.off("onPlaySoundReceive", onPlaySoundReceive)
            socket?.off("onPauseSoundReceive", onPauseSoundReceive)
            socket?.off("onStopSoundReceive", onStopSoundReceive)
        }

    }, [socket, soundList])

    useEffect(() => {
        if (isValidate) return
        setUserIsAdminMode(false)
    }, [isValidate])

    return (
        <div className="flex flex-col rounded px-3 py-1 bg-custom shadow-xl w-full">
            <div className="flex justify-between">
                <div className="flex justify-start items-center">
                    <Image
                        src={`https://mc-heads.net/head/${user.name}`}
                        alt="" width={70} height={70}
                        className="object-contain"
                    />
                    <div className="ml-2">
                        <h1 className="text-white flex items-center w-full">
                            <span className="mr-2 hidden sm:block">
                                <FaUserAlt/>
                            </span>
                            <span className="capitalize">
                                {user.name}
                            </span>
                            <div className="flex items-center">
                                {isMute ? (
                                    <div className="ms-2 self-center">
                                        <span
                                            className="whitespace-nowrap ring-1 ring-red-900 text-xs font-medium mr-2 px-1.5 py-0.5 rounded dark:bg-red-500 dark:text-white flex">
                                            <span className="me-1 self-center hidden sm:block">
                                                <BsFillMicMuteFill/>
                                            </span>
                                            Server Mute
                                        </span>
                                    </div>
                                ) : ""}
                                {userIsAdminMode ? (
                                    <div className="ms-2 self-center">
                                    <span
                                        className="ring-1 ring-cyan-900 text-xs font-medium mr-2 px-1.5 py-0.5 rounded dark:bg-cyan-500 dark:text-white flex">
                                        <span className="me-1 self-center hidden sm:block">
                                            <ImUserTie/>
                                        </span>
                                        Admin
                                    </span>
                                    </div>
                                ) : ""}
                            </div>
                        </h1>

                        <div className="my-1 btn-gradient w-full h-[1px] rounded-xl"/>

                        <h3 className={`text-sm text-white ${isValidate ? "bg-emerald-500" : "bg-red-500"}  px-3 py-1 rounded flex items-center w-fit`}>
                            <span className="text-xl mr-2 hidden sm:block">
                                {isValidate ? <TbPlugConnected/> : <TbPlugConnectedX/>}
                            </span>
                            <span className="capitalize">
                                {isValidate ? "Connected" : "Disconnect"}
                            </span>
                        </h3>
                    </div>
                </div>
                <div className="flex flex-col justify-between my-1.5 text-white">
                    <div className="flex justify-end">
                        <button disabled={status == "loading"} className="text-red-500 cursor-pointer text-xl"
                                onClick={() => signOut({
                                    redirect: true,
                                    callbackUrl: "/login"
                                })}>
                            <RxExit/>
                        </button>
                    </div>
                    <StartButton/>
                </div>
            </div>
            <div className="my-1 btn-gradient w-full h-[1px] rounded-xl flex justify-between"/>
            <div className="w-full flex justify-between items-center">
                <h3 className="flex items-center text-white">
                    <span className="px-1">
                        <TbArrowBigRightLineFilled/>
                    </span>
                    Connected to:
                    {isValidate ? (<>
                        <span className="ml-1 font-bold text-green-500">
                            {isValidate ? server : ""}
                        </span>
                        <span className="ms-1">
                            <BsFillPeopleFill/>
                        </span>
                        <span className="ms-1">
                            {users.filter(item => item.server == server).length}
                        </span>
                    </>) : ""}
                </h3>
                <div
                    className="text-transparent bg-clip-text bg-gradient-to-r from-[#221854] to-[#F04FE7] flex items-center">
                    All
                    <span className="ms-1">
                        {users.length}
                    </span>
                    <span className="ms-1 text-[#F04FE7]">
                        <BsFillPeopleFill/>
                    </span>
                </div>
            </div>
        </div>
    )
}
export default UserInfo

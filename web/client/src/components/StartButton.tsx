"use client"
import {GiSoundWaves} from "react-icons/gi";
import {useUserStore} from "@/store/UserStore";
import {useValidateStore} from "@/store/ValidateStore";
import {useEffect, useState} from "react";
import Progress from "@/components/Porgress/Progress";
import {useSocketStore} from "@/store/SocketStore";
import {Socket, io} from "socket.io-client";
import {useSession} from "next-auth/react";
import {useStreamStore} from "@/store/StreamStore";
import {useOnlineUsersStore} from "@/store/OnlineUsersStore";
import {useRouter} from "next/navigation";
import {useLoadingStore} from "@/store/LoadingStore";
import {decrypt, encrypt} from "@/utils";
import {DefaultEventsMap} from "@socket.io/component-emitter";
import {useControlStore} from "@/store/ControlStore";

const StartButton = () => {

    const {status} = useSession()
    const user = useUserStore(state => state)
    const {noiseSuppression} = useControlStore(state => state)
    const {socket, setSocket, disconnectSocket, setPeer} = useSocketStore(state => state)
    const {addUser, removeAllOnline, setAdminModeAll} = useOnlineUsersStore(state => state)
    const {initStream, closeStream} = useStreamStore(state => state)
    const {isValidate, setError, setValidate} = useValidateStore(state => state)
    const {startButton, setDisconnectButton} = useLoadingStore(state => state)
    const [isLoading, setIsLoading] = useState<boolean>(false)
    const [socketConnected, setSocketConnected] = useState<boolean>(false)
    const [peerConnection, setPeerConnection] = useState<boolean>(false)
    const route = useRouter()

    useEffect(() => {
        let socket: Socket<DefaultEventsMap, DefaultEventsMap>
        const doAsync = async () => {
            const res = await fetch("/api/user/data", {
                method: "GET",
                headers: {"Content-Type": "application/json"},
            })

            if (!res.ok) return
            const {token} = await res.json()
            const {player: data, socketURL} = decrypt(token)
            if (data.webIsOnline) {
                route.push("/?error=multiUser")
            } else {
                try {

                    socket = io(socketURL, {
                        auth: {
                            from: "web",
                            token: encrypt({
                                name: data.name,
                                uuid: data.uuid,
                            })
                        },
                    })
                    setSocket(socket)


                    socket.on("connect", () => {
                        setSocketConnected(true)
                        socket.emit("onPlayerJoin", encrypt({
                            name: data.name,
                            uuid: data.uuid
                        }))
                    })


                    socket.on("disconnect", () => {
                        setSocketConnected(true)
                        setValidate(false)
                        closeStream()
                        removeAllOnline()
                        setAdminModeAll()
                    })

                    import("peerjs").then(({default: Peer}) => {
                        const newUrl = new URL(socketURL)
                        const peer = new Peer(data.uuid, {
                            host: newUrl.hostname,
                            port: Number(newUrl.port) || 443,
                            path: "/melodymine",
                        })
                        setPeer(peer)
                        setPeerConnection(true)

                        peer.on("connection", () => {
                            setPeerConnection(true)
                        })

                        peer.on("disconnected", () => {
                            setValidate(false)
                            setPeerConnection(false)
                        })
                    })


                } catch (ex) {

                }
            }
        }

        doAsync()
        return () => {
            disconnectSocket()
            setValidate(false)
            closeStream()
            removeAllOnline()
            setAdminModeAll()
        }

    }, [])


    const handleStart = async () => {
        if (status == "loading") return

        setIsLoading(true)
        const res = await fetch("/api/user/data", {
            method: "GET",
            headers: {"Content-Type": "application/json"},
        })

        if (!res.ok) {
            setIsLoading(false)
            return
        }

        const {token} = await res.json()
        const {player: data} = decrypt(token)
        user.initUser({...data, isActiveVoice: true})

        if (!data.serverIsOnline) {
            setError("serverIsOnline")
            setIsLoading(false)
            return
        }

        try {
            const stream = await navigator.mediaDevices.getUserMedia({
                audio: {
                    noiseSuppression,
                }, video: false
            })
            initStream(stream)
            setValidate(true)
            addUser({
                name: data.name,
                uuid: data.uuid,
                server: data.server,
                isMute: data.isMute
            })

            stream.getAudioTracks().forEach(track => {
                track.getSettings().noiseSuppression = true
            })

            socket?.emit("onPlayerStartVoice", encrypt({
                name: data.name,
                uuid: data.uuid,
                server: data.server,
            }))
            setDisconnectButton()
            setIsLoading(false)
        } catch (ex) {
            setError("micPermission")
            setValidate(false)
            setIsLoading(false)
        }
    }

    if (isValidate) return

    return (
        <div className="flex self-center">
            {isLoading || startButton || !socketConnected || !peerConnection ? (
                <span className="pr-5 pl-2 self-center">
                        <Progress/>
                    </span>
            ) : ""}
            <button
                className="text-sm btn-gradient px-3 py-1 rounded text-white shadow-xl flex items-center"
                onClick={handleStart}
                disabled={isLoading || status == "loading" || startButton || !socketConnected}
            >
                Start
                <span className="px-1 hidden sm:block text-2xl">
                    <GiSoundWaves/>
                    </span>
                Melody
            </button>
        </div>
    )
}
export default StartButton

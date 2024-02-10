import express from 'express';
import http from "http"
import https from "https";
import {Server} from "socket.io"
import {prisma} from "./utils/connect";
import {callData, CustomSocket, IClient, RenewData, SoundSettings} from "./interfaces";
import {decrypt, encrypt} from "./utils";
import fs from "fs"

const privateKeyPath = "./ssl/privkey.pem"
const certPath = "./ssl/cert.pem"
const chainPath = "./ssl/chain.pem"

const app = express()
let server
if (process.env.SSL) {
    server = https.createServer({
        key: fs.readFileSync(privateKeyPath),
        cert: fs.readFileSync(certPath),
        ca: fs.readFileSync(chainPath),
    }, app)
} else {
    server = http.createServer(app)
}
const PORT = process.env.PORT || 4000

const io = new Server(server, {
    cors: {
        origin: "*",
        credentials: false,
        allowedHeaders: "*"
    },
    pingInterval: 60000,
    pingTimeout: 120000,
    transports: ["websocket"],
    allowEIO3: true,
})


io.use((socket: CustomSocket, next) => {
    const from = socket.handshake.auth.from
    if (from) {
        if (from == "web") {
            const token = socket.handshake.auth.token
            if (!token) return next(new Error('Authentication failed'))
            const data = decrypt(token)
            if (data) {
                socket.melodyClient = {
                    from: from,
                    name: data.name,
                    uuid: data.uuid
                }
                return next()
            }
        } else {
            const key = socket.handshake.auth.key
            if (key) {
                if (key == process.env.WEBSOCKET_PLUGIN_AUTH_KEY) {
                    socket.melodyClient = {
                        from: socket.handshake.auth.from,
                        server: socket.handshake.auth.server,
                    }
                    return next()
                }
            }
        }
    }
})

io.on("connection", async (socket: CustomSocket) => {
    // console.log(`Client connected from: ${socket.melodyClient.from} server: ${socket.melodyClient.server} ${socket.melodyClient.name ? `name: ${socket.melodyClient.name}` : ""}`)
    await socket.join(socket.melodyClient.from)


    // Plugin Listeners
    socket.on("onPlayerLeavePlugin", async (data) => {
        const leaveUser = await prisma.melodymine.findUnique({
            where: {
                uuid: data.uuid,
            }
        })
        if (leaveUser) {
            if (leaveUser.server == socket.melodyClient.server) {
                await prisma.melodymine.update({
                    where: {uuid: data.uuid},
                    data: {isActiveVoice: false}
                })
                socket.broadcast.except("plugin").emit("onPlayerLeaveReceivePlugin", encrypt(data))
            } else {
                const leaveUserSocket = io.sockets.sockets.get(leaveUser.socketID)
                await leaveUserSocket.leave(data.server)
                await leaveUserSocket.join(leaveUser.server)
                socket.broadcast.except("plugin").emit("onPlayerChangeServer", encrypt({
                    uuid: leaveUser.uuid,
                    name: leaveUser.name,
                    server: leaveUser.server
                }))
                socket.to("plugin").emit("onPlayerChangeServerToWeb", leaveUser)
            }
        }

    })

    socket.on("onEnableVoicePlugin", data => {
        io.to(data.socketID).emit("onEnableVoiceReceive", encrypt({
            uuid: data.uuid,
            server: data.server
        }))
    })

    socket.on("onDisableVoicePlugin", data => {
        io.to(data.socketID).emit("onDisableVoiceReceive", encrypt({
            uuid: data.uuid,
        }))
    })

    socket.on("onSetVolumePlugin", data => {
        io.to(data.socketID).emit("onSetVolumeReceive", {
            uuid: data.uuid,
            distance: data.distance,
            settings: data.settings,
            playerLocation: data.playerLocation,
            targetLocation: data.targetLocation,
            playerDirection: data.playerDirection,
            targetDirection: data.targetDirection
        })
    })

    socket.on("onAdminModeEnablePlugin", data => {
        socket.to(data.server).emit("onAdminModeEnableReceive", encrypt(data))
    })

    socket.on("onAdminModeDisablePlugin", data => {
        socket.to(data.server).emit("onAdminModeDisableReceive", encrypt(data))
    })

    socket.on("onPlayerInitAdminModePlugin", data => {
        io.to(data.socketID).emit("onPlayerInitAdminModeReceive", encrypt({
            name: data.name,
            uuid: data.uuid,
            server: data.server,
        }))
    })

    socket.on("onPlayerMutePlugin", data => {
        socket.broadcast.except("plugin").emit("onPlayerMuteReceive", encrypt(data))
    })

    socket.on("onPlayerUnmutePlugin", data => {
        socket.broadcast.except("plugin").emit("onPlayerUnmuteReceive", encrypt(data))
    })


    socket.on("onSetControlPlugin", data => {
        socket.to("web").emit("onSetControlPluginReceive", encrypt(data))
    })


    socket.on("onStartCallPlugin", (data: callData) => {

        socket.to(data.player.socketID).emit("onStartCallSelfPluginReceive", encrypt({
            name: data.player.name,
            uuid: data.player.uuid,
        }))

        socket.to(data.target.socketID).emit("onStartCallTargetPluginReceive", encrypt({
            name: data.target.name,
            uuid: data.target.uuid,
        }))

    })

    socket.on("onEndCallPlugin", (data: callData) => {
        socket.to(data.player.socketID).emit("onEndCallSelfPluginReceive", encrypt({
            name: data.player.name,
            uuid: data.player.uuid,
        }))

        socket.to(data.target.socketID).emit("onEndCallTargetPluginReceive", encrypt({
            name: data.target.name,
            uuid: data.target.uuid,
        }))
    })

    socket.on("onPendingCallEndPlugin", (data: callData) => {
        socket.to(data.player.socketID).emit("onPendingCallEndSelfPluginReceive", encrypt({
            name: data.player.name,
            uuid: data.player.uuid,
        }))

        socket.to(data.target.socketID).emit("onPendingCallEndTargetPluginReceive", encrypt({
            name: data.target.name,
            uuid: data.target.uuid,
        }))
    })

    socket.on("onAcceptCallPlugin", (data: callData) => {
        socket.to(data.player.socketID).emit("onAcceptCallSelfPluginReceive", encrypt({
            name: data.player.name,
            uuid: data.player.uuid,
        }))

        socket.to(data.target.socketID).emit("onAcceptCallTargetPluginReceive", encrypt({
            name: data.target.name,
            uuid: data.target.uuid,
        }))
    })

    socket.on("onDenyCallPlugin", (data: callData) => {
        socket.to(data.player.socketID).emit("onDenyCallSelfPluginReceive", encrypt({
            name: data.player.name,
            uuid: data.player.uuid,
        }))

        socket.to(data.target.socketID).emit("onDenyCallTargetPluginReceive", encrypt({
            name: data.target.name,
            uuid: data.target.uuid,
        }))
    })

    socket.on("onPlaySoundPlugin", (data: {
        socketID: string
        sendTOAll: boolean
        soundName: string

    }) => {
        socket.to(data.sendTOAll ? socket.melodyClient.server : data.socketID).emit("onPlaySoundReceive", encrypt({
            sound: data.soundName,
        }))
    })

    socket.on("onPauseSoundPlugin", (data: {
        socketID: string
        sendTOAll: boolean
        soundName: string
    }) => {

        socket.to(data.sendTOAll ? socket.melodyClient.server : data.socketID).emit("onPauseSoundReceive", encrypt({
            sound: data.soundName
        }))
    })

    socket.on("onStopSoundPlugin", (data: {
        socketID: string
        sendTOAll: boolean
        soundName: string

    }) => {
        socket.to(data.sendTOAll ? socket.melodyClient.server : data.socketID).emit("onStopSoundReceive", encrypt({
            sound: data.soundName,
        }))
    })


    socket.on("onRenewData", (data) => {
        const {soundSettings, playerList} = data

        JSON.parse(playerList)?.forEach((player: RenewData) => {
            player.enableVoice.forEach(enableVoice => {
                io.to(enableVoice.socketID).emit("onEnableVoiceReceive", encrypt({
                    uuid: player.uuid,
                    server: player.server
                }))
            })


            player.volume.forEach(volume => {
                io.to(volume.socketID).emit("onSetVolumeReceive", {
                    uuid: player.uuid,
                    distance: volume.distance,
                    settings: JSON.parse(soundSettings),
                    playerLocation: volume.playerLocation,
                    targetLocation: volume.targetLocation,
                    playerDirection: volume.playerDirection,
                    targetDirection: volume.targetDirection
                })
            })

            player.disableVoice.forEach(disableVoice => {

                io.to(disableVoice.socketID).emit("onDisableVoiceReceive", encrypt({
                    uuid: player.uuid,
                }))

            })
        })

    })


    // Web Listeners
    socket.on("onPlayerTalk", token => {
        const data = decrypt(token)
        socket.to("plugin").emit("onPlayerTalkReceive", data)
    })


    socket.on("onOffer", async (token: string) => {
        const data = decrypt(token)
        try {
            const user = await prisma.melodymine.findUnique({
                where: {uuid: data.uuid},
                select: {socketID: true, server: true}
            })
            io.to(user.socketID).emit("onReceiveOffer", encrypt({
                uuid: socket.melodyClient.uuid,
                offer: data.offer,
            }))
        } catch (ex) {
            console.log(ex)
        }
    })

    socket.on("onAnswer", async (token: string) => {
        const data = decrypt(token)
        try {
            const user = await prisma.melodymine.findUnique({
                where: {uuid: data.uuid},
                select: {socketID: true}
            })
            io.to(user.socketID).emit("onReceiveAnswer", encrypt({
                uuid: socket.melodyClient.uuid,
                answer: data.answer,
            }))
        } catch (ex) {
            console.log(ex)
        }
    })

    socket.on("onCandidate", async (token: string) => {
        const data = decrypt(token)
        try {
            const user = await prisma.melodymine.findUnique({
                where: {uuid: data.uuid},
                select: {socketID: true}
            })
            io.to(user.socketID).emit("onReceiveCandidate", encrypt({
                uuid: socket.melodyClient.uuid,
                candidate: data.candidate,
            }))
        } catch (ex) {
            console.log(ex)
        }
    })


    socket.on("onPlayerChangeControl", token => {
        const data = decrypt(token)
        socket.to("web").emit("onPlayerChangeControlReceive", encrypt(data))
        socket.to("plugin").emit("onPlayerChangeControlReceive", data)

    })

    socket.on("onPlayerJoin", async (token: string) => {
        const data = decrypt(token) as IClient
        try {
            const findUser = await prisma.melodymine.findUnique({
                where: {uuid: data.uuid}
            })
            if (findUser) {
                const result = await prisma.melodymine.update({
                    where: {uuid: data.uuid},
                    data: {
                        socketID: socket.id,
                        isActiveVoice: false,
                        webIsOnline: true,
                    }
                })
                socket.to("plugin").emit("onPlayerJoinToWeb", result)
            }
        } catch (ex) {
            console.log(ex)
        }

        try {
            const onlineUsers = await prisma.melodymine.findMany({
                where: {
                    webIsOnline: true,
                    serverIsOnline: true,
                    isActiveVoice: true,
                },
                select: {
                    name: true,
                    uuid: true,
                    server: true,
                    isMute: true
                }
            })
            if (onlineUsers.length > 0) socket.emit("onPlayerJoinReceive", encrypt(onlineUsers))

        } catch (ex) {
            console.log(ex)
        }
    })

    socket.on("onPlayerStartVoice", async (token: string) => {
        const data = decrypt(token) as IClient
        socket.melodyClient.server = data.server
        await socket.join(data.server)
        try {
            const result = await prisma.melodymine.update({
                where: {uuid: data.uuid},
                data: {isActiveVoice: true}
            })
            socket.broadcast.except("plugin").emit("onPlayerStartVoiceReceive", encrypt({
                name: result.name,
                uuid: result.uuid,
                server: result.server,
                isMute: result.isMute
            }))
            socket.to("plugin").emit("onPlayerStartVoiceWeb", {...result, isActiveVoice: true})
        } catch (ex) {
            console.log(ex)
        }
    })


    socket.on("onPlayerEndVoice", async (token: string) => {
        const data = decrypt(token)
        socket.leave(data.server)
        try {
            const result = await prisma.melodymine.update({
                where: {uuid: data.uuid},
                data: {isActiveVoice: false}
            })
            socket.broadcast.except("plugin").emit("onNewPlayerLeave", encrypt(data))
            socket.to("plugin").emit("onPlayerEndVoiceWeb", {...result, isActiveVoice: false})
        } catch (ex) {
            console.log(ex)
        }
    })


    socket.on("disconnect", async () => {
        // console.log(`Client disconnected from: ${socket.melodyClient.from} server: ${socket.melodyClient.server} ${socket.melodyClient.name ? `name: ${socket.melodyClient.name}` : ""}`)
        if (socket.melodyClient.from != "plugin") {
            try {
                const findUser = await prisma.melodymine.findUnique({
                    where: {socketID: socket.id}
                })

                if (findUser) {
                    const result = await prisma.melodymine.update({
                        where: {uuid: findUser.uuid},
                        data: {
                            socketID: null,
                            webIsOnline: false,
                            isActiveVoice: false,
                        },
                    })
                    socket.broadcast.except("plugin").emit("onNewPlayerLeave", encrypt({
                        name: result.name,
                        uuid: result.uuid,
                        server: result.server
                    }))
                    socket.to("plugin").emit("onNewPlayerLeaveWeb", result)
                }
            } catch (ex) {
                console.log(ex)
            }
        } else {
            await prisma.melodymine.updateMany({
                where: {server: socket.melodyClient.server},
                data: {
                    isActiveVoice: false,
                    serverIsOnline: false
                },
            })


            socket.broadcast.except("plugin").emit("onPluginDisabled", encrypt({
                server: socket.melodyClient.server
            }))
        }
    })
})

server.listen(PORT, async () => {
    console.log(`Server listening on port ${PORT}.`)
    await prisma.melodymine.updateMany({
        where: {
            webIsOnline: true
        },
        data: {
            webIsOnline: false
        }
    })
})

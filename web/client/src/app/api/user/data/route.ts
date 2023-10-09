import {NextResponse} from "next/server";
import {getAuthSession} from "@/utils/auth";
import {prisma} from "@/utils/connect";
import CryptoJS from "crypto-js";


export const GET = async () => {

    const session = await getAuthSession()
    if (session) {
        try {
            const player = await prisma.melodymine.findUnique({
                select: {
                    uuid: true,
                    name: true,
                    server: true,
                    isMute: true,
                    isActiveVoice: true,
                    serverIsOnline: true,
                    webIsOnline: true,
                },
                where: {
                    uuid: session.user.uuid
                },
            })
            if (player) {
                const data = {
                    "player": player,
                    "socketURL": process.env.WEBSOCKET_URL,
                }
                CryptoJS.AES.encrypt(JSON.stringify(data), process.env.WEBSOCKET_KEY!!).toString()
                const token = CryptoJS.AES.encrypt(JSON.stringify(data), process.env.WEBSOCKET_KEY!!).toString()
                return new NextResponse(JSON.stringify({"token": token}), {status: 200})
            } else {
                return new NextResponse(JSON.stringify({message: "You must first join the server"}), {status: 200})
            }

        } catch (err) {

        }
    } else {
        return new NextResponse(JSON.stringify({message: "You are not authenticated!"}), {status: 401})
    }
}

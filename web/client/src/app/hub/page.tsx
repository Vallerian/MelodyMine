import {Metadata} from "next";
import SoundControl from "@/components/SoundControl";
import UserInfo from "@/components/UserInfo";
import UserList from "@/components/UserList";
import {getAuthSession} from "@/utils/auth";
import {redirect} from "next/navigation";
import {IUser} from "@/interfaces";
import {prisma} from "@/utils/connect";

const checkMulti = async (name: string) => {
    const player = await prisma.melodymine.findFirst({
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
            name: name
        },
    })
    if (player) {
        return !!player.webIsOnline
    } else {
        return false
    }
}

export const metadata: Metadata = {
    title: "Hub",
}
const Page = async () => {
    const session = await getAuthSession()

    if (!session?.user) redirect('/login')
    const isMulti = await checkMulti(session?.user.name!!)
    if (isMulti) redirect("/?error=multiUser")

    return (
        <>
            <div className="h-screen w-full flex flex-col text-white justify-center items-center">
                <div
                    className="h-screen w-full sm:h-5/6 sm:w-5/6 flex flex-col p-3 items-center bg-[#D3D3D3FF] rounded text-black shadow-inner shadow-[#323232]">
                    {/*HEADER*/}
                    <div className="flex items-center justify-between w-full">
                        <UserInfo
                            user={session.user as IUser}
                            websocketKey={process.env.WEBSOCKET_KEY}
                            iceServers={process.env.ICE_SERVERS}
                        />
                    </div>

                    {/*BODY*/}
                    <div className="w-full h-screen overflow-y-auto overflow-x-hidden disScroll">
                        <UserList/>
                    </div>
                </div>
                <SoundControl/>
            </div>
        </>
    )
}
export default Page

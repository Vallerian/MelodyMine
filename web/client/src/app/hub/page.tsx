import {Metadata} from "next";
import SoundControl from "@/components/SoundControl";
import UserInfo from "@/components/UserInfo";
import UserList from "@/components/UserList";
import {getAuthSession} from "@/utils/auth";
import {redirect} from "next/navigation";
import {IUser} from "@/interfaces/User";

const checkMulti = async () => {
    const res = await fetch("http://localhost:3000/api/user/data", {
        method: "GET",
        headers: {"Content-Type": "application/json"},
    })

    if (!res.ok) return

    const data = await res.json()
    return data.webIsOnline

}

export const metadata: Metadata = {
    title: "Hub",
}
const Page = async () => {
    const session = await getAuthSession()

    const isMulti = await checkMulti()
    if (!session?.user) redirect('/')
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

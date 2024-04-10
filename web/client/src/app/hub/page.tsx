import {Metadata} from "next";
import SoundControl from "@/components/SoundControl";
import UserInfo from "@/components/UserInfo";
import UserList from "@/components/UserList";
import {getAuthSession} from "@/utils/auth";
import {redirect} from "next/navigation";
import {IUser} from "@/interfaces";



export const metadata: Metadata = {
    title: "Hub",
}
const Page = async () => {
    const session = await getAuthSession()
    if (!session?.user) redirect('/login')

    return (
        <>
            <div className="h-screen w-full flex flex-col text-white justify-center items-center">
                <div
                    className="h-screen w-full sm:h-5/6 sm:w-5/6 flex flex-col p-3 items-center bg-[#2c2f3a] rounded-[10px] text-black shadow-inner shadow-[#323232]">
                    {/*HEADER*/}
                    <div className="flex items-center justify-between w-full">
                        <UserInfo
                            user={session.user as IUser}
                            websocketKey={process.env.WEBSOCKET_KEY}
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

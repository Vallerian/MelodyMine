import React from 'react'
import LoginForm from "@/components/LoginForm";
import Image from "next/image";
import {Metadata} from "next";
import {getAuthSession} from "@/utils/auth";
import {redirect} from "next/navigation";
import ContactUs from "@/components/ContactUs";
import Settings from "@/config";


export const metadata: Metadata = {
    title: 'Login',
}
const Page = async () => {
    const showForm = false;
    const session = await getAuthSession()

    if (session?.user) redirect('/hub')

    return (
        <div className="text-white flex justify-center items-center w-full h-screen">
            <div
                className="h-screen w-full sm:h-2/3 sm:w-2/3 flex flex-col sm:flex-row bg-[#D3D3D3FF] rounded text-black shadow-inner shadow-[#323232]"
                style={{display: showForm ? 'block' : 'none'}}
            >

                {/*LEFT SIDE*/}
                <div className="flex-[1] flex sm:flex-[3] md:flex-[2] lg:flex-[3] relative">
                    <Image src="/login.jpg" alt="" fill className="object-cover"/>
                </div>

                {/*RIGHT SIDE*/}
                <div
                    className="flex-[4] sm:flex-[3] md:flex-[2] lg:flex-[2] px-3 lg:px-10 flex flex-col gap-5 justify-center items-center">
                    <h1 className="font-bold text-xl xl:text-3xl">Welcome</h1>
                    <p className="">
                        Log into your account or join the Minecraft Server and Register
                        <span
                            className="text-transparent bg-clip-text bg-gradient-to-r from-[#221854] to-[#F04FE7] rounded ml-1">
                            {Settings.serverIp}
                        </span>
                    </p>
                    <LoginForm/>
                    <ContactUs/>
                </div>

            </div>
            <div className="text-center" style={{display: showForm ? 'none' : 'block'}}>
                <h3 className='text-3xl font-bold pb-8 text-[#e20a3e]'>اول باید وارد شوید!</h3>
                <p className='text-xl' style={{lineHeight: '2.5rem'}}>
                    برای ورود به وویس چت در قدم اول وارد گیم مود مورد نظر بشید،
                    <br />
                    سپس دستور <span dir="ltr" className='text-left text-white mx-2 items-center p-1 bg-[#2c2f3a] rounded-[10px] shadow-inner shadow-[#323232]'>/voice</span>
                    را وارد کنید و سپس روی لینکی که به شما ارسال میشود کلیک کنید.
                </p>
            </div>
        </div>
    )
}
export default Page

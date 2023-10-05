"use client"
import Link from "next/link";
import {useSession} from "next-auth/react";

const HeroButton = () => {

    const {data: session, status} = useSession()

    return (
        <Link href={status === "loading" ? "" : status === "authenticated" ? "/hub" : "/login"}
              className="bg-[#406272] text-white font-[500] w-fit p-[10px] border-0 rounded-[5px]">
            Start Now
        </Link>
    )
}
export default HeroButton

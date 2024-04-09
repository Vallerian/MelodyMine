"use client"
import Link from "next/link";
import {useSession} from "next-auth/react";
import {useRouter, useSearchParams} from "next/navigation";
import {useLayoutEffect} from "react";
import {useValidateStore} from "@/store/ValidateStore";

const HomeStart = () => {
    const {status} = useSession()
    const params = useSearchParams()
    const route = useRouter()
    const {setError, setValidate} = useValidateStore(status => status)

    useLayoutEffect(() => {
        if (params.has("error")) {
            setValidate(false)
            setError(params.get("error")!)
            route.replace("/")
        }
    }, [params])

    return (
        <>
            <Link href={status == "loading" ? "" : status == "authenticated" ? "/hub" : "/login"}
                  className="bg-[#e20a3e] text-white font-[500] w-fit p-[12px] border-0 rounded-[10px] hover:bg-[#9b0d2e] duration-500 animate-pulse">
                شروع وویس چت
            </Link>
        </>
    )
}
export default HomeStart

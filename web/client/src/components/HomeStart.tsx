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
                  className="bg-[#406272] text-white font-[500] w-fit p-[10px] border-0 rounded-[5px]">
                Start Now
            </Link>
        </>
    )
}
export default HomeStart
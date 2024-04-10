"use client"

import {BiError} from "react-icons/bi";
import {AiOutlineCloseCircle} from "react-icons/ai";
import React, {useEffect, useRef, useState} from "react";
import {useValidateStore} from "@/store/ValidateStore";
import ContactUs from "@/components/ContactUs";
import Settings from "@/config";
import errorMessages from "@/errorMessages";

const ErrorBox = () => {
    const {error, errorBox, setErrorBox} = useValidateStore(state => state)
    const box = useRef<HTMLDivElement>(null)
    const [errorMessage, setErrorMessage] = useState("Unknown Error!")

    useEffect(() => {
        document.onclick = event => {
            if (!box.current?.contains(event.target as HTMLElement)) setErrorBox(false)
        }
    }, [])


    useEffect(() => {
        const message = errorMessages.find(msg => msg.errorType.toLowerCase() == error.toLowerCase())?.errorMessage
        if (message) {


            setErrorMessage(message.replace("%serverIp%", Settings.serverIp))
        }
    }, [error])

    if (!errorBox) return


    return (
        <div
            className="w-full h-screen fixed z-20 bg-[#00000080] flex justify-center items-center text-center transition-all">
            <div ref={box}
                 className="mx-2 w-full h-1/3 sm:w-3/4 md:w-3/5 lg:w-3/6 rounded-xl shadow ring-1 ring-neutral-950 overflow-hidden">
                <div className="h-full flex flex-col">
                    <div className="flex items-center w-full h-auto bg-custom text-white text-2xl py-1">
                        <h3 className="w-full text-white flex items-center justify-center ">
                            <span className="ml-1 text-red-600">
                            <BiError/>
                            </span>
                            ارور
                        </h3>
                        <button className="ml-1 text-amber-300" onClick={() => setErrorBox(false)}>
                            <AiOutlineCloseCircle/>
                        </button>
                    </div>
                    <div className="flex flex-col bg-error h-full">
                        <div className="flex flex-col gap-5 justify-center items-center h-full">
                            <p className="sm:w-4/5 md:w-5/6 lg:w-5/6 mx-2 rounded-xl p-3 shadow-inner bg-[#00000020]">
                                {errorMessage}
                            </p>
                        </div>
                        <div>
                            <ContactUs/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default ErrorBox;

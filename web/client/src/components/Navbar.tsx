import React from 'react'
import Image from "next/image";
import Link from "next/link";

const Navbar = () => {
    return (
        <div className="relative sm:fixed flex justify-between items-center w-[inherit] z-10 py-3">
            <div className="flex items-center gap-3">
                <Link href={{
                    href: "https://mc.gameup.ir",
                    slashes: true
                }}>
                    <Image src="/logo.png" alt="melodymine logo" width={60} height={60}
                        className="object-contain pl-3" />
                </Link>
            </div>


            {/* ------------------ <>_<> Don't touch this Lines <>_<> ------------------ */}
            <div className="pr-3">
                <Link href="https://github.com/Vallerian/MelodyMine" target="_blank"
                    className="text-white flex items-center px-3 py-3 bg-[#2c2f3a] rounded-[10px] cursor-pointer shadow-inner shadow-[#323232]">
                    <Image src="/github.png" alt="github logo" width={20} height={20} className="ml-2" />
                    گیتهاب سازنده
                </Link>
            </div>
            {/* ------------------ <>_<> Don't touch this Lines <>_<> ------------------ */}


        </div>
    )
}
export default Navbar

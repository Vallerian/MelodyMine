import React from 'react'
import Image from "next/image";
import Link from "next/link";

const Navbar = () => {
    return (
        <div className="relative sm:fixed flex justify-between items-center w-full z-10 py-3">
            <Link href={{
                href: "/",
                slashes: true
            }}>
                <Image src="/melody-logo.png" alt="melodymine logo" width={60} height={60}
                       className="object-contain pl-3"/>
            </Link>



            {/* ------------------ <>_<> Don't touch this Lines <>_<> ------------------ */}
            <div className="pr-3">
                <Link href="https://github.com/Vallerian/MelodyMine" target="_blank"
                      className="text-[#000000] flex items-center px-2 py-2 bg-[#D3D3D3FF] rounded-lg cursor-pointer shadow-inner shadow-[#323232]">
                    <Image src="/github.png" alt="github logo" width={20} height={20} className="mr-2"/>
                    Github
                </Link>
            </div>
            {/* ------------------ <>_<> Don't touch this Lines <>_<> ------------------ */}


        </div>
    )
}
export default Navbar

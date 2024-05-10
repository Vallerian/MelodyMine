import Image from 'next/image'
import Hero3D from "@/components/Hero3D";
import HomeStart from "@/components/HomeStart";


export default async function Home() {

    return (
        <div className="h-screen flex flex-col lg:flex-row items-center w-full pt-20 lg:pt-0">
            {/*LEFT SIDE*/}
            <div className="flex-[3] flex flex-col justify-center gap-[20px] pl-5 md:pl-20">
                <h1 className="text-[55px] sm:text-[74px] text-white">Melody Mine</h1>
                <div className="flex items-center">
                    <Image src="/line.png" alt="line" width={40} height={5} className="hidden sm:block mr-3"/>
                    <h2 className="text-2xl text-[#DDB216] ">Talk Together in Minecraft Server</h2>
                </div>
                <p className="text-[24px] text-[#D3D3D3FF]">
                    Experience a new dimension of Minecraft camaraderie with MelodyMine.
                </p>
                <HomeStart/>
            </div>

            {/*RIGHT SIDE*/}
            <div className="flex-[3] flex relative">
                <Hero3D/>
                <Image
                    src="/hero.png"
                    alt="" width={390} height={390}
                    className="object-contain top-0 bottom-0 left-0 right-0 m-auto absolute imgAnimated"
                />
            </div>
        </div>
    )
}

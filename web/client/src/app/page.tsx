import Image from 'next/image'
import Hero3D from "@/components/Hero3D";
import HomeStart from "@/components/HomeStart";


export default async function Home() {

    return (
        <div className="h-screen flex flex-col lg:flex-row items-center w-full text-right pt-20 lg:pt-0 pb-0 md:pb-[2rem]">
            {/*LEFT SIDE*/}
            <div className="flex-[3] flex flex-col justify-center gap-[20px] pr-5 md:pr-20">
                <h1 className="text-[55px] sm:text-[74px] text-white font-bold">گیم آپ</h1>
                <div className="flex items-center">
                    <Image src="/line.png" alt="line" width={40} height={5} className="hidden sm:block ml-3"/>
                    <h2 className="text-xl text-[#DDB216]">به راحتی با دوستاتون تو ماینکرفت صحبت کنید :)</h2>
                </div>
                <p className="text-[20px] text-[#D3D3D3FF] text-justify">
                    با وویس چت گیم آپ میتونید یک تجربه جدید از ماینکرفت داشته باشید که قبلا هیچوقت نداشتید! به راحتی روی شروع وویس چت کلیک کنید و وارد دنیای جدیدی از ماینکرفت بشید و با دوستانتون صحبت کنید.
                </p>
                <HomeStart/>
            </div>

            {/*RIGHT SIDE*/}
            <div className="flex-[3] flex relative">
                <Hero3D/>
                <Image
                    src="/hero.png"
                    alt="" width={800} height={800}
                    className="object-contain top-0 bottom-0 left-0 right-0 m-auto absolute imgAnimated"
                />
            </div>
        </div>
    )
}

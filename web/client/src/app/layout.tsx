import './globals.css'
import type {Metadata} from 'next'
import {Inter} from 'next/font/google'
import Navbar from "@/components/Navbar";
import AuthProvider from "@/components/AuthProvider";
import ErrorBox from "@/components/Porgress/ErrorBox";


const inter = Inter({subsets: ['latin']})

export const metadata: Metadata = {
    metadataBase: new URL("https://mc-voice.gameup.ir"),
    robots: {
        index: true,
        follow: true,
        googleBot: {
            index: true,
            follow: true,
            'max-image-preview': 'large',
            'max-snippet': -1,
        },
    },
    title: {
        default: 'GameUP Voice Chat',
        template: 'GameUP | %s'
    },
    themeColor: "#e20a3e",
    authors: [{name: "TAHER7", url: "https://discord.com/users/403446004193558531"}],
    description: 'با وویس چت گیم آپ میتونید یک تجربه جدید از ماینکرفت داشته باشید که قبلا هیچوقت نداشتید',
    keywords: ["GameUP", "Minecraft voice", "Minecraft Voice Chat", "Minecraft Voice Chat Plugin", "Minecraft Voice Plugin", "Minecraft"],
    icons: {
        icon: "/logo.png",
        shortcut: "/logo.png",
    }
}

export default function RootLayout({children}: { children: React.ReactNode }) {
    return (
        <html lang="en">
        <body className={inter.className}>
        <AuthProvider>
            <main
                className="h-screen w-full bg-custom flex-row items-center justify-between overflow-y-auto md:flex-col overflow-x-hidden disScroll">
                <div className="w-[95%] mx-auto py-4">
                    <ErrorBox/>
                    <Navbar/>
                    {children}
                </div>
            </main>
        </AuthProvider>
        </body>
        </html>
    )
}

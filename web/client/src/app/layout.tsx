import './globals.css'
import type {Metadata} from 'next'
import {Inter} from 'next/font/google'
import Navbar from "@/components/Navbar";
import AuthProvider from "@/components/AuthProvider";
import ErrorBox from "@/components/Porgress/ErrorBox";


const inter = Inter({subsets: ['latin']})

export const metadata: Metadata = {
    metadataBase: new URL("https://melodymine.taher7.ir"),
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
        default: 'MelodyMine',
        template: 'MelodyMine | %s'
    },
    authors: [{name: "TAHER7", url: "https://discord.com/users/403446004193558531"}],
    description: 'MelodyMine is a tools for connect minecraft server to web for talk together',
    keywords: ["MelodyMine", "Minecraft voice", "Minecraft Voice Chat", "Minecraft Voice Chat Plugin", "Minecraft Voice Plugin", "Minecraft"],
    icons: {
        icon: "/melody-logo.png",
        shortcut: "/melody-logo.png",
    },
    themeColor: "#1F7265",
    openGraph: {
        type: "website",
        title: "MelodyMine",
        url: new URL("https://melodymine.taher7.ir"),
        siteName: "MelodyMine (MineCraft Voice Plugin)",
        description: "MelodyMine is a revolutionary system for Minecraft servers where players can simultaneously enter a web interface, communicate, voice chat, and make their in-game interactions richer.",
        images: "/melody-logo.png",
        locale: 'en_US',
    },
    twitter: {
        title: "MelodyMine",
        site: "MelodyMine (MineCraft Voice Plugin)",
        description: "MelodyMine is a revolutionary system for Minecraft servers where players can simultaneously enter a web interface, communicate, voice chat, and make their in-game interactions richer.",
        images: "/melody-logo.png",
        creator: "TAHER7",
    }
}

export default function RootLayout({children}: { children: React.ReactNode }) {
    return (
        <html lang="en">
        <body className={inter.className}>
        <AuthProvider>
            <main
                className="h-screen w-full bg-custom flex-row items-center justify-between overflow-y-auto md:flex-col overflow-x-hidden disScroll">
                <ErrorBox/>
                <Navbar/>
                {children}
            </main>
        </AuthProvider>
        </body>
        </html>
    )
}

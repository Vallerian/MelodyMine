import {getServerSession, NextAuthOptions} from "next-auth"
import CredentialsProvider from "next-auth/providers/credentials"
import {PrismaAdapter} from "@next-auth/prisma-adapter"
import {prisma} from "./connect"
import {checkPass, verifyCode} from "@/utils/index";

declare module "next-auth" {
    interface Session {
        user: User & {
            realname: string
            uuid: string
        }
    }

    interface User {
        realname: string
        uuid: string
    }

}

declare module "next-auth/jwt" {
    interface JWT {
        username: string
        realname: string
        uuid: string
    }
}

export const authOptions: NextAuthOptions = {
    secret: process.env.NEXTAUTH_SECRET,
    adapter: PrismaAdapter(prisma),
    pages: {
        signIn: '/login',
        error: '/login'
    },
    session: {strategy: "jwt"},
    providers: [
        CredentialsProvider({
                id: "credentials",
                name: "Credentials",
                credentials: {
                    username: {label: "Username", type: "text", placeholder: "Username"},
                    password: {label: "Password", type: "password", placeholder: "Password"},
                    verifyCode: {label: "verifyCode"},
                },
                async authorize(credentials) {
                    try {
                        if (!credentials?.verifyCode) {
                            if (credentials?.username.length! > 20) throw "username"
                            if (credentials?.password.length! > 50) throw "password"

                            const user = await prisma.authme.findUnique({
                                where: {username: credentials?.username.toLowerCase()!}
                            })

                            if (!user) throw "username"
                            const isPasswordCorrect = checkPass(credentials?.password!, user?.password!)
                            if (!isPasswordCorrect) throw "password"
                            return {
                                name: user.realname,
                            } as any

                        } else {
                            if (credentials?.verifyCode.length != 20) throw "invalidVerifyCode"
                            if (!Number(credentials?.verifyCode)) throw "invalidVerifyCode"
                            const hasUser = await prisma.melodymine.findUnique({
                                where: {verifyCode: credentials?.verifyCode},
                            })
                            if (!hasUser) throw "invalidVerifyCode"
                            await prisma.melodymine.update({
                                where: {uuid: hasUser.uuid!},
                                data: {verifyCode: verifyCode()}
                            })
                            return {
                                name: hasUser.name,
                                uuid: hasUser.uuid
                            } as any
                        }
                    } catch (err) {
                        console.log(err)
                        throw new Error(`Server Error.`)
                    }
                }
            }
        )
    ],
    callbacks: {
        async session({session, user, token}) {
            if (token) {
                session.user.realname = token.realname!
                session.user.uuid = token.uuid!
            }
            return session
        },
        async jwt({token, session, user}) {
            if (user) {
                token.realname = user.realname
                token.uuid = user.uuid
            }
            return token
        },
    },
}
export const getAuthSession = () => getServerSession(authOptions)
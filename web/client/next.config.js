/** @type {import('next').NextConfig} */
const nextConfig = {
    images: {
        remotePatterns: [
            {
                hostname: "mc-heads.net"
            }
        ]
    },
    reactStrictMode: false
}

module.exports = nextConfig

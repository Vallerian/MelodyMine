import CryptoJS from 'crypto-js';

export const encrypt = (data: any) => {
    try {
        const key = process.env.WEBSOCKET_WEB_AUTH_KEY
        return  CryptoJS.AES.encrypt(JSON.stringify(data), key).toString()
    } catch (e) {
        return null
    }
}

export const decrypt = async (token: string) => {
    try {
        const key = process.env.WEBSOCKET_WEB_AUTH_KEY
        const bytes = CryptoJS.AES.decrypt(token, key)
        return await JSON.parse(bytes.toString(CryptoJS.enc.Utf8))
    } catch (ex) {
        return null
    }
}


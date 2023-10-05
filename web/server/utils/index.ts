import CryptoJS from 'crypto-js';

export const encrypt = (data: any) => {
    try {
        const key = process.env.WEBSOCKE_WEB_AUTH_KEY
        CryptoJS.AES.encrypt(JSON.stringify(data), key).toString()
        return CryptoJS.AES.encrypt(JSON.stringify(data), key).toString()
    } catch (e) {
        return null
    }
}

export const decrypt = (token: string) => {
    try {
        const key = process.env.WEBSOCKE_WEB_AUTH_KEY
        const bytes = CryptoJS.AES.decrypt(token, key)
        return JSON.parse(bytes.toString(CryptoJS.enc.Utf8))
    } catch (ex) {
        return null
    }
}


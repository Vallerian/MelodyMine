import * as crypto from 'crypto';
import {randomInt} from 'crypto';
import {useUserStore} from "@/store/UserStore";
import CryptoJS from 'crypto-js';

function hash(algorithm: string, data: string): string {
    const hash = crypto.createHash(algorithm);
    hash.update(data)
    return hash.digest('hex');
}

export const checkPass = (input: string, password: string): boolean => {
    const parts = password.split("$")
    const inputHash = hash("sha256", `${hash("sha256", input)}${parts[2]}`)
    return parts.length === 4 && parts[3] === inputHash

}

export const verifyCode = (): string => {
    const length: number = 20;
    const stringBuilder: string[] = [];
    for (let i = 0; i < length; i++) {
        const digit: number = randomInt(10);
        stringBuilder.push(digit.toString());
    }
    return stringBuilder.join('');
}

export const encrypt = (data: any) => {
    const key = useUserStore.getState().secretKey
    CryptoJS.AES.encrypt(JSON.stringify(data), key).toString()
    return CryptoJS.AES.encrypt(JSON.stringify(data), key).toString()
}

export const decrypt = (token: string) => {
    try {
        const key = useUserStore.getState().secretKey
        const bytes = CryptoJS.AES.decrypt(token, key)
        return JSON.parse(bytes.toString(CryptoJS.enc.Utf8))
    } catch (ex) {
        return {}
    }
}

export const calculateDistance = (x1: number, y1: number, z1: number, x2: number, y2: number, z2: number): number => {
    return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2));
}

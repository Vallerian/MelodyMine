"use client"
import {usePeersStore} from "@/store/PeersStore";
import {useStreamStore} from "@/store/StreamStore";
import {useSocketStore} from "@/store/SocketStore";
import {encrypt} from "@/utils/index";


export const createPeer = (
    uuid: string,
    peer: RTCPeerConnection
) => {
    const stream = useStreamStore.getState().stream
    const socket = useSocketStore.getState().socket

    stream?.getTracks().forEach(track => {
        peer.addTrack(track, stream)
    })

    peer.onicecandidate = async event => {
        if (!event.candidate) return
        socket?.emit("onCandidate", encrypt({
            "uuid": uuid,
            "candidate": event.candidate
        }))
    }

    peer.oniceconnectionstatechange = () => {
        if (peer.iceConnectionState === 'failed' ||
            peer.iceConnectionState === 'disconnected') {
            usePeersStore.getState().removePeer(uuid)
        }
    }

    return peer

}
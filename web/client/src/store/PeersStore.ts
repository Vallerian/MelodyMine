import {createWithEqualityFn} from "zustand/traditional";
import {shallow} from "zustand/shallow";
import {createPeer} from "@/utils/createPeer";
import {useSocketStore} from "@/store/SocketStore";
import {useValidateStore} from "@/store/ValidateStore";
import {IPeer} from "@/interfaces";
import {encrypt} from "@/utils";
import {useUserStore} from "@/store/UserStore";


interface State {
    peers: IPeer[]
}

interface Actions {
    addPeer: (uuid: string, server: string, peer: RTCPeerConnection) => void
    removePeer: (uuid: string, checkAdmin?: boolean) => void
    setIcecandidate: (uuid: string, candidate: RTCIceCandidate) => void
    setDescription: (uuid: string, description: RTCSessionDescription) => void
    createOffer: (uuid: string, server: string) => void
    createAnswer: (uuid: string, server: string, offer: RTCSessionDescription) => void
    removeAll: (checkAdmin?: boolean) => void
}

export const usePeersStore = createWithEqualityFn<State & Actions>((setState, getState) => ({
    peers: [],
    addPeer: async (uuid, server, peer) => {
        const peers = [...getState().peers]
        peers.push({uuid, server, peer})
        setState(() => ({peers}))
    },
    removePeer: (uuid, checkAdmin) => {
        let peers = [...getState().peers]
        const item = peers.find(item => item.uuid == uuid)
        if (item) {
            if (!checkAdmin) {
                item.peer.close()
                peers = peers.filter(item => item.uuid != uuid)
                setState(() => ({peers}))
            } else {
                const server = useUserStore.getState().server
                if (item.server != server) {
                    item.peer.close()
                    peers = peers.filter(item => item.uuid != uuid)
                    setState(() => ({peers}))
                }
            }
        }
    },
    setIcecandidate: async (uuid, icecandidate) => {
        const peers = getState().peers
        const item = peers.find(item => item.uuid == uuid)
        if (item?.peer) {
            try {
                if (item.peer.iceConnectionState !== "closed") {
                    await item.peer.addIceCandidate(icecandidate)
                }
            } catch (ex) {

            }
        }
    },
    setDescription: async (uuid, description) => {
        const peers = [...getState().peers]
        const item = peers.find(item => item.uuid == uuid)
        try {
            await item?.peer.setRemoteDescription(description)
        } catch (ex) {

        }
    },
    removeAll: (checkAdmin) => {
        let peers = [...getState().peers]
        if (!checkAdmin) {
            peers.forEach(peer => {
                peer.peer.close()
            })
            setState(() => ({peers: []}))
        } else {
            const server = useUserStore.getState().server
            peers.forEach(peer => {
                if (peer.server != server) peer.peer.close()
            })
            peers = peers.filter(peer => {
                if (peer.server == server) return peer
            })
            setState(() => ({peers: peers}))
        }
    },
    createOffer: async (uuid, server) => {
        const isValidate = useValidateStore.getState().isValidate
        if (!isValidate) return
        const iceServers = useUserStore.getState().iceServers
        const peer = new RTCPeerConnection({iceServers: JSON.parse(iceServers)})
        getState().addPeer(uuid, server, peer)
        const socket = useSocketStore.getState().socket
        const offerPeer = createPeer(uuid, peer)
        const offer = await offerPeer.createOffer({offerToReceiveAudio: true})
        await peer.setLocalDescription(offer)
        socket?.emit("onOffer", encrypt({
            uuid,
            offer
        }))
    },
    createAnswer: async (uuid, server, offer) => {
        const socket = useSocketStore.getState().socket
        const peers = [...getState().peers]
        let answerPeer: RTCPeerConnection
        const hasPeer = peers.find(item => item.uuid == uuid)
        if (!hasPeer) {
            const iceServers = useUserStore.getState().iceServers
            const peer = new RTCPeerConnection({iceServers: JSON.parse(iceServers)})
            answerPeer = createPeer(uuid, peer)
            getState().addPeer(uuid, server, answerPeer)
        } else {
            answerPeer = hasPeer.peer
        }
        await answerPeer.setRemoteDescription(offer)
        const answer = await answerPeer.createAnswer()
        await answerPeer.setLocalDescription(answer)

        socket?.emit("onAnswer", encrypt({
            uuid: uuid,
            answer
        }))

    }
}), shallow)


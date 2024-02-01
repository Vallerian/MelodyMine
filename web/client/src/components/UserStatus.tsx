import {ImConnection} from "react-icons/im";

interface Props {
    iceState: string
    descriptionType: string
    connectionType: string
}


const UserStatus = ({iceState, descriptionType, connectionType}: Props) => {
    return (
        <>
            <div className="bg-status px-3 text-sm p-1 text-white absolute z-10 rounded-l-xl shadow-xl">
                <ul className="">
                    <li className="flex items-center">
                        <span className="flex gap-2 items-center">
                            <span className={`${iceState == undefined ? "text-gray-500" : "text-green-500"}`}>
                                <ImConnection/>
                            </span>
                            Ice State:
                        </span>
                        <span className="capitalize text-black bg-gray-300 text-center ms-2 px-2 py-0 rounded text-xs">
                            {iceState}
                        </span>
                    </li>
                    <li className="flex items-center">
                        <span className="flex gap-2 items-center">
                            <span
                                className={`${connectionType == "" ? "text-gray-500" : "text-green-500"}`}>
                                <ImConnection/>
                            </span>
                            Connection type:
                        </span>
                        <span className="capitalize text-black bg-gray-300 text-center ms-2 px-2 py-0 rounded text-xs">
                            {connectionType}
                        </span>
                    </li>
                    <li className="flex items-center">
                        <span className="flex gap-2 items-center">
                            <span className={`${descriptionType == undefined ? "text-gray-500" : "text-green-500"}`}>
                                <ImConnection/>
                            </span>
                            Description Type:
                        </span>
                        <span className="capitalize text-black bg-gray-300 text-center ms-2 px-2 py-0 rounded text-xs">
                            {descriptionType}
                        </span>
                    </li>
                </ul>
            </div>
        </>
    )
}
export default UserStatus
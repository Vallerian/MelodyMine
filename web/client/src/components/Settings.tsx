"use client"
import {IoMdSettings} from "react-icons/io";
import React, {ChangeEvent, useEffect, useRef, useState} from "react";
import {AiOutlineCloseCircle} from "react-icons/ai";
import {useControlStore} from "@/store/ControlStore";
import {useValidateStore} from "@/store/ValidateStore";
import {MdOutlineNoiseAware} from "react-icons/md";
import {FaMicrophone} from "react-icons/fa";
import {Checkbox} from "@nextui-org/checkbox";
import {Select, SelectItem} from "@nextui-org/select";

const Settings = () => {
    const {
        sound3D,
        setSound3D,
        noiseSuppression,
        setNoiseSuppression,
        echoCancellation,
        setEchoCancellation,
        audioInput,
        audioOutput,
        mediaDevices,
        setAudioInput,
        setAudioOutput,
        sound3DModel,
        setSound3DModel
    } = useControlStore(state => state)
    const {isValidate} = useValidateStore(state => state)
    const [isOpenBox, setIsOpenBox] = useState<boolean>(false)
    const boxWrapper = useRef<HTMLDivElement>(null)
    const box = useRef<HTMLDivElement>(null)

    useEffect(() => {
        const onClickEvent = (event: MouseEvent) => {
            if (!box.current?.contains(event.target as HTMLElement) && boxWrapper.current?.contains(event.target as HTMLElement)) setIsOpenBox(false)
        }

        window.addEventListener("click", onClickEvent)

        return () => {
            window.removeEventListener("click", onClickEvent)
        }
    }, [isOpenBox])

    const handleResetDefault = () => {
        setSound3D(true)
        setNoiseSuppression(true)
        setEchoCancellation(true)
        setSound3DModel("inverse")

        mediaDevices.forEach(device => {
            if (device.deviceId == "default") {
                if (device.kind == "audioinput") {
                    setAudioInput(device)
                }

                if (device.kind == "audiooutput") {
                    setAudioOutput(device)
                }
            }
        })
    }

    const handleSetDevice = (event: ChangeEvent<HTMLSelectElement>) => {
        const device = mediaDevices.find(device => device.deviceId == event.target.value)
        if (!device) return
        if (device.kind == "audiooutput") {
            setAudioOutput(device)
        }
        if (device.kind == "audioinput") {
            setAudioInput(device)
        }
    }

    const handleSetSound3DModel = (event: ChangeEvent<HTMLSelectElement>) => {
        setSound3DModel(event.target.value as "inverse" | "linear" | "exponential")
    }

    return (
        <div className="items-center hidden sm:flex">
            <button onClick={() => setIsOpenBox(prevState => !prevState)}>
                <IoMdSettings className="text-xl text-slate-500"/>
            </button>
            {isOpenBox ? (
                <div ref={boxWrapper}
                     className="z-20 fixed inset-0 w-full h-screen bg-black bg-opacity-30 flex items-center justify-center backdrop-blur-sm backdrop-saturate-150 transition-all">
                    <div ref={box}
                         className="mx-2 bg-custom shadow-2xl w-full h-auto sm:w-3/4 md:w-3/5 lg:w-3/6 rounded-xl ring-1 ring-neutral-950 overflow-hidden flex flex-col gap-2">
                        <div className="h-full flex flex-col ">
                            <div className="flex items-center w-full h-auto text-white text-2xl py-1 mb-5">
                                <h3 className="w-full text-white flex items-center justify-center ">
                                    <IoMdSettings className="mr-1 text-slate-500"/>
                                    Settings
                                </h3>
                                <button className="mr-1 text-amber-300" onClick={() => setIsOpenBox(false)}>
                                    <AiOutlineCloseCircle/>
                                </button>
                            </div>
                            <div className="flex h-full px-2 w-full my-5 lg:flex-row flex-col gap-5">
                                <ul className="flex flex-col gap-5 h-full w-full">
                                    <li className="flex flex-col gap-5 w-full">
                                        <div className="flex items-center gap-1">
                                            <Checkbox
                                                className="dark text-nowrap"
                                                color="primary"
                                                isSelected={sound3D}
                                                icon={<MdOutlineNoiseAware/>}
                                                onChange={() => setSound3D(!sound3D)}
                                            >
                                                3D Sound
                                            </Checkbox>

                                        </div>
                                    </li>
                                    <li>
                                        <div className="flex flex-col gap-1">
                                            <Checkbox
                                                icon={<MdOutlineNoiseAware/>}
                                                className="dark"
                                                color="primary"
                                                isDisabled={isValidate as boolean}
                                                isSelected={noiseSuppression}
                                                onChange={() => setNoiseSuppression(!noiseSuppression)}
                                            >
                                                Noise Suppression
                                            </Checkbox>
                                            {isValidate ? (
                                                <p className="text-tiny text-foreground-400">
                                                    This field only available on disconnect state.
                                                </p>
                                            ) : ""}
                                        </div>
                                    </li>
                                    <li>
                                        <div className="flex flex-col gap-1">
                                            <Checkbox
                                                icon={<MdOutlineNoiseAware/>}
                                                width={200}
                                                className="dark"
                                                color="primary"
                                                isDisabled={isValidate as boolean}
                                                isSelected={echoCancellation}
                                                onChange={() => setEchoCancellation(!echoCancellation)}
                                            >
                                                Echo Cancellation
                                            </Checkbox>
                                            {isValidate ? (
                                                <p className="text-tiny text-foreground-400">
                                                    This field only available on disconnect state.
                                                </p>
                                            ) : ""}
                                        </div>
                                    </li>
                                </ul>

                                <ul className="flex flex-col gap-5 items-start w-full">
                                    <li className="flex items-center gap-3 w-full">
                                        <Select
                                            size="sm"
                                            label="Select the 3D sound model."
                                            className="max-w-xs dark w-full"
                                            isDisabled={!sound3D}
                                            onChange={handleSetSound3DModel}
                                            selectedKeys={[sound3DModel]}
                                            startContent={<MdOutlineNoiseAware/>}
                                            description={!sound3D ? "This field only available when the 3D sound field is enabled." : ""}

                                        >
                                            <SelectItem className="dark" key="inverse">Inverse</SelectItem>
                                            <SelectItem className="dark" key="linear">Linear</SelectItem>
                                            <SelectItem className="dark" key="exponential">Exponential</SelectItem>
                                        </Select>
                                    </li>

                                    <li className="flex flex-col items-start w-full">
                                        <Select
                                            startContent={<FaMicrophone/>}
                                            isDisabled={isValidate as boolean}
                                            onChange={handleSetDevice}
                                            size="sm"
                                            label="Select Microphone."
                                            className="max-w-xs dark w-full"
                                            selectedKeys={[audioInput?.deviceId!!]}
                                            description={isValidate ? "This field only available on disconnect state." : ""}
                                        >
                                            {mediaDevices.filter(value => value.kind == "audioinput").map((device, index) => (
                                                <SelectItem
                                                    key={device.deviceId}
                                                    value={device.deviceId}
                                                    className="capitalize"
                                                >
                                                    {device.label.split('(')[1].trim().replace(/^[\d-()]+/g, '').replace(")", "")}
                                                </SelectItem>
                                            ))}
                                        </Select>
                                    </li>

                                    {/*<li className="flex flex-col items-start w-full">*/}
                                    {/*    <Select*/}
                                    {/*        startContent={<IoHeadsetSharp/>}*/}
                                    {/*        isDisabled={isValidate as boolean}*/}
                                    {/*        size="sm"*/}
                                    {/*        label="Select the Speaker."*/}
                                    {/*        className="max-w-xs dark w-full"*/}
                                    {/*        onChange={handleSetDevice}*/}
                                    {/*        description={isValidate ? "This field only available on disconnect state." : ""}*/}
                                    {/*        selectedKeys={[audioOutput?.deviceId!!]}*/}
                                    {/*    >*/}
                                    {/*        {mediaDevices.filter(value => value.kind == "audiooutput").map((device) => (*/}
                                    {/*            <SelectItem*/}
                                    {/*                key={device.deviceId}*/}
                                    {/*                value={device.deviceId}*/}
                                    {/*            >*/}
                                    {/*                {device.label}*/}
                                    {/*            </SelectItem>*/}
                                    {/*        ))}*/}
                                    {/*    </Select>*/}
                                    {/*</li>*/}
                                </ul>
                            </div>

                        </div>


                        <div className="px-2 flex w-full justify-end">
                            <button
                                disabled={isValidate as boolean}
                                onClick={handleResetDefault}
                                className={`text-xs text-[#000000] flex items-center px-2 py-2 bg-[#D3D3D3FF] rounded-lg shadow-inner shadow-[#323232] m-3
                                    ${!isValidate ? "transition-all hover:transition-all hover:shadow-md hover:shadow-[#D3D3D3FF]" : ""}`}>
                                Reset Default
                            </button>

                        </div>
                    </div>
                </div>
            ) : ""}
        </div>
    )
}

export default Settings
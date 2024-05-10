"use client"

import {signIn, useSession} from 'next-auth/react';
import * as Yup from 'yup';
import {FormikHelpers, useFormik} from "formik";
import {useRouter, useSearchParams} from "next/navigation";
import {useLayoutEffect, useState} from "react";
import {AiOutlineEye, AiOutlineEyeInvisible} from "react-icons/ai";
import Progress from "@/components/Porgress/Progress";
import {useValidateStore} from "@/store/ValidateStore";
import {PiArrowFatLineRightFill} from "react-icons/pi";

interface submitValues {
    username: string,
    password: string
}

const LoginForm = () => {

    const route = useRouter()
    const {data: session, status} = useSession()
    const [showPassword, setShowPassword] = useState<boolean>(false)
    const params = useSearchParams()
    const {setError, setAutoStart} = useValidateStore(status => status)
    const formik = useFormik({
        initialValues: {username: '', password: ''},
        onSubmit: (values, formikHelpers) => handleSubmit({values: values, helpers: formikHelpers}),
        validationSchema: Yup.object({
            username: Yup.string()
                .max(20)
                .required('Please enter your username'),
            password: Yup.string()
                .max(50)
                .required('Please enter your password'),
        })
    })

    if (status === "authenticated") route.push("/hub")

    useLayoutEffect(() => {
        const doAsync = async () => {
            if (params.has("verifyCode")) {
                if (params.has("start")) {
                    setAutoStart(true)
                }

                const verifyCode = params.get("verifyCode")
                route.replace("/login")
                if (!Number(verifyCode)) setError("invalidVerifyCode")
                if (verifyCode?.length != 20) setError("invalidVerifyCode")
                const res = await signIn('credentials', {
                    redirect: false,
                    verifyCode: verifyCode,
                })
                if (res?.error) {
                    console.log("error=", res?.error)
                    setError(res?.error)
                } else {
                    route.push("/hub")
                }
            }
        }

        doAsync()
    }, [params])

    const handleSubmit = async ({values, helpers}: { values: submitValues, helpers: FormikHelpers<any> }) => {
        const res = await signIn('credentials', {
            redirect: false,
            username: values.username,
            password: values.password,
        })

        if (res?.error) {
            if (res?.error === "username") {
                helpers.setFieldError("username", "Username not found")
            }

            if (res?.error === "password") {
                helpers.setFieldError("password", "Password incorrect")
            }
            helpers.setSubmitting(false)
        } else {
            route.push("/hub")
        }
    }

    return (

        <form className="flex flex-col w-full gap-5 items-center justify-center"
              onSubmit={event => {
                  event.preventDefault()
                  formik.handleSubmit(event)
              }}>
            <div className="flex flex-col items-center w-full">
                <div className="flex items-center w-full btn-gradient rounded">
                    <label className="px-3 text-white">
                        <PiArrowFatLineRightFill/>
                    </label>
                    <input
                        type="text"
                        name="username"
                        placeholder="Username"
                        aria-label="enter your username"
                        aria-required="true"
                        className="px-1 py-2 w-full outline-none rounded-r"
                        onChange={formik.handleChange}
                        onBlur={formik.handleBlur}
                        value={formik.values.username}
                    />
                </div>
                {formik.touched.username && formik.errors.username ? (
                    <div className="text-red-600 text-sm flex justify-start w-full">
                        {formik.errors.username}
                    </div>
                ) : null}
            </div>

            <div className="flex flex-col items-center w-full">
                <div className="flex items-center w-full btn-gradient rounded">
                    <label className="px-3 text-white">
                        <PiArrowFatLineRightFill/>
                    </label>
                    <input
                        type={showPassword ? "text" : "password"}
                        name="password"
                        placeholder="Password"
                        aria-label="enter your password"
                        aria-required="true"
                        className="px-1 py-2 w-full outline-none"
                        onChange={formik.handleChange}
                        onBlur={formik.handleBlur}
                        value={formik.values.password}
                    />

                    <div className="bg-white text-2xl h-full flex items-center justify-center rounded-r px-2 py-2">
                        <span className="cursor-pointer" onClick={() => setShowPassword(prevState => !prevState)}>
                            {!showPassword ? <AiOutlineEyeInvisible/> : <AiOutlineEye/>}
                        </span>
                    </div>

                </div>
                {formik.touched.password && formik.errors.password ? (
                    <div className="text-red-600 text-sm flex justify-start w-full">
                        {formik.errors.password}
                    </div>
                ) : null}
            </div>

            <button
                type="submit"
                className="w-full py-2 rounded text-white btn-gradient shadow-lg"
                disabled={formik.isSubmitting || status === "loading"}
            >
                {formik.isSubmitting || status === "loading" ? <Progress/> : 'Sign In'}
            </button>
        </form>
    )
}
export default LoginForm

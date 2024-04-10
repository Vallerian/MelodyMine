import Link from "next/link";
import React from "react";
import Settings from "@/config";

const ContactUs = () => {
    return (
        <p className="text-sm pb-2">
            مشکلی دارید؟
            <Link className="underline" target="_blank"
                  href={Settings.contactUsLink}>
                با ما در ارتباط باشید
            </Link>
        </p>
    )
}
export default ContactUs

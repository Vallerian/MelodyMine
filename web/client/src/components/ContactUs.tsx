import Link from "next/link";
import React from "react";
import Settings from "@/config";

const ContactUs = () => {
    return (
        <p className="text-sm pb-2">
            Have a problem ?
            <Link className="underline" target="_blank"
                  href={Settings.contactUsLink}>
                Contact us
            </Link>
        </p>
    )
}
export default ContactUs
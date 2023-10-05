import styles from "./progress.module.css"

const Progress = () => {
    return (
        <div className="flex justify-center">
            <span className="invisible relative w-0">
                hidden
            </span>
            <span className={`${styles.loader} absolute`}></span>
        </div>
    )
}
export default Progress


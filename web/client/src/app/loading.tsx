import styles from "./loading.module.css"

const Loading = () => {
    return (
        <div className="w-full h-screen flex justify-center items-center absolute z-30 bg-custom">
            <span className={`${styles.loader}`}></span>
        </div>
    )
}
export default Loading

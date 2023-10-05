import {createWithEqualityFn} from "zustand/traditional";
import {shallow} from "zustand/shallow";

interface State {
    isValidate: Boolean
    isError: Boolean
    errorBox: Boolean
    error: String
}

interface Actions {
    setValidate: (isValidate: Boolean) => void
    setIsError: (isError: Boolean) => void
    setErrorBox: (errorBox: Boolean) => void
    setError: (error: String) => void
}

export const useValidateStore = createWithEqualityFn<State & Actions>((setState) => ({
    isValidate: false,
    isError: false,
    errorBox: false,
    error: "",
    setValidate: isValidate => setState(() => ({isValidate})),
    setIsError: isError => setState(() => ({isError})),
    setErrorBox: errorBox => setState(() => ({errorBox})),
    setError: error => setState(() => ({error, errorBox: true})),
}), shallow)


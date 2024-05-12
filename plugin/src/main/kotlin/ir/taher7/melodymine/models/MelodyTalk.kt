package ir.taher7.melodymine.models

import com.google.gson.annotations.Expose

data class MelodyTalk(
    @Expose val uuid: String,
    @Expose val server: String,
    @Expose val isTalk: Boolean,
)

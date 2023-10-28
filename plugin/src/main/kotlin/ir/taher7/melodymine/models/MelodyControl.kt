package ir.taher7.melodymine.models

import com.google.gson.annotations.Expose
import org.bukkit.entity.Player

data class MelodyControl(
    @Expose(deserialize = false) var player: Player? = null,
    @Expose val uuid: String,
    @Expose val name: String,
    @Expose var type: String,
    @Expose var server: String,
    @Expose var value: Boolean,
)

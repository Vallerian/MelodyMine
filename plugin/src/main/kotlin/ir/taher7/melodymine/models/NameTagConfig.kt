package ir.taher7.melodymine.models

data class NameTagConfig(
    val enable: Boolean,
    val textVisible: Boolean,
    val text: String,
    val position: Position,
    val item: Item,
)

data class Position(
    val x: Double,
    val y: Double,
    val z: Double,
)

data class Item(
    val type: String,
    val customData: Int,
)

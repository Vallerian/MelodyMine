package ir.taher7.melodymine.models

import com.google.gson.annotations.Expose

data class RenewData(
    @Expose val p: MutableList<RenewPlayer>,
    @Expose val c: MutableList<List<Int>>? = null,
    @Expose val d: MutableList<List<Int>>? = null,
    @Expose val v: MutableList<List<Int>>? = null,
)

data class RenewPlayer(
    @Expose val id: Int,
    @Expose val l: List<Int>,
    @Expose val d: List<Double>,
)


data class SoundSettings(
    @Expose val sound3D: Boolean,
    @Expose val lazyHear: Boolean,
    @Expose val maxDistance: Int,
    @Expose val refDistance: Int,
    @Expose val innerAngle: Int,
    @Expose val outerAngle: Int,
    @Expose val outerVolume: Double,
)

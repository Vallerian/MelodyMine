package ir.taher7.melodymine.models

import com.google.gson.annotations.Expose

data class RenewData(
    @Expose val name: String,
    @Expose val uuid: String,
    @Expose val server: String,
    @Expose val enableVoice: MutableList<EnableVoiceTask>,
    @Expose val disableVoice: MutableList<DisableVoiceTask>,
    @Expose val volume: MutableList<VolumeTask>,
)

data class EnableVoiceTask(
    @Expose val socketID: String,
)

data class DisableVoiceTask(
    @Expose val socketID: String,
)

data class Location(
    @Expose val x: Double,
    @Expose val y: Double,
    @Expose val z: Double,
)

data class VolumeTask(
    @Expose val socketID: String,
    @Expose val distance: Double,
    @Expose val playerLocation: Location,
    @Expose val targetLocation: Location,
    @Expose val playerDirection: Location,
    @Expose val targetDirection: Location,
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

package earth.terrarium.tempad.common.registries

import earth.terrarium.tempad.Tempad.tempadId
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.level.Level

object ModTags {
    val LOCATION_SAVING_NOT_SUPPORTED: TagKey<Level> = TagKey.create(Registries.DIMENSION, "location_saving_not_supported".tempadId)
    val LEAVING_NOT_SUPPORTED: TagKey<Level> = TagKey.create(Registries.DIMENSION, "leaving_not_supported".tempadId)
}
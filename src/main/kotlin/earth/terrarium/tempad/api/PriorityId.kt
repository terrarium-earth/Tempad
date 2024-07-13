package earth.terrarium.tempad.api

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import net.minecraft.resources.ResourceLocation

class PriorityId(val id: ResourceLocation, private val priority: Int): Comparable<PriorityId> {
    override fun compareTo(other: PriorityId): Int {
        return priority - other.priority
    }

    companion object {
        val byteCodec = ObjectByteCodec.create(
            ExtraByteCodecs.RESOURCE_LOCATION.fieldOf(PriorityId::id),
            ByteCodec.INT.fieldOf(PriorityId::priority),
            ::PriorityId
        )
    }
}
package earth.terrarium.tempad.api

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import net.minecraft.resources.ResourceLocation

data class PriorityId(val id: ResourceLocation, private val priority: Priority): Comparable<PriorityId> {
    override fun compareTo(other: PriorityId): Int {
        return priority.priority.compareTo(other.priority.priority)
    }
}

enum class Priority(val priority: Int) {
    LOWEST(0),
    LOW(1),
    NORMAL(2),
    HIGH(3),
    HIGHEST(4)
}
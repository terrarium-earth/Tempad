package earth.terrarium.tempad.common.utils

import com.mojang.serialization.Codec
import net.minecraft.core.component.DataComponentType
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.world.entity.Entity
import net.neoforged.neoforge.attachment.AttachmentHolder
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.common.MutableDataComponentHolder
import java.util.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T> attachmentType(supplier: () -> T, builder: AttachmentType.Builder<T>.() -> Unit): AttachmentType<T> {
    return AttachmentType.builder(supplier).apply(builder).build()
}

var <T> AttachmentType.Builder<T>.codec: Codec<T>
    get() = error("No getter for codec")
    set(value) {
        this.serialize(value)
    }

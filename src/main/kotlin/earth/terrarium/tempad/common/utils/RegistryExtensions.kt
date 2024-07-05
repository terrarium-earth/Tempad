package earth.terrarium.tempad.common.utils

import com.mojang.serialization.Codec
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.StreamCodecByteCodec
import net.minecraft.core.component.DataComponentType
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.EntityCapability
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider
import net.neoforged.neoforge.capabilities.ICapabilityProvider
import net.neoforged.neoforge.capabilities.ItemCapability
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent

fun <T> attachmentType(supplier: () -> T, builder: AttachmentType.Builder<T>.() -> Unit): AttachmentType<T> {
    return AttachmentType.builder(supplier).apply(builder).build()
}

var <T> AttachmentType.Builder<T>.codec: Codec<T>
    get() = error("No getter for codec")
    set(value) {
        this.serialize(value)
    }

fun <T> componentType(builder: DataComponentType.Builder<T>.() -> Unit): DataComponentType<T> {
    return DataComponentType.builder<T>().apply(builder).build()
}

var <T> DataComponentType.Builder<T>.serialize: Codec<T>
    get() = error("No getter for codec")
    set(value) {
        this.persistent(value)
    }

var <T> DataComponentType.Builder<T>.networkSerialize: ByteCodec<T>
    get() = error("No getter for synced")
    set(value) {
        this.networkSynchronized(StreamCodecByteCodec.toRegistry(value))
    }
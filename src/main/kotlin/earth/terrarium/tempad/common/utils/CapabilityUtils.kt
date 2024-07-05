package earth.terrarium.tempad.common.utils

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.neoforge.capabilities.*

class BlockCapabilityHelper<T, C>(val event: RegisterCapabilitiesEvent, val capability: BlockCapability<T, C>) {
    operator fun set(vararg blocks: Block, provider: IBlockCapabilityProvider<T, C>) {
        event.registerBlock(capability, provider, *blocks)
    }

    operator fun set(vararg blocks: BlockEntityType<*>, provider: ICapabilityProvider<BlockEntity, C, T>) {
        blocks.forEach { event.registerBlockEntity(capability, it, provider) }
    }
}

fun <T, C> RegisterCapabilitiesEvent.register(capability: BlockCapability<T, C>) = BlockCapabilityHelper(this, capability)

class ItemCapabilityHelper<T, C>(val event: RegisterCapabilitiesEvent, val capability: ItemCapability<T, C>) {
    operator fun set(vararg items: Item, provider: ICapabilityProvider<ItemStack, C, T>) {
        items.forEach { event.registerItem(capability, provider, it) }
    }
}

fun <T, C> RegisterCapabilitiesEvent.register(capability: ItemCapability<T, C>) = ItemCapabilityHelper(this, capability)

class EntityCapabilityHelper<T, C>(val event: RegisterCapabilitiesEvent, val capability: EntityCapability<T, C>) {
    operator fun set(vararg entities: EntityType<*>, provider: ICapabilityProvider<Entity, C, T>) {
        entities.forEach { event.registerEntity(capability, it, provider) }
    }
}

fun <T, C> RegisterCapabilitiesEvent.register(capability: EntityCapability<T, C>) = EntityCapabilityHelper(this, capability)

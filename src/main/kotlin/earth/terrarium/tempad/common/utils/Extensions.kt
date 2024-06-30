package earth.terrarium.tempad.common.utils

import com.teamresourceful.resourcefulconfig.api.types.entries.Observable
import com.teamresourceful.resourcefullib.common.network.Network
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.menu.AbstractTempadMenu
import earth.terrarium.tempad.common.registries.ModNetworking
import net.minecraft.client.gui.components.WidgetSprites
import net.minecraft.core.Holder
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EntityType.EntityFactory
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.level.EntityGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.capabilities.ItemCapability
import kotlin.reflect.KProperty

operator fun TagKey<EntityType<*>>.contains(entity: EntityType<*>): Boolean = entity.`is`(this)

operator fun TagKey<EntityType<*>>.contains(entity: Entity): Boolean = entity.type.`is`(this)

operator fun TagKey<Item>.contains(item: Item): Boolean = item.builtInRegistryHolder().`is`(this)

operator fun TagKey<Item>.contains(item: ItemStack): Boolean = item.`is`(this)

operator fun TagKey<Block>.contains(block: Block): Boolean = block.builtInRegistryHolder().`is`(this)

operator fun TagKey<Block>.contains(state: BlockState): Boolean = state.`is`(this)

operator fun TagKey<Fluid>.contains(fluid: Fluid): Boolean = fluid.builtInRegistryHolder().`is`(this)

operator fun <T> TagKey<T>.contains(key: Holder.Reference<T>): Boolean = key.`is`(this)

operator fun MinecraftServer?.get(dimId: ResourceKey<Level>): ServerLevel? = this?.getLevel(dimId)

inline fun <reified T: Entity> EntityGetter.getEntities(area: AABB, noinline predicate: (T) -> Boolean): List<T> {
    return this.getEntitiesOfClass(T::class.java, area, predicate)
}

fun <T: Entity> ResourcefulRegistry<EntityType<*>>.register(id: String, builder: EntityType.Builder<T>): RegistryEntry<EntityType<T>> = this.register(id) { builder.build(id) }

fun <T: Entity> entityType(factory: EntityFactory<T>, category: MobCategory, builder: EntityType.Builder<T>.() -> Unit): EntityType.Builder<T> {
    return EntityType.Builder.of(factory, category).apply(builder)
}

operator fun Inventory.get(slot: Int): ItemStack = this.getItem(slot)

operator fun Inventory.set(slot: Int, stack: ItemStack) = this.setItem(slot, stack)

var Entity.pos: Vec3
    get() = this.position()
    set(value) = this.setPos(value)

fun ResourceLocation.appSprites(): WidgetSprites = WidgetSprites(
    ResourceLocation.fromNamespaceAndPath(this.namespace, "textures/gui/app/${this.path}/normal"),
    ResourceLocation.fromNamespaceAndPath(this.namespace, "textures/gui/app/${this.path}/disabled"),
    ResourceLocation.fromNamespaceAndPath(this.namespace, "textures/gui/app/${this.path}/hover"),
)

fun String.btnSprites(): WidgetSprites = WidgetSprites(
    ResourceLocation.fromNamespaceAndPath(Tempad.MOD_ID, "textures/gui/button/${this}/normal"),
    ResourceLocation.fromNamespaceAndPath(Tempad.MOD_ID, "textures/gui/button/${this}/disabled"),
    ResourceLocation.fromNamespaceAndPath(Tempad.MOD_ID, "textures/gui/button/${this}/hover"),
)

fun ResourceLocation.appTitle(): Component = Component.translatable(this.toLanguageKey("app"))

fun String.toLanguageKey(type: String): Component = Component.translatable("${type}.${Tempad.MOD_ID}.${this}")

fun <T: Packet<T>> T.sendToServer() = ModNetworking.CHANNEL.sendToServer(this)

fun <T: Packet<T>> T.sendToClient(player: Player) = ModNetworking.CHANNEL.sendToPlayer(this, player)

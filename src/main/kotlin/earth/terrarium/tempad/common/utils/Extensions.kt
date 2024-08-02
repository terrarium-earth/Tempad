package earth.terrarium.tempad.common.utils

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.ObjectEntryByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.StreamCodecByteCodec
import com.teamresourceful.resourcefullib.common.color.Color
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.context.InventoryContext
import earth.terrarium.tempad.common.registries.ModNetworking
import net.minecraft.client.gui.components.WidgetSprites
import net.minecraft.core.GlobalPos
import net.minecraft.core.Holder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.TagKey
import net.minecraft.world.Container
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EntityType.EntityFactory
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.EntityGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.bus.api.Event
import net.neoforged.neoforge.capabilities.ItemCapability
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.SimpleFluidContent
import java.util.*
import java.util.function.Function

operator fun TagKey<EntityType<*>>.contains(entity: EntityType<*>): Boolean = entity.`is`(this)

operator fun TagKey<EntityType<*>>.contains(entity: Entity): Boolean = entity.type.`is`(this)

operator fun TagKey<Item>.contains(item: Item): Boolean = item.builtInRegistryHolder().`is`(this)

operator fun TagKey<Item>.contains(item: ItemStack): Boolean = item.`is`(this)

operator fun TagKey<Block>.contains(block: Block): Boolean = block.builtInRegistryHolder().`is`(this)

operator fun TagKey<Block>.contains(state: BlockState): Boolean = state.`is`(this)

operator fun TagKey<Fluid>.contains(fluid: Fluid): Boolean = fluid.builtInRegistryHolder().`is`(this)

operator fun TagKey<Fluid>.contains(fluid: FluidStack): Boolean = fluid.fluid.`is`(this)

operator fun <T> TagKey<T>.contains(key: Holder.Reference<T>): Boolean = key.`is`(this)

operator fun MinecraftServer?.get(dimId: ResourceKey<Level>): ServerLevel? = this?.getLevel(dimId)

operator fun <T> ItemStack.get(capability: ItemCapability<T, Void>) = this.getCapability(capability)

operator fun ItemStack.minus(amount: Int): ItemStack {
    val copy = this.copy()
    copy.shrink(amount)
    return if (copy.isEmpty) ItemStack.EMPTY else copy
}

operator fun ItemStack.plus(amount: Int): ItemStack {
    val copy = this.copy()
    copy.grow(amount)
    return copy
}

operator fun FluidStack.minus(amount: Int): FluidStack {
    val copy = this.copy()
    copy.amount -= amount
    return if (copy.isEmpty) FluidStack.EMPTY else copy
}

operator fun FluidStack.plus(amount: Int): FluidStack {
    val copy = this.copy()
    copy.amount += amount
    return copy
}

operator fun SimpleFluidContent.minus(amount: Int): SimpleFluidContent {
    val copy = this.copy()
    copy.amount -= amount
    return if (copy.isEmpty) SimpleFluidContent.EMPTY else SimpleFluidContent.copyOf(copy)
}

operator fun SimpleFluidContent.plus(amount: Int): SimpleFluidContent {
    val copy = this.copy()
    copy.amount += amount
    return SimpleFluidContent.copyOf(copy)
}

inline fun <reified T: Entity> EntityGetter.getEntities(area: AABB, noinline predicate: (T) -> Boolean): List<T> {
    return this.getEntitiesOfClass(T::class.java, area, predicate)
}

fun <T: Entity> ResourcefulRegistry<EntityType<*>>.register(id: String, builder: EntityType.Builder<T>): RegistryEntry<EntityType<T>> = this.register(id) { builder.build(id) }

fun <T: Entity> entityType(factory: EntityFactory<T>, category: MobCategory, builder: EntityType.Builder<T>.() -> Unit): EntityType.Builder<T> {
    return EntityType.Builder.of(factory, category).apply(builder)
}

operator fun Container.get(slot: Int): ItemStack = this.getItem(slot)

operator fun Container.set(slot: Int, stack: ItemStack) = this.setItem(slot, stack)

var Entity.pos: Vec3
    get() = this.position()
    set(value) = this.setPos(value)

val Entity.globalPos: GlobalPos get() = GlobalPos(this.level().dimension(), this.blockPosition())

fun ResourceLocation.appSprites(): WidgetSprites = WidgetSprites(
    ResourceLocation.fromNamespaceAndPath(this.namespace, "app/${this.path}/normal"),
    ResourceLocation.fromNamespaceAndPath(this.namespace, "app/${this.path}/disabled"),
    ResourceLocation.fromNamespaceAndPath(this.namespace, "app/${this.path}/hover"),
)

fun String.btnSprites(): WidgetSprites = sprites("button")

fun String.sprites(type: String) = WidgetSprites(
    ResourceLocation.fromNamespaceAndPath(Tempad.MOD_ID, "${type}/${this}/normal"),
    ResourceLocation.fromNamespaceAndPath(Tempad.MOD_ID, "${type}/${this}/disabled"),
    ResourceLocation.fromNamespaceAndPath(Tempad.MOD_ID, "${type}/${this}/hover"),
)

fun ResourceLocation.appTitle(): Component = Component.translatable(this.toLanguageKey("app"))

fun String.toLanguageKey(type: String): Component = Component.translatable("${type}.${Tempad.MOD_ID}.${this}")

fun <T: Packet<T>> T.sendToServer() = ModNetworking.channel.sendToServer(this)

fun <T: Packet<T>> T.sendToClient(player: Player) = ModNetworking.channel.sendToPlayer(this, player)

fun InteractionHand.getSlot(player: Player): Int = if (this == InteractionHand.MAIN_HAND) player.inventory.selected else 40

fun Color.darken(factor: Float): Color = Color((this.intRed * factor).toInt(),
    (this.intGreen * factor).toInt(), (this.intBlue * factor).toInt(), this.intAlpha)

infix fun <K, V> Codec<K>.to(valueC: Codec<V>): Codec<Map<K, V>> = Codec.unboundedMap(this, valueC)

infix fun <K, V> ByteCodec<K>.to(valueC: ByteCodec<V>): ByteCodec<Map<K, V>> = ByteCodec.mapOf(this, valueC)

fun <O, T: Any> MapCodec<Optional<T>>.nullableGetter(getter: Function<O, T?>): RecordCodecBuilder<O, Optional<T>> {
    return RecordCodecBuilder.of({ Optional.ofNullable(getter.apply(it)) }, this)
}

fun <O, T: Any> ByteCodec<T>.nullableFieldOf(getter: Function<O, T?>): ObjectEntryByteCodec<O, Optional<T>> {
    return ObjectEntryByteCodec(this.optionalOf()) { Optional.ofNullable(getter.apply(it)) }
}

fun Player.ctx(slot: Int) = InventoryContext(this, slot)

fun <T: Event> T.post(): T = NeoForge.EVENT_BUS.post(this)

fun Entity.teleportTo(pos: Vec3) = this.teleportTo(pos.x, pos.y, pos.z)

val String.vanillaId: ResourceLocation
    get() = ResourceLocation.withDefaultNamespace(this)

val <T> StreamCodec<RegistryFriendlyByteBuf, T>.byteCodec: ByteCodec<T> get() = StreamCodecByteCodec.ofRegistry(this)

val GlobalPos.dimDisplay: Component get() = Component.translatable(this.dimension.location().toLanguageKey("dimension"))
val GlobalPos.xDisplay: Component get() = Component.translatable("gui.${Tempad.MOD_ID}.x", this.pos().x)
val GlobalPos.yDisplay: Component get() = Component.translatable("gui.${Tempad.MOD_ID}.y", this.pos().y)
val GlobalPos.zDisplay: Component get() = Component.translatable("gui.${Tempad.MOD_ID}.z", this.pos().z)

package earth.terrarium.tempad.api.locations

import com.mojang.authlib.GameProfile
import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.tva_device.ChrononHandler
import earth.terrarium.tempad.api.tva_device.UpgradeHandler
import earth.terrarium.tempad.common.utils.GAME_PROFILE_BYTE_CODEC
import net.minecraft.ChatFormatting
import net.minecraft.core.UUIDUtil
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.inventory.tooltip.TooltipComponent
import net.minecraft.world.item.Item
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipProvider
import java.util.*
import java.util.function.Consumer

sealed interface LocationGetter {
    fun get(upgrades: UpgradeHandler, energy: ChrononHandler): NamedGlobalVec3?

    companion object {
        val codec: Codec<LocationGetter> = Codec.either(DirectLocation.codec, IndirectLocation.codec).xmap(
            { either -> either.map({it as LocationGetter}, {it as LocationGetter}) },
            { uncasted ->
                if (uncasted is DirectLocation) {
                    return@xmap Either.left(uncasted)
                } else if (uncasted is IndirectLocation) {
                    return@xmap Either.right(uncasted)
                } else {
                    throw Error("Unexpected, expected DirectLocation or IndirectLocation")
                }
            }
        )

        val byteCodec: ByteCodec<LocationGetter> = ByteCodec.either(DirectLocation.byteCodec, IndirectLocation.byteCodec).map(
            { either -> either.map({it as LocationGetter}, {it as LocationGetter}) },
            { uncasted ->
                if (uncasted is DirectLocation) {
                    return@map com.teamresourceful.bytecodecs.utils.Either.ofLeft(uncasted)
                } else if (uncasted is IndirectLocation) {
                    return@map com.teamresourceful.bytecodecs.utils.Either.ofRight(uncasted)
                } else {
                    throw Error("Unexpected, expected DirectLocation or IndirectLocation")
                }
            }
        )
    }
}

data class DirectLocation(val location: NamedGlobalVec3) : LocationGetter, TooltipProvider {
    companion object {
        val codec: Codec<DirectLocation> = NamedGlobalVec3.CODEC.xmap(::DirectLocation) { it.location }.codec()

        val byteCodec: ByteCodec<DirectLocation> = NamedGlobalVec3.BYTE_CODEC.map(::DirectLocation) { it.location }
    }

    override fun get(upgrades: UpgradeHandler, energy: ChrononHandler): NamedGlobalVec3 = location

    override fun addToTooltip(
        context: Item.TooltipContext,
        tooltipAdder: Consumer<Component?>,
        tooltipFlag: TooltipFlag,
    ) {
        tooltipAdder.accept(MutableComponent.create(location.name.contents).withColor(Tempad.ORANGE.value))
        tooltipAdder.accept(location.dimensionText.withStyle(ChatFormatting.GRAY))
        tooltipAdder.accept(Component.literal("X: ${location.x}").withStyle(ChatFormatting.DARK_GRAY))
        tooltipAdder.accept(Component.literal("Y: ${location.y}").withStyle(ChatFormatting.DARK_GRAY))
        tooltipAdder.accept(Component.literal("Z: ${location.z}").withStyle(ChatFormatting.DARK_GRAY))
    }
}

data class IndirectLocation(val accessor: GameProfile, val info: Component, val provider: ResourceLocation, val id: UUID) : LocationGetter, TooltipProvider {
    companion object {
        val codec: Codec<IndirectLocation> = RecordCodecBuilder.create { it.group(
            ExtraCodecs.GAME_PROFILE.fieldOf("accessor").forGetter(IndirectLocation::accessor),
            ComponentSerialization.CODEC.fieldOf("info").forGetter(IndirectLocation::info),
            ResourceLocation.CODEC.fieldOf("provider").forGetter(IndirectLocation::provider),
            UUIDUtil.CODEC.fieldOf("id").forGetter(IndirectLocation::id),
        ).apply(it, ::IndirectLocation) }

        val byteCodec: ByteCodec<IndirectLocation> = ObjectByteCodec.create(
            GAME_PROFILE_BYTE_CODEC.fieldOf { it.accessor },
            ExtraByteCodecs.COMPONENT.fieldOf { it.info },
            ExtraByteCodecs.RESOURCE_LOCATION.fieldOf { it.provider },
            ByteCodec.UUID.fieldOf { it.id },
            ::IndirectLocation,
        )
    }

    override fun get(upgrades: UpgradeHandler, energy: ChrononHandler): NamedGlobalVec3? {
        return TempadLocations[provider]?.invoke(accessor, upgrades, energy)?.get(id)
    }

    override fun addToTooltip(
        context: Item.TooltipContext,
        tooltipAdder: Consumer<Component?>,
        tooltipFlag: TooltipFlag,
    ) {
        tooltipAdder.accept(info)
        tooltipAdder.accept(Component.translatable("item.tempad.location_card.created_by", Component.literal(accessor.name).withStyle(
            ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY))
        if (!tooltipFlag.hasShiftDown()){
            tooltipAdder.accept(Component.translatable("misc.tempad.shift_key_info", Component.translatable("key.keyboard.left.shift").withStyle(
                ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY))
        } else{
            tooltipAdder.accept(Component.translatable("item.tempad.location_card.id", id.toString()).withStyle(ChatFormatting.DARK_GRAY))
        }
    }
}

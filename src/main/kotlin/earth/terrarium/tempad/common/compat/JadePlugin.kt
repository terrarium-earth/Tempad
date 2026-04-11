package earth.terrarium.tempad.common.compat

import earth.terrarium.olympus.client.constants.MinecraftColors
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.locations.DirectLocation
import earth.terrarium.tempad.api.locations.IndirectLocation
import earth.terrarium.tempad.api.tva_device.ChrononHandler
import earth.terrarium.tempad.api.tva_device.chronons
import earth.terrarium.tempad.client.TempadUI
import earth.terrarium.tempad.common.block.MetronomeBe
import earth.terrarium.tempad.common.block.MetronomeBlock
import earth.terrarium.tempad.common.block.RudimentaryTempadBE
import earth.terrarium.tempad.common.block.RudimentaryTempadBlock
import earth.terrarium.tempad.common.block.WorkstationBE
import earth.terrarium.tempad.common.block.WorkstationBlock
import earth.terrarium.tempad.common.block.timedoor_marker.AbstractMarkerBe
import earth.terrarium.tempad.common.block.timedoor_marker.AbstractMarkerBlock
import earth.terrarium.tempad.common.entity.TimedoorEntity
import earth.terrarium.tempad.common.registries.id
import earth.terrarium.tempad.common.registries.owner
import earth.terrarium.tempad.common.utils.get
import earth.terrarium.tempad.tempadId
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.Block
import net.minecraft.world.phys.Vec2
import snownee.jade.api.*
import snownee.jade.api.config.IPluginConfig
import snownee.jade.api.ui.BoxStyle.GradientBorder
import snownee.jade.api.ui.Element
import snownee.jade.impl.ui.ProgressElement
import snownee.jade.impl.ui.SimpleProgressStyle
import kotlin.math.roundToInt

@WailaPlugin
class JadePlugin: IWailaPlugin {
    override fun registerClient(registration: IWailaClientRegistration) {
        registration.registerEntityComponent(TimedoorComponentProvider, TimedoorEntity::class.java)
        registration.registerBlockComponent(ChrononComponentProvider, Block::class.java)
        registration.registerBlockComponent(AnchorComponentProvider, AbstractMarkerBlock::class.java)
        registration.registerBlockComponent(RudimentaryTempadComponentProvider, RudimentaryTempadBlock::class.java)
        registration.registerBlockComponent(MetronomeComponentProvider, MetronomeBlock::class.java)
        registration.registerBlockComponent(WorkstationComponentProvider, WorkstationBlock::class.java)
    }
}

object TimedoorComponentProvider: IEntityComponentProvider {
    override fun getUid(): Identifier = "timedoor".tempadId

    override fun appendTooltip(tooltip: ITooltip, accessor: EntityAccessor, config: IPluginConfig) {
        val timedoorEntity = accessor.entity as? TimedoorEntity ?: return
        if (timedoorEntity.closingTime - TimedoorEntity.ANIMATION_LENGTH > 0) {
            tooltip.add(Component.translatable("jade.tempad.will_close",
                ((timedoorEntity.closingTime) / 20f).roundToInt()
            ))
        } else if (timedoorEntity.closingTime != -1) {
            tooltip.add(Component.translatable("jade.tempad.closing"))
        }
        tooltip.add(Component.translatable("jade.tempad.pos", Component.translatable(timedoorEntity.targetDimension.location().toLanguageKey("dimension")), "${timedoorEntity.targetPos.x().toInt()}, ${timedoorEntity.targetPos.y().toInt()}, ${timedoorEntity.targetPos.z().toInt()}"))
    }
}

object AnchorComponentProvider: IBlockComponentProvider {
    val id = "anchor".tempadId
    override fun getUid(): Identifier = id

    override fun appendTooltip(tooltip: ITooltip, accessor: BlockAccessor, config: IPluginConfig) {
        (accessor.blockEntity as? AbstractMarkerBe)?.let {
            it.owner?.let {
                tooltip.add(Component.translatable("item.tempad.location_card.created_by", Component.literal(it.name).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY))
            }
            it.id?.let {
                if (accessor.player.isShiftKeyDown) {
                    tooltip.add(Component.translatable("item.tempad.location_card.id", it.toString()).withStyle(ChatFormatting.DARK_GRAY))
                } else {
                    tooltip.add(Component.translatable("misc.tempad.shift_key_info", Component.keybind(Minecraft.getInstance().options.keyShift.name).withStyle(
                        ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY))
                }
            }
        }
    }
}

object RudimentaryTempadComponentProvider: IBlockComponentProvider {
    val id = "rudimentary".tempadId

    override fun getUid(): Identifier = id

    override fun appendTooltip(tooltip: ITooltip, accessor: BlockAccessor, config: IPluginConfig) {
        (accessor.blockEntity as? RudimentaryTempadBE)?.let {
            val pos = it.portalTarget
            if(pos is DirectLocation) {
                tooltip.add(MutableComponent.create(pos.location.name.contents).withColor(Tempad.ORANGE.value))
                tooltip.add(pos.location.dimensionText.withStyle(ChatFormatting.GRAY))
                tooltip.add(Component.literal("X: ${pos.location.x}").withStyle(ChatFormatting.DARK_GRAY))
                tooltip.add(Component.literal("Y: ${pos.location.y}").withStyle(ChatFormatting.DARK_GRAY))
                tooltip.add(Component.literal("Z: ${pos.location.z}").withStyle(ChatFormatting.DARK_GRAY))
            } else if (pos is IndirectLocation) {
                val (player, info, _, id) = pos
                tooltip.add(info)
                tooltip.add(Component.translatable("item.tempad.location_card.created_by", Component.literal(player.name).withStyle(
                    ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY))
                if (!accessor.player.isShiftKeyDown){
                    tooltip.add(Component.translatable("misc.tempad.shift_key_info", Component.keybind(Minecraft.getInstance().options.keyShift.name).withStyle(
                        ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY))
                } else{
                    tooltip.add(Component.translatable("item.tempad.location_card.id", id.toString()).withStyle(ChatFormatting.DARK_GRAY))
                }
            }
        }
    }
}

object MetronomeComponentProvider: IBlockComponentProvider {
    val id = "metronome".tempadId

    override fun getUid(): Identifier = id

    override fun appendTooltip(tooltip: ITooltip, accessor: BlockAccessor, config: IPluginConfig) {
        (accessor.blockEntity as? MetronomeBe)?.let { blockEntity ->
            if (blockEntity.bootTime > 0) {
                val progress = 1 - (blockEntity.bootTime / 100f)
                tooltip.add(
                    ProgressElement(
                        progress,
                        Component.translatable("block.tempad.metronome.booting", (progress * 100).toInt()).append("%"),
                        SimpleProgressStyle().textColor(Tempad.ORANGE.value),
                        GradientBorder.DEFAULT_NESTED_BOX,
                        true
                    )
                )
            }
            blockEntity.owner?.let {
                tooltip.add(Component.translatable("item.tempad.location_card.created_by", Component.literal(it.name).withStyle(
                    ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY))
            }
        }
    }
}

object WorkstationComponentProvider: IBlockComponentProvider {
    val id = "workstation".tempadId
    override fun getUid(): Identifier = id

    override fun appendTooltip(tooltip: ITooltip, accessor: BlockAccessor, config: IPluginConfig) {
        (accessor.blockEntity as? WorkstationBE)?.let { blockEntity ->
            if (blockEntity.maxDownloadTime > 0) {
                val progress = 1f - (blockEntity.downloadTime / blockEntity.maxDownloadTime.toFloat())
                tooltip.add(
                    ProgressElement(
                        progress,
                        Component.translatable("block.tempad.workstation.installing", (progress * 100).toInt())
                            .append("%"),
                        SimpleProgressStyle().textColor(MinecraftColors.DARK_GRAY.value),
                        GradientBorder.DEFAULT_NESTED_BOX,
                        true
                    )
                )
            }
            blockEntity.inventory[0].owner?.let {
                tooltip.add(Component.translatable("item.tempad.location_card.created_by", Component.literal(it.name).withStyle(
                    ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY))
            }
        }
    }
}


object ChrononComponentProvider: IBlockComponentProvider {
    val id = "chronon".tempadId
    override fun getUid(): Identifier = id

    override fun appendTooltip(tooltip: ITooltip, accessor: BlockAccessor, config: IPluginConfig) {
        accessor.blockEntity?.chronons?.let {
            tooltip.add(ChrononElement(it))
        }
    }
}

class ChrononElement(val chronons: ChrononHandler): Element() {
    init {
        size(Vec2(74f, 13f))
    }

    override fun getSize(): Vec2? = size

    override fun render(graphics: GuiGraphics, x: Float, y: Float, maxX: Float, maxY: Float) {
        TempadUI.renderEnergyBar(graphics, Minecraft.getInstance().font, x.toInt(), y.toInt(), chronons.power, chronons.maxPower)
    }
}
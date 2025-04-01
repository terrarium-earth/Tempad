package earth.terrarium.tempad.common.compat

import com.teamresourceful.resourcefullib.client.scissor.ScissorBox
import com.teamresourceful.resourcefullib.client.utils.RenderUtils
import com.teamresourceful.resourcefullibkt.common.holder
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.locations.DirectLocation
import earth.terrarium.tempad.api.locations.IndirectLocation
import earth.terrarium.tempad.api.tva_device.ChrononHandler
import earth.terrarium.tempad.api.tva_device.chronons
import earth.terrarium.tempad.client.TempadUI
import earth.terrarium.tempad.client.tooltip.DirectPosTooltip
import earth.terrarium.tempad.client.tooltip.IndirectPosTooltip
import earth.terrarium.tempad.common.block.RudimentaryTempadBE
import earth.terrarium.tempad.common.block.RudimentaryTempadBlock
import earth.terrarium.tempad.common.block.SpatialAnchorBE
import earth.terrarium.tempad.common.block.SpatialAnchorBlock
import earth.terrarium.tempad.common.entity.TimedoorEntity
import earth.terrarium.tempad.common.registries.anchorId
import earth.terrarium.tempad.common.registries.id
import earth.terrarium.tempad.common.registries.owner
import earth.terrarium.tempad.common.registries.portalTarget
import earth.terrarium.tempad.tempadId
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.phys.Vec2
import snownee.jade.api.*
import snownee.jade.api.config.IPluginConfig
import snownee.jade.api.ui.Element
import kotlin.math.roundToInt

@WailaPlugin
class JadePlugin: IWailaPlugin {
    override fun registerClient(registration: IWailaClientRegistration) {
        registration.registerEntityComponent(TimedoorComponentProvider, TimedoorEntity::class.java)
        registration.registerBlockComponent(ChrononComponentProvider, Block::class.java)
        registration.registerBlockComponent(AnchorComponentProvider, SpatialAnchorBlock::class.java)
        registration.registerBlockComponent(RudimentaryTempadComponentProvider, RudimentaryTempadBlock::class.java)
    }
}

object TimedoorComponentProvider: IEntityComponentProvider {
    override fun getUid(): ResourceLocation = "timedoor".tempadId

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
    override fun getUid(): ResourceLocation = id

    override fun appendTooltip(tooltip: ITooltip, accessor: BlockAccessor, config: IPluginConfig) {
        (accessor.blockEntity as? SpatialAnchorBE)?.let {
            it.owner?.let {
                tooltip.add(Component.translatable("item.tempad.location_card.created_by", Component.literal(it.name).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY))
            }
            it.id?.let {
                tooltip.add(Component.translatable("item.tempad.location_card.id", it.toString()).withStyle(ChatFormatting.DARK_GRAY))
            }
        }
    }
}

object RudimentaryTempadComponentProvider: IBlockComponentProvider {
    val id = "rudimentary".tempadId

    override fun getUid(): ResourceLocation = id

    override fun appendTooltip(tooltip: ITooltip, accessor: BlockAccessor, config: IPluginConfig) {
        (accessor.blockEntity as? RudimentaryTempadBE)?.let {
            val pos = it.portalTarget
            if(pos is DirectLocation) {
                for (component in DirectPosTooltip(pos).text) {
                    tooltip.add(component)
                }
            } else if (pos is IndirectLocation) {
                for (component in IndirectPosTooltip(pos).text) {
                    tooltip.add(component)
                }
            }
        }
    }
}

object ChrononComponentProvider: IBlockComponentProvider {
    val id = "chronon".tempadId
    override fun getUid(): ResourceLocation = id

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
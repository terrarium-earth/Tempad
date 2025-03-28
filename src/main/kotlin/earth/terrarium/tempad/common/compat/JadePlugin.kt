package earth.terrarium.tempad.common.compat

import com.teamresourceful.resourcefullib.client.scissor.ScissorBox
import com.teamresourceful.resourcefullib.client.utils.RenderUtils
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.tva_device.ChrononHandler
import earth.terrarium.tempad.api.tva_device.chronons
import earth.terrarium.tempad.common.entity.TimedoorEntity
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

object ChrononComponentProvider: IBlockComponentProvider {
    val id = "chronon".tempadId
    override fun getUid(): ResourceLocation = id

    override fun appendTooltip(tooltip: ITooltip, accessor: BlockAccessor, config: IPluginConfig) {
        accessor.blockEntity?.chronons?.let {
            tooltip.add(ChrononElement(it))
        }
    }
}

object AnchorComponentProvider: IBlockComponentProvider {
    val id = "anchor".tempadId
    override fun getUid(): ResourceLocation = id

    override fun appendTooltip(tooltip: ITooltip, accessor: BlockAccessor, config: IPluginConfig) {
        
    }
}

class ChrononElement(val chronons: ChrononHandler): Element() {
    companion object {
        val bg = "power/background".tempadId
        val bar = "power/overlay".tempadId
    }

    init {
        size(Vec2(74f, 13f))
    }

    override fun getSize(): Vec2? = size

    override fun render(graphics: GuiGraphics, x: Float, y: Float, maxX: Float, maxY: Float) {
        graphics.blitSprite(bg, x.toInt(), y.toInt(), 74, 13)
        val uWidth = ((chronons.power.toFloat() / chronons.maxPower) * 72).roundToInt()
        graphics.blitSprite(bar, 72, 11, 0, 0, x.toInt() + 1, y.toInt() + 1, uWidth, 11)

        val minecraft = Minecraft.getInstance()
        val text = "${chronons.power}/${chronons.maxPower}"
        val xOffset = (74 - minecraft.font.width(text)) / 2
        RenderUtils.createScissor(minecraft, graphics, x.toInt() + 1, y.toInt() + 1, uWidth, 11).use {
            graphics.drawString(minecraft.font, text, x.toInt() + xOffset, y.toInt() + 3,
                0xFF000000.toInt(), false)
        }
        RenderUtils.createScissor(minecraft, graphics, x.toInt() + 1 + uWidth, y.toInt() + 1, 72 - uWidth, 11).use {
            graphics.drawString(minecraft.font, text, x.toInt() + xOffset, y.toInt() + 3,
                ChatFormatting.GOLD.color ?: 0, false)
        }
    }
}
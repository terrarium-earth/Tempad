package earth.terrarium.tempad.client.screen

import com.teamresourceful.resourcefullib.common.color.Color
import earth.terrarium.tempad.tempadId
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.CommonComponents
import net.minecraft.resources.ResourceLocation

class SpatialAnchorScreen(val blockPos: BlockPos, val color: Color, var name: String, var visibility: ResourceLocation): Screen(CommonComponents.EMPTY) {
    companion object {
        val SPRITE = "screen/spatial_anchor".tempadId
        const val WIDTH = 200
        const val HEIGHT = 120
    }

    var x: Int = 0
    var y: Int = 0

    override fun init() {
        x = width / 2 - WIDTH / 2
        y = height / 2 - HEIGHT / 2
    }

    override fun renderBackground(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick)
        graphics.blitSprite(SPRITE, x, y, WIDTH, HEIGHT)
    }
}
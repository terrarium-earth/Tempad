package earth.terrarium.tempad.client.widgets

import com.teamresourceful.resourcefullib.client.components.selection.ListEntry
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils
import earth.terrarium.tempad.api.app.TempadApp
import earth.terrarium.tempad.common.utils.appSprites
import earth.terrarium.tempad.common.utils.appTitle
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation

data class AppButton(val app: TempadApp<*>, val appId: ResourceLocation): ListEntry() {
    override fun setFocused(pFocused: Boolean) {}

    override fun isFocused(): Boolean = false

    override fun render(
        graphics: GuiGraphics,
        stack: ScissorBoxStack,
        id: Int,
        left: Int,
        top: Int,
        width: Int,
        height: Int,
        mouseX: Int,
        mouseY: Int,
        hovered: Boolean,
        partialTick: Float,
        selected: Boolean
    ) {
        graphics.blitSprite(appId.appSprites().get(app.isEnabled(Minecraft.getInstance().player!!), hovered), left, top, width, height)
        if (hovered) {
            ScreenUtils.setTooltip(appId.appTitle())
        }
    }
}

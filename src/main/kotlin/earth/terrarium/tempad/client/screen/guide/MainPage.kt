package earth.terrarium.tempad.client.screen.guide

import com.mojang.blaze3d.systems.RenderSystem
import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen
import earth.terrarium.olympus.client.ui.UIConstants
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.client.TempadUI
import earth.terrarium.tempad.data.client.ModLangData
import earth.terrarium.tempad.tempadId
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

class MainPage() : BaseCursorScreen(ModLangData.title) {
    companion object {
        const val pageWidth = 120
        const val pageHeight = 165
        const val coverMargin = 4
        const val pageMargin = 16
        const val screenWidth = (pageWidth + coverMargin) * 2
        const val screenHeight = pageHeight + (coverMargin * 2)

        val tvaLogo = "guide/images/tva".tempadId
    }

    var leftPos = 0
    var topPos = 0

    override fun init() {
        super.init()
        leftPos = (this.width - screenWidth) / 2
        topPos = (this.height - screenHeight) / 2
    }

    override fun renderBackground(
        graphics: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        partialTick: Float,
    ) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick)
        graphics.blitSprite(TempadUI.button.get(true, false), leftPos, topPos, screenWidth, screenHeight)
        graphics.blitSprite(
            UIConstants.BUTTON.get(true, false),
            leftPos + coverMargin,
            topPos + coverMargin,
            pageWidth,
            pageHeight
        )
        graphics.blitSprite(
            UIConstants.BUTTON.get(true, false),
            leftPos + pageWidth + coverMargin,
            topPos + coverMargin,
            pageWidth,
            pageHeight
        )

        RenderSystem.setShaderColor(0.3f, 0.3f, 0.3f, 1f)
        graphics.blitSprite(
            tvaLogo,
            leftPos + coverMargin + (pageWidth - 64) / 2 + 1,
            topPos + coverMargin + 41,
            64,
            32
        )
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        graphics.blitSprite(tvaLogo, leftPos + coverMargin + (pageWidth - 64) / 2, topPos + coverMargin + 40, 64, 32)

        graphics.drawString(
            font,
            ModLangData.title,
            leftPos + coverMargin + (pageWidth - font.width(ModLangData.title)) / 2,
            topPos + coverMargin + 78,
            Tempad.DARK_ORANGE.value,
            false
        )
        graphics.drawString(
            font,
            ModLangData.credits,
            leftPos + coverMargin + (pageWidth - font.width(ModLangData.credits)) / 2,
            topPos + pageHeight + coverMargin - pageMargin - 10,
            Tempad.DARK_ORANGE.value,
            false
        )
    }
}

private fun Component.toMutable(): MutableComponent {
    return MutableComponent.create(this.contents)
}

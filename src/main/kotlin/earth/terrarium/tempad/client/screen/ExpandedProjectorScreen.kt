package earth.terrarium.tempad.client.screen

import com.mojang.blaze3d.systems.RenderSystem
import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen
import earth.terrarium.tempad.tempadId
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component

open class ExpandedProjectorScreen() : BaseCursorScreen(CommonComponents.GUI_OK) {
    companion object {
        val projector = "screen/projector".tempadId // 166x30
        val bottomShading = "screen/spikey_shading".tempadId // 190x124
        val middleShading = "screen/middle_shading".tempadId // 242 x 53
        val bottomGlow = "screen/bottom_glow".tempadId // 242 x 16
        val background = "screen/base_screen".tempadId
        val topLeftShading = "screen/top_left_shading".tempadId // 174x117
        const val MAX_WIDTH = 800
        const val MAX_HEIGHT = 500
        const val PADDING = 16
        const val MARGIN_WIDTH = 30
        const val MARGIN_HEIGHT = 15
        const val PADDING_WIDTH = 14
        const val PADDING_HEIGHT = 44
        const val PROJECTOR_WIDTH = 166
        const val PROJECTOR_HEIGHT = 30
        const val BOTTOM_SHADING_WIDTH = 190
        const val BOTTOM_SHADING_HEIGHT = 124
        const val MIDDLE_SHADING_HEIGHT = 120
        const val GLOW_HEIGHT = 16
        const val TOP_LEFT_SHADING_WIDTH = 174
        const val TOP_LEFT_SHADING_HEIGHT = 117
    }

    var containerWidth = 0
    var containerHeight = 0
    var panelWidth = 0
    var panelHeight = 0
    var panelX = 0
    var panelY = 0
    private var projectorX = 0
    private var middleShadingX = 0
    private var bottomShadingX = 0

    override fun init() {
        super.init()
        containerWidth = minOf(width - MARGIN_WIDTH, MAX_WIDTH)
        containerHeight = minOf(height - MARGIN_HEIGHT, MAX_HEIGHT)
        panelWidth = containerWidth - PADDING_WIDTH
        panelHeight = containerHeight - PADDING_HEIGHT
        panelX = (width - panelWidth) / 2
        panelY = (containerHeight - panelHeight - PADDING * 2) / 2 + PADDING

        // Precompute sprite positions to avoid per-frame calculations
        projectorX = (width - PROJECTOR_WIDTH) / 2
        middleShadingX = panelX + (panelWidth - BOTTOM_SHADING_WIDTH) / 2
        bottomShadingX = panelX + (panelWidth - BOTTOM_SHADING_WIDTH) / 2
    }

    override fun renderBackground(
        graphics: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        @Suppress("UNUSED_PARAMETER") partialTick: Float,
    ) {
        RenderSystem.enableBlend()
        graphics.blitSprite(projector, projectorX, panelY + panelHeight, PROJECTOR_WIDTH, PROJECTOR_HEIGHT)
        graphics.blitSprite(bottomGlow, panelX, panelY + panelHeight - GLOW_HEIGHT, panelWidth, GLOW_HEIGHT)
        graphics.blitSprite(background, panelX - 7, panelY - 7, panelWidth + 14, panelHeight + 14)
        graphics.blitSprite(middleShading, panelX, panelY + panelHeight - MIDDLE_SHADING_HEIGHT, panelWidth, 53)
        graphics.blitSprite(bottomShading, bottomShadingX, panelY + panelHeight - BOTTOM_SHADING_HEIGHT, BOTTOM_SHADING_WIDTH, BOTTOM_SHADING_HEIGHT)
        graphics.blitSprite(topLeftShading, panelX, panelY, TOP_LEFT_SHADING_WIDTH, TOP_LEFT_SHADING_HEIGHT)
    }
}
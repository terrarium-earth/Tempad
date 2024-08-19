package earth.terrarium.tempad.client.tooltip

import net.minecraft.client.gui.Font
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.network.chat.Component
import org.joml.Matrix4f

interface MultilineTooltip: ClientTooltipComponent {
    val text: List<Component>
    override fun getHeight(): Int = text.size * 10
    override fun getWidth(font: Font): Int = text.maxOf { font.width(it) }

    override fun renderText(
        font: Font,
        mouseX: Int,
        mouseY: Int,
        matrix: Matrix4f,
        bufferSource: MultiBufferSource.BufferSource,
    ) {
        for ((index, line) in text.withIndex()) {
            font.drawInBatch(
                line,
                mouseX.toFloat(),
                mouseY.toFloat() + index * 10f,
                line.style.color?.value ?: -1,
                true,
                matrix,
                bufferSource,
                Font.DisplayMode.NORMAL,
                0,
                15728880
            )
        }
    }
}
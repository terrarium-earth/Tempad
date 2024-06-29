package earth.terrarium.tempad.client.widgets

import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.fuel.FuelConsumer
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Renderable

data class FuelBarWidget(val fuelConsumer: FuelConsumer, val x: Int, val y: Int): Renderable {
    companion object {
        const val WIDTH = 6
        const val HEIGHT = 54

        val SPRITE = "modal/background".tempadId
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val fuelAmount = fuelConsumer.fuelAmount
        val capacity = fuelConsumer.capacity
        val percentage = fuelAmount.toFloat() / capacity.toFloat()
        val barHeight = (percentage * HEIGHT).toInt()
        graphics.blitSprite(SPRITE, WIDTH, HEIGHT, 0, 0, x + 234, y + HEIGHT - barHeight, WIDTH, barHeight)
    }
}

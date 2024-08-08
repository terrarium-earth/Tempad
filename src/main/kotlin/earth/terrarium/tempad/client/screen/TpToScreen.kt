package earth.terrarium.tempad.client.screen

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.tempadId
import earth.terrarium.tempad.client.widgets.KListWidgetItem
import earth.terrarium.tempad.client.widgets.colored.ColoredButton
import earth.terrarium.tempad.client.widgets.colored.ColoredList
import earth.terrarium.tempad.common.registries.ModMenus
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.PlayerFaceRenderer
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.client.player.RemotePlayer
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.PlayerModelPart

class TpToScreen(menu: ModMenus.TpToMenu, inv: Inventory, title: Component) :
    AbstractTempadScreen<ModMenus.TpToMenu>(sprite, menu, inv, title) {
    companion object {
        val sprite = "screen/tp_to".tempadId

        val selectPlayer = Component.translatable("app.tempad.tp_to.selected")
        val teleportButton = Component.translatable("app.tempad.tp_to.teleport")
    }

    var player: Player? = null

    override fun init() {
        super.init()
        val btn = addRenderableWidget(
            ColoredButton(
                teleportButton,
                width = 76,
                height = 16,
                x = localLeft + 118,
                y = localTop + 98
            ) {
                player?.let {
                    // Teleport player
                }
            }
        )

        btn.active = false
        val listWidget = ColoredList(111, 74)

        val playerEntries = minecraft!!.player!!.connection.listedOnlinePlayers.map {
            PlayerEntry(minecraft!!, font, it) { entry ->
                player = object : RemotePlayer(minecraft!!.level!!, it.profile) {
                    override fun isModelPartShown(pPart: PlayerModelPart): Boolean = true
                }

                for (listEntry in listWidget) {
                    listEntry as? PlayerEntry ?: continue
                    listEntry.selected = false
                }

                entry.selected = true
                btn.active = true
            }
        }

        listWidget.set(playerEntries)
        listWidget.setPosition(localLeft + 4, localTop + 39)

        addRenderableWidget(listWidget)

    }

    override fun renderBg(graphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        super.renderBg(graphics, partialTick, mouseX, mouseY)
        player?.let {
            InventoryScreen.renderEntityInInventoryFollowsMouse(
                graphics, localLeft + 118, localTop + 21, localLeft + 192, localTop + 95, 60, 0.6f,
                mouseX.toFloat(),
                mouseY.toFloat(),
                it
            )
        } ?: {
            graphics.drawString(font, selectPlayer, localLeft + 118, localTop + 37, Tempad.ORANGE.value, false)
        }
    }
}

class PlayerEntry(val minecraft: Minecraft, val font: Font, val playerInfo: PlayerInfo, val onClick: (PlayerEntry) -> Unit) :
    KListWidgetItem {
    override var _x: Int = 0
    override var _y: Int = 0
    override var _focused: Boolean = false
    override var _width: Int = 0
    override var _height: Int = 13

    var selected: Boolean = false

    val playerTexture: ResourceLocation get() = minecraft.skinManager.getInsecureSkin(playerInfo.profile).texture()

    fun isMouseOver(mouseX: Int, mouseY: Int): Boolean {
        return mouseX >= _x && mouseX < _x + _width && mouseY >= _y && mouseY < _y + _height
    }

    override fun render(graphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        val mouseOver = isMouseOver(pMouseX, pMouseY)
        val color = if (selected) 0xFF000000.toInt() else if (mouseOver) Tempad.HIGHLIGHTED_ORANGE.value else Tempad.ORANGE.value

        if (selected) {
            graphics.fill(_x, _y, _x + _width, _y + _height, Tempad.ORANGE.value)
        }

        if (mouseOver && !selected) {
            graphics.fill(_x + 3, _y + 1, _x + 14, _y + 12, Tempad.HIGHLIGHTED_ORANGE.value)
        }

        PlayerFaceRenderer.draw(graphics, playerTexture, _x + 4, _y + 2, 9)

        graphics.drawString(font, playerInfo.profile.name, _x + 17, _y + 3, color, false)
    }

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        onClick(this)
        return true
    }
}

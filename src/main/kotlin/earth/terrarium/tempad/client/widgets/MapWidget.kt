package earth.terrarium.tempad.client.widgets

import com.mojang.math.Axis
import com.teamresourceful.resourcefullib.client.CloseablePoseStack
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.client.widgets.map.ClaimMapRenderer
import earth.terrarium.tempad.client.widgets.map.ClaimMapTopologyAlgorithm
import earth.terrarium.tempad.common.utils.*
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Renderable
import net.minecraft.client.player.LocalPlayer
import net.minecraft.core.GlobalPos
import net.minecraft.resources.ResourceLocation
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import java.util.function.Supplier

class MapWidget(var x: Int, var y: Int, var size: Int) : Renderable {
    var mapRenderer: ClaimMapRenderer? = null

    init {
        this.refreshMap()
    }

    fun renderLoading(graphics: GuiGraphics) {
        val font = Minecraft.getInstance().font
        graphics.drawCenteredString(font, "Loading...", (x + size / 2f).toInt(), (y + size / 2f).toInt(), 0xFFFFFF)
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        if (mapRenderer == null) {
            this.renderLoading(graphics)
        } else {
            val player = Minecraft.getInstance().player ?: return
            mapRenderer!!.render(graphics, this.x, this.y, this.size)
            CloseablePoseStack(graphics).use { pose ->
                pose.translate(0f, 0f, 2f)
                this.renderPlayerAvatar(player, graphics)
                pose.translate(0f, 0f, 0.1f)
            }
        }
    }

    fun refreshMap() {
        val player = Minecraft.getInstance().player ?: return

        val chunkPos = player.chunkPosition()
        val scale = 64
        val minX = chunkPos.minBlockX - scale
        val minZ = chunkPos.minBlockZ - scale
        val maxX = chunkPos.maxBlockX + scale + 1
        val maxZ = chunkPos.maxBlockZ + scale + 1

        val colors: Array<IntArray> =
            ClaimMapTopologyAlgorithm.setColors(minX, minZ, maxX, maxZ, player.clientLevel, player)
        this.mapRenderer = ClaimMapRenderer(colors, scale * 2 + 16)
    }

    private fun renderPlayerAvatar(player: LocalPlayer, graphics: GuiGraphics) {
        val left = (this.size) / 2f
        val top = (this.size) / 2f

        val playerX = player.x
        val playerZ = player.z
        var x = (playerX % 16) + (if (playerX >= 0) -8 else 8)
        var y = (playerZ % 16) + (if (playerZ >= 0) -8 else 8)

        val scale = this.size / 144.0

        x *= scale
        y *= scale

        CloseablePoseStack(graphics).use { pose ->
            pose.translate(this.x + left + x, this.y + top + y, 0.0)
            pose.mulPose(Axis.ZP.rotationDegrees(player.yRot))
            pose.translate(-4f, -4f, 0f)
            graphics.blit(MAP_ICONS, 0, 0, 0f, 0f, 8, 8, 8, 8)
        }
    }

    companion object {
        val MAP_ICONS: ResourceLocation = ResourceLocation.withDefaultNamespace("textures/map/decorations/player.png")
    }
}

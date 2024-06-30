package earth.terrarium.tempad.client.widgets

import com.mojang.math.Axis
import com.teamresourceful.resourcefullib.client.CloseablePoseStack
import earth.terrarium.tempad.client.widgets.map.ClaimMapRenderer
import earth.terrarium.tempad.client.widgets.map.ClaimMapTopologyAlgorithm
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Renderable
import net.minecraft.client.player.LocalPlayer
import net.minecraft.resources.ResourceLocation
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import java.util.function.Supplier

class MapWidget(var x: Int, var y: Int, var size: Int, var viewDistance: Int) : Renderable {
    var mapRenderer: ClaimMapRenderer? = null

    init {
        this.refreshMap()
    }

    fun renderLoading(graphics: GuiGraphics) {
        val font = Minecraft.getInstance().font
        graphics.drawCenteredString(font, "Loading...", (x + size / 2f).toInt(), (y + size / 2f).toInt(), 0xFFFFFF)
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val font = Minecraft.getInstance().font

        if (mapRenderer == null) {
            this.renderLoading(graphics)
        } else {
            val player = Minecraft.getInstance().player ?: return
            mapRenderer!!.render(graphics, this.x, this.y, this.size)
            CloseablePoseStack(graphics).use { pose ->
                pose.translate(0f, 0f, 2f)
                this.renderPlayerAvatar(player, graphics)
            }
        }
    }

    fun refreshMap() {
        val player = Minecraft.getInstance().player ?: return

        val chunkPos = player.chunkPosition()
        val scale = scaledRenderDistance
        val minX = chunkPos.minBlockX - scale
        val minZ = chunkPos.minBlockZ - scale
        val maxX = chunkPos.maxBlockX + scale + 1
        val maxZ = chunkPos.maxBlockZ + scale + 1

        if (scale / 8 > 12) {
            // If the render distance is greater than 12 chunks, run asynchronously to avoid stuttering.
            CompletableFuture.supplyAsync {
                ClaimMapTopologyAlgorithm.setColors(
                    minX,
                    minZ,
                    maxX,
                    maxZ,
                    player.clientLevel,
                    player
                )
            }.thenAcceptAsync(
                { colors: Array<IntArray> ->
                    this.mapRenderer =
                        ClaimMapRenderer(colors, scale * 2 + 16)
                }, Minecraft.getInstance()
            )
        } else {
            val colors: Array<IntArray> =
                ClaimMapTopologyAlgorithm.setColors(minX, minZ, maxX, maxZ, player.clientLevel, player)
            this.mapRenderer = ClaimMapRenderer(colors, scale * 2 + 16)
        }
    }

    val scaledRenderDistance: Int
        get() {
            var scale = this.viewDistance * 8
            scale -= scale % 16
            return scale
        }

    private fun renderPlayerAvatar(player: LocalPlayer, graphics: GuiGraphics) {
        val left = (this.size) / 2f
        val top = (this.size) / 2f

        val playerX = player.x
        val playerZ = player.z
        var x = (playerX % 16) + (if (playerX >= 0) -8 else 8)
        var y = (playerZ % 16) + (if (playerZ >= 0) -8 else 8)

        val scale = this.size / (scaledRenderDistance * 2f + 16)

        x *= scale.toDouble()
        y *= scale.toDouble()
        CloseablePoseStack(graphics).use { pose ->
            pose.translate(this.x + left + x, this.y + top + y, 0.0)
            pose.mulPose(Axis.ZP.rotationDegrees(player.yRot))
            pose.translate(-4f, -4f, 0f)
            graphics.blit(MAP_ICONS, 0, 0, 40f, 0f, 8, 8, 128, 128)
        }
    }

    companion object {
        val MAP_ICONS: ResourceLocation = ResourceLocation.withDefaultNamespace("textures/map/map_icons.png")
    }
}

package earth.terrarium.tempad.client.tooltip

import com.mojang.authlib.GameProfile
import earth.terrarium.tempad.Tempad
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.PlayerFaceExtractor
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.network.chat.Component
import net.minecraft.world.item.component.ResolvableProfile

class PlayerPosTooltip(val profile: GameProfile): ClientTooltipComponent {
    val text = Component.literal(profile.name)

    val playerTexture get() = Minecraft.getInstance().playerSkinRenderCache().createLookup(ResolvableProfile.createResolved(profile))

    override fun getHeight(p0: Font): Int = 10
    override fun getWidth(font: Font): Int = font.width(text) + 13

    override fun extractText(
        graphics: GuiGraphicsExtractor,
        font: Font,
        x: Int,
        y: Int,
    ) {
        super.extractText(graphics, font, x, y)
        graphics.text(font, text, x + 13, y + 1, Tempad.ORANGE.value, true)
    }

    override fun extractImage(
        font: Font,
        x: Int,
        y: Int,
        w: Int,
        h: Int,
        graphics: GuiGraphicsExtractor,
    ) {
        super.extractImage(font, x, y, w, h, graphics)
        val playerExtract = playerTexture.get()
        PlayerFaceExtractor.extractRenderState(graphics, playerExtract.playerSkin(), x, y, 9)
    }
}
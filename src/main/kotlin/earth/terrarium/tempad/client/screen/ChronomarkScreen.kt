package earth.terrarium.tempad.client.screen

import com.teamresourceful.resourcefullib.common.color.Color
import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.constants.MinecraftColors
import earth.terrarium.olympus.client.layouts.Layouts
import earth.terrarium.tempad.client.state.MutableState
import earth.terrarium.tempad.common.network.c2s.UpdateAnchorPacket
import earth.terrarium.tempad.common.network.c2s.UpdateChronomarkPacket
import earth.terrarium.tempad.common.utils.sendToServer
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

class ChronomarkScreen(pos: BlockPos, name: String, color: Color, access: ResourceLocation, locked: Boolean, yOffset: Float, angle: Int) :
    TimedoorMarkerScreen(pos, name, color, access, locked) {
    val yOffset = MutableState.of(yOffset.toDouble())
    val angle = MutableState.of(angle)

    init {
        bgWidth = 215
    }

    override fun init() {
        super.init()
        val entries = Layouts.column().withGap(4).withPosition((width - bgWidth) / 2 + 110, (height - bgHeight) / 2 + 23)

        entries.withChild(Widgets.text(Component.translatable("screen.tempad.marker.yoffset")).withColor(MinecraftColors.GRAY).withShadow())
        entries.withChild(Widgets.doubleInput(yOffset) {
            it.withSize(100, 20)
        })

        entries.withChild(Widgets.text(Component.translatable("screen.tempad.marker.angle")).withColor(MinecraftColors.GRAY).withShadow())
        entries.withChild(Widgets.intInput(angle) {
            it.withSize(100, 20)
        })

        entries.build(::addRenderableWidget)
    }

    override fun sync() {
        UpdateChronomarkPacket(pos, color.value, name.value, access.get(), locked.get(), yOffset.get().toFloat(), angle.get()).sendToServer()
    }
}
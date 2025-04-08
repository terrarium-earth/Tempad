package earth.terrarium.tempad.client.screen

import com.teamresourceful.resourcefullib.common.color.Color
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

class ChronomarkScreen(pos: BlockPos, name: String, color: Color, access: ResourceLocation, locked: Boolean) :
    TimedoorMarkerScreen(pos, name, color, access, locked) {
    init {
        title = Component.translatable("block.tempad.chronomark")
    }
}
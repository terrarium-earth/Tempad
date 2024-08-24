package earth.terrarium.tempad.api.locations

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefullib.common.color.Color
import earth.terrarium.tempad.tempadId
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

object EmptyLocation: NamedGlobalPos {
    override val name: Component = CommonComponents.EMPTY
    override val pos: Vec3? = null
    override val dimension: ResourceKey<Level>? = null
    override val angle: Float = 0.0f
    override val color: Color = Color.DEFAULT
    override val type: LocationType<*> = LocationType("empty".tempadId, ByteCodec.unit(EmptyLocation), MapCodec.unit(EmptyLocation))

    override fun consume(player: Player): Component {
        return CommonComponents.EMPTY
    }
}
package earth.terrarium.tempad.api.locations

import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.color.Color
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.block.AnchorPointBE
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.color
import earth.terrarium.tempad.common.utils.angle
import earth.terrarium.tempad.tempadId
import net.minecraft.core.GlobalPos
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

class AnchorPointPos(val globalPos: GlobalPos): NamedGlobalPos {
    companion object {
        val byteCodec = ExtraByteCodecs.GLOBAL_POS.map(::AnchorPointPos) { it.globalPos }
        val codec = GlobalPos.MAP_CODEC.xmap(::AnchorPointPos) { it.globalPos }
        val type = LocationType<AnchorPointPos>("anchor_point".tempadId, byteCodec, codec)
    }

    val blockEntity get() = Tempad.server?.getLevel(globalPos.dimension)?.getBlockEntity(globalPos.pos) as? AnchorPointBE

    override val name: Component get() = blockEntity?.name ?: ModBlocks.anchorPoint.name
    override val pos: Vec3? = Vec3.atCenterOf(globalPos.pos).add(0.0, 0.5, 0.0)
    override val dimension: ResourceKey<Level>? = globalPos.dimension
    override val angle: Float get() = blockEntity?.angle ?: 0.0f
    override val color: Color get() = blockEntity?.color ?: Tempad.ORANGE
    override val type: LocationType<*> = Companion.type

    override fun consume(player: Player): Component {
        TODO("Not yet implemented")
    }
}
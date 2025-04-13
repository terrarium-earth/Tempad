package earth.terrarium.tempad.common.block.timedoor_marker

import earth.terrarium.tempad.api.locations.NamedGlobalVec3
import earth.terrarium.tempad.api.player_access.PlayerAccessApi
import earth.terrarium.tempad.common.network.s2c.OpenChronomark
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.accessId
import earth.terrarium.tempad.common.registries.angle
import earth.terrarium.tempad.common.registries.color
import earth.terrarium.tempad.common.registries.locked
import earth.terrarium.tempad.common.registries.yOffset
import earth.terrarium.tempad.common.utils.sendToClient
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.Vec3
import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.sin

class ChronomarkBE(pos: BlockPos, state: BlockState) : AbstractMarkerBe(ModBlocks.chronomarkBE, pos, state) {
    override val landingPosition: Vec3 get() {
        val starting = blockPos.bottomCenter.add(0.0, yOffset.toDouble(), 0.0)
        val xOffset = cos(toRadians(landingAngle.toDouble() + 90))
        val zOffset = sin(toRadians(landingAngle.toDouble() + 90))
        return starting.add(xOffset, 0.0, zOffset)
    }
    override val landingAngle: Float get() = angle.toFloat()

    override fun openScreen(player: Player) {
        OpenChronomark(blockPos, color, posName.string, PlayerAccessApi.ids, accessId, locked, yOffset, angle).sendToClient(player)
    }
}
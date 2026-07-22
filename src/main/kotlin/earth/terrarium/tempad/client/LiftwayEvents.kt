package earth.terrarium.tempad.client

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.block.LiftwayBlock
import earth.terrarium.tempad.common.network.c2s.LiftActionPacket
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.ModSounds
import earth.terrarium.tempad.common.utils.sendToServer
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerPlayer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ClientTickEvent
import kotlin.jvm.optionals.getOrNull

@EventBusSubscriber(modid = Tempad.MOD_ID)
object LiftwayEvents {
    var wasSneaking = false
    var wasJumping = false

    @SubscribeEvent
    fun interceptInput(event: ClientTickEvent.Post) {
        val minecraft = Minecraft.getInstance()
        val onLiftway = minecraft.player?.blockStateOn?.`is`(ModBlocks.liftway) ?: false

        val jumping = minecraft.options.keyJump.isDown
        if (onLiftway && jumping && !wasJumping) {
            minecraft.player?.setDeltaMovement(0.0, 0.0, 0.0)
            LiftActionPacket(true).sendToServer()
        }
        wasJumping = jumping

        val sneaking = minecraft.options.keyShift.isDown
        if (onLiftway && sneaking && !wasSneaking) {
            minecraft.player?.setDeltaMovement(0.0, 0.0, 0.0)
            LiftActionPacket(false).sendToServer()
        }
        wasSneaking = sneaking
    }

    fun lift(player: ServerPlayer, dir: Direction) {
        if (player.blockStateOn.block !is LiftwayBlock) return
        val yCap = if (dir == Direction.UP) 20 else -20
        val yMin = if (dir == Direction.UP) 1 else -1
        BlockPos.betweenCornersInDirection(player.blockPosition().offset(-1, yMin, -1), player.blockPosition().offset(1, yCap, 1), dir.unitVec3).forEach { pos ->
            val blockState = player.level().getBlockState(pos)
            if (blockState.`is`(ModBlocks.liftway)) {
                val originBlockEntity = player.level().getBlockEntity(player.blockPosition(), ModBlocks.liftwayBE).getOrNull() ?: return@forEach
                val blockEntity = player.level().getBlockEntity(pos, ModBlocks.liftwayBE).getOrNull() ?: return@forEach
                originBlockEntity.triggerMovement()
                blockEntity.triggerMovement()
                player.level().playLocalSound(player.blockPosition(), ModSounds.timedoorEnterStereo, player.soundSource, 1.0f, 1.0f, true)
                player.teleportTo(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5)
                return
            }
        }
    }
}
package earth.terrarium.tempad.common.network

import earth.terrarium.tempad.api.context.ContextHolder
import earth.terrarium.tempad.api.context.SyncableContext
import earth.terrarium.tempad.common.registries.ModItems
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player

/** Max length of user supplied names (locations, markers) accepted from the client. */
const val MAX_NAME_LENGTH = 64

/** Validates a client-supplied context and requires it to point at a Tempad. */
fun ContextHolder<*>.resolveTempad(player: Player): SyncableContext<*>? = resolve(player) { it.`is`(ModItems.tempad) }

/**
 * Whether a C2S packet may act on the block at [pos]. Checks loaded state first so that a forged position can never
 * make the server load or generate a chunk.
 */
fun Player.canEditBlockFromPacket(pos: BlockPos): Boolean {
    val level = level()
    return level.isLoaded(pos) && canInteractWithBlock(pos, 1.0) && mayInteract(level, pos)
}

fun String.clampName(): String = if (length > MAX_NAME_LENGTH) substring(0, MAX_NAME_LENGTH) else this

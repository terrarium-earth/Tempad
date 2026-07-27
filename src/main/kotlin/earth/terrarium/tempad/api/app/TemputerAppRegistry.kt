package earth.terrarium.tempad.api.app

import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import earth.terrarium.tempad.api.access.ItemAccessAddress
import net.minecraft.core.BlockPos
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.neoforged.neoforge.transfer.access.ItemAccess

fun interface TemputerAppProvider {
    operator fun invoke(level: Level, player: Player, pos: BlockPos): TempadApp<*>?
}

data class TemputerAppHolder(val id: Identifier, val pos: BlockPos)

object TemputerAppRegistry {
    private val apps = mutableMapOf<Identifier, TemputerAppProvider>()

    val BYTE_CODEC = ObjectByteCodec.create(
        ExtraByteCodecs.IDENTIFIER.fieldOf { it.id },
        ExtraByteCodecs.BLOCK_POS.fieldOf { it.pos },
        ::TemputerAppHolder
    )

    @JvmName("register")
    operator fun set(id: Identifier, provider: TemputerAppProvider) {
        apps[id] = provider
    }

    operator fun get(id: Identifier, player: Player, pos: BlockPos): TempadApp<*>? {
        return apps[id]?.invoke(player.level(), player, pos)
    }

    fun getAll(player: Player, pos: BlockPos): Map<Identifier, TempadApp<*>> {
        return apps.mapValues { it.value(player.level(), player, pos) }.mapNotNull { it.value?.let { app -> it.key to app } }.toMap()
    }

    fun getIds(): Set<Identifier> {
        return apps.keys
    }
}
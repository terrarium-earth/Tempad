package earth.terrarium.tempad.api.app

import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import earth.terrarium.tempad.api.access.ItemAccessAddress
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.transfer.access.ItemAccess

fun interface TempadAppProvider {
    operator fun invoke(ctx: ItemAccessAddress<*>): TempadApp<*>?
}

data class TempadAppHolder(val id: Identifier, val ctx: ItemAccessAddress<*>) {
    fun getApp(player: Player): TempadApp<*>? {
        return TempadAppRegistry[id, player, ctx]
    }

    fun getCtx(player: Player): ItemAccess {
        return ctx.getAccess(player)
    }
}

object TempadAppRegistry {
    private val apps = mutableMapOf<Identifier, TempadAppProvider>()

    val BYTE_CODEC = ObjectByteCodec.create(
        ExtraByteCodecs.IDENTIFIER.fieldOf { it.id },
        ItemAccessAddress.codec.fieldOf { it.ctx },
        ::TempadAppHolder
    )

    @JvmName("register")
    operator fun set(id: Identifier, provider: TempadAppProvider) {
        apps[id] = provider
    }

    operator fun get(id: Identifier, player: Player, ctx: ItemAccessAddress<*>): TempadApp<*>? {
        return apps[id]?.invoke(ctx)
    }

    fun getAll(ctx: ItemAccessAddress<*>): Map<Identifier, TempadApp<*>> {
        return apps.mapValues { it.value(ctx) }.mapNotNull { it.value?.let { app -> it.key to app } }.toMap()
    }

    fun getIds(): Set<Identifier> {
        return apps.keys
    }
}
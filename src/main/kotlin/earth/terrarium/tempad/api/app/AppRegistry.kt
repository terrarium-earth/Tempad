package earth.terrarium.tempad.api.app

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import earth.terrarium.tempad.api.access.ItemAccessAddress
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.transfer.access.ItemAccess

fun interface AppProvider {
    operator fun invoke(ctx: ItemAccessAddress<*>, isStationary: Boolean): TempadApp<*>?
}

data class AppHolder(val id: Identifier, val ctx: ItemAccessAddress<*>, val isStationary: Boolean) {
    fun getApp(player: Player): TempadApp<*>? {
        return AppRegistry.get(id, player, ctx, isStationary)
    }

    fun getCtx(player: Player): ItemAccess {
        return ctx.getAccess(player)
    }
}

object AppRegistry {
    private val apps = mutableMapOf<Identifier, AppProvider>()

    val BYTE_CODEC = ObjectByteCodec.create(
        ExtraByteCodecs.IDENTIFIER.fieldOf { it.id },
        ItemAccessAddress.codec.fieldOf { it.ctx },
        ByteCodec.BOOLEAN.fieldOf { it.isStationary },
        ::AppHolder
    )

    @JvmName("register")
    operator fun set(id: Identifier, provider: AppProvider) {
        apps[id] = provider
    }

    operator fun get(id: Identifier, player: Player, ctx: ItemAccessAddress<*>, isStationary: Boolean): TempadApp<*>? {
        return apps[id]?.invoke(ctx, isStationary)
    }

    fun getAll(ctx: ItemAccessAddress<*>, isStationary: Boolean): Map<Identifier, TempadApp<*>> {
        return apps.mapValues { it.value(ctx, isStationary) }.mapNotNull { it.value?.let { app -> it.key to app } }.toMap()
    }

    fun getIds(): Set<Identifier> {
        return apps.keys
    }
}
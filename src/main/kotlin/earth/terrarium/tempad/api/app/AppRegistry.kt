package earth.terrarium.tempad.api.app

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import earth.terrarium.tempad.api.context.ContextHolder
import earth.terrarium.tempad.api.context.SyncableContext
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.Player

fun interface AppProvider {
    operator fun invoke(ctx: SyncableContext<*>, isStationary: Boolean): TempadApp<*>?
}

data class AppHolder(val id: Identifier, val ctx: ContextHolder<*>, val isStationary: Boolean) {
    fun getApp(player: Player): TempadApp<*>? {
        return AppRegistry.get(id, ctx.getCtx(player), isStationary)
    }

    fun getCtx(player: Player): SyncableContext<*> {
        return ctx.getCtx(player)
    }
}

object AppRegistry {
    private val apps = mutableMapOf<Identifier, AppProvider>()

    val BYTE_CODEC = ObjectByteCodec.create(
        ExtraByteCodecs.RESOURCE_LOCATION.fieldOf { it.id },
        ContextHolder.codec.fieldOf { it.ctx },
        ByteCodec.BOOLEAN.fieldOf { it.isStationary },
        ::AppHolder
    )

    @JvmName("register")
    operator fun set(id: Identifier, provider: AppProvider) {
        apps[id] = provider
    }

    operator fun get(id: Identifier, ctx: SyncableContext<*>, isStationary: Boolean): TempadApp<*>? {
        return apps[id]?.invoke(ctx, isStationary)
    }

    fun getAll(ctx: SyncableContext<*>, isStationary: Boolean): Map<Identifier, TempadApp<*>> {
        return apps.mapValues { it.value(ctx, isStationary) }.mapNotNull { it.value?.let { app -> it.key to app } }.toMap()
    }

    fun getIds(): Set<Identifier> {
        return apps.keys
    }
}
package earth.terrarium.tempad.common.apps

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.menu.MenuContent
import com.teamresourceful.resourcefullib.common.menu.MenuContentSerializer
import earth.terrarium.tempad.api.context.ContextHolder
import earth.terrarium.tempad.common.utils.RecordCodecMenuContentSerializer

abstract class AppContent<T: AppContent<T>> (val ctx: ContextHolder<*>, val isStationary: Boolean, byteCodec: ByteCodec<T>) : MenuContent<T> {
    private val serializer: MenuContentSerializer<T> = RecordCodecMenuContentSerializer(byteCodec)
    override fun serializer(): MenuContentSerializer<T> = serializer
}

class BasicAppContent(ctx: ContextHolder<*>, isStationary: Boolean) : AppContent<BasicAppContent>(ctx, isStationary, codec) {
    companion object {
        val codec = ObjectByteCodec.create(
            ContextHolder.codec.fieldOf(BasicAppContent::ctx),
            ByteCodec.BOOLEAN.fieldOf(BasicAppContent::isStationary),
            ::BasicAppContent
        )
    }
}
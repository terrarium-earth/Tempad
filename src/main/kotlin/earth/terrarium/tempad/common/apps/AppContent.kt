package earth.terrarium.tempad.common.apps

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.menu.MenuContent
import com.teamresourceful.resourcefullib.common.menu.MenuContentSerializer
import earth.terrarium.tempad.api.access.ItemAccessAddress
import earth.terrarium.tempad.common.utils.RecordCodecMenuContentSerializer

abstract class AppContent<T: AppContent<T>> (val ctx: ItemAccessAddress<*>, val isStationary: Boolean, byteCodec: ByteCodec<T>) : MenuContent<T> {
    private val serializer: MenuContentSerializer<T> = RecordCodecMenuContentSerializer(byteCodec)
    override fun serializer(): MenuContentSerializer<T> = serializer
}

class BasicAppContent(ctx: ItemAccessAddress<*>, isStationary: Boolean) : AppContent<BasicAppContent>(ctx, isStationary, codec) {
    companion object {
        val codec = ObjectByteCodec.create(
            ItemAccessAddress.codec.fieldOf(BasicAppContent::ctx),
            ByteCodec.BOOLEAN.fieldOf(BasicAppContent::isStationary),
            ::BasicAppContent
        )
    }
}
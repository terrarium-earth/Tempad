package earth.terrarium.tempad.common.apps

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.menu.MenuContent
import com.teamresourceful.resourcefullib.common.menu.MenuContentSerializer
import earth.terrarium.tempad.api.access.ItemAccessAddress
import earth.terrarium.tempad.common.utils.RecordCodecMenuContentSerializer

abstract class AppContent<T: AppContent<T>> (val ctx: ItemAccessAddress<*>, byteCodec: ByteCodec<T>) : MenuContent<T> {
    private val serializer: MenuContentSerializer<T> = RecordCodecMenuContentSerializer(byteCodec)
    override fun serializer(): MenuContentSerializer<T> = serializer
}

class BasicAppContent(ctx: ItemAccessAddress<*>) : AppContent<BasicAppContent>(ctx, codec) {
    companion object {
        val codec = ObjectByteCodec.create(
            ItemAccessAddress.codec.fieldOf(BasicAppContent::ctx),
            ::BasicAppContent
        )
    }
}
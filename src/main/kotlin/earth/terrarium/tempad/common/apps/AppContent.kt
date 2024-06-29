package earth.terrarium.tempad.common.apps

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefullib.common.menu.MenuContent
import com.teamresourceful.resourcefullib.common.menu.MenuContentSerializer
import earth.terrarium.tempad.common.utils.RecordCodecMenuContentSerializer

abstract class AppContent<T: AppContent<T>> (val slotId: Int, byteCodec: ByteCodec<T>) : MenuContent<T> {
    private val serializer: MenuContentSerializer<T> = RecordCodecMenuContentSerializer(byteCodec)
    override fun serializer(): MenuContentSerializer<T> = serializer
}
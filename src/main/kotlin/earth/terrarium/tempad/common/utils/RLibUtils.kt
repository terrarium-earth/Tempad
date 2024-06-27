package earth.terrarium.tempad.common.utils

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefullib.common.menu.CodecMenuContentSerializer
import com.teamresourceful.resourcefullib.common.menu.MenuContent

data class RecordCodecMenuContentSerializer<T: MenuContent<T>>(val codec: ByteCodec<T>) : CodecMenuContentSerializer<T> {
    override fun codec(): ByteCodec<T> = codec
}
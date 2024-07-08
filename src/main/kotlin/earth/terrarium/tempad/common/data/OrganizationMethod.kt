package earth.terrarium.tempad.common.data

import com.teamresourceful.resourcefullib.common.codecs.EnumCodec
import com.teamresourceful.bytecodecs.defaults.EnumCodec as EnumByteCodec

enum class OrganizationMethod {
    DIMENSION,
    TYPE,
    ALPHABETICAL;

    companion object {
        val CODEC = EnumCodec.of(OrganizationMethod::class.java)
        val BYTE_CODEC = EnumByteCodec(OrganizationMethod::class.java)
    }
}
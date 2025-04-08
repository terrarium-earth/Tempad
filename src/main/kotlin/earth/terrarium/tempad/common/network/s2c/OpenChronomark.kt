package earth.terrarium.tempad.common.network.s2c

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.color.Color
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType
import earth.terrarium.tempad.client.TempadClient
import earth.terrarium.tempad.common.network.ClientPacketCompanion
import earth.terrarium.tempad.tempadId
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation

data class OpenChronomark(val blockPos: BlockPos, val color: Color, val name: String, val access: ResourceLocation, val locked: Boolean, val yOffset: Float, val angle: Int): Packet<OpenChronomark> {
    override fun type(): ClientboundPacketType<OpenChronomark> = Companion

    companion object: ClientPacketCompanion<OpenChronomark> {
        override val id: ResourceLocation = "open_chronomark".tempadId

        override val byteCodec: ByteCodec<OpenChronomark> = ObjectByteCodec.create(
            ExtraByteCodecs.BLOCK_POS.fieldOf(OpenChronomark::blockPos),
            Color.BYTE_CODEC.fieldOf(OpenChronomark::color),
            ByteCodec.STRING.fieldOf(OpenChronomark::name),
            ExtraByteCodecs.RESOURCE_LOCATION.fieldOf(OpenChronomark::access),
            ByteCodec.BOOLEAN.fieldOf(OpenChronomark::locked),
            ByteCodec.FLOAT.fieldOf(OpenChronomark::yOffset),
            ByteCodec.INT.fieldOf(OpenChronomark::angle),
            ::OpenChronomark
        )

        override fun onReceive(packet: OpenChronomark) {
            TempadClient.openChronomark(packet)
        }
    }
}
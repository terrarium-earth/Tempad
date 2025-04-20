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

data class OpenTimedoorMarker(val blockPos: BlockPos, val color: Color, val name: String, val canAccess: Boolean, val locked: Boolean): Packet<OpenTimedoorMarker> {
    override fun type(): ClientboundPacketType<OpenTimedoorMarker> = Companion

    companion object: ClientPacketCompanion<OpenTimedoorMarker> {
        override val id: ResourceLocation = "open_timedoor_marker".tempadId

        override val byteCodec: ByteCodec<OpenTimedoorMarker> = ObjectByteCodec.create(
            ExtraByteCodecs.BLOCK_POS.fieldOf(OpenTimedoorMarker::blockPos),
            Color.BYTE_CODEC.fieldOf(OpenTimedoorMarker::color),
            ByteCodec.STRING.fieldOf(OpenTimedoorMarker::name),
            ByteCodec.BOOLEAN.fieldOf(OpenTimedoorMarker::canAccess),
            ByteCodec.BOOLEAN.fieldOf(OpenTimedoorMarker::locked),
            ::OpenTimedoorMarker
        )

        override fun onReceive(packet: OpenTimedoorMarker) {
            TempadClient.openTimedoorMarker(packet)
        }
    }
}
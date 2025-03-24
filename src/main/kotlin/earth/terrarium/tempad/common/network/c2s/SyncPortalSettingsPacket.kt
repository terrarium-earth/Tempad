package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import earth.terrarium.tempad.api.context.ContextHolder
import earth.terrarium.tempad.api.context.WorkstationContext
import earth.terrarium.tempad.api.context.modify
import earth.terrarium.tempad.api.locations.IndirectLocation
import earth.terrarium.tempad.common.data.PortalPlacementComponent
import earth.terrarium.tempad.common.network.ServerPacketCompanion
import earth.terrarium.tempad.common.registries.owner
import earth.terrarium.tempad.common.registries.portalOffset
import earth.terrarium.tempad.common.registries.selectedPos
import earth.terrarium.tempad.common.utils.nullableFieldOf
import earth.terrarium.tempad.common.utils.safeLet
import earth.terrarium.tempad.tempadId
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import java.util.*
import kotlin.jvm.optionals.getOrNull

data class SyncPortalSettingsPacket(
    val xOffset: Float,
    val yOffset: Float,
    val zOffset: Float,
    val angle: Float,
    val isVertical: Boolean,
    val providerId: ResourceLocation?,
    val id: UUID?,
    val ctx: ContextHolder<*>,
) : Packet<SyncPortalSettingsPacket> {
    constructor(
        xOffset: Float,
        yOffset: Float,
        zOffset: Float,
        angle: Float,
        isVertical: Boolean,
        providerId: Optional<ResourceLocation>,
        id: Optional<UUID>,
        ctx: ContextHolder<*>,
    ) : this(
        xOffset,
        yOffset,
        zOffset,
        angle,
        isVertical,
        providerId.getOrNull(),
        id.getOrNull(),
        ctx
    )

    companion object : ServerPacketCompanion<SyncPortalSettingsPacket> {
        override val id: ResourceLocation = "sync_portal_settings".tempadId
        override val byteCodec: ByteCodec<SyncPortalSettingsPacket> = ObjectByteCodec.create(
            ByteCodec.FLOAT.fieldOf { it.xOffset },
            ByteCodec.FLOAT.fieldOf { it.yOffset },
            ByteCodec.FLOAT.fieldOf { it.zOffset },
            ByteCodec.FLOAT.fieldOf { it.angle },
            ByteCodec.BOOLEAN.fieldOf { it.isVertical },
            ExtraByteCodecs.RESOURCE_LOCATION.nullableFieldOf { it.providerId },
            ByteCodec.UUID.nullableFieldOf { it.id },
            ContextHolder.codec.fieldOf { it.ctx },
            ::SyncPortalSettingsPacket
        )

        override fun onReceive(message: SyncPortalSettingsPacket, player: Player) {
            message.ctx.getCtx(player).modify {
                it.portalOffset = PortalPlacementComponent(
                    message.xOffset,
                    message.yOffset,
                    message.zOffset,
                    message.angle,
                    message.isVertical
                )
                safeLet(message.providerId, message.id) { provider, id ->
                    val ctx = message.ctx.getCtx(player) as? WorkstationContext ?: return@safeLet
                    it.selectedPos = IndirectLocation(ctx.workstation!!.owner!!, provider, id)
                }
            }
        }
    }

    override fun type(): PacketType<SyncPortalSettingsPacket> = SyncPortalSettingsPacket
}
package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import earth.terrarium.tempad.api.access.ItemAccessAddress
import earth.terrarium.tempad.api.access.WorkstationItemAccess
import earth.terrarium.tempad.api.locations.IndirectLocation
import earth.terrarium.tempad.common.data.PortalPlacementComponent
import earth.terrarium.tempad.common.network.ServerPacketCompanion
import earth.terrarium.tempad.common.registries.ModComponents
import earth.terrarium.tempad.common.registries.owner
import earth.terrarium.tempad.common.utils.commit
import earth.terrarium.tempad.common.utils.exchange
import earth.terrarium.tempad.common.utils.nullableFieldOf
import earth.terrarium.tempad.common.utils.safeLet
import earth.terrarium.tempad.common.utils.transfer
import earth.terrarium.tempad.tempadId
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.Player
import java.util.*
import kotlin.jvm.optionals.getOrNull

data class SyncPortalSettingsPacket(
    val xOffset: Float,
    val yOffset: Float,
    val zOffset: Float,
    val angle: Int,
    val isVertical: Boolean,
    val providerId: Identifier?,
    val id: UUID?,
    val ctx: ItemAccessAddress<*>,
) : Packet<SyncPortalSettingsPacket> {
    constructor(
        xOffset: Float,
        yOffset: Float,
        zOffset: Float,
        angle: Int,
        isVertical: Boolean,
        providerId: Optional<Identifier>,
        id: Optional<UUID>,
        ctx: ItemAccessAddress<*>,
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
        override val id: Identifier = "sync_portal_settings".tempadId
        override val byteCodec: ByteCodec<SyncPortalSettingsPacket> = ObjectByteCodec.create(
            ByteCodec.FLOAT.fieldOf { it.xOffset },
            ByteCodec.FLOAT.fieldOf { it.yOffset },
            ByteCodec.FLOAT.fieldOf { it.zOffset },
            ByteCodec.INT.fieldOf { it.angle },
            ByteCodec.BOOLEAN.fieldOf { it.isVertical },
            ExtraByteCodecs.IDENTIFIER.nullableFieldOf { it.providerId },
            ByteCodec.UUID.nullableFieldOf { it.id },
            ItemAccessAddress.codec.fieldOf { it.ctx },
            ::SyncPortalSettingsPacket
        )

        override fun onReceive(message: SyncPortalSettingsPacket, player: Player) {
            transfer {
                val access = message.ctx.getAccess(player)

                var resource = access.resource

                resource = access.resource
                    .with(ModComponents.portalOffset, PortalPlacementComponent(
                        Math.clamp(message.xOffset, -5f, 5f),
                        Math.clamp(message.yOffset, -5f, 5f),
                        Math.clamp(message.zOffset, -5f, 5f),
                        message.angle,
                        message.isVertical,
                    ))

                safeLet(message.providerId, message.id) { provider, id ->
                    val ctx = access as? WorkstationItemAccess ?: return@safeLet
                    resource = resource.with(ModComponents.selectedPos, IndirectLocation(ctx.workstation!!.owner!!, Component.empty(), provider, id))
                }

                access.exchange(resource, 1)
                commit()
            }
        }
    }

    override fun type(): PacketType<SyncPortalSettingsPacket> = SyncPortalSettingsPacket
}
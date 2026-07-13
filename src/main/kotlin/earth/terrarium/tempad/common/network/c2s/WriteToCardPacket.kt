package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType
import earth.terrarium.tempad.tempadId
import earth.terrarium.tempad.api.locations.TempadLocations
import earth.terrarium.tempad.api.access.ItemAccessAddress
import earth.terrarium.tempad.common.registries.ModComponents
import earth.terrarium.tempad.common.registries.ModItemAccess
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.registries.portalTarget
import earth.terrarium.tempad.common.utils.commit
import earth.terrarium.tempad.common.utils.exchange
import earth.terrarium.tempad.common.utils.stack
import earth.terrarium.tempad.common.utils.transfer
import net.minecraft.resources.Identifier
import net.neoforged.neoforge.transfer.access.ItemAccess
import net.neoforged.neoforge.transfer.item.ItemResource
import java.util.UUID

class WriteToCardPacket(val providerId: Identifier, val locationId: UUID, val ctx: ItemAccessAddress<*>): Packet<WriteToCardPacket> {
    companion object {
        val type = CodecPacketType.Server.create(
            "write_to_card".tempadId,
            ObjectByteCodec.create(
                ExtraByteCodecs.IDENTIFIER.fieldOf { it.providerId },
                ByteCodec.UUID.fieldOf { it.locationId },
                ItemAccessAddress.codec.fieldOf { it.ctx },
                ::WriteToCardPacket
            ),
            NetworkHandle.handle { message, player ->
                if (TempadLocations[message.providerId] == null) return@handle
                val ctx = message.ctx.getAccess(player)
                val menuCtx = ItemAccess.forPlayerCursor(player, player.containerMenu)

                if (!menuCtx.stack.`is`(ModItems.locationCard)) return@handle
                transfer {
                    var newCard = ItemResource.of(ModItems.locationCard)
                    TempadLocations[player, ctx, message.providerId]?.getSerializable(message.locationId)?.let {
                        newCard = newCard.with(ModComponents.portalTarget, it)
                    }
                    menuCtx.exchange(newCard, 1)
                    commit()
                }
            }
        )
    }

    override fun type(): PacketType<WriteToCardPacket> = type
}
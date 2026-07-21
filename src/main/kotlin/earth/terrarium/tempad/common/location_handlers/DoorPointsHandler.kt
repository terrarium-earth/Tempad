package earth.terrarium.tempad.common.location_handlers

import com.mojang.authlib.GameProfile
import com.mojang.serialization.Codec
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.locations.IndirectLocation
import earth.terrarium.tempad.api.locations.LocationGetter
import earth.terrarium.tempad.api.locations.LocationHandler
import earth.terrarium.tempad.api.locations.NamedGlobalVec3
import earth.terrarium.tempad.common.block.timedoor_marker.AbstractMarkerBe
import earth.terrarium.tempad.common.registries.basicDoorPoints
import earth.terrarium.tempad.common.registries.id
import earth.terrarium.tempad.common.registries.steelDoorPoints
import earth.terrarium.tempad.common.utils.get
import earth.terrarium.tempad.common.utils.safeLet
import earth.terrarium.tempad.tempadId
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.core.UUIDUtil
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerLevel
import java.util.*

class DoorPointsData(anchors: Map<UUID, GlobalPos>) {
    companion object {
        val codec = Codec.unboundedMap(UUIDUtil.STRING_CODEC, GlobalPos.CODEC).xmap<DoorPointsData>(::DoorPointsData, DoorPointsData::anchors)
    }

    val anchors = mutableMapOf<UUID, GlobalPos>()

    init {
        this.anchors.putAll(anchors)
    }

    fun getPostions(accessor: GameProfile): Map<UUID, NamedGlobalVec3> {
        return anchors.mapValues { getBlockEntity(it.value) }.filterValues { it != null && it.canAccess(accessor) }.mapValues { it.value!!.namedGlobalVec3 }
    }

    operator fun plusAssign(block: AbstractMarkerBe) {
        if (block.id == null) block.id = UUID.randomUUID()
        safeLet(block.id, block.level) { id, level ->
            anchors[id] = GlobalPos.of(level.dimension(), block.blockPos)
        }
    }

    operator fun minusAssign(locationId: UUID) {
        anchors.remove(locationId)
    }
}

fun getBlockEntity(pos: GlobalPos): AbstractMarkerBe? {
    val level = Tempad.server?.get(pos.dimension())
    return level?.getBlockEntity(pos.pos)?.let { it as? AbstractMarkerBe }
}

class DoorPointsHandler(val gameProfile: GameProfile): LocationHandler {
    override val locations: Map<UUID, NamedGlobalVec3> get() = (basicDoorPoints?.getPostions(gameProfile) ?: mutableMapOf()) + (steelDoorPoints?.getPostions(gameProfile) ?: mutableMapOf())
    override fun minusAssign(locationId: UUID) {}

    override fun getSerializable(locationId: UUID): LocationGetter? {
        val pos = locations[locationId] ?: return null
        return IndirectLocation(gameProfile, Component.translatable("locations.tempad.anchor_point", MutableComponent.create(pos.name.contents).withStyle(
            ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY), ID, locationId)
    }

    override fun calculateCost(
        fromLevel: ServerLevel,
        from: BlockPos,
        to: UUID,
    ): Int {
        if (to in (steelDoorPoints?.getPostions(gameProfile) ?: mutableMapOf())) {
            return (0.75 * super.calculateCost(fromLevel, from, to)).toInt()
        }
        return super.calculateCost(fromLevel, from, to)
    }

    companion object {
        val ID = "markers".tempadId
    }
}
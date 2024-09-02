package earth.terrarium.tempad.common.location_handlers

import com.mojang.authlib.GameProfile
import com.mojang.serialization.Codec
import com.teamresourceful.resourcefullib.common.utils.SaveHandler
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.tempadId
import earth.terrarium.tempad.api.locations.LocationHandler
import earth.terrarium.tempad.api.locations.NamedGlobalVec3
import earth.terrarium.tempad.common.block.SpatialAnchorBE
import earth.terrarium.tempad.common.registries.anchorPoints
import earth.terrarium.tempad.common.registries.posId
import earth.terrarium.tempad.common.utils.get
import earth.terrarium.tempad.common.utils.parse
import net.minecraft.core.GlobalPos
import net.minecraft.core.UUIDUtil
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.world.level.Level
import java.util.*

class AnchorPointsData(anchors: Map<UUID, GlobalPos>) {
    companion object {
        val codec = Codec.unboundedMap(UUIDUtil.STRING_CODEC, GlobalPos.CODEC).xmap<AnchorPointsData>(::AnchorPointsData, AnchorPointsData::anchors)
    }

    val anchors = mutableMapOf<UUID, GlobalPos>()

    init {
        this.anchors.putAll(anchors)
    }

    fun getPostions(accessor: GameProfile): Map<UUID, NamedGlobalVec3> {
        return anchors.mapValues { getBlockEntity(it.value) }.filterValues { it != null }.mapValues { it.value!!.namedGlobalVec3 }
    }

    operator fun plusAssign(block: SpatialAnchorBE) {
        anchors[block.posId!!] = GlobalPos.of(block.level!!.dimension(), block.blockPos)
    }

    operator fun minusAssign(locationId: UUID) {
        anchors.remove(locationId)
    }
}

fun getBlockEntity(pos: GlobalPos): SpatialAnchorBE? {
    val level = Tempad.server?.get(pos.dimension())
    return level?.getBlockEntity(pos.pos)?.let { it as? SpatialAnchorBE }
}

class AnchorPointsHandler(val gameProfile: GameProfile): LocationHandler {
    override val locations: Map<UUID, NamedGlobalVec3> get() = anchorPoints.getPostions(gameProfile)
    override fun minusAssign(locationId: UUID) {}
}
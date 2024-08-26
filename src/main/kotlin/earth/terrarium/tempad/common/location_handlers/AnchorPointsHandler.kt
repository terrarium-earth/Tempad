package earth.terrarium.tempad.common.location_handlers

import com.mojang.serialization.Codec
import com.teamresourceful.resourcefullib.common.utils.SaveHandler
import earth.terrarium.tempad.tempadId
import earth.terrarium.tempad.api.locations.LocationHandler
import earth.terrarium.tempad.api.locations.AnchorPointPos
import earth.terrarium.tempad.common.registries.posId
import earth.terrarium.tempad.common.utils.parse
import net.minecraft.core.UUIDUtil
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import java.util.*

class AnchorPointsData: SaveHandler() {
    companion object {
        val codec = Codec.unboundedMap(UUIDUtil.STRING_CODEC, AnchorPointPos.codec.codec())
        val clientOnly = AnchorPointsData()
        val type = HandlerType.create(clientOnly, ::AnchorPointsData)
    }

    val anchors: Map<UUID, AnchorPointPos>
        field = mutableMapOf()

    override fun loadData(tag: CompoundTag) {
        anchors.clear()
        codec.parse(tag)?.let {
            anchors.putAll(it)
        }
    }

    override fun saveData(tag: CompoundTag) {
        codec.encode(anchors, NbtOps.INSTANCE, tag)
    }

    operator fun plusAssign(pos: AnchorPointPos) {
        val id = UUID.randomUUID()
        anchors[id] = pos
        pos.blockEntity?.posId = id
        setDirty()
    }

    operator fun minusAssign(locationId: UUID) {
        anchors.remove(locationId)
        setDirty()
    }

    operator fun get(locationId: UUID): AnchorPointPos? = anchors[locationId]
}

val Level.anchorPointData get() = SaveHandler.read(this, AnchorPointsData.type, "tempad_anchor_points")

class AnchorPointsHandler(val player: Player): LocationHandler {
    companion object {
        val id = "warps".tempadId
    }

    override val locations: Map<UUID, AnchorPointPos>
        get() = player.level().anchorPointData.anchors

    override fun minusAssign(locationId: UUID) {}
}
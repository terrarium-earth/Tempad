package earth.terrarium.tempad.api.player_access

import com.mojang.authlib.GameProfile
import earth.terrarium.tempad.tempadId
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level
import net.neoforged.neoforge.capabilities.ItemCapability

interface PlayerAccess {
    fun canAccess(level: Level, owner: GameProfile, accessor: GameProfile): Boolean

    companion object {
        val item = ItemCapability.createVoid("player_access".tempadId, PlayerAccess::class.java)
    }
}

object PlayerAccessApi {
    val noAccess: ResourceLocation = "private".tempadId

    val visbility: Map<ResourceLocation, PlayerAccess>
        field = mutableMapOf()

    val ids: List<ResourceLocation> get() = visbility.keys.sorted()

    @JvmStatic
    @JvmName("register")
    operator fun set(id: ResourceLocation, visibility: PlayerAccess) {
        visbility[id] = visibility
    }

    @JvmStatic
    operator fun get(id: ResourceLocation): PlayerAccess? = visbility[id]
}
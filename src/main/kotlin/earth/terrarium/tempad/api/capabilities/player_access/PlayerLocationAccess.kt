package earth.terrarium.tempad.api.capabilities.player_access

import com.mojang.authlib.GameProfile
import earth.terrarium.tempad.tempadId
import net.minecraft.resources.Identifier
import net.minecraft.world.level.Level
import net.neoforged.neoforge.capabilities.ItemCapability
import net.neoforged.neoforge.transfer.access.ItemAccess

// Looking back at this, I truly do not understand what I was thinking when I named this. PlayerAccess is pretty confusing
// given that ItemAccess is a thing and this is not similar in the slightest.
interface PlayerLocationAccess {
    fun canLocate(level: Level, owner: GameProfile, accessor: GameProfile): Boolean

    companion object {
        val item = ItemCapability.create("player_access".tempadId, PlayerLocationAccess::class.java, ItemAccess::class.java)
    }
}

object PlayerAccessApi {
    val noAccess: Identifier = "private".tempadId

    val visbility: Map<Identifier, PlayerLocationAccess>
        field = mutableMapOf()

    val ids: List<Identifier> get() = visbility.keys.sorted()

    @JvmStatic
    @JvmName("register")
    operator fun set(id: Identifier, visibility: PlayerLocationAccess) {
        visbility[id] = visibility
    }

    @JvmStatic
    operator fun get(id: Identifier): PlayerLocationAccess? = visbility[id]
}
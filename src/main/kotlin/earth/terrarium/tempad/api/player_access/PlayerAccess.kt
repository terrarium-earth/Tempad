package earth.terrarium.tempad.api.player_access

import com.mojang.authlib.GameProfile
import earth.terrarium.tempad.tempadId
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.capabilities.ItemCapability

interface PlayerAccess {
    fun canAccess(level: Level, owner: GameProfile, accessor: GameProfile): Boolean

    companion object {
        val item = ItemCapability.createVoid("player_access".tempadId, PlayerAccess::class.java)
    }
}

val ItemStack.playerAccess: PlayerAccess? get() = getCapability(PlayerAccess.item)

object PlayerAccessApi {
    val noAccess: Identifier = "private".tempadId

    val visbility: Map<Identifier, PlayerAccess>
        field = mutableMapOf()

    val ids: List<Identifier> get() = visbility.keys.sorted()

    @JvmStatic
    @JvmName("register")
    operator fun set(id: Identifier, visibility: PlayerAccess) {
        visbility[id] = visibility
    }

    @JvmStatic
    operator fun get(id: Identifier): PlayerAccess? = visbility[id]
}
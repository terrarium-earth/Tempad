package earth.terrarium.tempad.common.compat

import com.mojang.authlib.GameProfile
import dev.ftb.mods.ftbteams.FTBTeamsAPIImpl
import earth.terrarium.tempad.api.player_access.PlayerAccess
import earth.terrarium.tempad.api.player_access.PlayerAccessApi
import earth.terrarium.tempad.tempadId
import net.minecraft.world.level.Level
import kotlin.jvm.optionals.getOrNull

object FTBTeamsCompat : PlayerAccess {
    override fun canAccess(level: Level, owner: GameProfile, accessor: GameProfile): Boolean {
        return FTBTeamsAPIImpl.INSTANCE.manager.getTeamForPlayerID(owner.id).getOrNull()?.members?.contains(accessor.id) == true
    }
}

fun initFTBTeamsAccess() {
    PlayerAccessApi["ftbteams".tempadId] = FTBTeamsCompat
}
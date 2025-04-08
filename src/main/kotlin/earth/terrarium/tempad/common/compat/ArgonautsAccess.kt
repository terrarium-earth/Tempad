package earth.terrarium.tempad.common.compat

import com.mojang.authlib.GameProfile
import earth.terrarium.argonauts.api.teams.guild.GuildApi
import earth.terrarium.argonauts.api.teams.party.PartyApi
import earth.terrarium.tempad.api.player_access.PlayerAccess
import earth.terrarium.tempad.api.player_access.PlayerAccessApi
import earth.terrarium.tempad.tempadId
import net.minecraft.world.level.Level
import kotlin.jvm.optionals.getOrNull

enum class ArgonautsAccess: PlayerAccess {
    Guild {
        override fun canAccess(level: Level, owner: GameProfile, accessor: GameProfile): Boolean {
            return GuildApi.API.getPlayerGuild(level, owner.id).getOrNull()?.let {
                it.isAllied(accessor.id) || it.isMember(accessor.id)
            } == true
        }
    },
    Party {
        override fun canAccess(level: Level, owner: GameProfile, accessor: GameProfile): Boolean {
            return PartyApi.API.getPlayerParty(owner.id).getOrNull()?.isMember(accessor.id) == true
        }
    }
}

fun initArgonautsAccess() {
    PlayerAccessApi["guild".tempadId] = ArgonautsAccess.Guild
    PlayerAccessApi["party".tempadId] = ArgonautsAccess.Party
}
package earth.terrarium.tempad.common.compat

/*
import com.mojang.authlib.GameProfile
import earth.terrarium.odyssey_allies.api.friends.FriendsApi
import earth.terrarium.odyssey_allies.api.teams.guild.GuildApi
import earth.terrarium.odyssey_allies.api.teams.party.PartyApi
import earth.terrarium.tempad.api.capabilities.player_access.PlayerLocationAccess

import earth.terrarium.tempad.api.capabilities.player_access.PlayerAccessApi
import earth.terrarium.tempad.tempadId
import net.minecraft.world.level.Level
import kotlin.jvm.optionals.getOrNull

enum class AlliesLocationAccess: PlayerLocationAccess {
    Guild {
        override fun canLocate(level: Level, owner: GameProfile, accessor: GameProfile): Boolean {
            return GuildApi.API.getPlayerGuild(level, owner.id).getOrNull()?.let {
                it.isAllied(accessor.id) || it.isMember(accessor.id)
            } == true
        }
    },
    Party {
        override fun canLocate(level: Level, owner: GameProfile, accessor: GameProfile): Boolean {
            return PartyApi.API.getPlayerParty(owner.id).getOrNull()?.isMember(accessor.id) == true
        }
    },
    Friends {
        override fun canLocate(level: Level, owner: GameProfile, accessor: GameProfile): Boolean {
            return FriendsApi.API.areFriends(level, owner, accessor)
        }
    }
}

fun initAlliesAccess() {
    PlayerAccessApi["guild".tempadId] = AlliesLocationAccess.Guild
    PlayerAccessApi["party".tempadId] = AlliesLocationAccess.Party
    PlayerAccessApi["friends".tempadId] = AlliesLocationAccess.Friends
}

 */
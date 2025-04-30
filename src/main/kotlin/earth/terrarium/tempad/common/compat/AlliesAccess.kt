package earth.terrarium.tempad.common.compat

import com.mojang.authlib.GameProfile
import earth.terrarium.odyssey_allies.api.friends.FriendsApi
import earth.terrarium.odyssey_allies.api.teams.guild.GuildApi
import earth.terrarium.odyssey_allies.api.teams.party.PartyApi
import earth.terrarium.tempad.api.player_access.PlayerAccess

import earth.terrarium.tempad.api.player_access.PlayerAccessApi
import earth.terrarium.tempad.tempadId
import net.minecraft.world.level.Level
import kotlin.jvm.optionals.getOrNull

enum class AlliesAccess: PlayerAccess {
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
    },
    Friends {
        override fun canAccess(level: Level, owner: GameProfile, accessor: GameProfile): Boolean {
            return FriendsApi.API.areFriends(level, owner, accessor)
        }
    }
}

fun initAlliesAccess() {
    PlayerAccessApi["guild".tempadId] = AlliesAccess.Guild
    PlayerAccessApi["party".tempadId] = AlliesAccess.Party
    PlayerAccessApi["friends".tempadId] = AlliesAccess.Friends
}
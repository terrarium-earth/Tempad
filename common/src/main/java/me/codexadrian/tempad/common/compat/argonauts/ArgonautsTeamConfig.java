package me.codexadrian.tempad.common.compat.argonauts;

import earth.terrarium.argonauts.api.guild.Guild;
import earth.terrarium.argonauts.api.guild.GuildApi;
import earth.terrarium.argonauts.api.party.Party;
import earth.terrarium.argonauts.api.party.PartyApi;
import earth.terrarium.argonauts.common.handlers.base.members.Group;
import me.codexadrian.tempad.api.teams.TeamConfig;
import me.codexadrian.tempad.api.teams.TeamConfigApi;
import me.codexadrian.tempad.common.Tempad;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class ArgonautsTeamConfig {
    public static final ResourceLocation GUILD_ID = new ResourceLocation(Tempad.MODID, "argonauts_guild");
    public static final ResourceLocation PARTY_ID = new ResourceLocation(Tempad.MODID, "argonauts_party");
    public static final ResourceLocation EITHER_ID = new ResourceLocation(Tempad.MODID, "argonauts_all");

    public static final TeamConfig GUILD = (level, owner, member) -> {
        Guild guild = GuildApi.API.getPlayerGuild(level.getServer(), owner);
        return isInTeam(guild, member);
    };

    public static final TeamConfig PARTY = (level, owner, member) -> {
        Party party = PartyApi.API.getPlayerParty(owner);
        return isInTeam(party, member);
    };

    public static final TeamConfig EITHER = (level, owner, member) -> {
        Guild guild = GuildApi.API.getPlayerGuild(level.getServer(), owner);
        Party party = PartyApi.API.getPlayerParty(owner);
        return isInTeam(guild, member) || isInTeam(party, member);
    };

    public static void init() {
        TeamConfigApi.API.register(GUILD_ID, GUILD);
        TeamConfigApi.API.register(PARTY_ID, PARTY);
        TeamConfigApi.API.register(EITHER_ID, EITHER);
    }

    public boolean test(Level level, UUID owner, UUID member) {
        if (owner.equals(member)) return true;

        Guild guild = GuildApi.API.getPlayerGuild(level.getServer(), owner);
        Party party = PartyApi.API.getPlayerParty(owner);
        if (guild != null) {
            if (guild.members().isMember(member) || guild.members().isAllied(member)) {
                return true;
            }
        }
        if (party != null) {
            return party.members().isMember(member) || party.members().isAllied(member);
        }
        return false;
    }

    public static boolean isInTeam(Group<?, ?> group, UUID member) {
        return group != null && (group.members().isMember(member) || group.members().isAllied(member));
    }
}

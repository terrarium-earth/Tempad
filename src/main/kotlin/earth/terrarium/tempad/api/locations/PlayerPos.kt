package earth.terrarium.tempad.api.locations

import com.mojang.authlib.GameProfile
import com.teamresourceful.resourcefullib.common.color.Color
import earth.terrarium.tempad.Tempad
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.common.utils.GAME_PROFILE_BYTE_CODEC
import earth.terrarium.tempad.common.utils.GAME_PROFILE_CODEC

data class PlayerPos(val playerProfile: GameProfile): NamedGlobalPos {
    val player: Player? get() = Tempad.server?.playerList?.getPlayer(playerProfile.id)

    override val color: Color = Tempad.ORANGE

    override val name: Component get() = player?.name ?: Component.literal(playerProfile.name)
    override val pos: Vec3 get() = player?.position() ?: Vec3.ZERO
    override val dimension: ResourceKey<Level> get() = player?.level()?.dimension() ?: Level.OVERWORLD
    override val angle: Float get() = player?.yRot ?: 0f

    override val display: List<Component> get() {
        val player = player ?: return listOf(Component.translatable("tempad.location.player_unavailable"))
        return listOf(
            Component.translatable(player.level().dimension().location().toLanguageKey("dimension")),
            Component.literal("X: ${player.blockX}"),
            Component.literal("Y: ${player.blockY}"),
            Component.literal("Z: ${player.blockZ}")
        )
    }

    override val type: LocationType<*> = Companion.type

    companion object {
        val byteCodec = GAME_PROFILE_BYTE_CODEC.map(::PlayerPos, PlayerPos::playerProfile)
        val codec = GAME_PROFILE_CODEC.xmap(::PlayerPos, PlayerPos::playerProfile)

        val type = LocationType<PlayerPos>("player_pos".tempadId, byteCodec, codec)
    }
}

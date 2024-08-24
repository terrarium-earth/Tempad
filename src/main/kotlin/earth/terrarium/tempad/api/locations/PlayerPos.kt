package earth.terrarium.tempad.api.locations

import com.mojang.authlib.GameProfile
import com.teamresourceful.resourcefullib.common.color.Color
import earth.terrarium.tempad.Tempad
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import earth.terrarium.tempad.tempadId
import earth.terrarium.tempad.common.utils.GAME_PROFILE_BYTE_CODEC
import earth.terrarium.tempad.common.utils.GAME_PROFILE_CODEC
import net.minecraft.world.inventory.tooltip.TooltipComponent

data class PlayerPos(val playerProfile: GameProfile): NamedGlobalPos, TooltipComponent {
    val player: Player? get() = Tempad.server?.playerList?.getPlayer(playerProfile.id)

    override val color: Color = Tempad.ORANGE

    override val name: Component get() = player?.name ?: Component.literal(playerProfile.name)
    override val pos: Vec3 get() = player?.position() ?: Vec3.ZERO
    override val dimension: ResourceKey<Level> get() = player?.level()?.dimension() ?: Level.OVERWORLD
    override val angle: Float get() = player?.yRot ?: 0f

    override val type: LocationType<*> = Companion.type

    override fun consume(player: Player): Component {
        return Component.translatable("location.tempad.player.error")
    }

    companion object {
        val byteCodec = GAME_PROFILE_BYTE_CODEC.map(::PlayerPos, PlayerPos::playerProfile)
        val codec = GAME_PROFILE_CODEC.xmap(::PlayerPos, PlayerPos::playerProfile)

        val type = LocationType<PlayerPos>("player".tempadId, byteCodec, codec)
    }
}

// /give @s tempad:location_card[tempad:card_location={"type":"tempad:player", "id":[1,1,1,1], "name":"CodexAdrian"}]
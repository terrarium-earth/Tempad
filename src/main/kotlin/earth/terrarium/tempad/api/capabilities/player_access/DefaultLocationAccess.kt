package earth.terrarium.tempad.api.capabilities.player_access

import com.mojang.authlib.GameProfile
import net.minecraft.world.level.Level

enum class DefaultLocationAccess: PlayerLocationAccess {
    Public {
        override fun canLocate(level: Level, owner: GameProfile, accessor: GameProfile): Boolean = true
    }
}
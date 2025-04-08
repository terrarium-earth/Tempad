package earth.terrarium.tempad.api.player_access

import com.mojang.authlib.GameProfile
import net.minecraft.world.level.Level

enum class DefaultAccess: PlayerAccess {
    Public {
        override fun canAccess(level: Level, owner: GameProfile, accessor: GameProfile): Boolean = true
    }
}
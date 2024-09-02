package earth.terrarium.tempad.api.visibility

import com.mojang.authlib.GameProfile

enum class DefaultVisbility: Visbility {
    PUBLIC {
        override fun isVisible(owner: GameProfile, accessor: GameProfile): Boolean = true
    },
    PRIVATE {
        override fun isVisible(owner: GameProfile, accessor: GameProfile): Boolean = owner == accessor
    }
}
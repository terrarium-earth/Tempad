package earth.terrarium.tempad.api.visibility

import com.mojang.authlib.GameProfile
import net.minecraft.resources.ResourceLocation

interface Visbility {
    fun isVisible(owner: GameProfile, accessor: GameProfile): Boolean
}

object VisbilityApi {
    val visbility: Map<ResourceLocation, Visbility>
        field = mutableMapOf()

    @JvmStatic
    @JvmName("register")
    operator fun set(id: ResourceLocation, visibility: Visbility) {
        visbility[id] = visibility
    }

    @JvmStatic
    operator fun get(id: ResourceLocation): Visbility = visbility[id] ?: DefaultVisbility.PRIVATE
}
package earth.terrarium.tempad.api.visibility

import com.mojang.authlib.GameProfile
import net.minecraft.resources.ResourceLocation

interface AnchorVisibility {
    fun isVisible(owner: GameProfile, accessor: GameProfile): Boolean
}

object AnchorVisbilityApi {
    val visbility: Map<ResourceLocation, AnchorVisibility>
        field = mutableMapOf()

    fun registerVisibility(id: ResourceLocation, visibility: AnchorVisibility) {
        visbility[id] = visibility
    }
}
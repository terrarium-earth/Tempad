package earth.terrarium.tempad.api.macro

import earth.terrarium.tempad.Tempad.tempadId
import earth.terrarium.tempad.common.data.favoriteLocationData
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

typealias TempadMacro = (player: Player, stack: ItemStack, slot: Int) -> Unit

object MacroRegistry {
    private val macros = mutableMapOf<ResourceLocation, TempadMacro>()

    fun init() {
        registerMacro("open_favorites".tempadId, DEFAULT_MACRO)
    }

    // Open timedoor to favorite location
    private val DEFAULT_MACRO: TempadMacro = { player, _, _ ->
        player.favoriteLocationData?.let {
            TODO("Open timedoor to favorite location")
        }
    }

    fun registerMacro(id: ResourceLocation, macro: TempadMacro) {
        macros[id] = macro
    }

    operator fun get(id: ResourceLocation): TempadMacro? {
        return macros[id]
    }

    fun getOrDefault(id: ResourceLocation): TempadMacro {
        return macros[id] ?: DEFAULT_MACRO
    }
}
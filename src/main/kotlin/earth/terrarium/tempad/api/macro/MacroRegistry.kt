package earth.terrarium.tempad.api.macro

import earth.terrarium.tempad.common.registries.ModMacros
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

typealias TempadMacro = (player: Player, stack: ItemStack, slot: Int) -> Unit
private val macros = mutableMapOf<ResourceLocation, TempadMacro>()

object MacroRegistry: Map<ResourceLocation, TempadMacro> by macros {
    fun register(id: ResourceLocation, macro: TempadMacro) {
        macros[id] = macro
    }

    fun getOrDefault(id: ResourceLocation): TempadMacro {
        return macros[id] ?: ModMacros.DEFAULT_MACRO
    }

    fun getIds(): Set<ResourceLocation> {
        return macros.keys
    }
}
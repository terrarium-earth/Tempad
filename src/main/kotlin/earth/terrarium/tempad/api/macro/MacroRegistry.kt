package earth.terrarium.tempad.api.macro

import earth.terrarium.tempad.api.test.SyncableContext
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player

fun interface TempadMacro {
    fun run(player: Player, ctx: SyncableContext<*>)
}

private val macros = mutableMapOf<ResourceLocation, TempadMacro>()

object MacroRegistry: Map<ResourceLocation, TempadMacro> by macros {
    @JvmName("register")
    operator fun set(id: ResourceLocation, macro: TempadMacro) {
        macros[id] = macro
    }

    fun getIds(): Set<ResourceLocation> {
        return macros.keys
    }
}
package earth.terrarium.tempad.api.macro

import earth.terrarium.tempad.api.context.ContextInstance
import net.minecraft.resources.ResourceLocation

fun interface TempadMacro {
    fun run(ctx: ContextInstance)
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
package earth.terrarium.tempad.api.macro

import earth.terrarium.tempad.api.context.SyncableContext
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.Player

fun interface TempadMacro {
    fun run(player: Player, ctx: SyncableContext<*>)
}

private val macros = mutableMapOf<Identifier, TempadMacro>()

object MacroRegistry: Map<Identifier, TempadMacro> by macros {
    @JvmName("register")
    operator fun set(id: Identifier, macro: TempadMacro) {
        macros[id] = macro
    }

    fun getIds(): Set<Identifier> {
        return macros.keys
    }
}
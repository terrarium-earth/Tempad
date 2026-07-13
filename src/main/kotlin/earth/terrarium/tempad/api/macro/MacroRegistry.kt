package earth.terrarium.tempad.api.macro

import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.transfer.access.ItemAccess

fun interface TempadMacro {
    fun run(player: Player, ctx: ItemAccess)
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
package earth.terrarium.tempad.api.tva_device

import earth.terrarium.tempad.api.ActionType
import earth.terrarium.tempad.api.context.ItemContext
import earth.terrarium.tempad.tempadId
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.ItemCapability

interface ChrononHandler {
    val power: Int
    val maxPower: Int
    val canInsert: Boolean get() = true
    val canExtract: Boolean get() = true

    fun extract(amount: Int, action: ActionType): Int
    fun insert(amount: Int, action: ActionType): Int


    companion object Capabilities {
        val block = BlockCapability.createSided("chronon".tempadId, ChrononHandler::class.java)
        val item = ItemCapability.createVoid("chronon".tempadId, ChrononHandler::class.java)
    }
}

val ChrononHandler.hasRoom get() = insert(1, ActionType.Simulate) == 1

fun ChrononHandler.drainAndExecute(amount: Int, action: () -> Unit) {
    val extracted = this.extract(amount, ActionType.Simulate)
    if (extracted == amount) {
        this.extract(amount, ActionType.Execute)
        action()
    }
}

/**
 * Moves up to [amount] chronons and returns how many moved. Nothing is executed when nothing can move, so handlers
 * that sync on Execute (blocks) don't send updates for no-op transfers.
 */
fun move(from: ChrononHandler, to: ChrononHandler, amount: Int): Int {
    if (amount <= 0) return 0
    val extracted = from.extract(amount, ActionType.Simulate)
    if (extracted <= 0) return 0
    val inserted = to.insert(extracted, ActionType.Execute)
    if (inserted > 0) from.extract(inserted, ActionType.Execute)
    return inserted
}

val BlockEntity.chronons get() = level!!.getCapability(ChrononHandler.Capabilities.block, blockPos, Direction.UP)

val ItemStack.chronons get() = getCapability(ChrononHandler.Capabilities.item)
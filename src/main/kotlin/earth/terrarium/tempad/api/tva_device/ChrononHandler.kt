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

fun move(from: ChrononHandler, to: ChrononHandler, amount: Int) {
    val extracted = from.extract(amount, ActionType.Simulate)
    val inserted = to.insert(extracted, ActionType.Execute)
    from.extract(inserted, ActionType.Execute)
}

val BlockEntity.chronons get() = level!!.getCapability(ChrononHandler.Capabilities.block, blockPos, Direction.UP)

val ItemStack.chronons get() = getCapability(ChrononHandler.Capabilities.item)
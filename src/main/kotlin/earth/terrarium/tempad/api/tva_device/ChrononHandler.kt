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

    fun extract(amount: Int, action: ActionType): Int
    fun insert(amount: Int, action: ActionType): Int

    companion object Capabilities {
        val block = BlockCapability.createSided("chronon".tempadId, ChrononHandler::class.java)
        val item = ItemCapability.createVoid("chronon".tempadId, ChrononHandler::class.java)
    }
}

val ChrononHandler.hasRoom get() = power < maxPower

fun move(from: ChrononHandler, to: ChrononHandler, amount: Int) {
    val extracted = from.extract(amount, ActionType.Simulate)
    val inserted = to.insert(extracted, ActionType.Execute)
    from.extract(inserted, ActionType.Execute)
}

val BlockEntity.chronons get() = level!!.getCapability(ChrononHandler.Capabilities.block, blockPos, Direction.UP)

val ItemStack.chronons get() = getCapability(ChrononHandler.Capabilities.item)
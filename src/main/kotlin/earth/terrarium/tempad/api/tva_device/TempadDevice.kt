package earth.terrarium.tempad.api.tva_device

import earth.terrarium.tempad.api.context.ItemContext
import earth.terrarium.tempad.tempadId
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.ItemCapability

enum class ActionType {
    Execute,
    Simulate
}

interface TempadDevice {
    val power: Int
    val maxPower: Int
    val installedUpgrades: Set<ResourceLocation>

    fun extract(amount: Int, action: ActionType): Int
    fun insert(amount: Int, action: ActionType): Int

    operator fun contains(upgrade: ResourceLocation): Boolean

    fun installUpgrade(upgrade: ResourceLocation, action: ActionType): Boolean

    companion object Capabilities {
        val block = BlockCapability.create<TempadDevice, Void>("tempad_device".tempadId, TempadDevice::class.java, Void::class.java)
        val item = ItemCapability.create("tempad_device".tempadId, TempadDevice::class.java, ItemContext::class.java)
    }
}
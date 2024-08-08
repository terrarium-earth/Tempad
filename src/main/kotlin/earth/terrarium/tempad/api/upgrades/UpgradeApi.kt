package earth.terrarium.tempad.api.upgrades

import earth.terrarium.tempad.api.context.SyncableContext
import earth.terrarium.tempad.common.registries.installedUpgrades
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

object UpgradeApi {
    val registry: Map<ResourceLocation, Item>
        field = mutableMapOf()

    fun register(id: ResourceLocation, item: Item): ResourceLocation {
        registry[id] = item
        return id
    }

    fun get(id: ResourceLocation): Item {
        return registry[id] ?: throw IllegalArgumentException("Upgrade with id $id not found")
    }

    fun get(item: Item): ResourceLocation {
        for ((id, upgrade) in registry) {
            if (item === upgrade) {
                return id
            }
        }
        throw IllegalArgumentException("Upgrade with item $item not found")
    }
}

fun SyncableContext<*>.isInstalled(upgrade: ItemStack): Boolean {
    val upgradeId: ResourceLocation = UpgradeApi.get(upgrade.item)
    return stack.installedUpgrades.upgrades.contains(upgradeId)
}

fun SyncableContext<*>.install(upgrade: ItemStack) {
    val upgradeId: ResourceLocation = UpgradeApi.get(upgrade.item)
    if(!stack.installedUpgrades.upgrades.contains(upgradeId)) {
        stack.installedUpgrades += upgradeId
        upgrade.shrink(1)
    }
}

fun SyncableContext<*>.uninstall(id: ResourceLocation) {
    if(stack.installedUpgrades.upgrades.contains(id)) {
        stack.installedUpgrades -= id
        addStack(UpgradeApi.get(id).defaultInstance)
    }
}
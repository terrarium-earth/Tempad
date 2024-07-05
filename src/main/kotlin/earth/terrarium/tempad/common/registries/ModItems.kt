package earth.terrarium.tempad.common.registries

import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry
import com.teamresourceful.resourcefullibkt.common.createRegistry
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.items.TempadItem
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item

object ModItems {
    val REGISTRY: ResourcefulRegistry<Item> = ResourcefulRegistries.create(BuiltInRegistries.ITEM, Tempad.MOD_ID)

    val TEMPAD: TempadItem by REGISTRY.register("tempad") { TempadItem() }

    val ADVANCED_TEMPAD: TempadItem by REGISTRY.register("advanced_tempad") { TempadItem() }
}
package earth.terrarium.tempad.common.registries

import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.items.TempadItem
import earth.terrarium.tempad.common.items.TimeTwisterItem
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item

object ModItems {
    val registry: ResourcefulRegistry<Item> = ResourcefulRegistries.create(BuiltInRegistries.ITEM, Tempad.MOD_ID)

    val tempad: TempadItem by registry.register("tempad") { TempadItem() }
    val advancedTempad: TempadItem by registry.register("advanced_tempad") { TempadItem() }
    val timeTwister: TimeTwisterItem by registry.register("time_twister") { TimeTwisterItem() }
}
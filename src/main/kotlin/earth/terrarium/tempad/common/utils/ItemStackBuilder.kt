package earth.terrarium.tempad.common.utils

import net.minecraft.core.component.DataComponentPatch
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

fun itemStack(item: Item, count: Int = 1, components: (DataComponentPatch.Builder.() -> Unit)? = null) {
    ItemStack(item, count).also { stack ->
        components?.let { stack.applyComponents(DataComponentPatch.builder().apply(it).build()) }
    }
}

package earth.terrarium.tempad.client.screen

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Style
import net.minecraft.world.item.ItemStack
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator

interface ExtendedWikiScreen {
    val tickers: MutableList<() -> Unit>

    fun handleComponentClicked(style: Style?): Boolean
}
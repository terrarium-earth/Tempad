package earth.terrarium.tempad.client.widgets

import com.teamresourceful.resourcefullib.client.components.selection.ListEntry

abstract class KListEntry: ListEntry() {
    abstract var isFocusedOn: Boolean

    override fun setFocused(pFocused: Boolean) {
        isFocusedOn = pFocused
    }

    override fun isFocused(): Boolean = isFocusedOn
}
package earth.terrarium.tempad.client.state

import net.minecraft.client.gui.components.AbstractWidget

class AppearanceState : MutableState<List<AbstractWidget>>(mutableListOf()) {
    var visible: Boolean = true
        set(value) {
            field = value
            this.value.forEach { it.visible = value }
        }

    var active: Boolean = true
        set(value) {
            field = value
            this.value.forEach { it.active = value }
        }

    operator fun plusAssign(widget: AbstractWidget) {
        value += widget
    }

    override var value: List<AbstractWidget>
        get() = super.value
        set(value) {
            super.value = value
            visible = visible
            active = active
        }
}
package earth.terrarium.tempad.common.network

import kotlin.reflect.KProperty

class ObservableEntry<T>(var value: T, val valueChanged: (prev: T, new: T) -> Unit = { _, _ -> }) {
    operator fun getValue(test: Any, property: KProperty<*>): T {
        return value
    }

    operator fun setValue(test: Any, property: KProperty<*>, value: T) {
        val previous = this.value
        this.value = value
        valueChanged(previous, value)
    }
}
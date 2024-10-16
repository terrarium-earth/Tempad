package earth.terrarium.tempad.client.state

import earth.terrarium.olympus.client.utils.State

open class MutableState<T>(value: T): State<T> {
    var listeners: MutableList<(T) -> Unit> = mutableListOf()

    open var value: T = value
        set(value) {
            field = value
            listeners.forEach { it(value) }
        }

    override fun set(p0: T) {
        value = p0
    }

    override fun get(): T {
        return value
    }

    operator fun getValue(thisRef: Any?, property: Any?): T {
        return value
    }

    operator fun setValue(thisRef: Any?, property: Any?, value: T) {
        this.value = value
    }

    companion object {
        fun <T> of(value: T, vararg listeners: (T) -> Unit): MutableState<T> {
            return MutableState(value).apply {
                this.listeners.addAll(listeners)
            }
        }

        fun <T: Any> of(vararg listeners: (T?) -> Unit): MutableState<T?> {
            return of(null, *listeners)
        }
    }
}
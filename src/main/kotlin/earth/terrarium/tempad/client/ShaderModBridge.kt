package earth.terrarium.tempad.client

import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles

object ShaderModBridge {
    private val SHADERS_ENABLED: MethodHandle?

    init {
        var shadersEnabled: MethodHandle? = null
        try {
            val irisApiClass = Class.forName("net.irisshaders.iris.api.v0.IrisApi")
            val instanceGetter = irisApiClass.getDeclaredMethod("getInstance")
            val irisApiInstance = instanceGetter.invoke(null)
            shadersEnabled = MethodHandles.lookup().unreflect(irisApiClass.getDeclaredMethod("isShaderPackInUse"))
                .bindTo(irisApiInstance)
        } catch (_: NoSuchMethodException) {
        } catch (_: Throwable) {
        }
        SHADERS_ENABLED = shadersEnabled
    }

    val shadersEnabled: Boolean
        get() = SHADERS_ENABLED?.let {
            try {
                it() as Boolean
            } catch (_: Throwable) {
                false
            }
        } ?: false
}
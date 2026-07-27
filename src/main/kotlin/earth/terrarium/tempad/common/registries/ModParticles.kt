package earth.terrarium.tempad.common.registries

import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries
import earth.terrarium.tempad.Tempad
import net.minecraft.core.registries.BuiltInRegistries
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.registries.RegisterEvent

object ModParticles {
    val registry = ResourcefulRegistries.create(BuiltInRegistries.PARTICLE_TYPE, Tempad.MOD_ID)
}
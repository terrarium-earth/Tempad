package earth.terrarium.tempad.common.registries

import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.tempadId
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.sounds.SoundEvent
import com.teamresourceful.resourcefullibkt.common.getValue
import net.minecraft.world.entity.LivingEntity

object ModSounds {
    val registry = ResourcefulRegistries.create(BuiltInRegistries.SOUND_EVENT, Tempad.MOD_ID)

    val timedoorOpen by registry.register("entity.timedoor.open") {
        LivingEntity.createLivingAttributes()
        SoundEvent.createVariableRangeEvent("entity.timedoor.open".tempadId)
    }

    val timedoorEnterMono by registry.register("entity.timedoor.enter.mono") {
        SoundEvent.createVariableRangeEvent("entity.timedoor.enter.mono".tempadId)
    }

    val timedoorEnterStereo by registry.register("entity.timedoor.enter.stereo") {
        SoundEvent.createVariableRangeEvent("entity.timedoor.enter.stereo".tempadId)
    }

    val upgradePlaceMono by registry.register("block.workstation.upgrade.place.mono") {
        SoundEvent.createVariableRangeEvent("block.workstation.upgrade.place.mono".tempadId)
    }

    val upgradePlaceStereo by registry.register("block.workstation.upgrade.place.stereo") {
        SoundEvent.createVariableRangeEvent("block.workstation.upgrade.place.stereo".tempadId)
    }

    val upgradeInstalling by registry.register("block.workstation.upgrade.installing") {
        SoundEvent.createVariableRangeEvent("block.workstation.upgrade.installing".tempadId)
    }
}
package earth.terrarium.tempad.common.compat.jade

import earth.terrarium.tempad.common.entity.TimedoorEntity
import earth.terrarium.tempad.tempadId
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import snownee.jade.api.*
import snownee.jade.api.config.IPluginConfig

@WailaPlugin
class JadePlugin: IWailaPlugin {
    override fun registerClient(registration: IWailaClientRegistration) {
        registration.registerEntityComponent(TimedoorComponentProvider, TimedoorEntity::class.java)
    }
}

object TimedoorComponentProvider: IEntityComponentProvider {
    override fun getUid(): ResourceLocation = "timedoor".tempadId

    override fun appendTooltip(tooltip: ITooltip, accessor: EntityAccessor, config: IPluginConfig) {
        val timedoorEntity = accessor.entity as? TimedoorEntity ?: return
        tooltip.append(Component.literal("Closing in: ${timedoorEntity.closingTime}"))
        tooltip.append(CommonComponents.NARRATION_SEPARATOR)
        tooltip.append(Component.literal("Target Dimension: ${timedoorEntity.targetDimension}"))
        tooltip.append(CommonComponents.NARRATION_SEPARATOR)
        tooltip.append(Component.literal("Target Position: ${timedoorEntity.targetPos}"))
    }
}
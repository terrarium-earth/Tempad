package earth.terrarium.tempad.client.model

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

class BooleanComponentProperty(
    val component: DataComponentType<Boolean>,
    val defaultValue: Boolean,
) : ConditionalItemModelProperty {
    companion object {
        val CODEC: MapCodec<BooleanComponentProperty> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                BuiltInRegistries.DATA_COMPONENT_TYPE.byNameCodec().fieldOf("component").forGetter { it.component },
                Codec.BOOL.optionalFieldOf("default", false).forGetter { it.defaultValue },
            ).apply(instance) { id, def -> BooleanComponentProperty(id as DataComponentType<Boolean>, def) }
        }

        fun of(component: DataComponentType<Boolean>, defaultValue: Boolean = false): BooleanComponentProperty =
            BooleanComponentProperty(component, defaultValue)
    }

    @Suppress("UNCHECKED_CAST")
    override fun get(stack: ItemStack, level: ClientLevel?, entity: LivingEntity?, seed: Int, context: ItemDisplayContext): Boolean {
        return stack.getOrDefault(component, defaultValue)
    }

    override fun type(): MapCodec<out ConditionalItemModelProperty> = CODEC
}
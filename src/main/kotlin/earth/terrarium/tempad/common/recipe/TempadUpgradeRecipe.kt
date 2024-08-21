package earth.terrarium.tempad.common.recipe

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.bytecodecs.StreamCodecByteCodec
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.registries.installedUpgrades
import earth.terrarium.tempad.common.utils.stack
import io.netty.buffer.ByteBuf
import net.minecraft.core.HolderLookup
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.SmithingRecipe
import net.minecraft.world.item.crafting.SmithingRecipeInput
import net.minecraft.world.level.Level

data class TempadUpgradeRecipe(val template: Ingredient, val addition: Ingredient, val output: ResourceLocation): SmithingRecipe {
    companion object Serializer: RecipeSerializer<TempadUpgradeRecipe> {
        val codec: MapCodec<TempadUpgradeRecipe> = RecordCodecBuilder.mapCodec {
            it.group(
                Ingredient.CODEC.fieldOf("template").forGetter(TempadUpgradeRecipe::template),
                Ingredient.CODEC.fieldOf("addition").forGetter(TempadUpgradeRecipe::addition),
                ResourceLocation.CODEC.fieldOf("upgrade").forGetter(TempadUpgradeRecipe::output)
            ).apply(it, ::TempadUpgradeRecipe)
        }

        val byteCodec: StreamCodec<RegistryFriendlyByteBuf, TempadUpgradeRecipe> = StreamCodecByteCodec.toRegistry(ObjectByteCodec.create(
            ExtraByteCodecs.INGREDIENT.fieldOf(TempadUpgradeRecipe::template),
            ExtraByteCodecs.INGREDIENT.fieldOf(TempadUpgradeRecipe::addition),
            ExtraByteCodecs.RESOURCE_LOCATION.fieldOf(TempadUpgradeRecipe::output),
            ::TempadUpgradeRecipe
        ))

        override fun codec(): MapCodec<TempadUpgradeRecipe> = codec
        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, TempadUpgradeRecipe> = byteCodec
    }

    override fun matches(input: SmithingRecipeInput, level: Level): Boolean {
        return input.base.item === ModItems.tempad && template.test(input.template) && addition.test(input.addition)
    }

    override fun assemble(input: SmithingRecipeInput, p_346030_: HolderLookup.Provider): ItemStack {
        if (input.base.item !== ModItems.tempad) return ItemStack.EMPTY
        return input.base.copyWithCount(1).apply {
            installedUpgrades += output
        }
    }

    override fun getResultItem(registries: HolderLookup.Provider): ItemStack {
        return ModItems.tempad.stack {
            installedUpgrades += output
        }
    }

    override fun getSerializer(): RecipeSerializer<*> = Serializer

    override fun isTemplateIngredient(stack: ItemStack): Boolean = template.test(stack)
    override fun isBaseIngredient(stack: ItemStack): Boolean = stack.item === ModItems.tempad
    override fun isAdditionIngredient(stack: ItemStack): Boolean = addition.test(stack)
}


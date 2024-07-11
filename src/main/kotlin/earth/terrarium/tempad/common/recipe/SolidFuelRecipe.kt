package earth.terrarium.tempad.common.recipe

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipe
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipeSerializer
import earth.terrarium.tempad.common.registries.ModRecipes
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.crafting.SingleRecipeInput
import net.minecraft.world.level.Level

class SolidFuelRecipe(val ingredient: Ingredient, val count: Int) : CodecRecipe<SingleRecipeInput> {
    companion object {
        @JvmField
        val CODEC = RecordCodecBuilder.mapCodec {
            it.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(SolidFuelRecipe::ingredient),
                Codec.INT.fieldOf("count").forGetter(SolidFuelRecipe::count)
            ).apply(it, ::SolidFuelRecipe)
        }

        @JvmField
        val BYTE_CODEC = ObjectByteCodec.create(
            ExtraByteCodecs.INGREDIENT.fieldOf(SolidFuelRecipe::ingredient),
            ByteCodec.INT.fieldOf(SolidFuelRecipe::count),
            ::SolidFuelRecipe
        )
    }

    fun matches(input: SingleRecipeInput): Boolean = input.item().count >= count && ingredient.test(input.item())

    override fun matches(input: SingleRecipeInput, level: Level): Boolean = matches(input)

    override fun getType(): RecipeType<*> = ModRecipes.solidFuelType
    override fun serializer(): CodecRecipeSerializer<out CodecRecipe<SingleRecipeInput>> = ModRecipes.solidFuelSerializer
}
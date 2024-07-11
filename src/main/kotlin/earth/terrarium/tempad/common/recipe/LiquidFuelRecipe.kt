package earth.terrarium.tempad.common.recipe

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipe
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipeSerializer
import earth.terrarium.tempad.common.registries.ModRecipes
import earth.terrarium.tempad.common.utils.FLUID_INGREDIENT_BYTE_CODEC
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.neoforged.neoforge.fluids.crafting.FluidIngredient

class LiquidFuelRecipe(val ingredient: FluidIngredient, val amount: Int): CodecRecipe<SingleFluidRecipeInput> {
    companion object {
        val CODEC = RecordCodecBuilder.mapCodec {
            it.group(
                FluidIngredient.CODEC.fieldOf("ingredient").forGetter(LiquidFuelRecipe::ingredient),
                Codec.INT.fieldOf("amount").forGetter(LiquidFuelRecipe::amount)
            ).apply(it, ::LiquidFuelRecipe)
        }

        val BYTE_CODEC = ObjectByteCodec.create(
            FLUID_INGREDIENT_BYTE_CODEC.fieldOf(LiquidFuelRecipe::ingredient),
            ByteCodec.INT.fieldOf(LiquidFuelRecipe::amount),
            ::LiquidFuelRecipe
        )
    }

    fun matches(input: SingleFluidRecipeInput): Boolean = input.stack.amount >= amount && ingredient.test(input.stack)

    override fun matches(input: SingleFluidRecipeInput, level: Level): Boolean = matches(input)

    override fun getType(): RecipeType<LiquidFuelRecipe> = ModRecipes.liquidFuelType

    override fun serializer(): CodecRecipeSerializer<LiquidFuelRecipe> = ModRecipes.liquidFuelSerializer
}
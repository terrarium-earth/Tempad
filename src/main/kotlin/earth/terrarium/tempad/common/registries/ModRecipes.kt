package earth.terrarium.tempad.common.registries

import com.teamresourceful.resourcefullib.common.recipe.CodecRecipeSerializer
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipeType
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.recipe.LiquidFuelRecipe
import earth.terrarium.tempad.common.recipe.SolidFuelRecipe
import net.minecraft.core.registries.BuiltInRegistries

object ModRecipes {
    val serializers = ResourcefulRegistries.create(BuiltInRegistries.RECIPE_SERIALIZER, Tempad.MOD_ID)
    val types = ResourcefulRegistries.create(BuiltInRegistries.RECIPE_TYPE, Tempad.MOD_ID)

    val solidFuelType by types.register("solid_fuel") { CodecRecipeType.of<SolidFuelRecipe>("solid_fuel") }

    val solidFuelSerializer by serializers.register("solid_fuel") {
        CodecRecipeSerializer(
            solidFuelType,
            SolidFuelRecipe.CODEC,
            SolidFuelRecipe.BYTE_CODEC
        )
    }

    val liquidFuelType by types.register("liquid_fuel") { CodecRecipeType.of<LiquidFuelRecipe>("liquid_fuel") }

    val liquidFuelSerializer by serializers.register("liquid_fuel") {
        CodecRecipeSerializer(
            liquidFuelType,
            LiquidFuelRecipe.CODEC,
            LiquidFuelRecipe.BYTE_CODEC
        )
    }
}
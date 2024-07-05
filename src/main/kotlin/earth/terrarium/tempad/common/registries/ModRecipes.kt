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
    val SERIALIZERS = ResourcefulRegistries.create(BuiltInRegistries.RECIPE_SERIALIZER, Tempad.MOD_ID)
    val TYPES = ResourcefulRegistries.create(BuiltInRegistries.RECIPE_TYPE, Tempad.MOD_ID)

    val SOLID_FUEL_TYPE by TYPES.register("solid_fuel") { CodecRecipeType.of<SolidFuelRecipe>("solid_fuel") }

    val SOLID_FUEL_SERIALIZER by SERIALIZERS.register("solid_fuel") {
        CodecRecipeSerializer(
            SOLID_FUEL_TYPE,
            SolidFuelRecipe.CODEC,
            SolidFuelRecipe.BYTE_CODEC
        )
    }

    val LIQUID_FUEL_TYPE by TYPES.register("liquid_fuel") { CodecRecipeType.of<LiquidFuelRecipe>("liquid_fuel") }

    val LIQUID_FUEL_SERIALIZER by SERIALIZERS.register("liquid_fuel") {
        CodecRecipeSerializer(
            LIQUID_FUEL_TYPE,
            LiquidFuelRecipe.CODEC,
            LiquidFuelRecipe.BYTE_CODEC
        )
    }
}
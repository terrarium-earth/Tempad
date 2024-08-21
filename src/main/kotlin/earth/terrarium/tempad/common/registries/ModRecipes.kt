package earth.terrarium.tempad.common.registries

import com.teamresourceful.resourcefullib.common.recipe.CodecRecipeSerializer
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.recipe.TempadUpgradeRecipe
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.crafting.RecipeType

object ModRecipes {
    val serializers = ResourcefulRegistries.create(BuiltInRegistries.RECIPE_SERIALIZER, Tempad.MOD_ID)

    val liquidFuelSerializer by serializers.register("upgrade") { TempadUpgradeRecipe.Serializer }
}
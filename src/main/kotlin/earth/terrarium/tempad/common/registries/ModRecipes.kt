package earth.terrarium.tempad.common.registries

import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.recipe.TempadUpgradeRecipe
import earth.terrarium.tempad.tempadId
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.crafting.RecipeBookCategory
import net.minecraft.world.item.crafting.RecipeType

object ModRecipes {
    val serializers = ResourcefulRegistries.create(BuiltInRegistries.RECIPE_SERIALIZER, Tempad.MOD_ID)
    val recipeTypes = ResourcefulRegistries.create(BuiltInRegistries.RECIPE_TYPE, Tempad.MOD_ID)
    val recipeBooks = ResourcefulRegistries.create(BuiltInRegistries.RECIPE_BOOK_CATEGORY, Tempad.MOD_ID)

    val upgradeSerializer by serializers.register("upgrade") { TempadUpgradeRecipe.serializer }
    val upgradeRecipe by recipeTypes.register("upgrade") { RecipeType.simple<TempadUpgradeRecipe>("upgrade".tempadId) }
    val upgradeRecipeBook by recipeBooks.register("upgrade") { RecipeBookCategory() }

    fun init() {
        serializers.init()
        recipeTypes.init()
    }
}
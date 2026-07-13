package earth.terrarium.tempad.data.server

import earth.terrarium.tempad.common.recipe.TempadUpgradeRecipe
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.tempadId
import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.*
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.Recipe
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.common.conditions.ModLoadedCondition
import net.neoforged.neoforge.common.conditions.NotCondition
import java.util.concurrent.CompletableFuture

class ModRecipeData(output: PackOutput, registries: CompletableFuture<HolderLookup.Provider>) :
    RecipeProvider.Runner(output, registries) {

    override fun getName() = "Tempad Recipes"

    override fun createRecipeProvider(registries: HolderLookup.Provider, output: RecipeOutput): RecipeProvider =
        Provider(registries, output)

    inner class Provider(registries: HolderLookup.Provider, val recipeOutput: RecipeOutput) : RecipeProvider(registries, recipeOutput) {

        // Modded items, from ModItems.name
        val timeSteel = 'T'
        val cell = 'C'
        val battery = 'B'
        val locationBroadcaster = 'L'
        val card = 'p'

        // Vanilla items, from Items.NAME
        val copper = 'c'
        val glass = 'g'
        val tintedGlass = 'G'
        val redstoneLamp = 'R'
        val redstone = 'r'
        val leather = 'l'
        val quartz = 'Q'
        val compass = 'D'
        val button = 'b'
        val amethyst = 'A'
        val emerald = 'e'
        val enderPearl = 'E'
        val ironBlock = 'I'
        val iron = 'i'
        val gold = 'a'
        val blackDye = 'd'
        val paper = 'P'
        val enderChest = 'h'
        val driedKelp = 'k'
        val clock = 'K'
        val netheriteScrap = 's'
        val book = 'o'
        val netherStar = '*'
        val knowledgeProjector = 'H'

        val air = ' '

        // modded
        fun ShapedRecipeBuilder.timeSteel() = define(timeSteel, ModItems.timeSteel)

        fun ShapedRecipeBuilder.cell() = define(cell, ModItems.chrononCell)

        fun ShapedRecipeBuilder.battery() = define(battery, ModItems.chrononBattery)

        fun ShapedRecipeBuilder.card() = define(card, ModItems.locationCard)

        fun ShapedRecipeBuilder.locationBroadcaster() = define(locationBroadcaster, ModItems.locationBroadcaster)

        fun ShapedRecipeBuilder.knowledgeProjector() = define(knowledgeProjector, ModItems.knowledgeProjector)

        //vanilla
        fun ShapedRecipeBuilder.copper() = define(copper, Tags.Items.INGOTS_COPPER)

        fun ShapedRecipeBuilder.glass() = define(glass, Tags.Items.GLASS_BLOCKS_COLORLESS)

        fun ShapedRecipeBuilder.tintedGlass() = define(tintedGlass, Tags.Items.GLASS_BLOCKS_TINTED)

        fun ShapedRecipeBuilder.redstoneLamp() = define(redstoneLamp, Items.REDSTONE_LAMP)

        fun ShapedRecipeBuilder.redstone() = define(redstone, Tags.Items.DUSTS_REDSTONE)

        fun ShapedRecipeBuilder.leather() = define(leather, Tags.Items.LEATHERS)

        fun ShapedRecipeBuilder.quartz() = define(quartz, Tags.Items.GEMS_QUARTZ)

        fun ShapedRecipeBuilder.compass() = define(compass, Items.COMPASS)

        fun ShapedRecipeBuilder.button() = define(button, Items.STONE_BUTTON)

        fun ShapedRecipeBuilder.amethyst() = define(amethyst, Tags.Items.GEMS_AMETHYST)

        fun ShapedRecipeBuilder.emerald() = define(emerald, Tags.Items.GEMS_EMERALD)

        fun ShapedRecipeBuilder.enderPearl() = define(enderPearl, Tags.Items.ENDER_PEARLS)

        fun ShapedRecipeBuilder.ironBlock() = define(ironBlock, Tags.Items.STORAGE_BLOCKS_IRON)

        fun ShapedRecipeBuilder.iron() = define(iron, Tags.Items.INGOTS_IRON)

        fun ShapedRecipeBuilder.gold() = define(gold, Tags.Items.INGOTS_GOLD)

        fun ShapedRecipeBuilder.paper() = define(paper, Items.PAPER)

        fun ShapedRecipeBuilder.blackDye() = define(blackDye, Tags.Items.DYES_BLACK)

        fun ShapedRecipeBuilder.enderChest() = define(enderChest, Items.ENDER_CHEST)

        fun ShapedRecipeBuilder.driedKelp() = define(driedKelp, Items.DRIED_KELP)

        fun ShapedRecipeBuilder.clock() = define(clock, Items.CLOCK)

        fun ShapedRecipeBuilder.netheriteScrap() = define(netheriteScrap, Items.NETHERITE_SCRAP)

        fun ShapedRecipeBuilder.book() = define(book, Items.BOOK)

        fun ShapedRecipeBuilder.netherStar() = define(netherStar, Items.NETHER_STAR)

        fun ShapedRecipeBuilder.pattern(vararg char: Char): ShapedRecipeBuilder {
            pattern(char.joinToString(""))
            return this
        }

        fun ShapedRecipeBuilder.unlockedBy(item: Item): ShapedRecipeBuilder {
            unlockedBy(getHasName(item), has(item))
            return this
        }

        fun RecipeOutput.shaped(item: Item, count: Int = 1, name: String? = null, contents: ShapedRecipeBuilder.() -> Unit) {
            val recipe = this@Provider.shaped(RecipeCategory.MISC, item, count)
            contents(recipe)
            if (name == null) recipe.save(this) else recipe.save(this, name)
        }

        fun RecipeOutput.shapeless(item: Item, count: Int = 1, name: String? = null, contents: ShapelessRecipeBuilder.() -> Unit) {
            val recipe = this@Provider.shapeless(RecipeCategory.MISC, item, count)
            contents(recipe)
            if (name == null) recipe.save(this) else recipe.save(this, name)
        }

        fun RecipeOutput.clean(item: Item) {
            this@Provider.shapeless(RecipeCategory.MISC, item)
                .requires(item)
                .unlockedBy(getHasName(item), has(item))
                .save(this, ResourceKey.create(Registries.RECIPE, (BuiltInRegistries.ITEM.getKey(item).path + "_clean").tempadId))
        }

        fun RecipeOutput.upgrade(item: Item, identifier: Identifier, downloadTime: Int = 80) {
            val upgrade = TempadUpgradeRecipe(
                Ingredient.of(item),
                downloadTime,
                identifier,
            )
            val id = "upgrades/${identifier.path}".tempadId
            val recipeKey: ResourceKey<Recipe<*>> = ResourceKey.create(Registries.RECIPE, id)
            val builder = advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeKey))
                .rewards(AdvancementRewards.Builder.recipe(recipeKey))
                .requirements(AdvancementRequirements.Strategy.OR)
            accept(recipeKey, upgrade, builder.build(id.withPrefix("recipes/" + RecipeCategory.MISC.folderName + "/")))
        }

        override fun buildRecipes() {
            recipeOutput.upgrade(ModItems.playerTeleportUpgrade, ModItems.playerKey)
            recipeOutput.upgrade(ModItems.newLocationUpgrade, ModItems.newLocationKey)
            recipeOutput.upgrade(ModItems.guideUpgrade, ModItems.guideKey)

            recipeOutput.shaped(ModItems.tempad) {
                timeSteel()
                tintedGlass()
                enderPearl()
                quartz()
                redstoneLamp()
                battery()
                unlockedBy(ModItems.timeSteel)
                pattern(tintedGlass, tintedGlass, tintedGlass)
                pattern(quartz, enderPearl, redstoneLamp)
                pattern(timeSteel, battery, timeSteel)
            }

            recipeOutput.shaped(ModItems.timedoorProjector) {
                copper()
                glass()
                cell()
                enderPearl()
                ironBlock()
                unlockedBy(Items.ENDER_PEARL)
                pattern(copper, copper, air)
                pattern(glass, enderPearl, cell)
                pattern(ironBlock, ironBlock, ironBlock)
            }

            recipeOutput.shaped(ModItems.timedoorMarker) {
                iron()
                ironBlock()
                glass()
                enderPearl()
                unlockedBy(Items.ENDER_PEARL)
                pattern(iron, glass, iron)
                pattern(iron, enderPearl, iron)
                pattern(iron, ironBlock, iron)
            }

            recipeOutput.shaped(ModItems.locationBroadcaster) {
                glass()
                iron()
                emerald()
                redstone()
                compass()
                unlockedBy(Items.COMPASS)
                pattern(air, glass, air)
                pattern(emerald, compass, redstone)
                pattern(air, iron, air)
            }

            recipeOutput.shaped(ModItems.timeTwister) {
                timeSteel()
                enderPearl()
                tintedGlass()
                battery()
                unlockedBy(ModItems.timeSteel)
                pattern(timeSteel, tintedGlass, air)
                pattern(battery, enderPearl, tintedGlass)
                pattern(timeSteel, tintedGlass, air)
            }

            recipeOutput.shaped(ModItems.workstation) {
                tintedGlass()
                copper()
                quartz()
                timeSteel()
                unlockedBy(ModItems.timeSteel)
                pattern(tintedGlass, tintedGlass, air)
                pattern(copper, copper, copper)
                pattern(timeSteel, quartz, timeSteel)
            }

            recipeOutput.shaped(ModItems.chronomark) {
                iron()
                timeSteel()
                tintedGlass()
                ironBlock()
                enderPearl()
                unlockedBy(ModItems.timeSteel)
                pattern(iron, tintedGlass, iron)
                pattern(timeSteel, enderPearl, timeSteel)
                pattern(iron, ironBlock, iron)
            }

            recipeOutput.shaped(ModItems.chrononCell) {
                iron()
                copper()
                amethyst()
                redstone()
                unlockedBy(Items.AMETHYST_SHARD)
                pattern(iron, copper, iron)
                pattern(redstone, amethyst, redstone)
                pattern(iron, copper, iron)
            }

            recipeOutput.shaped(ModItems.chrononBattery) {
                iron()
                timeSteel()
                amethyst()
                cell()
                unlockedBy(ModItems.timeSteel)
                pattern(iron, timeSteel, iron)
                pattern(cell, amethyst, cell)
                pattern(iron, timeSteel, iron)
            }

            recipeOutput.shaped(ModItems.chronometer) {
                tintedGlass()
                timeSteel()
                clock()
                battery()
                unlockedBy(ModItems.timeSteel)
                pattern(air, tintedGlass, air)
                pattern(timeSteel, clock, timeSteel)
                pattern(air, battery, air)
            }

            recipeOutput.shaped(ModItems.chrononGenerator) {
                glass()
                iron()
                clock()
                cell()
                unlockedBy(Items.AMETHYST_SHARD)
                pattern(air, glass, air)
                pattern(iron, clock, iron)
                pattern(air, cell, air)
            }

            recipeOutput.shaped(ModItems.screeningDevice) {
                tintedGlass()
                timeSteel()
                emerald()
                redstone()
                compass()
                unlockedBy(ModItems.timeSteel)
                pattern(air, tintedGlass, air)
                pattern(emerald, compass, redstone)
                pattern(air, timeSteel, air)
            }

            recipeOutput.shaped(ModItems.metronome) {
                timeSteel()
                enderChest()
                battery()
                unlockedBy(ModItems.timeSteel)
                pattern(timeSteel, enderChest, timeSteel)
                pattern(timeSteel, battery, timeSteel)
                pattern(timeSteel, battery, timeSteel)
            }

            recipeOutput.shaped(ModItems.cardWallet) {
                copper()
                leather()
                card()
                unlockedBy(Items.COPPER_INGOT)
                pattern(copper, air, copper)
                pattern(leather, card, leather)
                pattern(copper, copper, copper)
            }

            recipeOutput.shaped(ModItems.playerTeleportUpgrade) {
                driedKelp()
                enderPearl()
                locationBroadcaster()
                unlockedBy(Items.DRIED_KELP)
                pattern(driedKelp, driedKelp, driedKelp)
                pattern(enderPearl, locationBroadcaster, enderPearl)
                pattern(driedKelp, driedKelp, driedKelp)
            }

            recipeOutput.shaped(ModItems.newLocationUpgrade) {
                driedKelp()
                enderPearl()
                netherStar()
                unlockedBy(Items.DRIED_KELP)
                pattern(driedKelp, driedKelp, driedKelp)
                pattern(enderPearl, netherStar, enderPearl)
                pattern(driedKelp, driedKelp, driedKelp)
            }

            recipeOutput.shaped(ModItems.guideUpgrade) {
                driedKelp()
                book()
                knowledgeProjector()
                unlockedBy(Items.DRIED_KELP)
                pattern(driedKelp, driedKelp, driedKelp)
                pattern(book, knowledgeProjector, book)
                pattern(driedKelp, driedKelp, driedKelp)
            }

            recipeOutput.shaped(ModItems.knowledgeProjector) {
                book()
                glass()
                copper()
                unlockedBy(Items.COPPER_INGOT)
                pattern(glass)
                pattern(book)
                pattern(copper)
            }

            recipeOutput.clean(ModItems.locationCard)
            recipeOutput.clean(ModItems.timedoorMarker)
            recipeOutput.clean(ModItems.chronomark)

            recipeOutput.shapeless(ModItems.locationCard, 4, "tempad:card_cheap") {
                requires(Tags.Items.INGOTS_IRON)
                requires(Tags.Items.DYES)
                unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
            }

            recipeOutput.shapeless(ModItems.locationCard, 16, "tempad:card_expensive") {
                requires(ModItems.timeSteel)
                requires(Tags.Items.DYES)
                unlockedBy(getHasName(ModItems.timeSteel), has(ModItems.timeSteel))
            }

            recipeOutput.withConditions(NotCondition(ModLoadedCondition("create"))).shapeless(ModItems.timeSteel, 2, "tempad:time_steel_shapeless") {
                requires(Items.IRON_INGOT)
                requires(Items.NETHERITE_SCRAP)
                requires(Items.NETHERITE_SCRAP)
                requires(Items.IRON_INGOT)
                unlockedBy(getHasName(Items.NETHERITE_SCRAP), has(Items.NETHERITE_SCRAP))
            }

            recipeOutput.withConditions(ModLoadedCondition("create")).shaped(ModItems.timeSteel, 2, "tempad:time_steel_shaped") {
                iron()
                netheriteScrap()
                unlockedBy(getHasName(Items.NETHERITE_SCRAP), has(Items.NETHERITE_SCRAP))
                pattern(netheriteScrap, iron)
                pattern(iron, netheriteScrap)
            }
        }
    }
}

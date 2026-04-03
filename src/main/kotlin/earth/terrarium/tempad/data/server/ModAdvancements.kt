package earth.terrarium.tempad.data.server

import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.tempadId
import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementHolder
import net.minecraft.advancements.AdvancementType
import net.minecraft.advancements.critereon.InventoryChangeTrigger
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.network.chat.Component
import net.neoforged.neoforge.common.data.AdvancementProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

class ModAdvancements(
    output: PackOutput,
    registries: CompletableFuture<HolderLookup.Provider>, existingFileHelper: ExistingFileHelper,
) : AdvancementProvider(output, registries, existingFileHelper, listOf(Companion)) {

    companion object : AdvancementGenerator {
        override fun generate(
            registries: HolderLookup.Provider,
            saver: Consumer<AdvancementHolder?>,
            existingFileHelper: ExistingFileHelper,
        ) {
            // For all time. Always!
            saver.accept(
                Advancement.Builder
                    .advancement()
                    .display(
                        ModItems.tempad,
                        Component.translatable("advancements.tempad.tempad.title"),
                        Component.translatable("advancements.tempad.tempad.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                    )
                    .addCriterion("has_tempad", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.tempad))
                    .build("tempad".tempadId)
            )

            // Time moves differently here
            saver.accept(
                Advancement.Builder
                    .advancement()
                    .display(
                        ModItems.timeTwister,
                        Component.translatable("advancements.tempad.time_twister.title"),
                        Component.translatable("advancements.tempad.time_twister.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                    )
                    .addCriterion(
                        "has_time_twister",
                        InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.timeTwister)
                    )
                    .build("time_twister".tempadId)
            )

            // Temporal Marvels
            saver.accept(
                Advancement.Builder
                    .advancement()
                    .display(
                        ModItems.timeTwister,
                        Component.translatable("advancements.tempad.timedoor_projector.title"),
                        Component.translatable("advancements.tempad.timedoor_projector.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                    )
                    .addCriterion(
                        "has_timedoor_projector",
                        InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.timeTwister)
                    )
                    .build("timedoor_projector".tempadId)
            )

            // Friends beyond time
            saver.accept(
                Advancement.Builder
                    .advancement()
                    .display(
                        ModItems.timeTwister,
                        Component.translatable("advancements.tempad.screening_device.title"),
                        Component.translatable("advancements.tempad.screening_device.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                    )
                    .addCriterion(
                        "has_screening_device",
                        InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.timeTwister)
                    )
                    .build("screening_device".tempadId)
            )
        }
    }
}

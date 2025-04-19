package earth.terrarium.tempad.data.client

import com.teamresourceful.resourcefullibkt.common.id
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.config.CommonConfigCache
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.ModItems
import net.minecraft.data.PackOutput
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.common.data.LanguageProvider
import java.text.NumberFormat

class ModLang(output: PackOutput) : LanguageProvider(output, Tempad.MOD_ID, "en_us") {
    companion object {
        private val entries = mutableMapOf<String, String>()

        val title = bookLang("title", "Knowledge Projector")
        val credits = bookLang("credits", "By CodexAdrian & Robonebi")
        val travelAdvisory = bookLang(
            "travel_advisory",
            "For agents dispatched to MC-21 or adjacent realms:\n" + "\n" + "While the technology developed by the TVA is the most advanced in the multiverse, it is not capable of the impossible. Just as time doors cannot be opened to different points of time within the TVA, they cannot be opened to different points of time in MC-21 adjacent realms. Much like the TVA, time passes differently in MC-21 and all related branches, and as such, the TVA's technology is not capable of connecting different points in time within these locations. Spatial travel remains unaffected.\n"
        )
        val iron = bookLang("iron", "Iron")
        val steel = bookLang("steel", "Steel")

        // Overview
        val overview = bookLang("overview", "Introduction")
        val crafting = bookLang("crafting", "Crafting")
        val usage = bookLang("usage", "Usage")

        //region chronon cell
        /**
         * Chronon Cells
         *
         * Chronons are essential fuel for TVA multiversal technology, thus the first step to recreating this technology
         * is generating and storing Chronons. Primitive Chronon Cells can store Chronons and be used as materials for more advanced technology.
         *
         * Crafting
         * Chronon Cells can be created with materials on hand, such as Iron Ingots, Copper Ingots, Redstone Dust, and Amethyst Shards.
         *
         * {Crafting Grid}
         *
         * Usage
         * Empty Chronon Cells can be charged slowly over time if carried alongside a Chronon Generator or a Chronometer,
         * or they can be placed inside a Metronome. Charged Chronon Cells will transfer their stored Chronons to all carried
         * devices automatically, so long as they are all present in the main inventory or curio slots. Chronon Cells hold {X} Chronons.
         */
        val cellOverview = bookLang(
            "cell_overview",
            "Chronons are essential fuel for TVA multiversal technology, thus the first step to recreating this technology " +
                    "is generating and storing Chronons. Primitive Chronon Cells can store Chronons and be used as materials for more advanced technology."
        )

        val cellCrafting = bookLang(
            "cell_crafting",
            "Chronon Cells can be created with materials on hand, such as Iron Ingots, " +
                    "Copper Ingots, Redstone Dust, and Amethyst Shards."
        )

        val cellUsage
            get() = bookLang(
                "cell_usage",
                "Empty Chronon Cells can be charged slowly over time if carried alongside a %s or a %s, " +
                        "or they can be placed inside a %s. Charged Chronon Cells will transfer their stored Chronons to all carried " +
                        "devices automatically, so long as they are all present in the main inventory or curio slots. Chronon Cells hold {X} Chronons.",
                ModItems.chrononGenerator,
                ModItems.chronometer,
                ModItems.metronome,
                CommonConfigCache.ChrononCell.capacity
            )
        //endregion

        // region chronon generator
        /**
         * Chronon Generators
         *
         * A Chronon Generator is a primitive device capable of generating Chronons for use with other devices.
         *
         * Crafting
         * Chronon Generators can be created from any basic Clock, along with a block of Glass, Iron Ingots, and a Chronon Cell.
         *
         * {Crafting Grid}
         *
         * Usage
         * Kept in the main inventory or a curio slot, a Chronon Generator or Chronometer will invert the temporal decay of
         * local background radiation to synthesize Chronons, which are then distributed to all carried devices or storage
         * cells automatically, so long as they are all present in the main inventory or curio slots.
         *
         * {If internal storage enabled}
         * Chronon Generators have their own internal buffer of storage capable of holding {X} Chronons. This buffer can
         * function as a battery for fast-charging other devices.
         *
         */
        val chrononGenOverview
            get() = bookLang(
                "chronongen_overview",
                "A Chronon Generator is a primitive device capable of generating Chronons for use with other devices."
            )

        val chrononGenCrafting
            get() = bookLang(
                "chronongen_crafting",
                "Chronon Generators can be created from any basic Clock, along with a block of Glass, Iron Ingots, and a %s",
                ModItems.chrononCell
            )

        val chrononGenUsage
            get() = bookLang(
                "chronongen_usage",
                "Kept in the main inventory or a curio slot, a Chronon Generator or %s will invert the temporal decay " +
                        "of local background radiation to synthesize Chronons, which are then distributed to all carried devices " +
                        "or storage cells automatically, so long as they are all present in the main inventory or curio slots.",
                ModItems.chronometer,
            )

        val chrononGenStorage
            get() = bookLang(
                "chronon_generator_storage",
                "Chronon Generators have their own internal buffer of storage capable of holding %s Chronons. This buffer can " +
                        "function as a battery for fast-charging other devices.",
                CommonConfigCache.ChrononGenerator.capacity.format()
            )

        //endregion

        //region location card
        /**
         * Location Cards
         *
         * Location Cards record spatial coordinates for use in multiversal devices.
         *
         * Crafting
         * Location Cards are created from Iron Ingots and any Dye. The recipe produces 4 Location Cards. Alternatively
         * one may substitute the iron for an ingot of Time Steel and receive 16 cards from the same recipe.
         *
         * {Crafting Grid}
         *
         * Usage
         * To write location data to a card, a Timedoor Marker or Chronomark is required at the correct coordinates or a
         * standard issue Tempad with the coordinates already saved inside. Right-clicking the card onto a Timedoor Marker
         * or Chronomark will link the card to the device. Right-clicking the card onto the information panel for a location
         * saved inside the Tempad will write the location data to the card.
         *
         * Written cards can be used to supply location data to Timedoor Projectors and Tempads. Cards obtained from other
         * players only function with the Tempad if placed into a carried Card Wallet, however all cards can be freely
         * used with the Timedoor Projector.
         *
         * Placing a written Location Card into the crafting grid will wipe the data stored on the card allowing it to be reused.
         *
         */

        val cardOverview =
            bookLang("card_overview", "Location Cards record spatial coordinates for use in multiversal devices.")

        val cardCrafting
            get() = bookLang(
                "card_crafting",
                "Location Cards are created from Iron Ingots and any Dye. The recipe produces 4 Location Cards. Alternatively " +
                        "one may substitute the iron for an ingot of %s and receive 16 cards from the same recipe.",
                ModItems.timeSteel
            )

        val cardUsage
            get() = bookLang(
                "card_usage",
                "To write location data to a card, a %s or %s is required at the correct coordinates or a " +
                        "standard issue %s with the coordinates already saved inside. Right-clicking the card onto a %s " +
                        "or %s will link the card to the device. Right-clicking the card onto the information panel for a location " +
                        "saved inside the %s will write the location data to the card.",
                ModItems.timedoorMarker,
                ModItems.chronomark,
                ModItems.tempad,
                ModItems.timedoorMarker,
                ModItems.chronomark,
                ModItems.tempad
            )

        val cardUsage2
            get() = bookLang(
                "card_usage_2",
                "Written cards can be used to supply location data to a %s or a %s. Cards obtained from other " +
                        "players only function with the %s if placed into a carried %s, however all cards can be freely " +
                        "used with the %s.",
                ModItems.timedoorProjector,
                ModItems.tempad,
                ModItems.tempad,
                ModItems.cardWallet,
                ModItems.timedoorProjector,
            )

        val cardUsage3 = bookLang(
            "card_usage_3",
            "Placing a written Location Card into the crafting grid will wipe the data stored on the card allowing it to be reused."
        )

        //endregion

        //region card wallet
        /**
         * Card Wallets are a convenient way of storing and carrying a large amount of Location Cards.
         *
         * Crafting
         * Card Wallets can be crafted with Copper Ingots, Leather, and a blank Location Card.
         *
         * {Crafting Grid}
         *
         * Usage
         * Right click the wallet in hand to open its storage. Location Cards may be placed in the storage for safe-keeping
         * to keep the inventory from becoming too cluttered.
         *
         * Once the user obtains a standard Tempad, Cards inside the wallet are automatically added to the Tempad’s location
         * list if they are placed inside a carried wallet. Note that this is unnecessary for Timedoor Markers or Chronomarks
         * owned by the user, as these devices are already added to the user’s Tempad. What this does enable, however, is the
         * continued sharing of location cards between users, and being able to access these shared cards via the Tempad’s interface.
         *
         * Only one Card Wallet can provide locations to the user’s Tempad at a given time. Priority is placed on the curio slot if available,
         * then hotbar slots from left to right, then inventory slots from left to right, top to bottom.
         */
        val walletOverview = bookLang(
            "wallet_overview",
            "Card Wallets are a convenient way of storing and carrying a large amount of Location Cards."
        )
        val walletCrafting
            get() = bookLang(
                "wallet_crafting",
                "Card Wallets can be crafted with Copper Ingots, Leather, and a blank %s.",
                ModItems.locationCard
            )

        val walletUsage
            get() = bookLang(
                "wallet_usage",
                "Right click the wallet in hand to open its storage. %ss may be placed in the storage for safe-keeping " +
                        "to keep the inventory from becoming too cluttered.",
                ModItems.locationCard
            )

        val walletUsage2
            get() = bookLang(
                "wallet_usage_2",
                "Once the user obtains a standard %s, Cards inside the wallet are automatically added to the %s’s location " +
                        "list if they are placed inside a carried wallet. Note that this is unnecessary for a %s or a %s " +
                        "owned by the user, as these devices are already added to the user’s %s. What this does enable, however, is the " +
                        "continued sharing of location cards between users, and being able to access these shared cards via the %s’s interface.",
                ModItems.tempad, ModItems.tempad, ModItems.timedoorMarker, ModItems.chronomark, ModItems.tempad,
                ModItems.tempad
            )

        val walletUsage3
            get() = bookLang(
                "wallet_usage_3",
                "Only one Card Wallet can provide locations to the user’s %s at a given time. Priority is placed on the curio slot if available, " +
                        "then hotbar slots from left to right, then inventory slots from left to right, top to bottom.",
                ModItems.tempad
            )
        //endregion

        //region timedoor marker
        /**
         * Timedoor Markers
         *
         * The Timedoor Marker is used to generate exact spatial coordinates and provide them to other devices.
         *
         * Crafting
         * They are crafted from a Block of Iron, Iron Ingots, an Ender Pearl, and a block of Glass.
         *
         * {Crafting Grid}
         *
         * Usage
         * To write data to a Location Card, place the Timedoor Marker and right-click it with a Location Card.
         * The card will then be written and can be used with Timedoor Projector. The Tempad displays Timedoor Markers
         * and Chronomarks owned by the user automatically, but Location Cards linked to devices can be shared with others.
         *
         * Right-clicking the device with an empty hand brings up an interface where device name, Timedoor color, and security options can be configured.
         */
        val markerOverview = bookLang(
            "marker_overview",
            "The Timedoor Marker is used to generate exact spatial coordinates and provide them to other devices."
        )

        val markerCrafting = bookLang(
            "marker_crafting",
            "They are crafted from a Block of Iron, Iron Ingots, an Ender Pearl, and a block of Glass."
        )

        val markerUsage
            get() = bookLang(
                "marker_usage",
                "To write data to a %s, place the %s and right-click it with a %s. The card will then be written and can be used with the %s. " +
                        "The %s displays %ss and %ss owned by the user automatically, but %ss linked to devices can be shared with others.",
                ModItems.locationCard, ModItems.timedoorMarker, ModItems.locationCard, ModItems.timedoorProjector,
                ModItems.tempad, ModItems.timedoorMarker, ModItems.chronomark, ModItems.locationCard
            )

        val markerUsage2
            get() = bookLang(
                "marker_usage_2",
                "Right-clicking the device with an empty hand brings up an interface where device name, Timedoor color, and security options can be configured."
            )
        //endregion

        //region timedoor projector
        /**
         * Timedoor Projectors
         *
         * When a standard issue Tempad is unavailable, rudimentary versions of the device can be crafted using materials on hand.
         * The Timedoor Projector is the most basic device capable of allowing teleportation via Timedoors.
         *
         * Crafting
         * Timedoor Projectors are crafted from Copper Ingots, a Glass block, an Ender Pearl, a Chronon Cell and Blocks of Iron.
         *
         * {Crafting Grid}
         *
         * Usage
         * Timedoor Projectors allow for the creation of Timedoors to spatial coordinates read from Location Cards.
         * These can be loaded into the device by right-clicking the card onto the Timedoor Projector while it is either in the inventory or placed in the world,
         * or by right-clicking a card in the inventory while holding the projector with the cursor. Cards can be retrieved from the device by either right-clicking
         * it on an empty inventory space, or by right-clicking the device where it is placed in the world.
         *
         * The device can be activated while held by using it (right-click). Holding crouch and using the device will place it on the block being looked at.
         * Placing the Timedoor Projector allows it to be triggered by a Redstone signal instead, such as from an adjacent button.
         *
         * Timedoors
         * Timedoors are doorways between two points in space and/or time. They appear as glowing panels of glass, and can be passed through in either direction like any other doorway.
         *
         * Timedoors consume Chronons from the Timedoor Projector upon generation and appear for a set amount of time before closing automatically.
         */
        val projectorOverview get() = bookLang(
            "projector_overview",
            "When a standard issue %s is unavailable, rudimentary versions of the device can be crafted using materials on hand. " +
                    "The Timedoor Projector is the most basic device capable of allowing teleportation via Timedoors.",
            ModItems.tempad
        )

        val projectorCrafting get() = bookLang(
            "projector_crafting",
            "Timedoor Projectors are crafted from Copper Ingots, a Glass block, an Ender Pearl, a %s and Blocks of Iron.",
            ModItems.chrononCell
        )

        val projectorUsage get() = bookLang(
            "projector_usage",
            "Timedoor Projectors allow for the creation of Timedoors to spatial coordinates read from %ss. These can be loaded into the device by right-clicking the card onto the %s while it is either in the inventory or placed in the world, " +
                    "or by right-clicking a card in the inventory while holding the projector with the cursor. Cards can be retrieved from the device by either right-clicking it on an empty inventory space, or by right-clicking the device where it is placed in the world.",
            ModItems.locationCard, ModItems.timedoorProjector
        )

        val projectorUsage2 get() = bookLang(
            "projector_usage_2",
            "The device can be activated while held by using it (right-click). Holding crouch and using the device will place it on the block being looked at. " +
                    "Placing the %s allows it to be triggered by a Redstone signal instead, such as from an adjacent button.",
            ModItems.timedoorProjector
        )

        val projectorTimedoors = bookLang(
            "projector_timedoors",
            "Timedoors are doorways between two points in space and/or time. They appear as glowing panels of glass, and can be passed through in either direction like any other doorway."
        )

        val projectorTimedoors2 get() = bookLang(
            "projector_timedoors_2",
            "Timedoors consume Chronons from the %s upon generation and appear for a set amount of time before closing automatically.",
            ModItems.timedoorProjector
        )

        //endregion

        //region location broadcaster
        /**
         * Location Broadcasters
         *
         * Location Broadcasters allow lost agents to be found and rescued by agents with specially upgraded Tempads.
         *
         * Crafting
         * Location Broadcasters are crafted with a block of Glass, an Emerald, a measure of Redstone Dust, a Compass, and an Iron Ingot.
         *
         * {Crafting Grid}
         *
         * Usage
         * If a Location Broadcaster is carried in the inventory or curio slot and is turned on, the carrier will appear in the list of possible teleports for Tempads with the Teleport to Players upgrade.
         *
         * The device can be used in the hand to toggle between its modes of operation. The screen on the device will glow green if teleportation is enabled, and will glow red if disabled. This allows the carrier to decide when they are open to Timedoors being opened to their location.
         */
        val locationBroadcastersOverview get() = bookLang("location_broadcasters_overview",
            "The %s allow lost agents to be found and rescued by agents with specially upgraded %s.",
            ModItems.locationBroadcaster, ModItems.tempad
        )

        val locationBroadcastersCrafting get() = bookLang("location_broadcasters_crafting",
            "%s are crafted with a block of Glass, an Emerald, a measure of Redstone Dust, a Compass, and an Iron Ingot.",
            ModItems.locationBroadcaster
        )

        val locationBroadcastersUsage get() = bookLang("location_broadcasters_usage",
            "If a %s is carried in the inventory or curio slot and is turned on, the carrier will appear in the list of possible teleports for %s with the Teleport to Players upgrade.",
            ModItems.locationBroadcaster, ModItems.tempad
        )

        val locationBroadcastersUsage2 get() = bookLang("location_broadcasters_toggle",
            "The device can be used in the hand to toggle between its modes of operation. The screen on the device will glow green if teleportation is enabled, and will glow red if disabled. This allows the carrier to decide when they are open to Timedoors being opened to their location.",
            ModItems.locationBroadcaster
        )
        //endregion

        //region time steel
        /**
         * Time Steel
         *
         * Time Steel is a crucial component to all advanced multiversal technology, as it is not affected by the passage of time.
         * Thus it is a reliable material to house and shield all of the sensitive components required for traversing space and time.
         *
         * Crafting
         * Time Steel can be acquired in MC-21 and adjacent realms by crafting Netherite Scraps with Iron Ingots.
         * Raw Iron to refine can be found in ore almost anywhere. Acquiring the scraps of Netherite, on the other hand, is contingent on access to a Nether dimension.
         * It is inadvisable to get stuck on a timeline without access to these resources, as developing multiversal technology without it can lead to unstable results.
         *
         * {Crafting Grid}
         *
         * Depending on the technology available in a given timeline, alternate or more efficient methods to produce Time Steel may be discovered.
         * Recipe repositories may be checked for more details.
         */
        val timeSteelOverview = bookLang("time_steel_overview",
            "Time Steel is a crucial component to all advanced multiversal technology, as it is not affected by the passage of time. " +
                    "Thus it is a reliable material to house and shield all of the sensitive components required for traversing space and time."
        )

        val timeSteelCrafting = bookLang("time_steel_crafting",
            "Time Steel can be acquired in MC-21 and adjacent realms by crafting Netherite Scraps with Iron Ingots. " +
                    "Raw Iron to refine can be found in ore almost anywhere. Acquiring the scraps of Netherite, on the other hand, is contingent on access to a Nether dimension. " +
                    "It is inadvisable to get stuck on a timeline without access to these resources, as developing multiversal technology without it can lead to unstable results."
        )

        val timeSteelCrafting2 = bookLang("time_steel_tech_discovery",
            "Depending on the technology available in a given timeline, alternate or more efficient methods to produce Time Steel may be discovered. " +
            "Recipe repositories may be checked for more details."
        )
        //endregion

        //region chronon battery
        /**
         * Chronon Batteries
         *
         * Chronon Batteries are devices for storing Chronons made from smaller cells. They hold more Chronons than individual Chronon Cells.
         *
         * Crafting
         * Chronon Batteries can be created with Time Steel, Iron Ingots, Chronon Cells, and Amethyst Shards, to power multiversal devices,
         * and are the ideal way to carry Chronons on the go.
         *
         * {Crafting Grid}
         *
         * Usage
         * Empty Chronon Batteries can be charged slowly over time if carried alongside a Chronon Generator or a Chronometer,
         * or they can be placed inside a Metronome. Charged Chronon Batteries will transfer their stored Chronons to all carried devices automatically,
         * so long as they are all present in the main inventory or curio slots. Chronon Batteries hold {X} Chronons.
         */
        val chrononBatteryOverview
            get() = bookLang(
                "chronon_battery_overview",
                "Chronon Batteries are devices for storing Chronons made from smaller cells. They hold more Chronons than individual %ss.",
                ModItems.chrononCell
            )

        val chrononBatteryCrafting
            get() = bookLang(
                "chronon_battery_crafting",
                "Chronon Batteries can be created with %s, Iron Ingots, %ss, and Amethyst Shards, to power multiversal devices, " +
                        "and are the ideal way to carry Chronons on the go.",
                ModItems.timeSteel, ModItems.chrononCell
            )

        val chrononBatteryUsage
            get() = bookLang(
                "chronon_battery_usage",
                "Empty Chronon Batteries can be charged slowly over time if carried alongside a %s or a %s, or they can be placed inside a %s. " +
                        "Charged Chronon Batteries will transfer their stored Chronons to all carried devices automatically, so long as they are all present in the main inventory or curio slots. " +
                        "Chronon Batteries hold %s Chronons.",
                ModItems.chrononGenerator,
                ModItems.chronometer,
                ModItems.metronome,
                CommonConfigCache.Battery.capacity.format()
            )
        //endregion

        //region chronometer
        /**
         * Chronometers
         *
         * Chronometers are a Chronon generation device carried by the user to passively generate Chronons for their other devices.
         * Properly crafted Chronometers generate Chronons at a faster rate than primitive solutions.
         *
         * Crafting
         * Chronometers can be created from any basic Clock, along with a few extra components. Also required will be a block of Tinted Glass, ingots of Time Steel, and a Chronon Battery.
         *
         * {Crafting Grid}
         *
         * Usage
         * Kept in the main inventory or a curio slot, a Chronon Generator or Chronometer will invert the temporal decay of local background radiation to synthesize Chronons,
         * which are then distributed to all carried devices or storage cells automatically, so long as they are all present in the main inventory or curio slots.
         *
         * {If internal storage enabled}
         * Chronometers have their own internal buffer of storage capable of holding {X} Chronons. This buffer can function as a battery for fast-charging other devices.
         */
        val chronometerOverview
            get() = bookLang(
                "chronometer_overview",
                "Chronometers are a Chronon generation device carried by the user to passively generate Chronons for their other devices. " +
                        "Properly crafted Chronometers generate Chronons at a faster rate than primitive solutions."
            )

        val chronometerCrafting
            get() = bookLang(
                "chronometer_crafting",
                "Chronometers can be created from any basic Clock, along with a few extra components. Also required will be a block of Tinted Glass, ingots of %s, and a %s.",
                ModItems.timeSteel, ModItems.chrononBattery
            )

        val chronometerUsage
            get() = bookLang(
                "chronometer_usage",
                "Kept in the main inventory or a curio slot, a %s or %s will invert the temporal decay of local background radiation to synthesize Chronons, " +
                        "which are then distributed to all carried devices or storage cells automatically, so long as they are all present in the main inventory or curio slots.",
                ModItems.chrononGenerator, ModItems.chronometer
            )

        val chronometerInternalStorage
            get() = bookLang(
                "chronometer_internal_storage",
                "Chronometers have their own internal buffer of storage capable of holding %s Chronons. This buffer can function as a battery for fast-charging other devices.",
                CommonConfigCache.Chronometer.capacity.format()
            )
        //endregion

        //region chronomark
        /**
         * Chronomarks
         *
         * The Chronomark is a stationary block used to generate exact spatial coordinates and provide them to other devices.
         * As an upgrade over Timedoor Markers, Chronomarks allow for an offset of up to {X} blocks to be specified in case the user wants to mount the device on a high ceiling or bury the device under the floor.
         * They also allow for more user filtering options.
         *
         * Crafting
         * Chronomarks are crafted from a Block of Iron, Iron Ingots, ingots of Time Steel, an Ender Pearl, and a block of Tinted Glass.
         *
         * {Crafting Grid}
         *
         * Usage
         * To write data to a Location Card, place the Chronomark and right-click it with a Location Card. The card will then be written and can be used with Timedoor Projector.
         * The Tempad displays Timedoor Markers and Chronomarks owned by the user automatically, but written Location Cards can be shared with others.
         *
         * Filters can be set on the Chronomark to only allow certain users to freely access the device based on available and compatible team or guild frameworks.
         * Right-clicking the device with an empty hand brings up an interface where device name, Timedoor color, security options, and offsets can be configured.
         */
        val chronomarkOverview
            get() = bookLang(
                "chronomark_overview",
                "The Chronomark is a stationary block used to generate exact spatial coordinates and provide them to other devices. " +
                        "As an upgrade over %s, Chronomarks allow for an offset of up to %s blocks to be specified in case the user wants to mount the device on a high ceiling or bury the device under the floor. " +
                        "They also allow for more user filtering options.",
                ModItems.timedoorMarker, CommonConfigCache.Chronomark.maxOffset.format()
            )

        val chronomarkCrafting
            get() = bookLang(
                "chronomark_crafting",
                "Chronomarks are crafted from a Block of Iron, Iron Ingots, ingots of %s, an Ender Pearl, and a block of Tinted Glass.",
                ModItems.timeSteel
            )

        val chronomarkUsage
            get() = bookLang(
                "chronomark_usage",
                "To write data to a %s, place the Chronomark and right-click it with a %s. The card will then be written and can be used with %s. " +
                        "The %s displays %ss and %ss owned by the user automatically, but written %ss can be shared with others.",
                ModItems.locationCard, ModItems.locationCard, ModItems.timedoorProjector,
                ModItems.tempad, ModItems.timedoorMarker, ModItems.chronomark, ModItems.locationCard
            )

        val chronomarkUsage2
            get() = bookLang(
                "chronomark_usage_2",
                "Filters can be set on the Chronomark to only allow certain users to freely access the device based on available and compatible team or guild frameworks. " +
                        "Right-clicking the device with an empty hand brings up an interface where device name, Timedoor color, security options, and offsets can be configured."
            )
        //endregion

        //region tempad
        /**
         *Tempads
         *
         * The standard issue Tempad is the single most powerful piece of technology covered in this database. A Tempad can create Timedoors to any coordinates saved in the device, as well as access a docked Time Twister to allow the user to view and move along their personal timeline.
         *
         * Crafting
         * The Tempad is crafted with blocks of Tinted Glass, a piece of Nether Quartz, an Ender Pearl, a Redstone Lamp, ingots of Time Steel, and a Chronon Battery.
         *
         * {Crafting Grid}
         *
         * Usage
         * Holding the Tempad and right-clicking will bring up the Tempad’s interface as well as the user’s inventory. Tempad functionality is organized into apps and presented to the user through the app tray on the left of the interface. Clicking these app buttons will open the appropriate app within the device and give access to all of its powerful functions.
         *
         * Location data can be added to the Tempad for teleporting via the New Location app. To use Location Cards with the Tempad, please see the Card Wallet.
         *
         * Timedoors
         * Timedoors are doorways between two points in space and/or time. They appear as glowing panels of glass, and can be passed through in either direction like any other doorway.
         *
         * Timedoors consume Chronons from the Tempad upon generation and appear for a set amount of time before closing automatically. Workstations given a constant redstone signal will open a door indefinitely for a continuous Chronon cost.
         *
         * Tempad Apps
         *
         * Teleport
         * Opening the Teleport app will present the user with a list of locations saved in the device, as well as the Timedoor Markers or Chronomarks owned by the user. Clicking these entries will populate the info panel to the right of the list, and enable the teleport button. Clicking the Teleport button will consume Chronons and generate a Timedoor to the location specified in the entry.
         *
         * Next to the search bar, there is a button that will cycle through the available sorting modes. Locations may be sorted by dimension, alphabetical order, or by type. In all modes, locations are sorted into collapsible categories to make the list easier to view.
         *
         * When a location is selected from the list, and the info panel to the right is populated, two additional buttons display at the bottom of the panel. The pin button will pin the location to the top of the list. Only one location may be pinned at a time. The X button will delete the location from the list. There is no confirmation for deletion, so caution is advised. A Location Card right-clicked onto this information panel will be written with the location data from the saved location.
         *
         * New Location
         * The New Location app allows the user to save their current coordinates in the Tempad to allow for travel via the Teleport app. A map is present on the screen to help users find their bearings when saving locations. The location can be named before saving, and the user may also specify a specific color for timedoors opened to the location.
         *
         * Travel Timeline
         * With a Time Twister docked inside the Tempad, access is gained to the Travel Timeline app. This expands the functionality of the docked Time Twister and gives more granular control, allowing the user to move back along to more points along their timeline instead of being limited to major events.
         *
         * Settings
         * The Settings app allows users to adjust the behaviour of certain Tempad functions. The function of the Tempad’s macro button can be changed, as well as which app opens by default when the Tempad is opened.
         *
         * Portal Setup
         * When docked on a Workstation, the terminal provides access to the Portal Setup app, allowing for selection of a destination from the user’s docked Tempad, and for precision tweaks to the opened Timedoor’s position and rotation (plus or minus {X} blocks from the default position). This app is not available without docking the Tempad. Please see the Workstation entry for more information.
         *
         */
        val tempadOverview
            get() = bookLang(
                "tempad_overview",
                "The standard issue %s is the single most powerful piece of technology covered in this database. " +
                        "A %s can create Timedoors to any coordinates saved in the device, as well as access a docked %s to allow the user to view and move along their personal timeline.",
                ModItems.tempad, ModItems.tempad, ModItems.timeTwister
            )

        val tempadCrafting
            get() = bookLang(
                "tempad_crafting",
                "The %s is crafted with blocks of Tinted Glass, a piece of Nether Quartz, an Ender Pearl, a Redstone Lamp, ingots of %s, and a %s.",
                ModItems.tempad, ModItems.timeSteel, ModItems.chrononBattery
            )

        val tempadUsage
            get() = bookLang(
                "tempad_usage",
                "Holding the %s and right-clicking will bring up the Tempad’s interface as well as the user’s inventory. " +
                        "Tempad functionality is organized into apps and presented to the user through the app tray on the left of the interface. " +
                        "Clicking these app buttons will open the appropriate app within the device and give access to all of its powerful functions.",
                ModItems.tempad
            )

        val tempadUsage2
            get() = bookLang(
                "tempad_usage_2",
                "Location data can be added to the %s for teleporting via the New Location app. To use %ss with the %s, please see the %s.",
                ModItems.tempad, ModItems.locationCard, ModItems.tempad, ModItems.cardWallet
            )

        val tempadTimedoors
            get() = bookLang(
                "tempad_timedoors",
                "Timedoors are doorways between two points in space and/or time. They appear as glowing panels of glass, and can be passed through in either direction like any other doorway."
            )

        val tempadTimedoors2
            get() = bookLang(
                "tempad_timedoors_2",
                "Timedoors consume Chronons from the %s upon generation and appear for a set amount of time before closing automatically. " +
                        "%ss given a constant redstone signal will open a door indefinitely for a continuous Chronon cost.",
                ModItems.tempad, ModItems.workstation
            )

        val tempadApps
            get() = bookLang(
                "tempad_apps",
                "Apps"
            )

        val tempadAppTeleport
            get() = bookLang(
                "tempad_app_teleport",
                "Opening the Teleport app will present the user with a list of locations saved in the device, as well as the %ss or %ss owned by the user. " +
                        "Clicking these entries will populate the info panel to the right of the list, and enable the teleport button. " +
                        "Clicking the Teleport button will consume Chronons and generate a Timedoor to the location specified in the entry.",
                ModItems.timedoorMarker, ModItems.chronomark
            )

        val tempadAppTeleport2
            get() = bookLang(
                "tempad_app_teleport_2",
                "Next to the search bar, there is a button that will cycle through the available sorting modes. " +
                        "Locations may be sorted by dimension, alphabetical order, or by type. In all modes, locations are sorted into collapsible categories to make the list easier to view."
            )

        val tempadAppTeleport3
            get() = bookLang(
                "tempad_app_teleport_3",
                "When a location is selected from the list, and the info panel to the right is populated, two additional buttons display at the bottom of the panel. " +
                        "The pin button will pin the location to the top of the list. Only one location may be pinned at a time. The X button will delete the location from the list. " +
                        "There is no confirmation for deletion, so caution is advised. A %s right-clicked onto this information panel will be written with the location data from the saved location.",
                ModItems.locationCard
            )

        val tempadAppNewLocation
            get() = bookLang(
                "tempad_app_new_location",
                "The New Location app allows the user to save their current coordinates in the %s to allow for travel via the Teleport app. " +
                        "A map is present on the screen to help users find their bearings when saving locations. " +
                        "The location can be named before saving, and the user may also specify a specific color for Timedoors opened to the location.",
                ModItems.tempad
            )

        val tempadAppTravelTimeline
            get() = bookLang(
                "tempad_app_travel_timeline",
                "With a %s docked inside the %s, access is gained to the Travel Timeline app. This expands the functionality of the docked %s " +
                        "and gives more granular control, allowing the user to move back along to more points along their timeline instead of being limited to major events.",
                ModItems.timeTwister, ModItems.tempad, ModItems.timeTwister
            )

        val tempadAppSettings
            get() = bookLang(
                "tempad_app_settings",
                "The Settings app allows users to adjust the behaviour of certain %s functions. " +
                        "The function of the Tempad’s macro button can be changed, as well as which app opens by default when the Tempad is opened.",
                ModItems.tempad
            )

        val tempadAppPortalSetup
            get() = bookLang(
                "tempad_app_portal_setup",
                "When docked on a %s, the terminal provides access to the Portal Setup app, allowing for selection of a destination from the user’s docked %s, " +
                        "and for precision tweaks to the opened Timedoor’s position and rotation (plus or minus %s blocks from the default position). " +
                        "This app is not available without docking the %s. Please see the %s entry for more information.",
                ModItems.workstation,
                ModItems.tempad,
                CommonConfigCache.Tempad.maxOffset.format(),
                ModItems.tempad,
                ModItems.workstation
            )

        //endregion

        //region workstation
        /**
         * Workstation
         *
         * The Workstation is a stationary computer terminal that allows the user to accomplish various tasks when a Tempad is placed on the docking pad.
         *
         * Crafting
         * It can be crafted with blocks of Tinted Glass, Copper Ingots, ingots of Time Steel, and a piece of Nether Quartz.
         *
         * {Crafting Grid}
         *
         * Usage
         * Docking a Tempad by right-clicking it onto the docking pad unlocks the cassette slot at the back to allow the use of Upgrade Tapes.
         * Right-clicking an Upgrade Tape into the cassette slot applies the upgrade on the tape to the docked Tempad, provided the specific upgrade isn’t already present on the device.
         *
         * When a Tempad is docked on the docking pad, the terminal to the left is also unlocked, allowing the user to set up a stationary teleportation device.
         * The usage of the Workstation in this way is similar to the Timedoor Projector, in that it accepts a redstone signal from any side and creates a Timedoor for the user.
         *
         * Right-clicking on the terminal, however, opens a new Portal Setup app, allowing for selection of a destination from the user’s docked Tempad,
         * and for precision tweaks to the opened Timedoor’s position and rotation (plus or minus {X} blocks in any direction from the default position).
         * Additionally, if a constant redstone signal is applied the Timedoor will remain open until the signal stops, after which it will resume its normal countdown to close.
         *
         * Player Teleport Upgrade
         * This upgrade allows the Tempad to lock on to the temporal aura of other players and open Timedoors directly to their location.
         * This functionality requires that the player is carrying an active Location Broadcaster or a Screening Device set to permit the user to teleport to them.
         *
         * {Crafting Grid}
         */
        val workstationOverview
            get() = bookLang(
                "workstation_overview",
                "The %s is a stationary computer terminal that allows the user to accomplish various tasks when a %s is placed on the docking pad.",
                ModItems.workstation, ModItems.tempad
            )

        val workstationCrafting
            get() = bookLang(
                "workstation_crafting",
                "It can be crafted with blocks of Tinted Glass, Copper Ingots, ingots of %s, and a piece of Nether Quartz.",
                ModItems.timeSteel
            )

        val workstationUsage
            get() = bookLang(
                "workstation_usage",
                "Docking a %s by right-clicking it onto the docking pad unlocks the cassette slot at the back to allow the use of Upgrade Tapes. " +
                        "Right-clicking an Upgrade Tape into the cassette slot applies the upgrade on the tape to the docked %s, provided the specific upgrade isn’t already present on the device.",
                ModItems.tempad, ModItems.tempad
            )

        val workstationUsage2
            get() = bookLang(
                "workstation_usage_2",
                "When a %s is docked on the docking pad, the terminal to the left is also unlocked, allowing the user to set up a stationary teleportation device. " +
                        "The usage of the %s in this way is similar to the %s, in that it accepts a redstone signal from any side and creates a Timedoor for the user.",
                ModItems.tempad, ModItems.workstation, ModItems.timedoorProjector
            )

        val workstationUsage3
            get() = bookLang(
                "workstation_usage_3",
                "Right-clicking on the terminal, however, opens a new Portal Setup app, allowing for selection of a destination from the user’s docked %s, " +
                        "and for precision tweaks to the opened Timedoor’s position and rotation (plus or minus %s blocks in any direction from the default position). " +
                        "Additionally, if a constant redstone signal is applied the Timedoors will remain open until the signal stops, after which it will resume its normal countdown to close.",
                ModItems.tempad, CommonConfigCache.Tempad.maxOffset.format()
            )

        val upgradePlayerTeleport
            get() = bookLang(
                "upgrade_player_teleport",
                "This upgrade allows the %s to lock on to the temporal aura of other players and open Timedoors directly to their location. " +
                        "This functionality requires that the player is carrying an active %s or a %s set to permit the user to teleport to them.",
                ModItems.tempad, ModItems.locationBroadcaster, ModItems.screeningDevice
            )

        //endregion

        //region time twister
        /**
         * Time Twister
         *
         * The Time Twister is a hand-held device that allows users to move back along significant events on their personal timeline.
         *
         * Crafting
         * It is crafted with ingots of Time Steel, blocks of Tinted Glass, a Chronon Battery, and an Ender Pearl.
         *
         * {Crafting Grid}
         *
         * Usage
         * Using the Time Twister in the hand will present the user with a radial display showing points in the user’s past where they changed dimensions or died.
         * Clicking one of the options will teleport the user back to the indicated location. Any points on the timeline forward from the selected point
         * (options counter-clockwise from the selected point) will be destroyed to prevent paradoxes.
         *
         * Note that if the selected point involves moving the user to a different dimension, new options will appear in the Time Twister for leaving your current dimension
         * and appearing in the new one. This means that in some instances the Time Twister can appear to not be deleting future entries properly,
         * when in fact this is a failure of the user to pay attention.
         *
         * If more granularity in timeline events is required, the Time Twister can be docked with a Tempad to provide access to the Travel Timeline app.
         * The Time Twister is docked by right-clicking it onto a Tempad in the inventory, or by right-clicking a Tempad onto the Time Twister.
         * This also provides the benefit of carrying the Time Twister without it taking an extra inventory slot.
         */

        val timeTwisterOverview
            get() = bookLang(
                "time_twister_overview",
                "The %s is a hand-held device that allows users to move back along significant events on their personal timeline.",
                ModItems.timeTwister
            )

        val timeTwisterCrafting
            get() = bookLang(
                "time_twister_crafting",
                "It is crafted with ingots of %s, blocks of Tinted Glass, a %s, and an Ender Pearl.",
                ModItems.timeSteel, ModItems.chrononBattery
            )

        val timeTwisterUsage
            get() = bookLang(
                "time_twister_usage",
                "Using the %s in the hand will present the user with a radial display showing points in the user’s past where they changed dimensions or died. " +
                        "Clicking one of the options will teleport the user back to the indicated location. Any points on the timeline forward from the selected point " +
                        "(options counter-clockwise from the selected point) will be destroyed to prevent paradoxes.",
                ModItems.timeTwister
            )

        val timeTwisterUsage2
            get() = bookLang(
                "time_twister_usage_2",
                "Note that if the selected point involves moving the user to a different dimension, new options will appear in the %s for leaving your current dimension " +
                        "and appearing in the new one. This means that in some instances the %s can appear to not be deleting future entries properly, " +
                        "when in fact this is a failure of the user to pay attention.",
                ModItems.timeTwister, ModItems.timeTwister
            )

        val timeTwisterUsage3
            get() = bookLang(
                "time_twister_usage_3",
                "If more granularity in timeline events is required, the %s can be docked with a %s to provide access to the Travel Timeline app. " +
                        "The %s is docked by right-clicking it onto a %s in the inventory, or by right-clicking a %s onto the %s. " +
                        "This also provides the benefit of carrying the %s without it taking an extra inventory slot.",
                ModItems.timeTwister,
                ModItems.tempad,
                ModItems.timeTwister,
                ModItems.tempad,
                ModItems.tempad,
                ModItems.timeTwister,
                ModItems.timeTwister
            )
        //endregion

        //region screening device
        /**
         * Screening Device
         *
         * The Screening Device is an advanced version of the Location Broadcaster that features more advanced filtering capability.
         *
         * Crafting
         * Screening Devices are crafted with a block of Tinted Glass, an Emerald, a measure of Redstone Dust, a Compass, and an ingot of Time Steel.
         *
         * {Crafting Grid}
         *
         * Usage
         * On a surface level, the Screening Device provides the same functionality as the Location Broadcaster, allowing users with upgraded Tempads to open Timedoors directly to their location.
         * Additionally, the Screening Device can also filter who is given this permission based on available and compatible team or guild frameworks.
         * Holding the device and right-click using it will cycle the device through its available modes.
         */
        val screeningDeviceOverview
            get() = bookLang(
                "screening_device_overview",
                "The %s is an advanced version of the %s that features more advanced filtering capability.",
                ModItems.screeningDevice, ModItems.locationBroadcaster
            )

        val screeningDeviceCrafting
            get() = bookLang(
                "screening_device_crafting",
                "Screening Devices are crafted with a block of Tinted Glass, an Emerald, a measure of Redstone Dust, a Compass, and an ingot of %s.",
                ModItems.timeSteel
            )

        val screeningDeviceUsage
            get() = bookLang(
                "screening_device_usage",
                "On a surface level, the %s provides the same functionality as the %s, allowing users with upgraded %s to open Timedoors directly to their location. " +
                        "Additionally, the %s can also filter who is given this permission based on available and compatible team or guild frameworks. " +
                        "Holding the device and right-click using it will cycle the device through its available modes.",
                ModItems.screeningDevice, ModItems.locationBroadcaster, ModItems.tempad, ModItems.screeningDevice
            )

        //endregion

        //region metronome
        /**
         * Metronomes
         *
         * Metronomes are a Chronon generation device that can be placed in the world to provide a stationary solution to charging devices and storing Chronons.
         *
         * Crafting
         * Metronomes are crafted with Ingots of Time Steel, Chronon Batteries, and an Ender Chest.
         *
         * {Crafting Grid}
         *
         * Usage
         * Generally, Metronomes cannot be charged within the inventory, and instead need to be placed into the world. There is an initial charge of Chronons needed to boot the device and gain access to its functionality after it is placed. This boot charge can be provided from a Chronon Cell or Chronon Battery in hand by crouch-right-clicking the Metronome.
         *
         * Once booted, the Metronome will begin to generate Chronons into its own internal battery, which holds {X} Chronons. Right clicking the device will bring up the interface, where devices can be placed to store Chronons via the left-hand slots or take Chronons via the right-hand slots. Additionally, the Metronome is coded to the user’s temporal aura, and may be locked to prevent unauthorized access via the lock button in the upper right. It is locked by default.
         *
         * {If config allows multi-charge}
         * If the user places additional Metronomes, after booting they will link with existing Metronomes owned by the user in a multiversal network, combining their maximum storage capacities. Linked Metronomes will also combine charging power to generate more Chronons with each pulse.
         *
         * {If config disallows multi-charge}
         * If the user places additional Metronomes, after booting they will link with existing Metronomes owned by the user in a multiversal network, combining their maximum storage capacities.
         */
        val metronomesOverview
            get() = bookLang(
                "metronomes_overview",
                "The %s are a Chronon generation device that can be placed in the world to provide a stationary solution to " +
                        "charging devices and storing Chronons.",
                ModItems.metronome
            )

        val metronomesCrafting
            get() = bookLang(
                "metronomes_crafting",
                "Metronomes are crafted with Ingots of %s, Chronon Batteries, and an Ender Chest.",
                ModItems.timeSteel
            )

        val metronomesUsage
            get() = bookLang(
                "metronomes_usage",
                "Generally, %s cannot be charged within the inventory, and instead need to be placed into the world. There " +
                        "is an initial charge of Chronons needed to boot the device and gain access to its functionality" +
                        " after it is placed. This boot charge can be provided from a %s or %s in hand by crouch-right-clicking" +
                        " the %s.",
                ModItems.metronome, ModItems.chrononCell, ModItems.chrononBattery, ModItems.metronome
            )

        val metronomesUsage2
            get() = bookLang(
                "metronomes_booted",
                "Once booted, the %s will begin to generate Chronons into its own internal battery, which holds %s Chronons. " +
                        "Right-clicking the device will bring up the interface, where devices can be placed to store Chronons" +
                        " via the left-hand slots or take Chronons via the right-hand slots. Additionally, the %s is coded " +
                        "to the user’s temporal aura, and may be locked to prevent unauthorized access via the lock button" +
                        " in the upper right. It is locked by default.",
                ModItems.metronome, CommonConfigCache.Metronome.capacity.format(), ModItems.metronome
            )

        val metronomesMultiCharge
            get() = bookLang(
                "metronomes_multi_charge",
                "If the user places additional %s, after booting they will link with existing %s owned by the user in a " +
                        "multiversal network, combining their maximum storage capacities. Linked %s will also combine " +
                        "charging power to generate more Chronons with each pulse.",
                ModItems.metronome, ModItems.metronome, ModItems.metronome
            )

        val metronomesNoMultiCharge
            get() = bookLang(
                "metronomes_no_multi_charge",
                "If the user places additional %s, after booting they will link with existing %s owned by the user in a " +
                        "multiversal network, combining their maximum storage capacities.",
                ModItems.metronome, ModItems.metronome
            )

        //endregion

        fun bookLang(key: String, value: String, vararg args: Any): Component {
            val finalKey = ModItems.handbook.descriptionId + "." + key
            val shouldBeNull = entries.put(finalKey, value)
            val args = args.map {
                return@map if (it is Item) {
                    Component.translatable(it.descriptionId)
                        .withStyle(
                            Style.EMPTY
                                .withColor(Tempad.HIGHLIGHTED_ORANGE.value)
                                .withUnderlined(true)
                                .withClickEvent(ClickEvent(ClickEvent.Action.CHANGE_PAGE, it.id.toString()))
                        )
                } else if (it is String) {
                    Component.literal(it).withColor(Tempad.HIGHLIGHTED_ORANGE.value)
                } else {
                    it as? Component
                }
            }.toTypedArray()

            if (shouldBeNull != null) {
                throw IllegalStateException("Duplicate translation key $finalKey")
            }
            return Component.translatable(finalKey, *args)
        }
    }

    override fun addTranslations() {
        for ((key, value) in entries) {
            add(key, value)
        }

        for (entry in ModItems.registry.entries) {
            try {
                add(entry.get(), entry.id.formatted)
            } catch (_: Exception) {
            }
        }

        for (entry in ModBlocks.blocks.entries) {
            try {
                add(entry.get(), entry.id.formatted)
            } catch (_: Exception) {
            }
        }

        add("datapack.tempad.required_location_upgrade", "Require Upgrade for Location Saving")
        add(ModItems.newLocationKey.toLanguageKey("upgrade"), "Create Saved Locations")
        add(ModItems.playerKey.toLanguageKey("upgrade"), "Teleport to Players")

        add("error.tempad.owner_mismatch", "This isn't yours. Return to owner or Reset item")
        add("error.tempad.block_locked", "%s is Locked")

        ModItems.screeningDevice.apply {
            addSub("off", "Off")
            addSub("screening", "Screening: %s")
        }

        ModItems.locationCard.apply {
            addSub("creator_toolip", "Created by %s")
            addSub("shift_toolip", "Hold [%s] for more info")
            addSub("add_location", "Right click to set location")
            addSub("added_location", "Added location: %s")
            addSub("dynamic_error", "You may not add player, spatial anchor, or other dynamic locations to this device")
            addSub("set_location", "Set location: %s")
            addSub("redeem", "%s: [%s] To Use")
            addSub("created_by", "Created by: %s")
            addSub("id", "ID: %s")
        }

        ModItems.cardWallet.apply {
            addSub("prefix", "Contains:")
            addSub("empty", "No cards")
        }

        ModBlocks.metronome.apply {
            addSub("booting", "Booting: %s")
        }

        ModBlocks.workstation.apply {
            addSub("installing", "Installing: %s")
        }

        addRoot("app.tempad.teleport", "Teleport") {
            it.addSub("pin", "Pin")
            it.addSub("unpin", "Unpin")
            it.addSub("teleport", "Teleport")
            it.addSub("delete", "Delete")
            it.addSub("no_selection", "No location selected. Click on a location view details")
        }

        addRoot("app.tempad.settings", "Settings") {
            it.addSub("default_macro", "Default Macro")
            it.addSub("default_macro.subtitle", "Default macro to use when shift-clicking Tempad or pressing [F] key")
            it.addSub("default_app", "Default App")
            it.addSub("default_app.subtitle", "Default app to open when right-clicking Tempad")
            it.addSub("organization_method", "Organization")
            it.addSub("organization_method.subtitle", "Default way to organize shown locations on the Tempad")
        }

        addRoot("app.tempad.portal_setup", "Portal Setup") {
            it.addSub("offset", "Offset: (-/+)")
            it.addSub("left_right", "L/R: ")
            it.addSub(
                "left_right.desc",
                "Positive places portal to your left, negative to your right \n\nAccepts decimal (e.g 1.3)"
            )
            it.addSub("up_down", "U/D: ")
            it.addSub("up_down.desc", "Positive places portal higher, negative lower \n\nAccepts decimal (e.g 1.3)")
            it.addSub("front_back", "F/B: ")
            it.addSub(
                "front_back.desc",
                "Positive places portal in front of you, negative behind you \n\nAccepts decimal (e.g 1.3)"
            )
            it.addSub("angle", "Angle: ")
            it.addSub("upright", "Is upright: ")
        }

        addRoot("app.tempad.new_location", "New Location") {
            it.addSub("name", "Name:")
            it.addSub("color", "Color:")
        }

        addRoot("app.tempad.timeline", "Travel Timeline") {
            it.addSub("tracking", "Tracking")
            it.addSub("teleport", "Teleport")
        }

        addRoot("key.tempad", "") {
            it.addSub("shortcut", "Open Tempad Screen")
            it.addSub("macro", "Use Tempad Macro")
            it.addSub("macro", "Open Tempad New Location App")
            it.addSub("macro", "Open Tempad Travel Timeline App")
        }
    }

    val ResourceLocation.formatted
        get() = path.split('_').joinToString(" ") { it.replaceFirstChar { it.uppercaseChar() } }

    fun Block.addSub(key: String, value: String) {
        add("$descriptionId.$key", value)
    }

    fun Item.addSub(key: String, value: String) {
        add("$descriptionId.$key", value)
    }

    fun String.addSub(key: String, value: String) {
        add("$this.$key", value)
    }

    fun addRoot(key: String, value: String, entries: (String) -> Unit = {}): String {
        add(key, value)
        entries.invoke(key)
        return key
    }
}

fun Number.format(): String = NumberFormat.getInstance().format(this)
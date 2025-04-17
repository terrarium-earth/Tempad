package earth.terrarium.tempad.data.client

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.ModItems
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.common.data.LanguageProvider

class ModLang(output: PackOutput) : LanguageProvider(output, Tempad.MOD_ID, "en_us") {
    companion object {
        private val entries = mutableMapOf<String, String>()

        val title = bookLang("title", "Knowledge Projector")
        val credits = bookLang("credits", "By CodexAdrian & Robonebi")
        val travelAdvisory = bookLang("travel_advisory", "For agents dispatched to MC-21 or adjacent realms:\n" + "\n" + "While the technology developed by the TVA is the most advanced in the multiverse, it is not capable of the impossible. Just as time doors cannot be opened to different points of time within the TVA, they cannot be opened to different points of time in MC-21 adjacent realms. Much like the TVA, time passes differently in MC-21 and all related branches, and as such, the TVA's technology is not capable of connecting different points in time within these locations. Spatial travel remains unaffected.\n")
        val iron = bookLang("iron", "Iron")
        val steel = bookLang("steel", "Steel")

        // Overview
        val overview = bookLang("overview", "Introduction")
        val crafting = bookLang("crafting", "Crafting")
        val usage = bookLang("usage", "Usage")

        // Chronon Cell
        val chrononHeader = bookLang("chrononheader", "Chronons")
        val chronons = bookLang("chronon", "Chronons are essential fuel for TVA multiversal technology, thus the first step to multiversal technology is generating and storing Chronons")
        val cellCrafting = bookLang("cell_crafting", "Primitive Chronon Cells can be created with materials on hand, such as Iron Ingots, Copper Ingots, Redstone Dust, and Amethyst Shards, to power multiversal devices.")
        // Inputs: Cell capacity
        val cellUsage = bookLang("cell_usage", "When chronons enter the cell via a chronon generator or other divide they'll hold %s chronons which can then be distributed to other items that need it, like the Timedoor Projector")

        // Chronon Gen
        // Inputs: Chronometer, Metronome
        val genCrafting = bookLang("gen_crafting", "While a %s or %s would be ideal for generating Chronons, primitive Chronon Generators can be created from any basic Clock, along with a block of Glass, Iron Ingots, and a Chronon Cell.")
        // Inputs: Chronometer
        val genUsage = bookLang("gen_usage", "Kept in the main inventory or a curio slot, a Chronon Generator or %s will invert the temporal decay of local background radiation to synthesize Chronons, which are then distributed to all carried devices or storage cells automatically, so long as they are all present in the main inventory or curio slots.")

        // Location Card
        // Inputs: Time Steel
        val cardCrafting = bookLang("card_text_2", "Location Cards record spatial coordinates for use in multiversal devices. They are created from Iron Ingots and any Dye. The recipe produces 4 Location Cards. Alternatively one may substitute the iron for an ingot of %s and receive 16 cards from the same recipe.")
        // Inputs: Timedoor Marker, Chronomark, Tempad, Timedoor Marker, Chronomark, Tempad, Card Wallet, Timedoor Projector
        val cardUsage = bookLang("card_text_1", "To write location data to a card, a %s or %s is required at the correct coordinates or a standard issue %s with the coordinates already saved inside. Placing a written Location Card into the crafting grid will wipe the data stored on the card. Cards linked to a %s or %s not owned by the user only function with the %s if placed into a carried %s, however all cards can be used with the %s.")

        // Card Wallet
        // Inputs: Location Card, Timedoor Projector, Location Card
        val walletCrafting = bookLang("wallet_text_1", "Card Wallets are a convenient way of storing and carrying an excess of %ss, especially when used in conjunction with a %s. Wallets can be crafted with Copper Ingots, Leather, and a blank %s.")
        // Inputs: Tempad
        val walletUsage = bookLang("wallet_text_2", "Card locations contained in the wallet are automatically added to their %s’s location list. Only the first wallet found in the player's inventory or curios slots will be added to the list.")

        // Timedoor Marker
        val markerCrafting = bookLang("marker_crafting", "The Timedoor Marker is used to generate exact spatial coordinates and provide them to other devices. They are crafted from a Block of Iron, Iron Ingots, an Ender Pearl, and a block of Glass.")
        // Inputs: Location Card, Location Card, Timedoor Projector, Tempad, Chronomark, Location Card
        val markerUsage = bookLang("marker_usage", "To write data to a %s, place the Timedoor Marker and right-click it with a %s. The card will then be written and can be used with %s. The %s displays Timedoor Markers and %s owned by the user automatically, but %s linked to devices can be shared with others.")

        // Timedoor Projector
        // Inputs: Tempad, Chronon Cell
        val projectorCrafting = bookLang("projector_crafting", "When a standard issue %s is unavailable, rudimentary versions of the device can be crafted using materials on hand. Materials required are Copper Ingots, a Glass block, an Ender Pearl, a %s and Blocks of Iron.")
        // Inputs: Location Card
        val projectorUsage = bookLang("projector_usage", "Timedoor Projectors allow for the creation of time doors to spatial coordinates read from a %s. These can be loaded into the device by right-clicking the card onto the Timedoor Projector while it is either in the inventory or placed in the world, or by right-clicking a card in the inventory while holding the projector with the cursor. Cards can be retrieved from the device by either right-clicking it on an empty inventory space, or by right clicking the device where it is placed in the world.")
        val projectorUsage2 = bookLang("projector_usage_2", "The device can be activated while held by using it (right-click). Holding crouch and using the device will place it on the block being looked at. Placing the Timedoor Projector allows it to be triggered by a Redstone signal instead, such as from an adjacent button.")

        // Location Broadcaster
        // Inputs: Tempad
        val broadcasterCrafting = bookLang("broadcaster_crafting", "Location Broadcasters allow lost agents to be found and rescued by agents with specially upgraded %ss. To craft one [insert nebi words here]")
        // Inputs: Tempad
        val broadcasterUsage = bookLang("broadcaster_usage", "If a Location Broadcaster is carried in the inventory or curio slot and is turned on, the carrier will appear in the list of possible teleports for %s with the Teleport to Players upgrade.")
        val broadcasterUsage2 = bookLang("broadcaster_usage_2", "The device can be used in the hand to toggle between its modes of operation. The screen on the device will glow green if teleportation is enabled, and will glow red if disabled. This allows the carrier to decide when they are open to Timedoors being opened to their location")

        // Time Steel
        val timeSteelUsage = bookLang("time_steel_usage", "Time Steel is a crucial component to all advanced multiversal technology, as it is not affected by the passage of time. Thus it is a reliable material to house and shield all of the sensitive components required for traversing space and time.")
        val timeSteelCrafting = bookLang("time_steel_crafting", "Time Steel can be acquired in MC-21 and adjacent realms by crafting Netherite Scraps with Iron Ingots. Raw Iron to refine can be found in ore almost anywhere. Acquiring the scraps of Netherite, on the other hand, is contingent on access to a Nether dimension. It is inadvisable to get stuck on a timeline without access to these resources, as developing multiversal technology without it can lead to unstable results.")
        val timeSteelCrafting2 = bookLang("time_steel_crafting_2", "Depending on the technology available in a given timeline, alternate or more efficient methods to produce Time Steel may be discovered. Recipe repositories may be checked for more details.")

        // Chronon Batteries
        // Inputs: Time Steel, Chronon Cell
        val batteryCrafting = bookLang("battery_crafting", "Chronon Batteries can be created with %s, Iron Ingots, %ss, and Amethyst Shards, to power multiversal devices, and are the ideal way to carry Chronons on the go.")
        // Inputs: Chronometer, Chronon Generator, Metronome
        val batteryUsage = bookLang("battery_usage", "Empty Chronon Batteries can be charged slowly over time if carried alongside a %s or primitive %s, or they can be placed inside a %s. Charged Chronon Batteries will transfer their stored Chronons to all carried devices automatically, so long as they are all present in the main inventory or curio slots.")

        // Chronometers
        // Inputs: Time Steel, Chronon Battery
        val chronometerCrafting = bookLang("chronometer_crafting", "Chronometers can be created from any basic Clock, along with a few extra components. Also required will be a block of Tinted Glass, ingots of %s, and a %s. Properly crafted Chronometers generate Chronons at a faster rate than more primitive solutions.")
        // Inputs: Chronon Generator, Chronon Generator
        val chronometerUsage = bookLang("chronometer_usage", "Kept in the main inventory or a curio slot, a %s or Chronometer will invert the temporal decay of local background radiation to synthesize Chronons, which are then distributed to all carried devices or storage cells automatically, so long as they are all present in the main inventory or curio slots. The Chronometer will not operate if another Chronometer or %s is in your inventory or curios slots.")

        // Chronomark
        // Inputs: Time Steel
        val chronomarkCrafting = bookLang("chronomark_crafting", "The Chronomark is used to generate exact spatial coordinates and provide them to other devices. They are crafted from a Block of Iron, Iron Ingots, ingots of %s, an Ender Pearl, and a block of Tinted Glass.")
        // Inputs: Location Card, Timedoor Projector, Location Card
        val chronomarkUsage = bookLang("chronomark_usage", "To write data to a %s, place the Chronomark and right-click it with a Location Card. The card will then be written and can be used with %s. The Tempad displays Chronomarks owned by the user automatically, but a %s linked to a device can be shared with others.")

        // Tempad
        // Inputs: Time Twister
        val tempadUsage = bookLang("tempad_usage", "The standard issue Tempad is the single most powerful piece of technology covered in this database. A Tempad can create Timedoors to any coordinates saved in the device, as well as access a docked %s to allow the user to view and move along their personal timeline.")
        val apps = bookLang("apps", "Apps")
        val tempadAppUsage = bookLang("tempad_usage_2", "Tempad functionality is organized into apps and presented to the user through the app tray on the left of the interface. Clicking these app buttons will open the appropriate app within the device and give access to all of its powerful functions.")
        val newLocationAppTitle = bookLang("new_location_app_title", "New Location")
        val newLocationApp = bookLang("new_location_app", "The New Location app allows the user to save their current coordinates in the Tempad to allow for travel via the Teleport app. A map is present on the screen to help users find their bearings when saving locations. The location can be named before saving, and the user may also specify a specific colour for timedoors opened to the location.")
        val teleportTitle = bookLang("teleport_usage", "Teleport")
        // Inputs: Timedoor Marker, Chronomark
        val teleportApp = bookLang("teleport_app", "Opening the Teleport app will present the user with a list of locations saved in the device, as well as the %ss or %ss owned by the user. Clicking these entries will populate the info panel to the right of the list, and enable the teleport button. Clicking the Teleport button will generate a Timedoor to the location specified in the entry.")
        val teleportSortingTitle = bookLang("teleport_sorting_title", "Sorting")
        val teleportSorting = bookLang("teleport_sorting", "Next to the search bar, there is a button that will cycle through the available sorting modes. Locations may be sorted by dimension, alphabetical order, or by type. In all modes, locations are sorted into collapsible categories to make the list easier to view.")
        val teleportLocationTitle = bookLang("teleport_location_title", "Pinning & Deleting")
        val teleportLocationManagement = bookLang("teleport_location_management", "When a location is selected from the list, and the info panel to the right is populated, two additional buttons display at the bottom of the panel. The pin button will pin the location to the top of the list. Only one location may be pinned at a time. The X button will delete the location from the list. There is no confirmation for deletion, so caution is advised.")
        val portalSetupTitle = bookLang("portal_setup_title", "Portal Setup")
        // Input: Workstation, Workstation
        val portalSetup = bookLang("portal_setup", "When docked on a %s, the terminal provides access to the Portal Setup app, allowing for selection of a destination from the user’s docked Tempad, and for precision tweaks to the opened Timedoor’s position and rotation (plus or minus 5 blocks from the default position). This app is not available without docking the Tempad. Please see the %s entry for more information.")
        val travelTimelineTitle = bookLang("travel_timeline_title", "Travel Timeline")
        // Inputs: Time Twister, Time Twister,
        val travelTimeline = bookLang("travel_timeline", "With a %s docked inside the Tempad, access is gained to the Travel Timeline app. This expands the functionality of the docked %s and gives more granular control, allowing the user to move back along to more points along their timeline instead of being limited to major events.")
        val settingsTitle = bookLang("settings_title", "Settings")
        val settings = bookLang("settings", "The Settings app allows users to adjust the behaviour of certaiin Tempad functions. The function of the Tempad’s macro button can be changed, as well as which app opens by default when the Tempad is opened.")

        // Workstation
        // Inputs: Tempad, Time Steel
        val workstationCrafting = bookLang("workstation_crafting", "The Workstation allows the user to accomplish various tasks when a %s is placed on the docking pad. It can be crafted with blocks of Tinted Glass, Copper Ingots, ingots of %s, and a piece of Nether Quartz.")
        // Inputs: Tempad, Tempad
        val workstationUsage = bookLang("workstation_usage", "Docking a %s by right-clicking it onto the docking pad unlocks the cassette slot at the back to allow the use of Upgrade Tapes. Right-clicking an Upgrade Tape into the cassette slot applies the upgrade on the tape to the docked %s, provided the specific upgrade isn’t already present on the device.")
        // Inputs: Tempad, Tempad, Location Broadcaster, Screening Device
        val workstationUpgrades = bookLang("workstation_upgrade", "The only upgrade available for the %s at this time is the Player Teleport Upgrade. This upgrade allows the %s to lock on to the temporal aura of other players and open Timedoors directly to their location. This functionality requires that the player is carrying an active %s or a %s set to permit the user to teleport to them.")
        // Inputs: Tempad, Timedoor Projector,
        val workstationTerminal = bookLang("workstation_terminal", "When a %s is docked on the docking pad, the terminal to the left is also unlocked, allowing the user to set up a stationary teleportation device. The usage of the Workstation in this way is similar to the %s, in that it accepts a redstone signal from any side and creates a Timedoor for the user.")
        // Inputs: Tempad
        val workstationApp = bookLang("workstation_app", "Right-clicking on the terminal, however, opens a new Portal Setup app, allowing for selection of a destination from the user’s docked %s, and for precision tweaks to the opened Timedoor’s position and rotation (plus or minus 5 blocks in any direction from the default position). Additionally, if a constant redstone signal is applied the Timedoor will remain open until the signal stops, after which it will resume it’s normal countdown to close.")

        // Time Twister
        // Inputs: Time Steel, Chronon Battery
        val twisterCrafting = bookLang("twister_crafting", "The Time Twister allows users to move back along significant events on their personal timeline. It is crafted with ingots of %s, blocks of Tinted Glass, a %s, and an Ender Pearl.")
        val twisterUsage = bookLang("twister_usage", "Using the Time Twister will present the user with a radial display showing points in the user’s past where they changed dimensions or died. Clicking one of the options will teleport the user back to the indicated location. Any points on the timeline forward from the selected point (options counter-clockwise from the selected point) will be destroyed to prevent paradoxes.")
        val twisterUsage2 = bookLang("twister_usage_2", "Note that if the selected involves moving the user to a different dimension, new options will appear in the Time Twister for leaving your current dimension and appearing in the new one. This means that in some instances the Time Twister can appear to not be deleting future entries properly, when in fact this a failure of the user to pay attention.")
        // Inputs: Tempad, Tempad, Tempad
        val twisterApp = bookLang("twister_app", "If more granularity in timeline events is required, the Time Twister can be docked with a %s to provide access to the Travel Timeline app. The Time Twister is docked by right-clicking it onto a %s in the inventory, or by right clicking a %s onto the Time Twister. This also provides the benefit of carrying the Time Twister without it taking an extra inventory slot.")

        // Screening Device
        // Inputs: Location Broadcaster, Time Steel
        val screeningCrafting = bookLang("screening_crafting", "The Screening Device is an advanced version of the %s. It can be crafted with a block of Tinted Glass, an Emerald, a measure of Redstone Dust, a Compass, and an ingot of %s.")
        // Inputs: Tempad
        val screeningUsage = bookLang("screening_usage", "On a surface level, provides the same functionality of allowing users with %ss to open Timedoors directly to their location. Additionally, the Screening Device can also filter who is given this permission based on available and compatible team or guild frameworks. Holding the device and right-click using it will cycle the device through its available modes.")

        // Metronome
        // Inputs: Time Steel, Chronon Battery
        val metronomeCrafting = bookLang("metronome_crafting", "Metronomes are a Chronon generation device that can be placed in the world to provide a stationary solution to charging devices and storing Chronons. They are crafted with Ingots of %s, %s, and an Ender Chest.")
        // Inputs: Chronon Cell, Chronon Battery
        val metronomeBooting = bookLang("metronome_booting", "Generally, Metronomes cannot be charged within the inventory, and instead need to be placed into the world. Additionally, there is an initial charge of Chronons needed to boot the device and gain access to its functionality. This boot charge can be provided from a %s or %s in hand by crouch-right-clicking the metronome.")
        val metronomeBattery = bookLang("metronome_battery", "Once booted, the Metronome will begin to generate Chronons into its own internal battery. Right clicking the device will bring up the interface, where devices can be placed to store Chronons via the left-hand slots or take Chronons via the right-hand slots. Additionally, the Metronome is attuned to the user’s temporal aura, and may be locked to prevent unauthorized access via the lock button in the upper right. It is locked by default.")
        val metronomeSyncing = bookLang("metronome_syncing", "If the user places additional metronomes, after booting they will link with existing metronomes owned by the user in a multiversal network, combining their maximum storage capacities. Depending on configuration, Metronomes can also combine charging power to generate more Chronons with each pulse. If your Metronomes in your reality lack this charge-combining function, please submit a support ticket to TVA Repairs & Advancement.")

        fun bookLang(key: String, value: String): String {
            val finalKey = ModItems.handbook.descriptionId + "." + key
            val shouldBeNull = entries.put(finalKey, value)
            if (shouldBeNull != null) {
                throw IllegalStateException("Duplicate translation key $finalKey")
            }
            return finalKey
        }
    }

    override fun addTranslations() {
        for ((key, value) in entries) {
            add(key, value)
        }

        for (entry in ModItems.registry.entries) {
            try {
                add(entry.get(), entry.id.formatted)
            } catch (_: Exception) {}
        }

        for (entry in ModBlocks.blocks.entries) {
            try {
                add(entry.get(), entry.id.formatted)
            } catch (_: Exception) {}
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
            it.addSub("left_right.desc", "Positive places portal to your left, negative to your right \n\nAccepts decimal (e.g 1.3)")
            it.addSub("up_down", "U/D: ")
            it.addSub("up_down.desc", "Positive places portal higher, negative lower \n\nAccepts decimal (e.g 1.3)")
            it.addSub("front_back", "F/B: ")
            it.addSub("front_back.desc", "Positive places portal in front of you, negative behind you \n\nAccepts decimal (e.g 1.3)")
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
    }

    val ResourceLocation.formatted get() = path.split('_').joinToString(" ") { it.replaceFirstChar { it.uppercaseChar() } }

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
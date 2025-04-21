package earth.terrarium.tempad.common.compat

import com.mojang.authlib.GameProfile
import earth.terrarium.cadmus.api.flags.FlagApi
import earth.terrarium.cadmus.api.flags.types.BooleanFlag
import earth.terrarium.cadmus.api.protections.Protection
import earth.terrarium.cadmus.api.teams.TeamId
import earth.terrarium.cadmus.common.protections.Protections
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.event.TimedoorEvent
import earth.terrarium.tempad.common.location_handlers.AnchorPointsHandler
import earth.terrarium.tempad.common.utils.translatable
import net.minecraft.core.BlockPos
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.Level
import net.neoforged.neoforge.common.NeoForge

object CadmusCompat {
    var timedoorGameRule: GameRules.Key<GameRules.BooleanValue>? = null
    var timedoorFlag: BooleanFlag? = null

    fun init() {
        timedoorGameRule = GameRules.register(
            "allowTimedoorsInClaims", GameRules.Category.MISC, GameRules.BooleanValue.create(true)
        )

        timedoorFlag = FlagApi.API.register(BooleanFlag("timedoors-summoning", true))

        val protection = Protections.register(TimedoorProtection)

        NeoForge.EVENT_BUS.addListener { event: TimedoorEvent.Open ->
            val player = event.opener
            val level = event.entity.level()
            val targetLevel = Tempad.server?.getLevel(event.entity.targetDimension) ?: return@addListener
            val canOpenAtStarting = protection.canOpenTimedoor(level, player, event.entity.blockPosition())
            val canOpenAtTarget = protection.canOpenTimedoor(targetLevel, player, BlockPos.containing(event.entity.targetPos))
            if (!canOpenAtStarting) {
                event.fail("cadmus.protection.timedoor_summoning.starting".translatable)
            }
            if (!canOpenAtTarget && event.provider != AnchorPointsHandler.ID) {
                event.fail("cadmus.protection.timedoor_summoning.target".translatable)
            }
        }
    }
}

object TimedoorProtection : Protection {
    override fun setting(): String = "allowTimedoorSummoning"

    override fun permission(): String = "tempad.timedoor_summoning"

    override fun personalPermission(): String = "tempad.personal.timedoor_summoning"

    override fun flag(): BooleanFlag? = CadmusCompat.timedoorFlag

    override fun gameRule(): GameRules.Key<GameRules.BooleanValue>? = CadmusCompat.timedoorGameRule

    fun canOpenTimedoor(level: Level, player: GameProfile, pos: BlockPos): Boolean {
        return level.isClientSide() || this.getId(level, pos)
            .map<Boolean?> { id: TeamId? -> this.isPlayerAllowed(level, player, id) }.orElse(true)
    }
}
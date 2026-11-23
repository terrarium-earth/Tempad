package earth.terrarium.tempad.common.network

import earth.terrarium.tempad.Tempad
import net.minecraft.world.entity.player.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Per-player cooldowns for C2S packets whose handlers do real work (building location lists, opening menus,
 * opening timedoors). A normal client never hits these limits — keybinds go through consumeClick and buttons are
 * clicked by hand — but a modified client could otherwise send thousands per second.
 *
 * Packets that only store a few values after validation (settings, locks, marker edits) are cheap and are deliberately
 * not throttled, since dropping them would lose the user's last change.
 */
enum class PacketThrottle(val cooldownTicks: Int) {
    /** OpenTempad / OpenApp / RedirectApp / OpenWallet share one bucket so alternating between them doesn't bypass it. */
    OPEN_MENU(5),
    MACRO(5),
    OPEN_TIMEDOOR(5),
    WRITE_CARD(5),
    EDIT_LOCATION(4),
    BACKTRACK(5);

    companion object {
        private val lastUse = ConcurrentHashMap<UUID, LongArray>()

        fun forget(player: UUID) {
            lastUse.remove(player)
        }
    }

    /**
     * Returns true if the packet should be dropped. Otherwise records the use and returns false.
     */
    fun isThrottled(player: Player): Boolean {
        val now = (Tempad.server?.tickCount ?: return false).toLong()
        val times = lastUse.computeIfAbsent(player.uuid) { LongArray(entries.size) { Long.MIN_VALUE / 2 } }
        if (now - times[ordinal] < cooldownTicks) return true
        times[ordinal] = now
        return false
    }
}

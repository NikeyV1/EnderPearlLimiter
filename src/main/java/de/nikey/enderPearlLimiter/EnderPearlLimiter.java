package de.nikey.enderPearlLimiter;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import de.nikey.enderPearlLimiter.util.SpamTracker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class EnderPearlLimiter extends JavaPlugin implements Listener {

    private final Map<UUID, SpamTracker> pearlTracker = new HashMap<>();
    private int maxPearls;
    private int timeWindow;
    private TextComponent spamWarningMessage;
    private boolean cancelEvent;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadSettings();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        pearlTracker.clear();
    }

    private void reloadSettings() {
        FileConfiguration config = getConfig();
        maxPearls = config.getInt("max-pearls", 4);
        cancelEvent = config.getBoolean("cancel-event", true);
        timeWindow = config.getInt("time-window", 5) * 1000; // Sekunden zu Millisek
        String message = getConfig().getString("spam-warning-message", "&cYou are throwing ender pearls too fast!");
        spamWarningMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }

    @EventHandler
    public void onPlayerThrowEnderPearl(PlayerLaunchProjectileEvent event) {
        if (!(event.getProjectile() instanceof EnderPearl)) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        SpamTracker tracker = pearlTracker.computeIfAbsent(uuid, k -> new SpamTracker(timeWindow, maxPearls));

        if (tracker.increment()) {
            player.sendMessage(spamWarningMessage);
            if (cancelEvent) {
                event.setCancelled(true);
            }
        }
    }
}

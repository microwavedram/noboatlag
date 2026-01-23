package uk.cloudmc.microwavedram.noboatlag;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

public final class NoBoatLag extends JavaPlugin implements PacketListener, Listener, CommandExecutor {

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.VEHICLE_MOVE) {
            Player player = event.getPlayer();

            if (player.getVehicle() instanceof Boat boat) {
                if (boat.getPassengers().getFirst() != player) return;

                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (getConfig().getBoolean("open_boat_utils_interpolation_fix")) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            DataOutputStream packet = new DataOutputStream(stream);

            // https://github.com/o7Moon/OpenBoatUtils/wiki/Packets#set-ten-step-interpolation
            try {
                packet.writeShort(29); // PacketId
                packet.writeShort(1); // Enabled
            } catch (IOException e) {
                return;
            }

            event.getPlayer().sendPluginMessage(
                    this,
                    "openboatutils:settings",
                    stream.toByteArray()
            );
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String @NotNull [] args) {
        if (command.getName().equalsIgnoreCase("noboatlag")) {

            if (!sender.hasPermission("noboatlag.reload")) {
                sender.sendMessage(Component.text("Missing Permission", NamedTextColor.RED));
                return true;
            }

            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                reloadConfig();
                sender.sendMessage("NoBoatLag: Configuration reloaded.");
                return true;
            } else {
                sender.sendMessage("Usage: /noboatlag reload");
                return true;
            }
        }

        return false;
    }


    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();

        PacketEvents.getAPI().getEventManager().registerListener(this, PacketListenerPriority.NORMAL);
    }

    @Override
    public void onEnable() {
        PacketEvents.getAPI().init();

        getServer().getPluginManager().registerEvents(this, this);

        Objects.requireNonNull(getCommand("noboatlag")).setExecutor(this);

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "openboatutils:settings");

        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
    }
}

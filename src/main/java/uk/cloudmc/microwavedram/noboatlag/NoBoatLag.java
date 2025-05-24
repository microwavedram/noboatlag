package uk.cloudmc.microwavedram.noboatlag;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.TreeSpecies;
import org.bukkit.craftbukkit.v1_21_R3.CraftServer;
import org.bukkit.craftbukkit.v1_21_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftBoat;
import org.bukkit.entity.Boat;
import org.bukkit.entity.ChestBoat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Optional;

public final class NoBoatLag extends JavaPlugin implements Listener {

    // Cancel the placement of boats, and instead spawn one of our Collisionless ones.
    @EventHandler
    public void onEntityPlace(EntityPlaceEvent entityPlaceEvent) {
        if (entityPlaceEvent.getEntity() instanceof Boat && !(entityPlaceEvent.getEntity() instanceof ChestBoat)) {
            Boat boat = (Boat) entityPlaceEvent.getEntity();

            EntityType type = ((CraftBoat) boat).getHandle().getType();

            spawnBoat(boat.getLocation(), type);

            getLogger().info(type.toString());

            Player player = entityPlaceEvent.getPlayer();
            PlayerInventory inventory = player.getInventory();

            if (player.getGameMode() != GameMode.CREATIVE) {
                if (entityPlaceEvent.getHand() == EquipmentSlot.HAND) {
                    inventory.setItemInMainHand(null);
                } else if (entityPlaceEvent.getHand() == EquipmentSlot.OFF_HAND) {
                    inventory.setItemInOffHand(null);
                }
            }

            // Prevent the real boat from spawning
            entityPlaceEvent.setCancelled(true);
        }
    }

    // Spawn one of our collisionless boats at a location.
    public void spawnBoat(Location location, net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.vehicle.Boat> entityType) {
        ServerLevel level = ((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle();

        CollisionlessBoat boat = new CollisionlessBoat(entityType, level, () -> Items.DIRT);
        float yaw = Location.normalizeYaw(location.getYaw());
        boat.setYRot(yaw);
        boat.yRotO = yaw;
        boat.setYHeadRot(yaw);

        boat.teleportTo(location.getX(), location.getY(), location.getZ());

        level.addFreshEntity(boat, CreatureSpawnEvent.SpawnReason.COMMAND);
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {}
}

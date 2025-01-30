package uk.cloudmc.microwavedram.noboatlag;

import net.minecraft.server.level.ServerLevel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R1.CraftServer;
import org.bukkit.craftbukkit.v1_21_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftBoat;
import org.bukkit.entity.Boat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class NoBoatLag extends JavaPlugin implements Listener {

    // Cancel the placement of boats, and instead spawn one of our Collisionless ones.
    @EventHandler
    public void onEntityPlace(EntityPlaceEvent entityPlaceEvent) {
        if (entityPlaceEvent.getEntity() instanceof Boat) {
            Boat boat = (Boat) entityPlaceEvent.getEntity();

            // Passing Enum ordinal as java refuses to cast the type to the NMS one.
            spawnBoat(boat.getLocation(), boat.getBoatType().ordinal());

            // Prevent the real boat from spawning
            entityPlaceEvent.setCancelled(true);
        }
    }

    // Spawn one of our collisionless boats at a location.
    public CraftBoat spawnBoat(Location location, int boat_type) {
        ServerLevel level = ((CraftWorld) location.getWorld()).getHandle();

        CollisionlessBoat boat = new CollisionlessBoat(level, location.getX(), location.getY(), location.getZ());
        float yaw = Location.normalizeYaw(location.getYaw());
        boat.setYRot(yaw);
        boat.yRotO = yaw;
        boat.setYHeadRot(yaw);

        // Manual Enum casting (internal NMS type)
        boat.setVariant(net.minecraft.world.entity.vehicle.Boat.Type.byId(boat_type));

        level.addFreshEntity(boat, CreatureSpawnEvent.SpawnReason.COMMAND);

        return new CraftBoat((CraftServer) Bukkit.getServer(), boat);
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {}
}

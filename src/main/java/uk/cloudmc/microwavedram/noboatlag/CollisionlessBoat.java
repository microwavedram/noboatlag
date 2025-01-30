package uk.cloudmc.microwavedram.noboatlag;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;

// https://github.com/FrostHexABG/TimingSystemNoBoatCollisions/blob/master/spigot-1.21.1/src/main/java/com/frosthex/timingsystem/noboatcollisions/nms_1_21_1/CollisionlessBoat.java
// BoatLag fix from TimingSystem (Primary plugin on main boatracing servers)

// **Boat Lag Explaination**
// Boat collisions are handled fully client-side, but the server also does collision checks.
// The boat seen by players is the "interpolated model", which lags behind the server position of the boat due to ping and the movement interpolation (and "fucky mojang code")
// The server and client will calculate collisions between boats, but the server calculations go off the server position of the boat, which is far
// ahead of where the interpolated boat is. This causes scuffed rubber banding when your interpolated boat is where the server position of another boat is, which we
// have lovingly named "Boat lag"
// By disabling the server collision checks between boats, this problem no longer occurs and boats stop bugging out due to the position discrepency.

// This collision method is only used between boats with players in them, and static/player boats.
// Boats just sitting around use a different collision method, which still works vannila.

public class CollisionlessBoat extends Boat {
    public CollisionlessBoat(Level world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    // Force all collision checks to fail
    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }
}

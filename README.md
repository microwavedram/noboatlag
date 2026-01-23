# No Boat Lag
A paper plugin that fixes buggy behaviour when boats collide, intended for use when Ice Boat Racing.

## Boat Lag Explanation
Boat collisions are handled by the client, but the server also does collision checks.
The boat seen by players is the "interpolated model", which lags behind the actual position of the boat due to ping and the movement interpolation's natural position offset (and some amount of mojang jank)

The boat driver's client's collision calculations handle everything required, but the server will still do collision checks between the driver's boat and the real position of other boats which results in the server sending `VehicleMove` packets to the driver which will temporarily freeze the driver in place and/or cause some really unsatisfactory rubberbanding.


This plugin disables the packet sent and removes all the buggy behavior, a similar fix is in place on most major Ice Boat Racing servers.

---
Full Credit to Makkuusen for coming up with the fix. ~~It has been pulled from the TimingSystem plugin and put into its own package for use on other servers, where the full TimingSystem plugin is severely overkill.~~
As of 2.0.0 the fix now uses a new approach with packet events.



package me.Fupery.ArtMap.Compatability;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.engine.EnginePermBuild;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.MassiveCore;
import com.massivecraft.massivecore.ps.PS;
import me.Fupery.ArtMap.Easel.EaselEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

class FactionsCompat implements RegionHandler {
    private final boolean loaded;

    FactionsCompat() {
        Factions.get();
        EnginePermBuild.get();
        MassiveCore.get();
        loaded = true;
    }

    @Override
    public boolean checkBuildAllowed(Player player, Location location) {
        PS loc = PS.valueOf(location);
        Faction hostFaction = BoardColl.get().getTerritoryAccessAt(loc).getHostFaction();
        return hostFaction == null || EnginePermBuild.canPlayerBuildAt(player, loc, true);
    }

    @Override
    public boolean checkInteractAllowed(Player player, Entity entity, EaselEvent.ClickType click) {
        PS loc = PS.valueOf(entity.getLocation());
        Faction hostFaction = BoardColl.get().getTerritoryAccessAt(loc).getHostFaction();
        return hostFaction == null || EnginePermBuild.canPlayerUseEntity(player, entity, true);
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}

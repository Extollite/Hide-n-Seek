package pl.extollite.hidenseek.data;

import cn.nukkit.block.Block;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector2;
import lombok.Getter;
import java.util.Map;

@Getter
public class MapEntry {
    private String name;
    private Location spawnSeekers;
    private Vector2 vec2Seekers;
    private Location spawnHiders;
    private Map<String, Block> mapBlocks;

    public MapEntry(String name, Location spawnSeekers, Location spawnHiders, Map<String, Block> mapBlocks) {
        this.name = name;
        this.spawnSeekers = spawnSeekers;
        this.spawnHiders = spawnHiders;
        vec2Seekers = new Vector2(spawnSeekers.x, spawnSeekers.z);
        this.mapBlocks = mapBlocks;
    }

    public MapEntry(String name, Map<String, Block> mapBlocks) {
        this.name = name;
        this.mapBlocks = mapBlocks;
    }

    public void setSpawnSeekers(Location spawnSeekers) {
        vec2Seekers = new Vector2(spawnSeekers.x, spawnSeekers.z);
        this.spawnSeekers = spawnSeekers;
    }

    public void setSpawnHiders(Location spawnHiders) {
        this.spawnHiders = spawnHiders;
    }
}

package me.usainsrht.utimber.model;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Objects;
import java.util.Set;

public class Tree {

    public Set<Material> logs;
    public Set<Material> leaves;
    public boolean diagonalCardinal;
    public boolean diagonalDiagonal;
    public int leafDistanceX;
    public int leafDistanceY;
    public boolean largeLog;
    public int maxLogHeight;
    public int minLogs;
    public int minLeaves;

    public Tree(ConfigurationSection config) {
        logs = config.getStringList("logs").stream().map(Material::matchMaterial).filter(Objects::nonNull).collect(java.util.stream.Collectors.toSet());
        leaves = config.getStringList("leaves").stream().map(Material::matchMaterial).filter(Objects::nonNull).collect(java.util.stream.Collectors.toSet());
        diagonalCardinal = config.getBoolean("diagonal_cardinal", false);
        diagonalDiagonal = config.getBoolean("diagonal_diagonal", false);
        leafDistanceX = config.getInt("leaf_distance_x", 2);
        leafDistanceY = config.getInt("leaf_distance_y", 3);
        largeLog = config.getBoolean("large_log", false);
        maxLogHeight = config.getInt("max_log_height", 7);
        minLogs = config.getInt("min_logs", 4);
        minLeaves = config.getInt("min_leaves", 12);
    }

    public enum Part {
        LOG, LEAF
    }

}

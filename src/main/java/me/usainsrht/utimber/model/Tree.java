package me.usainsrht.utimber.model;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Objects;
import java.util.Set;

public class Tree {

    public String name;
    public Set<Material> logs;
    public Set<Material> leaves;
    public Material sapling;
    public int leafDistanceX;
    public int leafDistanceY;
    public boolean largeLog;
    public int logDistanceY;
    public int logDistanceX;
    public int minLogs;
    public int minLeaves;
    public boolean separateLeaves;
    public boolean diagonalLeaves;

    public Tree(String name, ConfigurationSection config) {
        this.name = name;
        logs = config.getStringList("logs").stream().map(Material::matchMaterial).filter(Objects::nonNull).collect(java.util.stream.Collectors.toSet());
        leaves = config.getStringList("leaves").stream().map(Material::matchMaterial).filter(Objects::nonNull).collect(java.util.stream.Collectors.toSet());
        sapling = Material.matchMaterial(config.getString("sapling", "AIR"));
        leafDistanceX = config.getInt("leaf_distance_x", 2);
        leafDistanceY = config.getInt("leaf_distance_y", 2);
        largeLog = config.getBoolean("large_log", false);
        logDistanceY = config.getInt("log_distance_y", 7);
        logDistanceX = config.getInt("log_distance_x", 4);
        minLogs = config.getInt("min_logs", 4);
        minLeaves = config.getInt("min_leaves", 12);
        separateLeaves = config.getBoolean("separate_leaves", false);
        diagonalLeaves = config.getBoolean("diagonal_leaves", false);
    }

    public enum Part {
        LOG, LEAF
    }

}

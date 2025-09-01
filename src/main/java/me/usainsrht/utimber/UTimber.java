package me.usainsrht.utimber;

import me.usainsrht.utimber.command.CommandHandler;
import me.usainsrht.utimber.command.UTimberCommand;
import me.usainsrht.utimber.listener.BreakListener;
import me.usainsrht.utimber.listener.FallingBlockListener;
import me.usainsrht.utimber.model.Tree;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public final class UTimber extends JavaPlugin {

    public static UTimber instance;
    public Set<Tree> trees;
    public boolean debug;

    @Override
    public void onEnable() {
        instance = this;

        trees = new HashSet<>();

        saveDefaultConfig();
        loadConfig();

        getServer().getPluginManager().registerEvents(new BreakListener(), this);
        getServer().getPluginManager().registerEvents(new FallingBlockListener(), this);

        CommandHandler.register(new UTimberCommand("utimber"));
    }

    @Override
    public void onDisable() {

    }

    public void reload() {
        reloadConfig();

        loadConfig();
    }

    public void loadConfig() {
        trees.clear();

        if (getConfig().isConfigurationSection("tree")) {
            ConfigurationSection treeSection = getConfig().getConfigurationSection("tree");
            treeSection.getKeys(false).forEach(key -> {
                Tree tree = new Tree(key, treeSection.getConfigurationSection(key));
                trees.add(tree);
            });

        }

    }


}

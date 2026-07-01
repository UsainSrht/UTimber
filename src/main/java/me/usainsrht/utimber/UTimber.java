package me.usainsrht.utimber;

import me.usainsrht.utimber.command.CommandHandler;
import me.usainsrht.utimber.command.UTimberCommand;
import me.usainsrht.utimber.listener.BreakListener;
import me.usainsrht.utimber.listener.DamageListener;
import me.usainsrht.utimber.listener.FallingBlockListener;
import me.usainsrht.utimber.model.Tree;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import space.arim.morepaperlib.MorePaperLib;
import space.arim.morepaperlib.scheduling.GracefulScheduling;

import java.util.HashSet;
import java.util.Set;

public final class UTimber extends JavaPlugin {

    public static UTimber instance;
    public Set<Tree> trees;
    public boolean debug;
    private MorePaperLib morePaperLib;

    @Override
    public void onEnable() {
        instance = this;
        morePaperLib = new MorePaperLib(this);

        trees = new HashSet<>();

        saveDefaultConfig();
        loadConfig();

        getServer().getPluginManager().registerEvents(new BreakListener(), this);
        getServer().getPluginManager().registerEvents(new FallingBlockListener(), this);
        getServer().getPluginManager().registerEvents(new DamageListener(), this);

        CommandHandler.register(this, new UTimberCommand("utimber"));

        int pluginId = 30278;
        Metrics metrics = new Metrics(this, pluginId);
        //todo add api like tree destroy event, cancellable etc
    }

    @Override
    public void onDisable() {
        scheduling().cancelGlobalTasks();
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

    public MorePaperLib morePaperLib() {
        return morePaperLib;
    }

    public GracefulScheduling scheduling() {
        return morePaperLib.scheduling();
    }

    /**
     * @return whether timber is enabled for the player.
     */
    public boolean isTimberEnabled(org.bukkit.entity.Player player) {
        return !getConfig().getStringList("timber_disabled").contains(player.getUniqueId().toString());
    }

    /**
     * Enables or disables timber for the given player.
     */
    public void setTimberEnabled(org.bukkit.entity.Player player, boolean enabled) {
        String uuid = player.getUniqueId().toString();
        java.util.List<String> disabledList = getConfig().getStringList("timber_disabled");

        if (enabled) {
            if (disabledList.remove(uuid)) {
                getConfig().set("timber_disabled", disabledList);
                saveConfig();
            }
            return;
        }

        if (!disabledList.contains(uuid)) {
            disabledList.add(uuid);
            getConfig().set("timber_disabled", disabledList);
            saveConfig();
        }
    }


}

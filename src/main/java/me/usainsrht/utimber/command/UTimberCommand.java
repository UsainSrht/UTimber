package me.usainsrht.utimber.command;

import me.usainsrht.utimber.UTimber;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UTimberCommand extends Command {

    public UTimberCommand(String name) {
        super(name);
        this.setAliases(Arrays.asList("timber"));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            if (sender.hasPermission("utimber.reload")) completions.add("reload");
            if (sender.hasPermission("utimber.debug")) completions.add("debug");
            return completions;
        }
        return super.tabComplete(sender, alias, args);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                boolean enabled = !UTimber.instance.isTimberEnabled(player);
                UTimber.instance.setTimberEnabled(player, enabled);
                sender.sendMessage("utimber " + (enabled ? "enabled" : "disabled"));
            } else {
                sender.sendMessage("Only players can use this command");
            }
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("reload") && sender.hasPermission("utimber.reload")) {
            UTimber.instance.reload();
            sender.sendMessage("utimber reloaded");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("debug") && sender.hasPermission("utimber.debug")) {
            UTimber.instance.debug = !UTimber.instance.debug;
            sender.sendMessage("utimber debug " + (UTimber.instance.debug ? "enabled" : "disabled"));
            return true;
        }
        return true;
    }


}

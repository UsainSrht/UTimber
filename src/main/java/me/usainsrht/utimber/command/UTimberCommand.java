package me.usainsrht.utimber.command;

import me.usainsrht.utimber.UTimber;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class UTimberCommand extends Command {

    public UTimberCommand(String name) {
        super(name);
        this.setAliases(List.of("timber"));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
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

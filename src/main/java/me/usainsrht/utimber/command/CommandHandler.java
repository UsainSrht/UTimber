package me.usainsrht.utimber.command;

import me.usainsrht.utimber.UTimber;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;

public class CommandHandler {

    public static void register(UTimber plugin, Command... cmds) {
        CommandMap commandMap = plugin.morePaperLib().commandRegistration().getServerCommandMap();
        for (Command cmd : cmds) {
            commandMap.register(cmd.getName(), cmd);
        }
    }
}

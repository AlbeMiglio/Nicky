package it.albemiglio.nicky.commands.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class SubCommand {
    private final String name;

    private final String permission;

    public SubCommand(String name, String permission) {
        this.name = name;
        this.permission = permission;
    }

    public String getName() {
        return this.name;
    }

    public String getPermission() {
        return this.permission;
    }

    public abstract boolean onCommand(CommandSender sender, Command cmd, String label, String[] args);
}

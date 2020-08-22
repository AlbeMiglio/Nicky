package it.albemiglio.nicky;

import it.albemiglio.nicky.commands.DelNickCMD;
import it.albemiglio.nicky.commands.NickCMD;
import it.albemiglio.nicky.commands.NickyCMD;
import it.albemiglio.nicky.commands.RealNameCMD;
import it.albemiglio.nicky.databases.MySQL;
import it.albemiglio.nicky.databases.SQL;
import it.albemiglio.nicky.databases.SQLite;
import it.mycraft.powerlib.PowerLib;
import lombok.Getter;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class Nicky extends JavaPlugin {

    private static Permission VAULT_PERMS;

    @Getter
    private static Nicky instance;

    @Getter
    private static SQL database;

    @Getter
    private final Set<SQL> databases = new HashSet<>();

    @Getter
    private FileManager fileManager;

    public void onEnable() {
        instance = this;
        this.fileManager = new FileManager(this);

        this.databases.add(new MySQL(this));
        this.databases.add(new SQLite(this));

        Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        getCommand("nick").setExecutor(new NickCMD(this));
        getCommand("delnick").setExecutor(new DelNickCMD(this));
        getCommand("realname").setExecutor(new RealNameCMD());
        getCommand("nicky").setExecutor(new NickyCMD(this));
        if (!setupVault()) {
            log("Error connecting to Vault, make sure it's installed!");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (!setupPowerLib()) {
            log("Error connecting to PowerLib, make sure it's installed!");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (!setupDatabase()) {
            log("Error with database, are your details correct?");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }
    }

    public void onDisable() {
        database.disconnect();
    }

    private boolean setupDatabase() {
        String type = getConfig().getString("type");
        database = null;
        for (SQL database : this.databases) {
            if (type.equalsIgnoreCase(database.getConfigName())) {
                Nicky.database = database;
                log("Database set to " + database.getConfigName() + ".");
                break;
            }
        }
        if (database == null)
            log("Database type does not exist!");
        return database.checkConnection();
    }

    private boolean setupVault() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp != null)
            VAULT_PERMS = rsp.getProvider();
        return (VAULT_PERMS != null);
    }

    private boolean setupPowerLib() {
        return Bukkit.getServer().getPluginManager().isPluginEnabled(PowerLib.getInstance());
    }

    public static Permission getVaultPerms() {
        return VAULT_PERMS;
    }

    public void log(String message) {
        getLogger().info(message);
    }
}


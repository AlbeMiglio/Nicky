package it.albemiglio.nicky.databases;

import it.albemiglio.nicky.Nicky;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySQL extends SQL {
    public MySQL(Nicky plugin) {
        super(plugin);
    }

    protected Connection getNewConnection() {
        FileConfiguration fileConfiguration = this.plugin.getFileManager().getConfig();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://" +
                    fileConfiguration.getString("host") + ":" +
                    fileConfiguration.getString("port") + "/" +
                    fileConfiguration.getString("database");
            return DriverManager.getConnection(url,
                    fileConfiguration.getString("user"),
                    fileConfiguration.getString("password"));
        } catch (Exception e) {
            return null;
        }
    }

    public String getName() {
        return "MySQL";
    }
}


package it.albemiglio.nicky.databases;

import it.albemiglio.nicky.Nicky;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class SQL {
    private Connection connection;

    private HashMap<String, String> cache = new HashMap<>();

    protected Nicky plugin;

    public SQL(Nicky plugin) {
        this.plugin = plugin;
    }

    protected abstract Connection getNewConnection();

    protected abstract String getName();

    public String getConfigName() {
        return getName().toLowerCase().replace(" ", "");
    }

    private ArrayList<HashMap<String, String>> query(String sql, boolean hasReturn) {
        if (!checkConnection()) {
            this.plugin.getLogger().info("Error with database");
            return null;
        }
        try {
            PreparedStatement statement = this.connection.prepareStatement(sql);
            if (!hasReturn) {
                statement.execute();
                return null;
            }
            ResultSet set = statement.executeQuery();
            ResultSetMetaData md = set.getMetaData();
            int columns = md.getColumnCount();
            ArrayList<HashMap<String, String>> list = new ArrayList<>(50);
            while (set.next()) {
                HashMap<String, String> row = new HashMap<>(columns);
                for (int i = 1; i <= columns; i++)
                    row.put(md.getColumnName(i), set.getObject(i).toString());
                list.add(row);
            }
            if (list.isEmpty())
                return null;
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean checkConnection() {
        try {
            if (this.connection == null || this.connection.isClosed()) {
                this.connection = getNewConnection();
                if (this.connection == null || this.connection.isClosed())
                    return false;
                updateTables();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void updateTables() {
        query("CREATE TABLE IF NOT EXISTS nicky (uuid varchar(36) NOT NULL, nick varchar(64) NOT NULL, name varchar(32) NOT NULL, PRIMARY KEY (uuid))", false);
    }

    public void disconnect() {
        this.cache.clear();
        try {
            if (this.connection != null)
                this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String downloadNick(String uuid) {
        if (this.cache.containsKey(uuid))
            return this.cache.get(uuid);
        ArrayList<HashMap<String, String>> data = query("SELECT nick FROM nicky WHERE uuid = '" + uuid + "';", true);
        if (data == null) {
            this.cache.put(uuid, null);
            return null;
        }
        String nick = (String)((HashMap)data.get(0)).get("nick");
        this.cache.put(uuid, nick);
        return nick;
    }

    public List<SearchedPlayer> searchNicks(String search) {
        List<SearchedPlayer> results = new ArrayList<>();
        String sqlSearch = "%";
        for (char c : search.toCharArray())
            sqlSearch = sqlSearch + c + "%";
        ArrayList<HashMap<String, String>> data = query("SELECT uuid, nick, name FROM nicky WHERE nick LIKE '" + sqlSearch + "';", true);
        if (data == null)
            return null;
        for (HashMap<String, String> row : data)
            results.add(new SearchedPlayer(row.get("uuid"), row.get("nick"), row.get("name")));
        return results;
    }

    public class SearchedPlayer {
        private String uuid;

        private String nick;

        private String name;

        public SearchedPlayer(String uuid, String nick, String name) {
            this.uuid = uuid;
            this.nick = nick;
            this.name = name;
        }

        public String getUuid() {
            return this.uuid;
        }

        public String getNick() {
            return this.nick;
        }

        public String getName() {
            return this.name;
        }
    }

    public boolean isUsed(String nick) {
        ArrayList<HashMap<String, String>> data = query("SELECT nick FROM nicky WHERE nick = '" + nick + "';", true);
        return (data != null);
    }

    public void removeFromCache(String uuid) {
        if (this.cache.containsKey(uuid))
            this.cache.remove(uuid);
    }

    public void uploadNick(String uuid, String nick, String name) {
        this.cache.put(uuid, nick);
        query("INSERT INTO nicky (uuid, nick, name) VALUES ('" + uuid + "','" + nick + "','" + name + "');", false);
    }

    public void deleteNick(String uuid) {
        this.cache.put(uuid, null);
        query("DELETE FROM nicky WHERE uuid = '" + uuid + "';", false);
    }

    public void updatePlayerName(String uuid, String name) {
        query("UPDATE nicky SET name = '" + name + "' WHERE uuid = '" + uuid + "';", false);
    }
}


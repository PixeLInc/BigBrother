package io.pixelinc.bigbrother.database;

import io.pixelinc.bigbrother.BigBrother;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class MySQL {

    private SqlService sql;
    private String connectionURL;
    private BigBrother plugin;

    public Connection activeConnection;

    public MySQL(BigBrother plugin) {
        this.plugin = plugin;

        this.connectionURL = "jdbc:mysql://"
                + this.plugin.configManager.getCore().mUsername
                + ":"
                + this.plugin.configManager.getCore().mPassword
                + "@"
                + this.plugin.configManager.getCore().mHost
                + ":"
                + this.plugin.configManager.getCore().mPort
                + "/"
                + this.plugin.configManager.getCore().mDbName;
    }

    private DataSource getDataSource(String jdbcUrl) throws SQLException {
        if (sql == null)
            sql = Sponge.getServiceManager().provide(SqlService.class).get();

        return sql.getDataSource(jdbcUrl);
    }

    public Connection getConnection() {
        if (activeConnection == null || isClosed())
            openConnection();


        return activeConnection;
    }

    public void openConnection() {
        try {
            if (activeConnection != null && !activeConnection.isClosed())
                return;

           activeConnection = getDataSource(this.connectionURL).getConnection();
           plugin.logger.info("New MySQL Connection Spawned");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isClosed() {
        try {
            return this.activeConnection == null || this.activeConnection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    // create table block_logs ( id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT, uuid CHAR(36) NOT NULL, location TEXT, event varchar(50), block TEXT, PRIMARY KEY (id), KEY (uuid));
}

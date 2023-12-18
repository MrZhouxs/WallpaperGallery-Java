//package com.kerwin.gallery.config;
//
//import org.flywaydb.core.Flyway;
//import org.springframework.context.annotation.Configuration;
//
//import javax.annotation.PostConstruct;
//import javax.sql.DataSource;
//import java.sql.SQLException;
//import java.util.Locale;
//
///**
// * ==============================================================================
// * Author:       Kerwin
// * Created:      2023/11/21
// * Description:
// * ==============================================================================
// */
//@Configuration
//public class FlywayDbInitializer {
//
//    private final DataSource dataSource;
//
//    public FlywayDbInitializer(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }
//
//    @PostConstruct
//    public void migrateFlyway() throws SQLException {
//        String databaseProductName = dataSource.getConnection().getMetaData().getDatabaseProductName();
//        String dbType = databaseProductName.toLowerCase(Locale.ROOT);
//        System.out.println(dbType);
//        Flyway load = Flyway.configure().dataSource(dataSource).outOfOrder(true).locations("db/migration-" + dbType).load();
//        load.migrate();
//    }
//}

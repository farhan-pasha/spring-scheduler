package com.keembay.scheduler.config;


import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@Profile(value = {"prod", "dev"})
public class DatabaseConfiguration {

    @Value("${maxPoolSize:10}")
    private int maxPoolSize;

    @Value("${minimumIdle:10}")
    private int minimumIdle;

    @Bean
    public HikariDataSource getDataSource() throws URISyntaxException {

        URI dbUri = new URI(System.getenv("CLEARDB_DATABASE_URL"));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:mysql://" + dbUri.getHost() + dbUri.getPath();

        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("com.mysql.jdbc.Driver");
        dataSourceBuilder.url(dbUrl+"?autoReconnect=true");
        dataSourceBuilder.username(username);
        dataSourceBuilder.password(password);
        dataSourceBuilder.type(HikariDataSource.class);
        HikariDataSource hikariDataSource = (HikariDataSource) dataSourceBuilder.build();
        hikariDataSource.setMaximumPoolSize(maxPoolSize);
        hikariDataSource.setMinimumIdle(minimumIdle);
        return hikariDataSource;
    }

}
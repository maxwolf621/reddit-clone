
package com.pttbackend.pttclone.config.databaseconfiguration;

import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.pttbackend.pttclone.config.databaseconfiguration.property.HakariProperties;
import com.pttbackend.pttclone.config.databaseconfiguration.property.HakariReadProperties;
import com.pttbackend.pttclone.config.databaseconfiguration.property.HakariWriteProperties;
import com.pttbackend.pttclone.config.databaseconfiguration.property.MasterProperties;
import com.pttbackend.pttclone.config.databaseconfiguration.property.SlaveProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.pttbackend.pttclone", 
    transactionManagerRef = "transactionManager", 
    entityManagerFactoryRef = "entityManager"
)
@EnableTransactionManagement
@RequiredArgsConstructor
@Slf4j
public class DataSourceConfig {
    private final String SCAN_PACKAGE = "com.pttbackend.pttclone";
    private final SlaveProperties slaveProperties;
    private final MasterProperties masterProperties;
    private final HakariReadProperties hakariReadProperties;
    private final HakariWriteProperties hakariWriteProperties;

    // Slave & Master DataSource Configuration
    @Primary
    @Bean(name ="masterDataSource")
    public DataSource masterDataSourceConfig(){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(masterProperties.getUrl());
        config.setUsername(masterProperties.getUsername());
        config.setPassword(masterProperties.getPassword());
        config.setReadOnly(false);
        config.setPoolName("HAKARI_WRITE");
        config.setAutoCommit(hakariWriteProperties.getAutoCommit());
        config.setMaximumPoolSize(hakariWriteProperties.getMaximumPoolSize());
        config.setIdleTimeout(hakariWriteProperties.getIdleTimeout());
        config.setMinimumIdle(hakariWriteProperties.getMinimumIdle());
        config.setMaxLifetime(hakariWriteProperties.getMaxLifetime());
        config.setConnectionTimeout(hakariWriteProperties.getConnectionTimeout());
    

        return new HikariDataSource(config);
        /* 
        return DataSourceBuilder
            .create()
            .driverClassName("com.mysql.cj.jdbc.Driver")
            .url(
                masterProperties.getUrl()
            ).username(
                masterProperties.getUsername()
            ).password(
                masterProperties.getPassword()
            )
            .build();
            */
    }

    @Bean(name = "slaveDataSource")
    public DataSource slaveDataSourceConfig(){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(slaveProperties.getUrl());
        config.setUsername(slaveProperties.getUsername());
        config.setPassword(slaveProperties.getPassword());
        config.setPoolName("HAKARI_READ");
        config.setReadOnly(true);
        config.setAutoCommit(hakariReadProperties.getAutoCommit());
        config.setMaximumPoolSize(hakariReadProperties.getMaximumPoolSize());
        config.setIdleTimeout(hakariReadProperties.getIdleTimeout());
        config.setMinimumIdle(hakariReadProperties.getMinimumIdle());
        config.setMaxLifetime(hakariReadProperties.getMaxLifetime());
        config.setConnectionTimeout(hakariReadProperties.getConnectionTimeout());

        return new HikariDataSource(config);
        /** 
        return DataSourceBuilder
        .create()
        .driverClassName("com.mysql.cj.jdbc.Driver")
        .url(
            slaveProperties.getUrl()
        ).username(
            slaveProperties.getUsername()
        ).password(
            slaveProperties.getPassword()
        ).build();
        */
    }
    
    //  Database Router
    @Bean(name = "multipleDataSource")
    public DataSource dataBaseRouter(){
        // DataSource Configuration
        HashMap<Object, Object> dataSources = new HashMap<>();
        dataSources.put(DataSourceType.SLAVE, this.slaveDataSourceConfig());
        dataSources.put(DataSourceType.MASTER,this.masterDataSourceConfig());
        
        RoutingDataSource router = new RoutingDataSource(); 
        router.setTargetDataSources(dataSources);

        // Avoid DataSource router not initialized
        router.afterPropertiesSet();
        return router;   
    }
    /**
     * JPA Configuration
     */

    @Bean(name = "entityManager")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataBaseRouter());
        em.setPackagesToScan(new String[]{"com.pttbackend.pttclone.repository" , "com.pttbackend.pttclone.model"});
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());
        return em;
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager multiTransactionManager() {
        JpaTransactionManager transactionManager= new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactoryBean().getObject());
        return transactionManager;
    }   

    @Primary
    @Bean(name = "dbSessionFactory")
    public LocalSessionFactoryBean dbSessionFactory() {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataBaseRouter());
        sessionFactoryBean.setPackagesToScan(SCAN_PACKAGE);
        sessionFactoryBean.setHibernateProperties(additionalProperties());
        return sessionFactoryBean;
    }

    private Properties additionalProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.show_sql", true);
        properties.put("hibernate.format_sql", true);
        properties.put("hibernate.ddl-auto", "update");
        
        return properties;
    }
}

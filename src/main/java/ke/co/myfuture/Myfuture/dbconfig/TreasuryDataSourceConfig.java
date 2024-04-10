package ke.co.myfuture.Myfuture.dbconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(
        basePackages = "ke.co.myfuture.Myfuture.Treasury",
        entityManagerFactoryRef = "treasuryEntityManagerFactory",
        transactionManagerRef = "treasuryTransactionManager"
)
public class TreasuryDataSourceConfig {
    @Autowired
    private Environment env;

    @Bean
    @ConfigurationProperties(prefix="datasource.treasury")
    public DataSourceProperties treasuryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource treasuryDataSource() {
        DataSourceProperties treasuryDataSourceProperties = treasuryDataSourceProperties();
        return DataSourceBuilder.create()
                .driverClassName(treasuryDataSourceProperties.getDriverClassName())
                .url(treasuryDataSourceProperties.getUrl())
                .username(treasuryDataSourceProperties.getUsername())
                .password(treasuryDataSourceProperties.getPassword())
                .build();
    }

    @Bean
    public PlatformTransactionManager treasuryTransactionManager()
    {
        EntityManagerFactory factory = treasuryEntityManagerFactory().getObject();
        return new JpaTransactionManager(factory);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean treasuryEntityManagerFactory()
    {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(treasuryDataSource());
        factory.setPackagesToScan(new String[]{"ke.co.myfuture.Myfuture.Treasury"});
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.implicit_naming_strategy",
                "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy");
        jpaProperties.put("hibernate.physical_naming_strategy",
                "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
        jpaProperties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
        jpaProperties.put("hibernate.show-sql", env.getProperty("spring.jpa.show-sql"));
        factory.setJpaProperties(jpaProperties);

        return factory;
    }

    @Bean
    public DataSourceInitializer treasuryDataSourceInitializer()
    {
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(treasuryDataSource());
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
//        databasePopulator.addScript(new ClassPathResource("treasury-data.sql"));
        dataSourceInitializer.setDatabasePopulator(databasePopulator);
        dataSourceInitializer.setEnabled(env.getProperty("datasource.treasury.initialize", Boolean.class, false));
        return dataSourceInitializer;
    }


}

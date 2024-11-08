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
        basePackages = "ke.co.myfuture.Myfuture.IbukaGPTs",
        entityManagerFactoryRef = "ibuka_gptsEntityManagerFactory",
        transactionManagerRef = "ibuka_gptsTransactionManager"
)
public class IbukaGPTsDataSourceConfig {
    @Autowired
    private Environment env;

    @Bean
    @ConfigurationProperties(prefix="datasource.ibukagpts")
    public DataSourceProperties ibuka_gptsDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource ibuka_gptsDataSource() {
        DataSourceProperties ibuka_gptsDataSourceProperties = ibuka_gptsDataSourceProperties();
        return DataSourceBuilder.create()
                .driverClassName(ibuka_gptsDataSourceProperties.getDriverClassName())
                .url(ibuka_gptsDataSourceProperties.getUrl())
                .username(ibuka_gptsDataSourceProperties.getUsername())
                .password(ibuka_gptsDataSourceProperties.getPassword())
                .build();
    }

    @Bean
    public PlatformTransactionManager ibuka_gptsTransactionManager()
    {
        EntityManagerFactory factory = ibuka_gptsEntityManagerFactory().getObject();
        return new JpaTransactionManager(factory);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean ibuka_gptsEntityManagerFactory()
    {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(ibuka_gptsDataSource());
        factory.setPackagesToScan(new String[]{"ke.co.myfuture.Myfuture.IbukaGPTs"});
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
    public DataSourceInitializer ibuka_gptsDataSourceInitializer()
    {
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(ibuka_gptsDataSource());
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
//        databasePopulator.addScript(new ClassPathResource("ibuka_gpts-data.sql"));
        dataSourceInitializer.setDatabasePopulator(databasePopulator);
        dataSourceInitializer.setEnabled(env.getProperty("datasource.ibukagpts.initialize", Boolean.class, false));
        return dataSourceInitializer;
    }
}

package ke.co.myfuture.Myfuture.dbconfig;

import jakarta.persistence.EntityManagerFactory;
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

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(
        basePackages = "ke.co.myfuture.Myfuture.Commonauth",
        entityManagerFactoryRef = "commonauthEntityManagerFactory",
        transactionManagerRef = "commonauthTransactionManager"
)
public class CommonauthDataSourceConfig {

    @Autowired
    private Environment env;

    // =========================
    // DataSource Properties
    // =========================
    @Bean
    @ConfigurationProperties(prefix = "datasource.commonauth")
    public DataSourceProperties commonauthDataSourceProperties() {
        return new DataSourceProperties();
    }
    // =========================
    // DataSource
    // =========================

    @Bean(name = "commonauthDataSource")
    public DataSource commonauthDataSource() {
        return commonauthDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    // =========================
    // EntityManagerFactory
    // =========================
    @Bean(name = "commonauthEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean commonauthEntityManagerFactory() {

        LocalContainerEntityManagerFactoryBean factory =
                new LocalContainerEntityManagerFactoryBean();

        factory.setDataSource(commonauthDataSource());
        factory.setPackagesToScan("ke.co.myfuture.Myfuture.Commonauth");
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties jpaProperties = new Properties();

        jpaProperties.put(
                "hibernate.implicit_naming_strategy",
                "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy"
        );

        jpaProperties.put(
                "hibernate.physical_naming_strategy",
                "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy"
        );

        jpaProperties.put(
                "hibernate.hbm2ddl.auto",
                env.getProperty("spring.jpa.hibernate.ddl-auto", "none")
        );

        jpaProperties.put(
                "hibernate.show-sql",
                env.getProperty("spring.jpa.show-sql", "false")
        );

        factory.setJpaProperties(jpaProperties);

        return factory;
    }

    // =========================
    // Transaction Manager
    // =========================
    @Bean(name = "commonauthTransactionManager")
    public PlatformTransactionManager commonauthTransactionManager() {

        EntityManagerFactory emf =
                commonauthEntityManagerFactory().getObject();

        return new JpaTransactionManager(emf);
    }

    // =========================
    // DB Initializer (optional)
    // =========================
    @Bean
    public DataSourceInitializer commonauthSourceInitializer() {

        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(commonauthDataSource());

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();

        initializer.setDatabasePopulator(populator);

        initializer.setEnabled(
                env.getProperty("datasource.commonauth.initialize", Boolean.class, false)
        );

        return initializer;
    }
}
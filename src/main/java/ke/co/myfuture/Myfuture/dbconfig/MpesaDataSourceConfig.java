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
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(
        basePackages = "ke.co.myfuture.Myfuture.MpesaIntegration",
        entityManagerFactoryRef = "mpesaEntityManagerFactory",
        transactionManagerRef = "mpesaTransactionManager"
)
public class MpesaDataSourceConfig {

    @Autowired
    private Environment env;

    // =========================
    // DataSource Properties
    // =========================
    @Bean
    @ConfigurationProperties(prefix = "datasource.mpesa")
    public DataSourceProperties mpesaDataSourceProperties() {
        return new DataSourceProperties();
    }

    // =========================
    // DataSource
    // =========================
    @Bean(name = "mpesaDataSource")
    public DataSource mpesaDataSource() {

        DataSourceProperties props = mpesaDataSourceProperties();

        return DataSourceBuilder.create()
                .driverClassName(props.getDriverClassName())
                .url(props.getUrl())
                .username(props.getUsername())
                .password(props.getPassword())
                .build();
    }

    // =========================
    // EntityManagerFactory
    // =========================
    @Bean(name = "mpesaEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean mpesaEntityManagerFactory() {

        LocalContainerEntityManagerFactoryBean factory =
                new LocalContainerEntityManagerFactoryBean();

        factory.setDataSource(mpesaDataSource());
        factory.setPackagesToScan("ke.co.myfuture.Myfuture.MpesaIntegration");
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
    @Bean(name = "mpesaTransactionManager")
    public PlatformTransactionManager mpesaTransactionManager() {

        EntityManagerFactory emf =
                mpesaEntityManagerFactory().getObject();

        return new JpaTransactionManager(emf);
    }

    // =========================
    // DB Initializer (optional)
    // =========================
    @Bean
    public DataSourceInitializer mpesaDataSourceInitializer() {

        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(mpesaDataSource());

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        // populator.addScript(new ClassPathResource("mpesa-data.sql"));

        initializer.setDatabasePopulator(populator);

        initializer.setEnabled(
                env.getProperty("datasource.mpesa.initialize", Boolean.class, false)
        );

        return initializer;
    }
}
package ke.co.myfuture.Myfuture.dbconfig;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "ke.co.myfuture.Myfuture.Commonauth",
        entityManagerFactoryRef = "commonauthEntityManagerFactory",
        transactionManagerRef = "commonauthTransactionManager"
)
public class CommonauthDataSourceConfig {

    @Autowired
    private Environment env;

    @Bean
    @ConfigurationProperties(prefix = "datasource.commonauth")
    public DataSourceProperties commonauthDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "commonauthDataSource")
    public DataSource commonauthDataSource() {
        return commonauthDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean(name = "commonauthEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean commonauthEntityManagerFactory() {

        LocalContainerEntityManagerFactoryBean factory =
                new LocalContainerEntityManagerFactoryBean();

        factory.setDataSource(commonauthDataSource());
        factory.setPackagesToScan("ke.co.myfuture.Myfuture.Commonauth");

        // 🔥 CRITICAL FIX 1: Persistence unit isolation
        factory.setPersistenceUnitName("commonauthPU");

        // 🔥 CRITICAL FIX 2: Explicit JPA vendor adapter
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        // 🔥 CRITICAL FIX 3: Explicit JPA properties map (important for mapping context)
        Map<String, Object> props = new HashMap<>();

        props.put("hibernate.hbm2ddl.auto",
                env.getProperty("spring.jpa.hibernate.ddl-auto", "none"));

        props.put("hibernate.show-sql",
                env.getProperty("spring.jpa.show-sql", "false"));

        props.put("hibernate.physical_naming_strategy",
                "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");

        props.put("hibernate.implicit_naming_strategy",
                "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy");

        factory.setJpaPropertyMap(props);

        return factory;
    }

    @Bean(name = "commonauthTransactionManager")
    public PlatformTransactionManager commonauthTransactionManager() {

        EntityManagerFactory emf =
                commonauthEntityManagerFactory().getObject();

        return new JpaTransactionManager(emf);
    }
}
package itmo.tg.airbnb_xa.business.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(
        basePackages = {
                "itmo.tg.airbnb_xa.business.repository.main",
                "itmo.tg.airbnb_xa.security.repository"
        },
        entityManagerFactoryRef = "mainEntityManagerFactory",
        transactionManagerRef = "jtaTransactionManager"
)
public class MainDataSourceConfig {

    @Bean
    @Primary
    public DataSource mainDataSource() throws NamingException {
        return (DataSource) new InitialContext().lookup("java:/jdbc/main");
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean mainEntityManagerFactory() throws NamingException {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setJtaDataSource(mainDataSource());
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setPackagesToScan("itmo.tg.airbnb_xa.business.model.main", "itmo.tg.airbnb_xa.security.model");
        em.setPersistenceUnitName("mainPU");

        Properties props = new Properties();
        props.put("hibernate.transaction.jta.platform", "org.hibernate.engine.transaction.jta.platform.internal.JBossAppServerJtaPlatform");
        props.put("hibernate.transaction.coordinator_class", "jta");
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("javax.persistence.transactionType", "JTA");
        em.setJpaProperties(props);

        return em;
    }
}


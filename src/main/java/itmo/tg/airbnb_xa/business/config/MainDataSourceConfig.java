package itmo.tg.airbnb_xa.business.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jndi.JndiTemplate;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(
        basePackages = "itmo.tg.airbnb_xa.business.repository.main",
        entityManagerFactoryRef = "mainEntityManagerFactory",
        transactionManagerRef = "jtaTransactionManager"
)
public class MainDataSourceConfig {

    @Bean
    public DataSource mainDataSource() throws NamingException {
        return (DataSource) new JndiTemplate().lookup("java:/jdbc/main");
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean mainEntityManagerFactory() throws NamingException {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(mainDataSource());
        em.setPackagesToScan("itmo.tg.airbnb_xa.business.model.main");
        em.setPersistenceUnitName("mainPU");
        em.setJtaDataSource(mainDataSource());

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Properties jpaProperties = PropertyProvider.getProperties();
        em.setJpaProperties(jpaProperties);

        return em;
    }

}

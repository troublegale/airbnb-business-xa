package itmo.tg.airbnb_xa.business.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(
        basePackages = "itmo.tg.airbnb_xa.business.repository.fines",
        entityManagerFactoryRef = "finesEntityManagerFactory",
        transactionManagerRef = "jtaTransactionManager"
)
public class FinesDataSourceConfig {

    @Bean
    public DataSource finesDataSource() throws NamingException {
        return (DataSource) new InitialContext().lookup("java:/jdbc/fines");
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean finesEntityManagerFactory() throws NamingException {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setJtaDataSource(finesDataSource());
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setPackagesToScan("itmo.tg.airbnb_xa.business.model.fines");
        em.setPersistenceUnitName("finesPU");

        Properties props = PropertyProvider.getProperties();
        em.setJpaProperties(props);

        return em;
    }
}

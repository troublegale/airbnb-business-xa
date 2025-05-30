package itmo.tg.airbnb_xa.business.config;

import jakarta.transaction.TransactionManager;
import jakarta.transaction.UserTransaction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.naming.InitialContext;
import javax.naming.NamingException;

@Configuration
@EnableTransactionManagement
public class JtaConfig {

    @Bean(name = "jtaTransactionManager")
    public PlatformTransactionManager transactionManager() throws NamingException {
        UserTransaction userTransaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        TransactionManager transactionManager = (TransactionManager) new InitialContext().lookup("java:jboss/TransactionManager");

        return new JtaTransactionManager(userTransaction, transactionManager);
    }

}

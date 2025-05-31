package itmo.tg.airbnb_xa.business.config;

import jakarta.transaction.TransactionManager;
import jakarta.transaction.UserTransaction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.naming.InitialContext;
import javax.naming.NamingException;

@Configuration
public class JtaConfig {

    @Bean
    public UserTransaction userTransaction() throws NamingException {
        return (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
    }

    @Bean
    public JtaTransactionManager jtaTransactionManager(UserTransaction ut) throws NamingException {
        TransactionManager tm = (TransactionManager) new InitialContext().lookup("java:jboss/TransactionManager");
        return new JtaTransactionManager(ut, tm);
    }
}

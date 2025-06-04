package itmo.tg.airbnb_xa.business.config;

import java.util.Properties;

public class PropertyProvider {

    public static Properties getProperties() {
        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.transaction.jta.platform",
                "org.hibernate.engine.transaction.jta.platform.internal.JBossAppServerJtaPlatform");
        jpaProperties.put("jakarta.persistence.transactionType", "JTA");
        jpaProperties.put("hibernate.transaction.coordinator_class", "jta");
        return jpaProperties;
    }

}

package api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"api.b.repo"})
public class ConfigB {

	@Autowired
	private Environment env;
	
    @Bean(name = "dataSourceB")
    @ConfigurationProperties(prefix = "spring.datasource.b")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "entityManagerFactoryB")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                       @Qualifier("dataSourceB") DataSource dataSource) {
    	Map<String, Object> properties = new HashMap<>();
		properties.put("hibernate.show-sql", env.getProperty("spring.datasource.b.jpa.show-sql"));
		properties.put("spring.jpa.hibernate.ddl-auto", env.getProperty("spring.datasource.b.hibernate.dialect"));
		properties.put("hibernate.dialect", env.getProperty("spring.datasource.b.hibernate.dialect"));
		
    	return builder
            .dataSource(dataSource)
            .packages("api.b.model")
            .persistenceUnit("datasourceB")
            .build();
    }


    @Bean(name = "transactionManagerB")
    public PlatformTransactionManager transactionManager(@Qualifier("entityManagerFactoryB") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}

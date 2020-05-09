package api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"api.a.repo"})
public class ConfigA {
	@Autowired
	private Environment env;
	
    @Primary
    @Bean(name = "dataSourceA")
    @ConfigurationProperties(prefix = "spring.datasource.a")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "entityManagerFactoryA")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                       @Qualifier("dataSourceA") DataSource dataSource) {
        
    	Map<String, Object> properties = new HashMap<>();
		properties.put("hibernate.show-sql", env.getProperty("spring.datasource.a.jpa.show-sql"));
		properties.put("spring.jpa.hibernate.ddl-auto", env.getProperty("spring.datasource.a.hibernate.dialect"));
		properties.put("hibernate.dialect", env.getProperty("spring.datasource.a.hibernate.dialect"));
		
    	return builder
            .dataSource(dataSource)
            .properties(properties)
            .packages("api.a.model")
            .persistenceUnit("datasourceA")
            .build();
    }

    @Primary
    @Bean(name = "transactionManagerA")
    public PlatformTransactionManager transactionManager(@Qualifier("entityManagerFactoryA") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
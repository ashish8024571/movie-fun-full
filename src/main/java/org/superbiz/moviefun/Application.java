package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

//@SpringBootApplication
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ServletRegistrationBean actionServletRegistration(ActionServlet actionServlet) {
        return new ServletRegistrationBean(actionServlet, "/moviefun/*");
    }

    @Bean
    public DatabaseServiceCredentials envVariable(@Value("${VCAP_SERVICES}") String vcapServices) {
        return new DatabaseServiceCredentials(vcapServices);
    }

    @Bean
    public DataSource albumsDataSource(DatabaseServiceCredentials serviceCredentials) {
            MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("albums-mysql"));
           HikariConfig hikariConfig = new HikariConfig();
        //hikariConfig.setJdbcUrl( "jdbc_url" );
        hikariConfig.setDataSource(dataSource);
        HikariDataSource ds = new HikariDataSource(hikariConfig);
        return ds;
        //return hikariConfig;
    }

    @Bean
    public DataSource moviesDataSource(DatabaseServiceCredentials serviceCredentials) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("movies-mysql"));
        HikariConfig hikariConfig = new HikariConfig();
        //hikariConfig.setJdbcUrl( "jdbc_url" );
        hikariConfig.setDataSource(dataSource);
        HikariDataSource ds = new HikariDataSource(hikariConfig);
        return ds;
        //return dataSource;
    }

    @Bean
    public HibernateJpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
        hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        return hibernateJpaVendorAdapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean albumslocalContainerEntityManagerFactoryBean (DataSource albumsDataSource, HibernateJpaVendorAdapter jpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean albumEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        albumEntityManagerFactoryBean.setDataSource(albumsDataSource);
        albumEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        albumEntityManagerFactoryBean.setPackagesToScan("org.superbiz.moviefun.albums");
        albumEntityManagerFactoryBean.setPersistenceUnitName("albums");
        return albumEntityManagerFactoryBean;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean movieslocalContainerEntityManagerFactoryBean (DataSource moviesDataSource, HibernateJpaVendorAdapter jpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean moviesEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        moviesEntityManagerFactoryBean.setDataSource(moviesDataSource);
        moviesEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        moviesEntityManagerFactoryBean.setPackagesToScan("org.superbiz.moviefun.movies");
        moviesEntityManagerFactoryBean.setPersistenceUnitName("movies");
        return moviesEntityManagerFactoryBean;
    }
    @Bean
    public PlatformTransactionManager albumsPlatformTransactionManager(EntityManagerFactory albumslocalContainerEntityManagerFactoryBean){
        return new JpaTransactionManager(albumslocalContainerEntityManagerFactoryBean);
    }
    @Bean
    public PlatformTransactionManager moviesPlatformTransactionManager(EntityManagerFactory movieslocalContainerEntityManagerFactoryBean){
        return new JpaTransactionManager(movieslocalContainerEntityManagerFactoryBean);
    }


}


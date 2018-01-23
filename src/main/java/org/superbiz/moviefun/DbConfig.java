package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import static org.springframework.orm.jpa.vendor.Database.MYSQL;

@Configuration
public class DbConfig {
    @Bean
    public DataSource albumsDataSource(
            @Value("${moviefun.datasources.albums.url}") String url,
            @Value("${moviefun.datasources.albums.username}") String username,
            @Value("${moviefun.datasources.albums.password}") String password
    ) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);


        HikariDataSource hds = new HikariDataSource();
        hds.setDataSource(dataSource);

        return hds;
    }

    @Bean
    public DataSource moviesDataSource(
            @Value("${moviefun.datasources.movies.url}") String url,
            @Value("${moviefun.datasources.movies.username}") String username,
            @Value("${moviefun.datasources.movies.password}") String password
    ) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);

        HikariDataSource hds = new HikariDataSource();
        hds.setDataSource(dataSource);
        return hds;
    }


    @Bean
    public HibernateJpaVendorAdapter getHibernateAdapter()
    {
        HibernateJpaVendorAdapter hj = new HibernateJpaVendorAdapter();

        hj.setDatabase(MYSQL);
        hj.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        hj.setGenerateDdl(true);

        return hj;
    }

    @Bean
    @Qualifier("moviesCEF")
    public LocalContainerEntityManagerFactoryBean moviesContainerEntityFactory(DataSource moviesDataSource, HibernateJpaVendorAdapter hj)
    {
        LocalContainerEntityManagerFactoryBean lce = new LocalContainerEntityManagerFactoryBean();

        lce.setDataSource(moviesDataSource);
        lce.setJpaVendorAdapter(hj);
        lce.setPackagesToScan("org.superbiz.moviefun.movies");
        lce.setPersistenceUnitName("movies");

        return lce;
    }

    @Bean
    @Qualifier("albumsCEF")
    public LocalContainerEntityManagerFactoryBean albumsContainerEntityFactory(DataSource albumsDataSource, HibernateJpaVendorAdapter hj)
    {
        LocalContainerEntityManagerFactoryBean lce = new LocalContainerEntityManagerFactoryBean();

        lce.setDataSource(albumsDataSource);
        lce.setJpaVendorAdapter(hj);
        lce.setPackagesToScan("org.superbiz.moviefun.albums");
        lce.setPersistenceUnitName("albums");

        return lce;
    }

    @Bean
    public PlatformTransactionManager moviesPlatformTransactionManager(
            @Qualifier("moviesCEF")
                    LocalContainerEntityManagerFactoryBean moviesContainerEntityFactory)
    {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager(moviesContainerEntityFactory.getObject());

        return jpaTransactionManager;
    }

    @Bean
    public PlatformTransactionManager albumsPlatformTransactionManager(
            @Qualifier("albumsCEF")
                    LocalContainerEntityManagerFactoryBean albumsContainerEntityFactory)
    {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager(albumsContainerEntityFactory.getObject());

        return jpaTransactionManager;
    }
}

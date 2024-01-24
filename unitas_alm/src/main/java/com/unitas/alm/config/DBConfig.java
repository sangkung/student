package com.unitas.alm.config;

import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * DB설정관련 class
 * @author USER
 *
 */
//Configuration
//public class DBConfig {
//	
//	@Bean(destroyMethod = "close")
//	@ConfigurationProperties("spring.datasource.hikari")
//	public DataSource dataSource() {
//		return DataSourceBuilder.create().build();
//	}
//
//	@Bean
//	public SqlSessionFactory sqlSessionFactory() throws Exception {
//		SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
//		sqlSessionFactory.setDataSource(dataSource());
//		Resource configLocation = new PathMatchingResourcePatternResolver().getResource("classpath:mybatis-config.xml");
//		Resource[] mapperLocations = new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*Mapper.xml");
//		//sqlSessionFactory.setTypeAliasesPackage("com.app.almweb");
//		sqlSessionFactory.setConfigLocation(configLocation);
//		sqlSessionFactory.setMapperLocations(mapperLocations);
//		return sqlSessionFactory.getObject();
//	}
//
//	@Bean("sqlSession")
//	SqlSessionTemplate sqlSession(SqlSessionFactory sqlSessionFactory) throws Exception {
//		return new SqlSessionTemplate(sqlSessionFactory);
//	}
//	
//	@Bean
//	public PlatformTransactionManager transactionManager() {
//	    DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource());
//	    transactionManager.setGlobalRollbackOnParticipationFailure(false);
//	    return transactionManager;
//	}
//}

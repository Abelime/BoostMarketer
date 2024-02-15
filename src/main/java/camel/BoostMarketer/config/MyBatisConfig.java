package camel.BoostMarketer.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;


@Configuration
@MapperScan("camel.BoostMarketer.**.mapper")
public class MyBatisConfig {

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        // XML 매퍼 파일 위치 지정
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("classpath:/mapper/*.xml"));

        // Type Aliases 등록
        sessionFactory.setTypeAliasesPackage("camel.BoostMarketer.blog.dto");

        // MyBatis Configuration 설정
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration(); // 추가
        configuration.setMapUnderscoreToCamelCase(true); // 스네이크 케이스와 카멜 케이스 매핑 활성화
        sessionFactory.setConfiguration(configuration); // 설정 적용

        // 기타 설정들
        return sessionFactory.getObject();
    }
}

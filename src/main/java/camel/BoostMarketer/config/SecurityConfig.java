package camel.BoostMarketer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .requestMatchers("/user/**").permitAll()
//                        .requestMatchers("/my/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/assets/**","/css/**", "/js/**", "/images/**").permitAll()
                        .anyRequest().authenticated()
                );

        http
                .formLogin((auth) -> auth.loginPage("/user/sign-in")
                        .loginProcessingUrl("/user/loginProc")
                        .defaultSuccessUrl("/")
                        .permitAll()
                );

        http
                .logout((auth) -> auth.logoutUrl("/user/sign-out")
                        .logoutSuccessUrl("/user/sign-in")
                        .permitAll());

        http
                .csrf((auth) -> auth.disable());

        http
                .sessionManagement((auth) -> auth
                        .sessionFixation().changeSessionId()
                        .maximumSessions(1) //maximumSession(정수) : 하나의 아이디에 대한 다중 로그인 허용 개수
                        .maxSessionsPreventsLogin(true)); //다중 로그인 개수를 초과하였을 경우 처리 방법 (true -->초과시 새로운 로그인 차단) (false --> 초과시 기존 세션 하나 삭제)


        return http.build();
    }


}
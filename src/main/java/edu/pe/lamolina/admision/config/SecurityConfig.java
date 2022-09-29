package edu.pe.lamolina.admision.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import edu.pe.lamolina.admision.security.http.LoginSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService details;

    @Autowired
    LoginSuccessHandler loginHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/inscripcion/**").authenticated()
                .antMatchers("/guiapostulante/").permitAll()
                .antMatchers("/**").permitAll();

        http.formLogin()
                .loginPage("/login")
                .failureUrl("/login?error")
                .successHandler(loginHandler)
                .permitAll();

        http.logout()
                .logoutSuccessUrl("https://www.facebook.com/admisionagrarialamolina");

        http.sessionManagement().invalidSessionUrl("/");

        http.exceptionHandling().accessDeniedPage("/");

        http.csrf().disable();

        http.headers().frameOptions().sameOrigin();

    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(details);

        auth.authenticationProvider(provider);
    }

}

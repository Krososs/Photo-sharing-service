package pl.sk.photosharingservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.sk.photosharingservice.model.User;
import pl.sk.photosharingservice.repository.UserRepository;
import pl.sk.photosharingservice.service.UserService;


@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

//    private UserRepository userRepository;
//
//    @Autowired
//    public SecurityConfiguration(UserService userService, UserRepository userRepository) {
//
//        this.userRepository=userRepository;
//    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userService);
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.authorizeRequests().anyRequest().permitAll().and().csrf().disable();
//        http.authorizeRequests().antMatchers("/users/register").permitAll()
//                .antMatchers("/users/register").permitAll()
//                .and()
//                .formLogin().permitAll();

        //.antMatchers("/endpoint_jakis").authenticated() - dla zalogowanych tylko
        //.hasAnyRole("USER")
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

//    //testowo dodajemy andrzeja
//    @EventListener(ApplicationReadyEvent.class)
//    public void auto(){
//        //User user = new User("Andrzej", passwordEncoder().encode("1234"), "USER");
//        User user2 = new User("Andrzej", passwordEncoder().encode("1234"), "ROLE_USER","elo@elo.com");
//        userRepository.save(user2);
//    }
}

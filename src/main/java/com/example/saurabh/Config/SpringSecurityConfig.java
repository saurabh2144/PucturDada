package com.example.saurabh.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;


@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	
    	

        http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
        		 .requestMatchers("/admin","/toggleStatus").authenticated()
        		.anyRequest().permitAll()           // 👈 then allow everything else
        	)
       

            .formLogin(Customizer.withDefaults())
           // .httpBasic(Customizer.withDefaults())
            .oauth2Login(oauth -> oauth
            	    .loginPage("/loginuser")                 // your custom login page
            	    .defaultSuccessUrl("/usersuclo", true)   );     // 👈 redirect here after login
            	

        return http.build();
        
}}

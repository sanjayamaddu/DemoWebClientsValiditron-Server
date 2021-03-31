package au.unimelb.covidcare;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder encoder = 
          PasswordEncoderFactories.createDelegatingPasswordEncoder();
        auth
        .inMemoryAuthentication()
        .withUser("user")
        .password(encoder.encode("password"))
        .roles("USER")
        .and()
        .withUser("admin")
        .password(encoder.encode("admin"))
        .roles("USER", "ADMIN");
    }
 
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	 http
         .authorizeRequests()
         .antMatchers(HttpMethod.GET, "/api/getpatients/**").permitAll()
         .antMatchers(HttpMethod.GET, "/api/getreferrals/**").permitAll()
         .antMatchers(HttpMethod.GET, "/api/getnaxtavailablacovidcareid/**").permitAll()
         .antMatchers(HttpMethod.GET, "/api/getencounterlistasdisplayrow/**").permitAll()
         .antMatchers(HttpMethod.GET, "/api/getcovidcareids/**").permitAll()
         .antMatchers(HttpMethod.POST, "/api/postpatient/**").permitAll()
         .antMatchers(HttpMethod.GET,  "/api/getpatientbyreferralandcovidcareid/{referralClinicId}/{covidcareid}").permitAll()
         .antMatchers(HttpMethod.GET,  "/api/getencounterbyresourceid/{resID}").permitAll()
         .antMatchers(HttpMethod.POST, "/api/postencounterobservationsanddetectedissues/**").permitAll()
         .antMatchers(HttpMethod.GET, "/api/getsessionfhirlog/{fhirmessage}").permitAll()
         .anyRequest()
         .authenticated()
         .and()
         .cors()
         .and()
         .exceptionHandling()
         .and()
         .sessionManagement()
         .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
         .and()
         .csrf()
         .disable()
         .headers().contentSecurityPolicy("Content-Security-Policy 'script-src' 'self'").and().referrerPolicy(ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN).and()
         //.featurePolicy("geolocation 'self'");
    	 .featurePolicy("accelerometer 'none' camera 'none'; geolocation 'none'; gyroscope 'none'; magnetometer 'none'; midi 'none'; notifications 'none'; payment 'none'; usb 'none'");
         
    }

}

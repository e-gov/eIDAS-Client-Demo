package ee.ria.eidas.client.demo.config;

import ee.ria.eidas.client.demo.filter.EidasAuthenticationCallbackFilter;
import ee.ria.eidas.client.demo.filter.EidasAuthenticationFilter;
import ee.ria.eidas.client.demo.filter.EidasMetadataFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.Filter;
import java.util.List;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${eidas.client.ws.url}")
    private String eidasClientUrl;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().ignoringAntMatchers("/returnUrl");

		http
			.authorizeRequests()
				.antMatchers("/protected").authenticated()
				.and()
				.addFilterBefore(eidasAuthenticationFilter(), AnonymousAuthenticationFilter.class)
				.addFilterBefore(eidasAuthenticationCallbackFilter(), AnonymousAuthenticationFilter.class)
			.formLogin()
				.loginPage("/login");
	}

	@Bean
	public Filter eidasAuthenticationFilter() throws Exception {
		EidasAuthenticationFilter filter = new EidasAuthenticationFilter("/eidasLogin", eidasClientUrl);
		filter.setAuthenticationManager(authenticationManager());
		filter.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler("/login?error"));
		return filter;
	}

	@Bean
	public Filter eidasAuthenticationCallbackFilter() throws Exception {
		EidasAuthenticationCallbackFilter filter = new EidasAuthenticationCallbackFilter("/returnUrl", eidasClientUrl);
		filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler("/login?error"));
		return filter;
	}

    @Bean
    public FilterRegistrationBean eidasMetadataFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new EidasMetadataFilter(eidasClientUrl));
        registration.addUrlPatterns("/metadata");
        return registration;
    }
}
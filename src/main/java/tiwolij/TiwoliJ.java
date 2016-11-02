package tiwolij;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Locale.LanguageRange;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;

@SpringBootApplication
@EnableTransactionManagement
public class TiwoliJ extends WebMvcConfigurerAdapter {

	@Autowired
	private Environment env;

	public static void main(String[] args) {
		SpringApplication.run(TiwoliJ.class, args);
	}

	@Bean
	public LocaleResolver localeResolver() {
		List<Locale> list = new ArrayList<Locale>();
		Locale standard = new Locale(env.getProperty("tiwolij.defaultlocale"));

		Arrays.asList(env.getProperty("tiwolij.localizations", String[].class)).stream()
				.forEach(l -> list.add(new Locale(l)));

		SessionLocaleResolver slr = new SessionLocaleResolver() {
			@Override
			public Locale resolveLocale(HttpServletRequest request) {
				Locale sessionLocale = (Locale) WebUtils.getSessionAttribute(request, LOCALE_SESSION_ATTRIBUTE_NAME);
				Locale requestLocale = Locale.lookup(LanguageRange.parse(request.getHeader("Accept-Language")), list);

				if (sessionLocale != null)
					return sessionLocale;

				if (requestLocale != null)
					return requestLocale;

				return standard;
			}
		};

		slr.setDefaultLocale(standard);
		return slr;
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
		lci.setIgnoreInvalidLocale(true);
		lci.setParamName("lang");
		return lci;
	}

	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
		ms.setDefaultEncoding("UTF-8");
		ms.setBasenames("locales/messages");
		return ms;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}

}
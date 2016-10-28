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
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

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
		List<Locale> locales = new ArrayList<Locale>();
		Locale standard = new Locale(env.getProperty("tiwolij.defaultlocale"));

		Arrays.asList(env.getProperty("tiwolij.localizations").split(", ")).stream()
				.forEach(l -> locales.add(new Locale(l)));

		SessionLocaleResolver slr = new SessionLocaleResolver() {
			@Override
			public Locale resolveLocale(HttpServletRequest request) {
				String language = request.getHeader("Accept-Language");
				List<LanguageRange> range = LanguageRange.parse(language);
				Locale locale = Locale.lookup(range, locales);

				if (language.isEmpty() || locale == null)
					return standard;

				return locale;
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

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}

}
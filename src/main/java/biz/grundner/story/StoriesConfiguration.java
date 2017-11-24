package biz.grundner.story;

import biz.jovido.seed.content.ItemService;
import biz.jovido.seed.content.ModelFactoryProvider;
import biz.jovido.seed.content.SimpleModelFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author Stephan Grundner
 */
@Configuration
@Order(1000)
public class StoriesConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/contact", "/contact/");
    }

    @Bean
    public LocalValidatorFactoryBean validator(MessageSource messageSource) {
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.setValidationMessageSource(messageSource);
        return validatorFactoryBean;
    }

    @Bean
    public SimpleModelFactory smartModelFactory(ModelFactoryProvider modelFactoryProvider, ItemService itemService) {
        SimpleModelFactory modelFactory = new SimpleModelFactory(itemService);
        modelFactoryProvider.getModelFactories().add(modelFactory);

        return modelFactory;
    }

    @Bean
    public JavaMailSender mailSender() {
        return new JavaMailSenderImpl();
    }
}

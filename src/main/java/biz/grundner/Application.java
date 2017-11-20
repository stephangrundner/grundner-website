package biz.grundner;

import biz.jovido.seed.configuration.EnableSeed;
import biz.jovido.seed.content.Configurer;
import biz.jovido.seed.content.HierarchyService;
import biz.jovido.seed.content.StructureService;
import biz.jovido.seed.net.HostService;
import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

/**
 * @author Stephan Grundner
 */
@EnableSeed
@SpringBootApplication
@EntityScan("biz.grundner")
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(Application.class, args);
        Assert.isTrue(context.isRunning());
    }

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    @Scope("prototype")
    @ConfigurationProperties(prefix = "server.ajp", ignoreInvalidFields = true)
    protected Connector ajpConnector() {
        return new Connector("AJP/1.3");
    }

    @Bean
    @ConditionalOnProperty(name = "server.ajp.port")
    public EmbeddedServletContainerCustomizer servletContainerCustomizer(Connector ajpConnector) {
        return container -> {
            if (container instanceof TomcatEmbeddedServletContainerFactory) {
                ((TomcatEmbeddedServletContainerFactory) container)
                        .addAdditionalTomcatConnectors(ajpConnector);
            }
        };
    }

    private void photo(Configurer configurer) {
        configurer
                .createStructure("photo").setNestedOnly(true)
                .addImageAttribute("image")
                .addTextAttribute("title")
                    .setRequired(1)
                .setLabelAttribute("title");
    }

    private void textChapter(Configurer configurer) {
        configurer
                .createStructure("textChapter").setNestedOnly(true)
                .addTextAttribute("title")
                .addTextAttribute("subtitle")
                .addTextAttribute("text")
                    .setCapacity(Integer.MAX_VALUE)
                    .setMultiline(true)
                .setLabelAttribute("title");
    }

    private void stageChapter(Configurer configurer) {
        configurer
                .configure(this::photo)
                .createStructure("stageChapter").setNestedOnly(true)
                .addTextAttribute("title")
                .addItemAttribute("photo").setCapacity(1)
                    .addAcceptedStructure("photo")
                .setLabelAttribute("title");
    }

    private void photoChapter(Configurer configurer) {
        configurer
                .configure(this::photo)
                .createStructure("photoChapter").setNestedOnly(true)
                .addItemAttribute("photos").setCapacity(9)
                    .addAcceptedStructure("photo")
                .addSelectionAttribute("layout")
                    .addOption("standard")
                    .addOption("wide")
                    .addOption("tall")
                    .addOption("full")
                    .addOption("twoToOne")
                    .addOption("oneToTwo")
                    .addOption("threeToOne")
                    .addOption("oneToThree");
    }

    public void story(Configurer configurer) {
        configurer
                .configure(this::textChapter)
                .configure(this::photoChapter)
                .configure(this::stageChapter)
                .createStructure("story").setPublishable(true)
                    .addTextAttribute("title")
                    .addTextAttribute("subtitle")
                    .addTextAttribute("description")
                    .addItemAttribute("chapters").setCapacity(Integer.MAX_VALUE)
                        .addAcceptedStructure("textChapter")
                        .addAcceptedStructure("photoChapter")
                        .addAcceptedStructure("stageChapter")
                    .setLabelAttribute("title");
    }

    private void prepare() {

        HierarchyService hierarchyService = applicationContext.getBean(HierarchyService.class);
        StructureService structureService = applicationContext.getBean(StructureService.class);

        new Configurer(hierarchyService, structureService)
                .configure(this::story)
                .apply();

        HostService hostService = applicationContext.getBean(HostService.class);

        hostService.getOrCreateHost("localhost");
        hostService.getOrCreateHost("grundner.biz");
    }

    @PostConstruct
    void init() {
        PlatformTransactionManager transactionManager = applicationContext
                .getBean(PlatformTransactionManager.class);
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute((TransactionStatus status) -> {

            try {
                prepare();
                status.flush();
            } catch (Exception e) {
                status.setRollbackOnly();
                throw new RuntimeException(e);
            }

            return null;
        });
    }
}

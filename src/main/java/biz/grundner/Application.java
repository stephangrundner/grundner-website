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

    private void textOnlyChapter(Configurer configurer) {
        configurer
                .createStructure("textOnlyChapter").setNestedOnly(true)
                    .setTemplate("story/chapter/textOnly")
                    .addTextAttribute("title")
                    .addTextAttribute("subtitle")
                    .addTextAttribute("text")
                        .setMultiline(true)
                    .addYesNoAttribute("inverted");
    }

    private void photoOnlyChapter(Configurer configurer) {
        configurer
                .createStructure("photoOnlyChapter").setNestedOnly(true)
                    .setTemplate("story/chapter/photoOnly")
                    .addImageAttribute("photo")
                    .addSelectionAttribute("type")
                        .addOption("standard")
                        .addOption("full");
    }

    private void threePhotosChapter(Configurer configurer) {
        configurer
                .createStructure("threePhotosChapter").setNestedOnly(true)
                    .setTemplate("story/chapter/threePhotos")
                    .addImageAttribute("photos")
                        .setCapacity(3)
                        .setRequired(3)
                    .addSelectionAttribute("layout")
                        .addOption("twoToOne")
                        .addOption("oneToTwo");
    }

    private void photoWithTextChapter(Configurer configurer) {
        configurer
                .createStructure("photoWithTextChapter").setNestedOnly(true)
                    .setTemplate("story/chapter/photoWithText")
                    .addImageAttribute("photo")
                    .addTextAttribute("title")
                    .addTextAttribute("subtitle")
                    .addTextAttribute("text")
                        .setMultiline(true)
                    .addSelectionAttribute("layout")
                        .addOption("photoLeft")
                        .addOption("textLeft");
    }


    public void story(Configurer configurer) {
        configurer
                .configure(this::textOnlyChapter)
                .configure(this::photoOnlyChapter)
                .configure(this::threePhotosChapter)
                .configure(this::photoWithTextChapter)
                .createHierarchy("menu")
                .createStructure("story").setPublishable(true)
                    .setTemplate("story")
                    .addTextAttribute("title")
                    .addTextAttribute("subtitle").setRequired(0)
                    .addTextAttribute("summary")
                    .addImageAttribute("photo")
                    .addItemAttribute("chapters").setCapacity(Integer.MAX_VALUE).setRequired(0)
                        .addAcceptedStructure("textOnlyChapter")
                        .addAcceptedStructure("photoOnlyChapter")
                        .addAcceptedStructure("threePhotosChapter")
                        .addAcceptedStructure("photoWithTextChapter")
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

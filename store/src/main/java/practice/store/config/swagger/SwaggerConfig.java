package practice.store.config.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

@PropertySource("classpath:swagger.properties")
@EnableSwagger2
@Configuration
public class SwaggerConfig {

    @Value("${email.second.customer}")
    private String emailSecondCustomer;
    @Value("${password.second.customer}")
    private String passwordSecondCustomer;

    @Value("${default.auth.scope}")
    private String defaultAuthScope;
    @Value("${default.auth.description}")
    private String defaultAuthDescription;
    @Value("${default.auth.reference}")
    private String defaultAuthReference;

    @Value("${api.key.name}")
    private String apiKeyName;
    @Value("${api.key.key.name}")
    private String apiKeyKeyName;
    @Value("${api.key.pass.as}")
    private String apiKeyPassAs;

    @Value("${api.info.title}")
    private String apiInfoTitle;
    @Value("${api.info.desc}")
    private String apiInfoDescription;
    @Value("${api.info.name}")
    private String apiInfoName;
    @Value("${api.info.url}")
    private String apiInfoUrl;
    @Value("${api.info.email}")
    private String apiInfoEmail;
    @Value("${api.info.license}")
    private String apiInfoLicense;
    @Value("${api.info.version}")
    private String apiInfoVersion;


    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()))
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }


    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope(defaultAuthScope, defaultAuthDescription);
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference(defaultAuthReference, authorizationScopes));
    }

    private ApiKey apiKey() {
        return new ApiKey(apiKeyName, apiKeyKeyName, apiKeyPassAs);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(apiInfoTitle)
                .description(String.format(apiInfoDescription + " （ ͡° ͜ʖ ͡°)つ━☆ ", emailSecondCustomer, passwordSecondCustomer))
                .contact(new Contact(
                        apiInfoName,
                        apiInfoUrl,
                        apiInfoEmail))
                .license(apiInfoLicense)
                .version(apiInfoVersion)
                .build();
    }
}

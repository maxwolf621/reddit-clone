package com.pttbackend.pttclone.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.service.Contact;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.service.ApiInfo;

@Configuration
public class SwaggerConfig implements WebMvcConfigurer {
    
    @Value("${swaggerInfo.name}")
    private String name;
    @Value("${swaggerInfo.mail")
    private String mail;
    @Value("${swaggerInfo.url")
    private String url;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //Swagger UI property
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
    @Bean
    public Docket apiDocket(){
        // Swagger 3.0 : OAS_30
        return new Docket(DocumentationType.OAS_30)
                    .pathMapping("/")
                    .enable(true)
                    .apiInfo((ApiInfo) this.getApiInfo())
                    .host("http://localhost:8080")
                    // to build apiSelector
                    .select()
                    // scan the controller package
                    .apis(RequestHandlerSelectors.basePackage("com.pttbackend.pttclone.controller"))
                    // files(java packages) filter : any
                    // .apis(RequestHandlerSelectors.any())
                    // API's url path filter: any
                    .paths(PathSelectors.any()) 
                    .build();
    }
    
    /**
     * @return Object {@code ApiInfo} 
     */
    private Object getApiInfo() {
        return new ApiInfoBuilder()
                .title("Backend Project")
                .version("3.0")
                .description("Test")
                //license("Apache License Version 2.0")
                .contact(new Contact(name,url,mail)) 
                .build();
    }
}
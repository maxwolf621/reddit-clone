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

    /** 
     * @see <a href="https://stackoverflow.com/questions/43545540/swagger-ui-no-mapping-found-for-http-request">
     *　Details Mapping swagger-ui html　</a>
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /** 
     * Swagger 2.x.x version 
     *          
                 
    @Bean   
    public Docket apiDocket(){
        return new Docket(DocumentationType.SWAGGER_2).apiInfo((ApiInfo) getApiInfo()).select()
                 // INDICATE swagger only function api's url in this Control 
                 // .apis(RequestHandlerSelector.basePackge("com.demo.scanOnlyThis_Controller"))
                .apis(RequestHandlerSelectors.any()) // file(java packages) filter : any
                .paths(PathSelectors.any()) // API's url path filter: any
                .build();
    }
    */

    /**
     * @return Docket
     */
    @Bean
    public Docket apiDocket(){
        // Swagger 3.0 : OAS_30
        return new Docket(DocumentationType.OAS_30)
                    .apiInfo((ApiInfo) getApiInfo())
                    // to build apiSelector
                    .select()
                    // files(java packages) filter : any
                    .apis(RequestHandlerSelectors.any())
                    // API's url path filter: any
                    .paths(PathSelectors.any()) 
                    .build();
    }

    /**
     * @return Object {@code ApiInfo} 
     */
    private Object getApiInfo() {
        return new ApiInfoBuilder()
                .title("My Spring Boot Project")
                .version("2.0")
                .description("Test")
                //license("Apache License Version 2.0")
                .contact(new Contact(name,url,mail)) 
                .build();
    }
}

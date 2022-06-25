# A Spring Boot Project for Backend
- [Frontend Demo for this backend project](https://github.com/maxwolf621/SpringBootFrontend)

## Main Design Concept
- jks & jwt  
- `lombok` and `mapstruct` for DTOs and Models
- Oauth2 User Login (via `google` and `github`)
- the user avatar 
- Database for `User` , `Post` ,  ,`nested Comment` , `token` , `tags` , ... etc 
    - using `bidirectional` to enhance the efficiency

## Swagger-ui And Javadoc

Using Swagger-ui `http://localhost:8080/swagger-ui/` to test the Backend 
![image](https://user-images.githubusercontent.com/68631186/175774292-46b0c74a-3c9e-4e5e-b58b-0f957014856a.png)

Generate Javadoc for more details
```console
mvn javadocLjavadoc
```

## Reference
- [Spring Security Registration](https://github.com/Baeldung/spring-security-registration)  
- [usage of stream](https://stackoverflow.com/questions/48638338/spring-data-jpa-repositories-with-java-8-streams-detached-object)  
- [jwt exception](https://stackoverflow.com/questions/49085433/jjwt-library-and-handle-expiration-expiredjwtexception)   
- [get json meta from website](https://www.javachinna.com/generate-rich-link-preview-for-a-given-url-based-on-the-meta-tags-present-in-the-web-page-in-spring-boot/)  
- [How oauth2 works](https://marco.dev/angular-spring-boot-and-oauth2-part-1-how-it-works/)
- [Common Application Properties](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html)

#### Database
[`blob` data type example](https://stackoverflow.com/questions/57268541/how-to-stream-large-blob-from-database-to-application-using-jpa)
[How to use OrderBy with findAll in Spring Data](https://stackoverflow.com/questions/25486583/how-to-use-orderby-with-findall-in-spring-data)   
[Schema fo like,post,user](https://stackoverflow.com/questions/48160665/implementing-posts-comments-and-likes-in-db)  
[Query efficiency Discussion](https://www.reddit.com/r/PHP/comments/2hdbps/what_would_be_an_efficient_way_of_loading_from_a/)  
[Many To Many Database design](https://hackmd.io/@OceanChiu/ryM5xipxI)

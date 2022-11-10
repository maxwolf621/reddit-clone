# A Spring Boot Project for Backend
- [Frontend Demo for this backend project](https://github.com/maxwolf621/SpringBootFrontend)

## design
- JWT (login & reset password).
- Oauth2(`google` , `github`)
- `@schedule`(delete expired token and invalid user.)
- `lombok` (reducing Boilerplate code) 
- `mapstruct` (DTOs and POJO data conversion)
- `@Async` and `CompletableFuture` Multiple Threads 
- Database : Mysql & Redis
- Cache : Caffeine 
- JPA EntityGraph and Query (avoid n+1 query and eager fetching)
- AOP(logs of logics from difference layer)
- Junit5 & Swagger-ui

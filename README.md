# A Spring Boot Project for Backend
- [Frontend Demo for this backend project](https://github.com/maxwolf621/SpringBootFrontend)

## design
- JWT (login & reset password).
- Oauth2(`google` , `github`)
- `@schedule`(delete expired token and invalid user.)
- `lombok` (reducing Boilerplate code) 
- `mapstruct` (DTOs and POJO data conversion)
- Multiple Threads : `@Async` + `CompletableFuture` 
- Mysql (Master & Slave Read/Write Splitting) with JPA & hikariCP
- Redis (Redis Cluster)
- Cache Caffeine 
- EntityGraph and Query (avoid n+1 query and eager fetching)
- AOP(logs of logics from different layers + intersection of Slave datasource)
- Junit5/Mockito
- API : Swagger-ui
- ~~GraphQL~~
# Ontology Migration Tool

A tool for migrating ontologies (schema and data) underlying an information system. The tool's functionality
is inspired by [Liquibase](https://www.liquibase.com/) - the purpose is to safely and predicably evolve an ontology
with the information system that is based on it.

The migration tool accesses the underlying repository via its SPARQL endpoint and applies changesets that migrate
the schema and data to the version corresponding to the system.

## Requirements

- Java 17

## How to Get

Via Maven:

```xml

<dependency>
    <groupId>cz.cvut.kbss</groupId>
    <artifactId>ontology-migration-tool</artifactId>
</dependency>
```

## Usage

Assuming the target application is a Spring Boot application, the migration tool can be run as
an [application context](https://docs.spring.io/spring-boot/how-to/application.html#howto.application.customize-the-environment-or-application-context)
initializer. This ensures it is executed before the other application beans are created. For example:

1. Create a bean that implements the `ApplicationContextInitializer` interface

```java

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OntologyMigrationInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(@Nonnull ConfigurableApplicationContext appContext) {
        // Use environment to retrieve repository URL, username, password
        final String repoUrl = appContext.getEnvironment().getRequiredProperty("repository.url");
        final String repoUsername = appContext.getEnvironment().getProperty("repository.username");
        final String repoPassword = appContext.getEnvironment().getProperty("repository.password");

        // Create the migration runner
        final MigrationRunner runner = MigrationRunner.repository(repoUrl)
                                                      .username(repoUsername).password(repoPassword)
                                                      .build();
        // Run migration
        runner.run();
    }
}
```

2. Register the initializer via `SpringApplicationBuilder`:

```java

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class).initializers(new OntologyMigrationInitializer())
                                                       .run(args);
    }
}
```

## License

MIT

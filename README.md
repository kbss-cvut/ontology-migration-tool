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
public class OntologyMigrationInitializer 
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(@Nonnull ConfigurableApplicationContext appContext) {
        // Use environment to retrieve repository URL, username, password
        Environment env = appContext.getEnvironment();
        String repoUrl = env.getRequiredProperty("repository.url");
        String repoUsername = env.getProperty("repository.username");
        String repoPassword = env.getProperty("repository.password");

        // Create the migration runner
        MigrationRunner runner = MigrationRunner.repository(repoUrl)
                                                .username(repoUsername)
                                                .password(repoPassword)
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
        new SpringApplicationBuilder(Application.class)
                .initializers(new OntologyMigrationInitializer())
                .run(args);
    }
}
```

A `changelog.yaml` file (a different name/path can be configured when creating the `MigrationRunner`) should be accessible
on classpath. The structure of the file should correspond to `src/main/resources/changelog-scheme.json`, so for example:

```yaml
changeSets:
  - id: 1
    changes:
      - type: renameResource
        oldIri: "http://example.org/OldResource"
        newIri: "http://example.org/NewResource"
        graph: "http://example.org/TargetGraph"
```

## License

MIT

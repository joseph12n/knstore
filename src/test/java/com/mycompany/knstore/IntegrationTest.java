package com.mycompany.knstore;

import com.mycompany.knstore.config.AsyncSyncConfiguration;
import com.mycompany.knstore.config.JacksonConfiguration;
import com.mycompany.knstore.config.MongoDbTestContainer;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { KnstoreApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class })
@ImportTestcontainers(MongoDbTestContainer.class)
public @interface IntegrationTest {}

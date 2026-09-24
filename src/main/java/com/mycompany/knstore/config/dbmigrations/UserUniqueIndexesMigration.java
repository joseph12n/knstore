package com.mycompany.knstore.config.dbmigrations;

import com.mongodb.client.MongoCollection;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.PartialIndexFilter;

/**
 * Normalizes user emails to lowercase, removes duplicates by email and login, and creates
 * unique partial indexes on both fields.
 */
@ChangeUnit(id = "user-unique-email-login-index", order = "006")
public class UserUniqueIndexesMigration {

    private static final Logger LOG = LoggerFactory.getLogger(UserUniqueIndexesMigration.class);

    private static final String COLLECTION = "project_user";

    private final MongoTemplate template;

    public UserUniqueIndexesMigration(MongoTemplate template) {
        this.template = template;
    }

    @Execution
    public void changeSet() {
        MongoCollection<Document> collection = template.getCollection(COLLECTION);
        // Actualizacion con pipeline de aggregation: el segundo argumento debe ser un ARRAY
        // para que $toLower se EVALUE (si no, se guarda como valor literal).
        collection.updateMany(
            new Document("email", new Document("$type", "string")),
            List.of(new Document("$set", new Document("email", new Document("$toLower", "$email"))))
        );
        removeDuplicates(collection, "email");
        removeDuplicates(collection, "login");
        template.indexOps(COLLECTION).ensureIndex(
            new Index("email", Sort.Direction.ASC)
                .named("unique_user_email")
                .unique()
                .partial(PartialIndexFilter.of(Document.parse("{'email': {'$type': 'string'}}")))
        );
        template.indexOps(COLLECTION).ensureIndex(
            new Index("login", Sort.Direction.ASC)
                .named("unique_user_login")
                .unique()
                .partial(PartialIndexFilter.of(Document.parse("{'login': {'$type': 'string'}}")))
        );
    }

    @RollbackExecution
    public void rollback() {
        template.indexOps(COLLECTION).dropIndex("unique_user_email");
        template.indexOps(COLLECTION).dropIndex("unique_user_login");
    }

    private void removeDuplicates(MongoCollection<Document> collection, String field) {
        List<Document> documents = new ArrayList<>();
        collection
            .find(new Document(field, new Document("$type", "string")))
            .projection(new Document("_id", 1).append(field, 1).append("activated", 1).append("createdDate", 1))
            .forEach(documents::add);

        Map<String, List<Document>> groups = new HashMap<>();
        for (Document document : documents) {
            Object value = document.get(field);
            if (!(value instanceof String)) {
                continue;
            }
            groups.computeIfAbsent(((String) value).toLowerCase(), key -> new ArrayList<>()).add(document);
        }

        for (List<Document> group : groups.values()) {
            if (group.size() <= 1) {
                continue;
            }
            List<Document> sorted = group
                .stream()
                .sorted(
                    Comparator.comparing((Document document) -> document.getBoolean("activated", false))
                        .reversed()
                        .thenComparing(document -> document.getDate("createdDate"), Comparator.nullsFirst(Comparator.naturalOrder()))
                )
                .toList();
            List<Object> idsToRemove = new ArrayList<>();
            for (Document document : sorted.subList(0, sorted.size() - 1)) {
                idsToRemove.add(document.get("_id"));
            }
            collection.deleteMany(new Document("_id", new Document("$in", idsToRemove)));
            LOG.warn("Removed {} duplicate user(s) with the same {}", idsToRemove.size(), field);
        }
    }
}

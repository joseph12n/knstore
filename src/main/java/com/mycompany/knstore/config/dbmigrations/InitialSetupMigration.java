package com.mycompany.knstore.config.dbmigrations;

import com.mycompany.knstore.config.Constants;
import com.mycompany.knstore.domain.Authority;
import com.mycompany.knstore.domain.User;
import com.mycompany.knstore.security.AuthoritiesConstants;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import java.time.Instant;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Creates the initial database setup.
 */
@ChangeUnit(id = "users-initialization-fix-manager", order = "002")
public class InitialSetupMigration {

    private final MongoTemplate template;

    public InitialSetupMigration(MongoTemplate template) {
        this.template = template;
    }

    @Execution
    public void changeSet() {
        Authority userAuthority = ensureAuthority(AuthoritiesConstants.USER);
        Authority managerAuthority = ensureAuthority(AuthoritiesConstants.MANAGER);
        Authority adminAuthority = ensureAuthority(AuthoritiesConstants.ADMIN);
        Authority clienteAuthority = ensureAuthority(AuthoritiesConstants.CLIENTE);

        addUsers(userAuthority, adminAuthority, managerAuthority, clienteAuthority);
    }

    @RollbackExecution
    public void rollback() {}

    private Authority createAuthority(String authority) {
        Authority createdAuthority = new Authority();
        createdAuthority.setName(authority);
        return createdAuthority;
    }

    private Authority ensureAuthority(String authorityName) {
        Authority authority = template.findById(authorityName, Authority.class);
        if (authority == null) {
            authority = template.save(createAuthority(authorityName));
        }
        return authority;
    }

    private void addUsers(Authority userAuthority, Authority adminAuthority, Authority managerAuthority, Authority clienteAuthority) {
        template.save(upsertUser(createUser(userAuthority), "user"));
        template.save(upsertUser(createAdmin(adminAuthority, userAuthority), "admin"));
        template.save(upsertUser(createManager(managerAuthority, userAuthority), "manager"));
        template.save(upsertUser(createCliente(clienteAuthority, userAuthority), "cliente"));
    }

    private User upsertUser(User expectedUser, String login) {
        User existingUser = template.findOne(Query.query(Criteria.where("login").is(login)), User.class);
        if (existingUser == null) {
            return expectedUser;
        }

        existingUser.setFirstName(expectedUser.getFirstName());
        existingUser.setLastName(expectedUser.getLastName());
        existingUser.setEmail(expectedUser.getEmail());
        existingUser.setPassword(expectedUser.getPassword());
        existingUser.setActivated(expectedUser.isActivated());
        existingUser.setLangKey(expectedUser.getLangKey());
        existingUser.setCreatedBy(Constants.SYSTEM);
        existingUser.setCreatedDate(Instant.now());
        existingUser.getAuthorities().clear();
        existingUser.getAuthorities().addAll(expectedUser.getAuthorities());
        return existingUser;
    }

    private User createUser(Authority userAuthority) {
        User userUser = new User();
        userUser.setId("user-2");
        userUser.setLogin("user");
        userUser.setPassword("$2a$10$VEjxo0jq2YG9Rbk2HmX9S.k1uZBGYUHdUcid3g/vfiEl7lwWgOH/K");
        userUser.setFirstName("User");
        userUser.setLastName("User");
        userUser.setEmail("user@localhost");
        userUser.setActivated(true);
        userUser.setLangKey("es");
        userUser.setCreatedBy(Constants.SYSTEM);
        userUser.setCreatedDate(Instant.now());
        userUser.getAuthorities().add(userAuthority);
        return userUser;
    }

    private User createAdmin(Authority adminAuthority, Authority userAuthority) {
        User adminUser = new User();
        adminUser.setId("user-1");
        adminUser.setLogin("admin");
        adminUser.setPassword("$2a$10$gSAhZrxMllrbgj/kkK9UceBPpChGWJA7SYIb1Mqo.n5aNLq1/oRrC");
        adminUser.setFirstName("admin");
        adminUser.setLastName("Administrator");
        adminUser.setEmail("admin@localhost");
        adminUser.setActivated(true);
        adminUser.setLangKey("es");
        adminUser.setCreatedBy(Constants.SYSTEM);
        adminUser.setCreatedDate(Instant.now());
        adminUser.getAuthorities().add(adminAuthority);
        adminUser.getAuthorities().add(userAuthority);
        return adminUser;
    }

    private User createManager(Authority managerAuthority, Authority userAuthority) {
        User managerUser = new User();
        managerUser.setId("user-3");
        managerUser.setLogin("manager");
        managerUser.setPassword("$2a$10$gSAhZrxMllrbgj/kkK9UceBPpChGWJA7SYIb1Mqo.n5aNLq1/oRrC");
        managerUser.setFirstName("manager");
        managerUser.setLastName("Manager");
        managerUser.setEmail("manager@localhost");
        managerUser.setActivated(true);
        managerUser.setLangKey("es");
        managerUser.setCreatedBy(Constants.SYSTEM);
        managerUser.setCreatedDate(Instant.now());
        managerUser.getAuthorities().add(managerAuthority);
        managerUser.getAuthorities().add(userAuthority);
        return managerUser;
    }

    private User createCliente(Authority clienteAuthority, Authority userAuthority) {
        User clienteUser = new User();
        clienteUser.setId("user-4");
        clienteUser.setLogin("cliente");
        clienteUser.setPassword("$2a$10$VEjxo0jq2YG9Rbk2HmX9S.k1uZBGYUHdUcid3g/vfiEl7lwWgOH/K");
        clienteUser.setFirstName("cliente");
        clienteUser.setLastName("Cliente");
        clienteUser.setEmail("cliente@localhost");
        clienteUser.setActivated(true);
        clienteUser.setLangKey("es");
        clienteUser.setCreatedBy(Constants.SYSTEM);
        clienteUser.setCreatedDate(Instant.now());
        clienteUser.getAuthorities().add(clienteAuthority);
        clienteUser.getAuthorities().add(userAuthority);
        return clienteUser;
    }
}

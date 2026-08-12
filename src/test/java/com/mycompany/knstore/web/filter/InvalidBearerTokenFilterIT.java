package com.mycompany.knstore.web.filter;

import static com.mycompany.knstore.security.jwt.JwtAuthenticationTestUtils.BEARER;
import static com.mycompany.knstore.security.jwt.JwtAuthenticationTestUtils.createExpiredToken;
import static com.mycompany.knstore.security.jwt.JwtAuthenticationTestUtils.createTokenWithDifferentSignature;
import static com.mycompany.knstore.security.jwt.JwtAuthenticationTestUtils.createValidToken;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mycompany.knstore.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@IntegrationTest
class InvalidBearerTokenFilterIT {

    @Autowired
    private MockMvc mockMvc;

    @Value("${jhipster.security.authentication.jwt.base64-secret}")
    private String jwtKey;

    @Test
    void testExpiredTokenOnPublicCatalogEndpointIsTreatedAsAnonymous() throws Exception {
        mockMvc.perform(get("/api/productos").header(AUTHORIZATION, BEARER + createExpiredToken(jwtKey))).andExpect(status().isOk());
    }

    @Test
    void testInvalidSignatureTokenOnPublicCatalogEndpointIsTreatedAsAnonymous() throws Exception {
        mockMvc
            .perform(get("/api/categorias").header(AUTHORIZATION, BEARER + createTokenWithDifferentSignature()))
            .andExpect(status().isOk());
    }

    @Test
    void testValidTokenOnPublicCatalogEndpointIsNotStripped() throws Exception {
        mockMvc.perform(get("/api/productos").header(AUTHORIZATION, BEARER + createValidToken(jwtKey))).andExpect(status().isOk());
    }

    @Test
    void testExpiredTokenOnProtectedEndpointStillUnauthorized() throws Exception {
        mockMvc
            .perform(get("/api/authenticate").header(AUTHORIZATION, BEARER + createExpiredToken(jwtKey)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testExpiredTokenOnAdminEndpointStillUnauthorized() throws Exception {
        mockMvc
            .perform(get("/api/admin/users").header(AUTHORIZATION, BEARER + createExpiredToken(jwtKey)))
            .andExpect(status().isUnauthorized());
    }
}

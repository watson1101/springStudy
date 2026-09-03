package com.ms.learn.user;

import cn.dev33.satoken.sso.model.TicketModel;
import cn.dev33.satoken.sso.template.SaSsoServerTemplate;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserAuthIntegrationTest {

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String SSO_CLIENT = "microservice-frontend";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SaSsoServerTemplate ssoServerTemplate;

    @Test
    void registrationLoginCurrentUserAndLogoutWork() throws Exception {
        register("alice");

        String passwordHash = jdbcTemplate.queryForObject(
                "SELECT password_hash FROM t_user WHERE username = ?",
                String.class,
                "alice");
        assertThat(passwordHash).startsWith("$2");
        assertThat(passwordHash).doesNotContain("password123");

        mockMvc.perform(post("/api/user/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("alice", "wrong-password")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));

        String token = login("alice");

        mockMvc.perform(get("/api/user/auth/me")
                        .header(TOKEN_HEADER, TOKEN_PREFIX + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("alice"))
                .andExpect(jsonPath("$.data.roles[0]").value("user"))
                .andExpect(jsonPath("$.data.permissions[0]").value("user:self"));

        mockMvc.perform(post("/api/user/auth/logout")
                        .header(TOKEN_HEADER, TOKEN_PREFIX + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/user/auth/me")
                        .header(TOKEN_HEADER, TOKEN_PREFIX + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void permissionChecksFollowDatabaseRoleAssignments() throws Exception {
        register("operator");
        String token = login("operator");

        mockMvc.perform(get("/api/user/list")
                        .header(TOKEN_HEADER, TOKEN_PREFIX + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));

        jdbcTemplate.update("""
                INSERT INTO t_user_role (user_id, role_id)
                SELECT u.id, r.id
                FROM t_user u
                JOIN t_role r ON r.code = 'admin'
                WHERE u.username = ?
                """, "operator");

        mockMvc.perform(get("/api/user/list")
                        .header(TOKEN_HEADER, TOKEN_PREFIX + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[*].username").isArray());
    }

    @Test
    void ssoServerAuthenticatesAndIssuesOneTimeTicket() throws Exception {
        register("sso-user");

        mockMvc.perform(get("/sso/auth")
                        .param("client", SSO_CLIENT)
                        .param("redirect", "http://localhost:5173/callback"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));

        MvcResult loginResult = mockMvc.perform(post("/sso/doLogin")
                        .param("name", "sso-user")
                        .param("pwd", "password123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();
        String token = JsonPath.read(loginResult.getResponse().getContentAsString(), "$.data.tokenValue");

        MvcResult authResult = mockMvc.perform(get("/sso/auth")
                        .header(TOKEN_HEADER, TOKEN_PREFIX + token)
                        .param("client", SSO_CLIENT)
                        .param("redirect", "http://localhost:5173/callback"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String location = authResult.getResponse().getRedirectedUrl();
        assertThat(location).startsWith("http://localhost:5173/callback");
        String ticket = queryParameter(location, "ticket");
        TicketModel ticketModel = ssoServerTemplate.checkTicketParamAndDelete(ticket, SSO_CLIENT);
        assertThat(ticketModel.getLoginId().toString()).isNotBlank();
        assertThat(ssoServerTemplate.getTicket(ticket)).isNull();

        mockMvc.perform(get("/sso/auth")
                        .header(TOKEN_HEADER, TOKEN_PREFIX + token)
                        .param("client", SSO_CLIENT)
                        .param("redirect", "https://attacker.example/callback"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(30002));
    }

    private void register(String username) throws Exception {
        mockMvc.perform(post("/api/user/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson(username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value(username))
                .andExpect(jsonPath("$.data.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.data.roles[0]").value("user"));
    }

    private String login(String username) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/user/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson(username, "password123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tokenName").value(TOKEN_HEADER))
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.data.tokenValue");
    }

    private String queryParameter(String location, String name) {
        return Arrays.stream(URI.create(location).getRawQuery().split("&"))
                .map(parameter -> parameter.split("=", 2))
                .filter(pair -> pair[0].equals(name))
                .map(pair -> URLDecoder.decode(pair[1], StandardCharsets.UTF_8))
                .findFirst()
                .orElseThrow();
    }

    private String registerJson(String username) {
        return """
                {
                  "username": "%s",
                  "password": "password123",
                  "nickname": "Test User",
                  "email": "%s@example.com"
                }
                """.formatted(username, username);
    }

    private String loginJson(String username, String password) {
        return """
                {
                  "username": "%s",
                  "password": "%s"
                }
                """.formatted(username, password);
    }
}

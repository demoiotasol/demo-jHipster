package com.demo.app.service;

import com.demo.app.Application;
import com.demo.app.domain.PersistentToken;
import com.demo.app.domain.User;
import com.demo.app.repository.PersistentTokenRepository;
import com.demo.app.repository.UserRepository;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("dev")
public class UserServiceTest {

    @Inject
    private PersistentTokenRepository persistentTokenRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    @Test
    public void testRemoveOldPersistentTokens() {
        assertEquals(0, persistentTokenRepository.findAll().size());
        User admin = userRepository.findOne("admin");
        generateUserToken(admin, "1111-1111", new LocalDate());
        LocalDate now = new LocalDate();
        generateUserToken(admin, "2222-2222", now.minusDays(32));
        assertEquals(2, persistentTokenRepository.findAll().size());
        userService.removeOldPersistentTokens();
        assertEquals(1, persistentTokenRepository.findAll().size());
    }

    private void generateUserToken(User user, String tokenSeries, LocalDate localDate) {
        PersistentToken token = new PersistentToken();
        token.setSeries(tokenSeries);
        token.setUser(user);
        token.setTokenValue(tokenSeries + "-data");
        token.setTokenDate(localDate);
        token.setIpAddress("127.0.0.1");
        token.setUserAgent("Test agent");
        persistentTokenRepository.saveAndFlush(token);
    }
}

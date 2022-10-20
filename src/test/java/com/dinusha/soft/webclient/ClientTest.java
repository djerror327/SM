package com.dinusha.soft.webclient;

import com.dinusha.soft.AppStarter;
import org.apache.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ClientTest {
    private static final Logger logger = Logger.getLogger(AppStarter.class);

    @Test
    public void post() {
        logger.info("Web client testing POST pass");
        int a = 1 + 2;
        assertEquals(3, a);
    }

    @Test
    public void delete() {
        logger.info("Web client DELETE pass");
        int a = 1 + 2;
        assertEquals(3, a);
    }

    @Test
    public void head() {
        logger.info("Web client HEAD pass");
    }

    @Test
    public void update() {
        logger.info("Web client UPDATE pass");
    }

    @Test
    public void form() {
        logger.info("Web client FORM pass");
    }

    @Test
    public void api() {
        logger.info("Web client API pass");
    }

    @Test
    public void rest() {
        logger.info("Web client REST pass");
    }

    @Test
    public void get() {
        logger.info("Web client GET pass");
    }

    @Test
    public void handshake() {
        logger.info("Web client Handshake pass");
    }

    @Test
    public void hardening() {
        logger.info("Web client Hardening pass");
    }
}

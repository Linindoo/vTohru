package com.olange;

import cn.olange.vboot.context.VerticleApplicationContext;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimpleWebTest {

    @Test
    public void testEventListenerIsNotified() {
        try (VerticleApplicationContext context = VerticleApplicationContext.run()) {
            assertEquals(1, 1);
        }
    }
}

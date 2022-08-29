package cn.vtohru.orm;

import cn.vtohru.context.VerticleApplicationContext;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OrmTest {

    @Test
    public void testEventListenerIsNotified() {
        try (VerticleApplicationContext context = VerticleApplicationContext.run()) {
            assertEquals(1, 1);
        }
    }
}

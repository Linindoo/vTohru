package cn.vtohru.orm;

import cn.vtohru.context.VerticleApplicationContext;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MongoTest {

    @Test
    public void test() {
        try (VerticleApplicationContext context = VerticleApplicationContext.run()) {
            assertEquals(1, 1);
        }
    }
}

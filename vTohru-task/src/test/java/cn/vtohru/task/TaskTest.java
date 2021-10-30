package cn.vtohru.task;

import cn.vtohru.context.VerticleApplicationContext;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TaskTest {

    @Test
    public void test() {
        try (VerticleApplicationContext context = VerticleApplicationContext.run()) {
            assertEquals(1, 1);
        }
    }
}

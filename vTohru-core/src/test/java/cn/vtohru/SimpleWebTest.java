package cn.vtohru;

import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.model.SimpleModel;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimpleWebTest {

    @Test
    public void testEventListenerIsNotified() {
        try (VerticleApplicationContext context = VerticleApplicationContext.run()) {
            assertEquals(1, 1);
        }
    }

    @Test
    public void jsonTest() {
        SimpleModel simpleModel = new SimpleModel();
        simpleModel.setName("jsdfs");
        JsonObject entries = new JsonObject();
        entries.put("data", simpleModel);
        System.out.println(entries.toBuffer());
    }
}

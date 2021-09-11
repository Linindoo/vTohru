package cn.vtohru.orm.mapping;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.*;

import java.time.Instant;

public class Json {
    public static final ObjectMapper mapper = new ObjectMapper();
    public static ObjectMapper prettyMapper = new ObjectMapper();
    private static void initialize() {
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        prettyMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        prettyMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        SimpleModule module = new SimpleModule();
        mapper.registerModule(module);
        prettyMapper.registerModule(module);
    }

    static {
        initialize();
    }

}

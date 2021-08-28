package cn.vtohru.orm.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.IOException;

public class JsonConverter {
    public static Object convertJsonNodeToVertx(JsonNode jsonNode) throws IOException {
        if (jsonNode.isValueNode()) {
            return convertValueNode(jsonNode);
        } else if (jsonNode.isArray()) {
            JsonArray jsonArray = new JsonArray();
            ArrayNode arrayNode = (ArrayNode) jsonNode;
            for (JsonNode subNode : arrayNode) {
                jsonArray.add(convertJsonNodeToVertx(subNode));
            }
            return jsonArray;
        } else {
            return JsonObject.mapFrom(jsonNode);
        }
    }

    /**
     * Returns the fitting value for each value node type. The given jsonNode must be a value node.
     *
     * @param jsonNode
     *          the node to convert
     * @return the value of the node, which can be Number, Boolean, byte-array, or String
     * @throws IOException
     */
    public static Object convertValueNode(JsonNode jsonNode) throws IOException {
        if (!jsonNode.isValueNode())
            throw new IllegalArgumentException("Expected a value node, but got " + jsonNode.getNodeType());

        if (jsonNode.isNumber()) {
            return jsonNode.numberValue();
        } else if (jsonNode.isBoolean()) {
            return jsonNode.booleanValue();
        } else if (jsonNode.isBinary()) {
            return jsonNode.binaryValue();
        } else {
            return jsonNode.textValue();
        }
    }
}

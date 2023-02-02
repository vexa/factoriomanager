package at.faist.data.model.docker;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class ConfigJsonDeserializer extends StdDeserializer<Config> {
    public ConfigJsonDeserializer() {
        this(null);
    }

    protected ConfigJsonDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Config deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        if (node.get("subnet") != null) {
            var subnet = node.get("subnet").asText();
            return new SubnetConfig(subnet);
        }
        return null;
    }
}

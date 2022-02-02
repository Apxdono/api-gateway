package org.example.api.gw.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import okhttp3.HttpUrl;
import org.example.api.gw.utils.Checks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Deserializer for {@link HttpUrl}.
 */
public class HttpUrlDeserializer extends StdDeserializer<HttpUrl> {
    private static final Logger LOG = LoggerFactory.getLogger(HttpUrlDeserializer.class);

    public HttpUrlDeserializer() {
        super(HttpUrl.class);
    }

    @Override
    public HttpUrl deserialize(JsonParser jp, DeserializationContext dctx) throws IOException, JacksonException {
        var node = jp.getCodec().readTree(jp);

        var urlString = node.isValueNode() ? ((TextNode) node).textValue() : null;
        if (urlString == null) {
            LOG.warn("Cannot parse url. Received null instead of proper url string");
            return null;
        }
        var url = HttpUrl.parse(urlString);
        if (url == null) {
            LOG.warn("Failed to parse url. " + Checks.actualString(urlString));
        }
        return url;
    }
}

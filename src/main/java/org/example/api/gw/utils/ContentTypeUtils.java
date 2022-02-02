package org.example.api.gw.utils;

import io.javalin.http.ContentType;
import okhttp3.MediaType;

import java.util.Optional;

public final class ContentTypeUtils {
    private static final MediaType TEXT_PLAIN = MediaType.parse(ContentType.PLAIN);

    public static MediaType toMediaTypeSafe(String type) {
        return toMediaType(type).orElse(TEXT_PLAIN);
    }

    public static Optional<MediaType> toMediaType(String type) {
        return Optional.ofNullable(type)
                .map(MediaType::parse);
    }
}

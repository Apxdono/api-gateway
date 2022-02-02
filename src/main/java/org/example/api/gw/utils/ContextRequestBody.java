package org.example.api.gw.utils;

import io.javalin.http.Context;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class ContextRequestBody extends RequestBody {

    private final Context ctx;
    private final MediaType contentType;

    public ContextRequestBody(Context context, MediaType contentType) {
        this.ctx = context;
        this.contentType = contentType;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return contentType;
    }

    @Override
    public long contentLength() throws IOException {
        var length = this.ctx.contentLength();
        if (length > -1) {
            return length;
        }
        return super.contentLength();
    }

    @Override
    public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
        bufferedSink.writeAll(Okio.source(ctx.bodyAsInputStream()));
    }
}

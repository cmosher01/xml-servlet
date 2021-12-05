package nu.mine.mosher.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.*;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;

import java.util.*;

public class ContentTypeFilter extends HttpFilter {
    @Override
    @SneakyThrows
    public void doFilter(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final FilterChain chain) {
        val ctx = Objects.requireNonNull(request.getServletContext());

        val urlPath = ServletUtilities.pathInfo(request);
        val u = Optional.ofNullable(ctx.getResource(urlPath.toString()));
        if (u.isPresent()) {
            val urlResource = u.get();

            val metatika = new Metadata();
            metatika.set(TikaCoreProperties.RESOURCE_NAME_KEY, urlResource.getPath());

            try (val in = TikaInputStream.get(urlResource, metatika)) {
                val contentType = TikaConfig.getDefaultConfig().getDetector().detect(in, metatika);
                ctx.log("Detected content type: " + contentType);
                response.setContentType(contentType.toString());
                ctx.log("Set content type to: " + response.getContentType());

                val characterEncoding = Optional.ofNullable(TikaConfig.getDefaultConfig().getEncodingDetector().detect(in, metatika)).orElse(XmlFilterUtilities.DEFAULT_UNKNOWN_CHARSET);
                ctx.log("Detected character encoding: " + characterEncoding);
                response.setCharacterEncoding(characterEncoding.name());
                ctx.log("Set character encoding to: " + response.getCharacterEncoding());
            }
        }

        super.doFilter(request, response, chain);
    }
}

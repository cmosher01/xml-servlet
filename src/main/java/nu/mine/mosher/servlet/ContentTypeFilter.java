package nu.mine.mosher.servlet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.*;
import lombok.*;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.*;

import java.nio.charset.Charset;
import java.util.*;

public class ContentTypeFilter extends HttpFilter {
    public static final Charset DEFAULT_UNKNOWN_CHARSET = Charset.forName("windows-1252");

    @Override
    @SneakyThrows
    public void doFilter(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final FilterChain chain) {
        val ctx = Objects.requireNonNull(request.getServletContext());

        val optUrl = Optional.ofNullable(ctx.getResource(ServletUtilities.pathInfo(request).toString()));
        if (optUrl.isPresent()) {
            val urlResource = optUrl.get();

            val metatika = new Metadata();
            metatika.set(TikaCoreProperties.RESOURCE_NAME_KEY, urlResource.getPath());

            try (val in = TikaInputStream.get(urlResource, metatika)) {
                val contentType = TikaConfig.getDefaultConfig().getDetector().detect(in, metatika);
                ctx.log("Detected content type: " + contentType);
                response.setContentType(contentType.toString());

                val characterEncoding = Optional.ofNullable(TikaConfig.getDefaultConfig().getEncodingDetector().detect(in, metatika)).orElse(DEFAULT_UNKNOWN_CHARSET);
                ctx.log("Detected character encoding: " + characterEncoding);
                response.setCharacterEncoding(characterEncoding.name());
            }
        }

        super.doFilter(request, response, chain);
    }
}

package nu.mine.mosher.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import lombok.*;
import org.apache.tika.io.TikaInputStream;

import java.io.*;
import java.net.URL;
import java.util.*;

@WebServlet("/d1/d2/*")
public class PlayServlet extends HttpServlet {
    @Override
    @SneakyThrows
    public void doGet(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response) {
        val ctx = Objects.requireNonNull(request.getServletContext());

        val urlPath = ServletUtilities.pathInfo(request);

        if (urlPath.withoutTrailingSlash().equals("/health")) {
            return; // OK
        }

        if (ServletUtilities.isFile(ctx, urlPath)) {
            send(ctx.getResource(urlPath.toString()), response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private static void send(@NonNull final URL res, @NonNull final HttpServletResponse response) throws IOException {
        try (
            val inr = new InputStreamReader(TikaInputStream.get(res), response.getCharacterEncoding());
            val out = response.getWriter()
        ) {
            inr.transferTo(out);
        }
    }
}

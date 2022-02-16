package nu.mine.mosher.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PageStyleFilter extends HttpFilter {
    private String stylesheet;
    private String script;
    private String pageclass;

    @Override
    @SneakyThrows
    public void init(@NonNull final FilterConfig config) {
        super.init(config);

        this.stylesheet = ServletUtilities.requireParam(config, "stylesheet");
        this.script = ServletUtilities.requireParam(config, "script");
        this.pageclass = ServletUtilities.requireParam(config, "pageclass");
    }

    @Override
    @SneakyThrows
    protected void doFilter(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final FilterChain chain) {
        log.trace("filter entry");

        request.setAttribute("nu.mine.mosher.xml.stylesheet", this.stylesheet);
        request.setAttribute("nu.mine.mosher.xml.script", this.script);
        request.setAttribute("nu.mine.mosher.xml.pageclass", this.pageclass);

        log.trace("filter forward");
        super.doFilter(request, response, chain);
        log.trace("filter return");

        log.trace("filter exit");
    }

    @Override
    @SneakyThrows
    public void destroy() {
        this.pageclass = null;
        this.script = null;
        this.stylesheet = null;

        super.destroy();
    }
}

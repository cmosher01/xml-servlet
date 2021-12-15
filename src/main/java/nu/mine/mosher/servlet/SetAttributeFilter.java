package nu.mine.mosher.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.xml.transform.dom.*;
import javax.xml.transform.stream.StreamSource;
import java.util.*;

@Slf4j
public class SetAttributeFilter extends HttpFilter {
    private String attrName;
    private String attrValue;

    @Override
    @SneakyThrows
    public void init(@NonNull final FilterConfig config) {
        super.init(config);

        this.attrName = ServletUtilities.requireParam(config, "attrName");
        this.attrValue = ServletUtilities.requireParam(config, "attrValue");
    }

    @Override
    @SneakyThrows
    protected void doFilter(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final FilterChain chain) {
        log.trace("filter entry");

        request.setAttribute(this.attrName, this.attrValue);

        log.trace("filter forward");
        super.doFilter(request, response, chain);
        log.trace("filter return");

        log.trace("filter exit");
    }

    @Override
    @SneakyThrows
    public void destroy() {
        this.attrValue = null;
        this.attrName = null;

        super.destroy();
    }
}

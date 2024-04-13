package org.httptest.networkclient.filter;

import jakarta.servlet.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Enumeration;

public class ServiceProviderCharsetFilter implements Filter {
    private boolean force = false;
    private String encoding = "UTF-8";

    public ServiceProviderCharsetFilter() {
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        Enumeration<String> names = filterConfig.getInitParameterNames();

        while(names.hasMoreElements()) {
            String name = (String)names.nextElement();

            try {
                String f;
                if (StringUtils.equalsIgnoreCase("ENCODING", name)) {
                    f = StringUtils.trim(filterConfig.getInitParameter(name));
                    if (StringUtils.isNotBlank(f)) {
                        this.encoding = f;
                    }
                } else if (StringUtils.equalsIgnoreCase("FORCE", name)) {
                    f = StringUtils.trim(filterConfig.getInitParameter(name));
                    if (StringUtils.equalsAnyIgnoreCase(f, new CharSequence[]{"true", "false"})) {
                        this.force = Boolean.parseBoolean(f);
                    }
                }
            } catch (Exception var5) {
            }
        }

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (this.encoding != null && (this.force || request.getCharacterEncoding() == null)) {
            request.setCharacterEncoding(this.encoding);
            if (this.force) {
                response.setCharacterEncoding(this.encoding);
            }
        }

        chain.doFilter(request, response);
    }

    public void destroy() {
    }
}
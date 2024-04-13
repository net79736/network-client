package org.httptest.networkclient.config;

import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import lombok.extern.slf4j.Slf4j;
import org.httptest.networkclient.dispatch.SampleDispatcher;
import org.httptest.networkclient.filter.ServiceProviderCharsetFilter;
import org.httptest.networkclient.listener.NetworkClientConfigurationListener;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class NetworkClientConfig implements ServletContextInitializer {
    private static final String CONFIG_PARAM_NAME = "NETWORK_CLIENT_CONFIG_PATH";

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        log.trace("NetworkClientConfig.onStartup");

//        String HOME_PATH = System.getProperty("lee.home");
        final String HOME_PATH = "/Users/ijong-ug/IdeaProjects/network-client/src/main/resources/static";
        log.debug("HOME_PATH : {}", HOME_PATH);
        System.setProperty(CONFIG_PARAM_NAME, "filepath:/" + HOME_PATH + "/config/lee.properties");
        servletContext.setInitParameter(CONFIG_PARAM_NAME, "filepath:/" + HOME_PATH + "/config/lee.properties");
        servletContext.addListener(new NetworkClientConfigurationListener());

        FilterRegistration encodingFilter = servletContext.addFilter("ServiceProviderCharsetFilter", new ServiceProviderCharsetFilter());
        encodingFilter.setInitParameter("encoding", "UTF-8");
        encodingFilter.setInitParameter("force", "false");
        encodingFilter.addMappingForUrlPatterns(null, false, "/write/text/*");

        ServletRegistration networkClientDispatcher = servletContext.addServlet("SampleDispatcher", new SampleDispatcher());
        networkClientDispatcher.addMapping("/write/text/*");
    }
}

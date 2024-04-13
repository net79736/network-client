package org.httptest.networkclient.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.EnumSet;
import java.util.Properties;

import org.httptest.networkclient.config.Env;
import org.httptest.networkclient.dto.ConfigLoadType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NetworkClientConfigurationListener implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(NetworkClientConfigurationListener.class);
    private static final String CONFIG_PARAM_NAME = "NETWORK_CLIENT_CONFIG_PATH";
    private static final String FILE_PATH_PREFIX = "filepath:";

    public NetworkClientConfigurationListener() {
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();

        log.info("NetworkClient initialize start");
        String fileName = System.getProperty(CONFIG_PARAM_NAME);
        if (StringUtils.isBlank(fileName)) {
            if (servletContext != null) {
                fileName = servletContext.getInitParameter(CONFIG_PARAM_NAME);
                log.debug("servletContext.getInitParameter(\"{}\") = {}", "NETWORK_CLIENT_CONFIG_PATH", fileName);
            }
        } else {
            log.debug("System.getProperty(\"{}\") = {}", "NETWORK_CLIENT_CONFIG_PATH", fileName);
        }

        if (StringUtils.isBlank(fileName)) {
            throw new RuntimeException("Network Client Configuration file path null.");
        } else {
            ConfigLoadType loadType = ConfigLoadType.Unknown;
            if (StringUtils.startsWithIgnoreCase(fileName, FILE_PATH_PREFIX)) {
                fileName = StringUtils.substringAfter(fileName, FILE_PATH_PREFIX);
                loadType = ConfigLoadType.Filepath;
            } else {
                throw new RuntimeException("Network Client Configuration does not support ConfigLoadType.");
            }

            if (!ConfigLoadType.Unknown.equals(loadType) && EnumSet.allOf(ConfigLoadType.class).contains(loadType)) {
                log.info("Configuration file path: {}", fileName);
                log.info("Configuration file load type: {}", loadType);
                InputStream fileInputStream = null;

                try {
                    fileInputStream = EnumSet.of(ConfigLoadType.Classpath, ConfigLoadType.Classes).contains(loadType) ? Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName) : new FileInputStream(fileName);
                    Properties properties = new Properties();
                    properties.load((InputStream)fileInputStream);
                    Env.NETWORK_CLIENT_DOMAIN_CONTEXT = (String)StringUtils.defaultIfBlank(properties.getProperty("NETWORK_CLIENT_DOMAIN_CONTEXT"), "");
                    Env.NAME = (String)StringUtils.defaultIfBlank(properties.getProperty("NETWORK_URL"), "");
                    Env.MANAUL_LOGIN_URL = (String)StringUtils.defaultIfBlank(properties.getProperty("MANAUL_LOGIN_URL"), "");
                    Env.PROXY_SERVER_URL = (String)StringUtils.defaultIfBlank(properties.getProperty("PROXY_SERVER_URL"), "");

                    if (log.isDebugEnabled()) {
                        Class<Env> envClass = Env.class;
                        Field[] var8 = envClass.getDeclaredFields();
                        int var9 = var8.length;

                        for(int var10 = 0; var10 < var9; ++var10) {
                            Field field = var8[var10];
                            if (!Modifier.isFinal(field.getModifiers())) {
                                log.debug("[{}] is defined as [{}]", field.getName(), field.get(envClass));
                            }
                        }
                    }
                } catch (Exception var23) {
                    throw new RuntimeException(var23);
                } finally {
                    if (fileInputStream != null) {
                        try {
                            ((InputStream)fileInputStream).close();
                        } catch (IOException var22) {
                            if (log.isDebugEnabled()) {
                                log.warn("", var22);
                            } else {
                                log.warn("{}", var22.toString());
                            }
                        }
                    }

                }

                log.info("NetworkClientConfigurationListener initialize finished");
            } else {
                log.error("Unknown file load type: {}", loadType);
                throw new RuntimeException("Unknown file load type: " + loadType);
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        // do Nothing
    }

    public void setInitEnvSetting() {

    }
}

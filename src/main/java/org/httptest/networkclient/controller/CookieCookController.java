package org.httptest.networkclient.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.httptest.networkclient.config.cookie.CookieManager;
import org.httptest.networkclient.config.http.HttpConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.net.URLDecoder;
import java.util.List;

@Slf4j
@Controller
public class CookieCookController {

    @Autowired
    HttpConnector httpConnector;

    private static final String proxyUrl = "http://localhost:9010//network-client/valid-route/receive-cookie";

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("name", "jongwook");
        return "index";
    }

    @RequestMapping(value="/network-client/send-cookie", method = RequestMethod.POST)
    public void sendCookie(HttpServletRequest request, HttpServletResponse response) {
        log.info("CookieCookController sendCookie START");
        String name = request.getParameter("name");
        log.info("CookieCookController sendCookie name: {}", name);
        String retCode = null;
        try {
            ResponseEntity<String> responseEntity = httpConnector.sendHttpConnectorV2("senderManagerTicket", proxyUrl, HttpMethod.POST);
            // [응답 값]
            // [Set-Cookie:"name=senderManagerTicket_is_returned_from_receiver;
            // HTTPOnly; domain=localhost;path=/network-client/send-cookie", "Your-Header-Value",
            // Content-Type:"text/plain;charset=UTF-8", Content-Length:"0",
            // Date:"Sun, 14 Apr 2024 14:27:56 GMT", Keep-Alive:"timeout=60", Connection:"keep-alive"]
            HttpHeaders headersEntity = responseEntity.getHeaders();

            // 모든 쿠키를 출력한다.
            parintAllCookie(request);

            this.processCookieAfterHttp(request, response, headersEntity);
            retCode = responseEntity.getBody();
            log.info("CookieCookController sendCookie retCode: {}", retCode);
        } catch (Exception e) {
            log.error("[ERROR] CookieCookController sendCookie e : {}", e.getMessage());
        }
    }

    /**
     * Cookie Recevier 서버에서
     * CookieManager.addCookie("name", rawTicket + "_is_returned_from_receiver", "localhost", "/network-client/cookie-print", request, response);
     * 이런 형식으로 쿠키를 보내준 경우 해당 서블릿에서만 쿠키를 확인 할 수 있었다.
     *
     * @param request
     * @param response
     */
    @RequestMapping("/network-client/cookie-print")
    public void printCookie(HttpServletRequest request, HttpServletResponse response) {
        // 모든 쿠키를 출력한다.
        parintAllCookie(request);
    }

    private void processCookieAfterHttp(HttpServletRequest request, HttpServletResponse response, HttpHeaders headersEntity) {
        log.info("processCookieAfterHttp > headersEntity : {}", headersEntity);
        final String name = iteratorCookieByHeaders(headersEntity, "name");
        final String domain = iteratorCookieByHeaders(headersEntity, "domain");
        final String path = iteratorCookieByHeaders(headersEntity, "path");

        log.info("processCookieAfterHttp > name : {}", URLDecoder.decode(name));
        log.info("processCookieAfterHttp > domain : {}", domain);
        log.info("processCookieAfterHttp > path : {}", path); // path 가 "/" 인 경우 모든 URI path 에서 모두 출력이 가능

        setCookieAfterValidSessionCehck("newName", URLDecoder.decode(name), domain, path, request, response);
    }

    private void setCookieAfterValidSessionCehck(String key, String value, String domain, String path, HttpServletRequest request, HttpServletResponse response) {
        log.info("setCookieAfterValidSessionCehck key : {}, value: {}", key, value);
        CookieManager.addCookie(key, value, domain, path, request, response);
    }

    private String iteratorCookieByHeaders(HttpHeaders headers, String key) {
        List<String> cookies = headers.get("Set-Cookie");
        for (String cookie : cookies) {
            // 이름과 값 추출
            String value = extractValueByKey(cookie, key);
            if (!StringUtils.isEmpty(value)) return value;
        }
        return null;
    }

    private String extractValueByKey(String cookie, String key) {
        String[] parts = cookie.split(";");
        for (String part : parts) {
            String trimmedPart = part.trim();
            if (trimmedPart.startsWith(key + "=")) {
                return trimmedPart.substring(key.length() + 1);
            }
        }
        return null; // 해당 키를 찾지 못한 경우
    }

    private void parintAllCookie(HttpServletRequest request) {
        log.debug("쿠키 출력 중...");

        Cookie[] cookies = request.getCookies();
        if (!ObjectUtils.isEmpty(cookies)) {
            for (Cookie cookie : cookies) {
                log.debug(cookie.getName());
                log.debug(cookie.getValue());
            }
        }
    }

}

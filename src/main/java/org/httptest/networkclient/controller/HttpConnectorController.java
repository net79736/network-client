package org.httptest.networkclient.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.httptest.networkclient.config.http.HttpConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class HttpConnectorController {

    @Autowired
    HttpConnector httpConnector;

    @RequestMapping("/network-client/return-string-request")
    public ResponseEntity returnStringRequest() {
        log.info("HttpConnectorController returnStringRequest START");
        try {
            httpConnector.sendHttpConnectorV2("제주도 여행권", "http://localhost:8080/network-client/return-string-response", HttpMethod.GET);
        } catch (Exception e) {
            log.error("[ERROR] HttpConnectorController returnStringRequest e : ", e.getMessage());
        }
        return ResponseEntity.ok("");
    }


    @RequestMapping("/network-client/return-string-response")
    public ResponseEntity<String> returnStringRseponse(@RequestParam String rawTicket, HttpServletRequest request, HttpServletResponse response) {
        log.info("HttpConnectorController returnStringRequest START");
        log.info("HttpConnectorController rawTicket : {}", rawTicket);

        return ResponseEntity.ok("jongwookLEE's ticket Name: " + rawTicket);
    }

}

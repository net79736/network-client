package org.httptest.networkclient.config.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class HttpConnector {

    @Autowired
    HttpsUtility httpsUtility;

    /**
     * @param rawTicket      암호화할 ticket 정보
     * @param proxyVerifyUrl 프록시서버 요청 API 주소
     * @return 검증 응답 문자열
     * @throws Exception
     */
    public ResponseEntity<String> sendHttpConnectorV2(String rawTicket, String proxyVerifyUrl) throws Exception {
        String url = String.format("%s?rawTicket=%s", proxyVerifyUrl, rawTicket);

        log.info("sendHttpConnectorV2 START");
        log.info("sendHttpConnectorV2 url : {}", url);

        HttpClient httpClient = httpsUtility.httpClient();
        HttpComponentsClientHttpRequestFactory factory = httpsUtility.factory(httpClient);
        RestTemplate restTemplate = httpsUtility.restTemplate(factory);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

        return responseEntity;
    }

}

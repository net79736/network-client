package org.httptest.networkclient.config.http;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

// org.apache.hc.client5 로 검색 시 RestTemplate 를 생성하는 샘플코드 내용 복사
// 링크 : https://whybk.tistory.com/154
@Configuration
public class HttpsUtility {
    @Bean
    HttpClient httpClient() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

        // 모든 인증서를 신뢰하도록 설정한다
        TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();

        // Https 인증 요청시 호스트네임 유효성 검사를 진행하지 않게 한다.
        // SSL 연결을 위한 소켓 팩토리를 생성합니다. 이때 호스트네임 유효성 검사를 수행하지 않도록 설정합니다.
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

        // ConnectionSocketFactory를 레지스트리에 등록합니다. 여기서는 HTTPS와 HTTP 프로토콜에 대해 SSL 및 일반 소켓 팩토리를 등록합니다.
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslsf) // HTTPS 프로토콜을 위한 SSLConnectionSocketFactory 등록
                .register("http", new PlainConnectionSocketFactory()).build(); // HTTP 프로토콜을 위한 PlainConnectionSocketFactory 등록

        // ConnectionManager 생성
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

        // HttpClientBuilder를 사용하여 HttpClient 생성
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setConnectionManager(connectionManager);
        return httpClientBuilder.build();
    }

    @Bean
    HttpComponentsClientHttpRequestFactory factory(HttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(3000); // 클라이언트가 서버에 연결을 시도하는 데 걸리는 시간을 제어
        factory.setConnectionRequestTimeout(10*1000); // 클라이언트가 서버에 대한 연결을 요청하고 해당 연결을 기다리는 데 걸리는 시간을 제어
        factory.setHttpClient(httpClient);

        return factory;
    }

    @Bean
    RestTemplate restTemplate(HttpComponentsClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }

}

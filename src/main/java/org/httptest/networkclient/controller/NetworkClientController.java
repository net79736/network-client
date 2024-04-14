package org.httptest.networkclient.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.httptest.networkclient.config.AES256Utils;
import org.httptest.networkclient.config.Env;
import org.httptest.networkclient.service.NetworkClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@Slf4j
@Controller
public class NetworkClientController {

    @Autowired
    AES256Utils aes256Utils;
    @Autowired
    private NetworkClientService networkClientService;

    @RequestMapping("/network-client/connect")
    public String connectNetworkClientServer(HttpServletRequest request, Model model) {
        log.info("NetworkClientController connectNetworkClientServer START");
        log.info("NetworkClientController connectNetworkClientServer Env.MANAUL_LOGIN_URL : {}", Env.MANAUL_LOGIN_URL);
        log.info("NetworkClientController connectNetworkClientServer Env.PROXY_SERVER_URL : {}", Env.PROXY_SERVER_URL);
        log.info("NetworkClientController connectNetworkClientServer Env.NAME : {}", Env.NAME);
        log.info("NetworkClientController connectNetworkClientServer Env.NETWORK_CLIENT_DOMAIN_CONTEXT : {}", Env.NETWORK_CLIENT_DOMAIN_CONTEXT);

        try {
            request.setCharacterEncoding("UTF-8");
            HttpSession session = request.getSession();

            // returnURL 정보를 가져온다.
            String urlName = "returnURL=";
            String returnURL = networkClientService.getNextUrl(request.getQueryString(), urlName);

            returnURL = URLDecoder.decode(returnURL, "UTF-8");

            returnURL = returnURL.replaceAll("\\$", "\\&");
            log.info(" ### returnURL : {}", returnURL);

            /************************************************************
             *	ReturnURL 설정
             ************************************************************/
            if (!returnURL.trim().equals("")) session.setAttribute("returnURL", returnURL);
        } catch (Exception e) {}

        model.addAttribute("NAME", Env.NAME);
        model.addAttribute("NETWORK_CLIENT_PAGE", Env.NETWORK_CLIENT_DOMAIN_CONTEXT);

        return "network-client-business";
    }

    // (Step1)
    @RequestMapping(value = {"/network-client/business"} , method = RequestMethod.GET)
    public String businessNetworkClientServer(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
        log.info("businessNetworkClientServer Env.NETWORK_CLIENT_DOMAIN_CONTEXT : {}", Env.NETWORK_CLIENT_DOMAIN_CONTEXT);
        log.info("businessNetworkClientServer Env.MANAUL_LOGIN_URL : {}", Env.MANAUL_LOGIN_URL);
        log.info("NetworkClientController businessNetworkClientServer START");

        try {
            this.setReturnUrlToSessionArea(request); // returnURL 을 session 영역에 저장한다.
        } catch (UnsupportedEncodingException e) {
            log.error("[ERROR] setReturnUrlToSessionArea : {}", e.getMessage());
            throw new RuntimeException(e);
        }

        // 인증서버 통신 체크
        // TODO : 추후에 로직 추가 예정

        try {
            // 실제 통신 실행
        } catch (Exception err) {
            // 서버와 통신이 되지 않을 경우 네트워크 에러 페이지로 리다이렉션 (OK)
        }

        model.addAttribute("NAME", Env.NAME); // NAME
        model.addAttribute("NETWORK_CLIENT_PAGE", Env.MANAUL_LOGIN_URL);
        return "network-client-business";
    }

    // returnURL 을 session 영역에 저장한다.
    public void setReturnUrlToSessionArea(HttpServletRequest request) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        // returnURL 정보를 가져온다.
        String urlName = "returnURL=";
        String returnURL = networkClientService.getNextUrl(request.getQueryString(), urlName);

        returnURL = URLDecoder.decode(returnURL, "UTF-8");

        returnURL = returnURL.replaceAll("\\$", "\\&");
        log.info(" ### returnURL : {}", returnURL);

        /************************************************************
         *	ReturnURL 설정
         ************************************************************/
        if (!returnURL.trim().equals("")) session.setAttribute("returnURL", returnURL);
    }

    // 에러페이지로 이동 (강제 로그아웃)
    @RequestMapping(value = {"/network-client/error"} , method = RequestMethod.GET)
    public String errorIsignPlusServer(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
        log.info("errorIsignPlusServer START");
        HttpSession session = request.getSession();

        // 세션 영역에 저장된 resultCode, resultMessage
        String resultCode = session.getAttribute("resultCode") == null ? "" : session.getAttribute("resultCode").toString();
        String resultMessage = session.getAttribute("resultMessage") == null ? "" : session.getAttribute("resultMessage").toString();
        model.addAttribute("resultCode", resultCode);
        model.addAttribute("resultMessage", resultMessage);
        return "network-client-error";
    }

    // 성공 여부 확인
    @RequestMapping(value = "/network-client/result", method = RequestMethod.POST)
    public String resultNetworkClientServer(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
        try {
            model.addAttribute("resultCode", "0000");
            model.addAttribute("resultMessage", "OK");
        } catch (Exception e) {
            log.error("[ERROR] resultNetworkClientServer message : {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return "network-client-result";
    }

    // 에러 발생 시 로그아웃
    @RequestMapping(value = "/network-client/logout", method = RequestMethod.POST)
    public String logout(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
        log.info("logout START::::");
        HttpSession session = request.getSession();

        try {
            session.invalidate();
        } catch (IllegalStateException e) {
            log.error("[ERROR] failed to session invalidate : {}", e.getMessage());
        }

        return "network-client-error";
    }
}




package org.httptest.networkclient.service;


import lombok.extern.slf4j.Slf4j;
import org.httptest.networkclient.config.AES256Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NetworkClientService {

    @Autowired
    AES256Utils aes256Utils;

    public String getNextUrl(String fullPath, String urlName) {
        if(null == fullPath) return "";
        int idx = fullPath.indexOf(urlName);
        String nextURL = "";
        if(idx > -1)
            nextURL =  fullPath.substring(idx+urlName.length());

        return nextURL;
    }

    // SSO 아이디 값 암호화
    public String mapToEncryptedSsoId(String ssoId) throws Exception {
        return aes256Utils.encrypt_AES(ssoId);
    }

}

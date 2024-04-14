package org.httptest.networkclient.config.cron;


import lombok.extern.slf4j.Slf4j;
import org.httptest.networkclient.config.AES256Utils;
import org.httptest.networkclient.config.FileUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AES256FileChangerCron {

    @Autowired
    AES256Utils aes256Utils;

    /**
     *  초 분 시 일 월 주(년)
     *  "0 0 12 * * ?" : 아무 요일, 매월, 매일 12:00:00
     *  "0 15 10 ? * *" : 모든 요일, 매월, 아무 날이나 10:15:00
     *  "0 15 10 * * ?" : 아무 요일, 매월, 매일 10:15:00
     *  "0 15 10 * * ? *" : 모든 연도, 아무 요일, 매월, 매일 10:15
     *  "0 15 10 * * ? : 2005" 2005년 아무 요일이나 매월, 매일 10:15
     *  "0 * 14 * * ?" : 아무 요일, 매월, 매일, 14시 매분 0초
     *  "0 0/5 14 * * ?" : 아무 요일, 매월, 매일, 14시 매 5분마다 0초
     *  "0 0/5 14,18 * * ?" : 아무 요일, 매월, 매일, 14시, 18시 매 5분마다 0초
     *  "0 0-5 14 * * ?" : 아무 요일, 매월, 매일, 14:00 부터 매 14:05까지 매 분 0초
     *  "0 10,44 14 ? 3 WED" : 3월의 매 주 수요일, 아무 날짜나 14:10:00, 14:44:00
     *  "0 15 10 ? * MON-FRI" : 월~금, 매월, 아무 날이나 10:15:00
     *  "0 15 10 15 * ?" : 아무 요일, 매월 15일 10:15:00
     *  "0 15 10 L * ?" : 아무 요일, 매월 마지막 날 10:15:00
     *  "0 15 10 ? * 6L" : 매월 마지막 금요일 아무 날이나 10:15:00
     *  "0 15 10 ? * 6L 2002-2005" : 2002년부터 2005년까지 매월 마지막 금요일 아무 날이나 10:15:00
     *  "0 15 10 ? * 6#3" : 매월 3번째 금요일 아무 날이나 10:15:00
     */
    @Scheduled(cron = "0 0-59 11 * * ?")
    public void fileChangeCron() {
        log.info("fileChangeCron START");
        String newAESIv = aes256Utils.generateRandomStringWithByteLength(16);
        String newAESKey = aes256Utils.generateRandomStringWithByteLength(32);

        String AESIvFileAbsPath = "/Users/ijong-ug/IdeaProjects/network-client/src/main/resources/static/aes256/AESIv";
        String AESKeyFileAbsPath = "/Users/ijong-ug/IdeaProjects/network-client/src/main/resources/static/aes256/AESKey";

        FileUtility.writeFile(AESIvFileAbsPath, "UTF-8", newAESIv);
        FileUtility.writeFile(AESKeyFileAbsPath, "UTF-8", newAESKey);
    }

}

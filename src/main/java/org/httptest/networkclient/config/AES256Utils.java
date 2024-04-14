package org.httptest.networkclient.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
public class AES256Utils {
    public static String algorithms = "AES/CBC/PKCS5Padding";
    // AES/CBC/PKCS5Padding -> AES, CBC operation mode, PKCS5 padding scheme 으로 초기화된 Cipher 객체

    // private final String SYSTEM_HOME_PATH = System.getProperty("focs.home"); // 톰캣의 경우 톰캣의 bin 폴덕 내에 setenv.sh 에 설정하여 할당하였음.
    /**
     * setenv.sh 샘플 내용
     *
     * JAVA_OPTS=$JAVA_OPTS" -Dfile.encoding=UTF-8 -server -Xms1024m -Xmx1024m -XX:NewSize=384m -XX:MaxNewSize=384m -XX:PermSize=128m -XX:MaxPermSize=128m"
     * JAVA_OPTS=$JAVA_OPTS" -XX:-HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$CATALINA_HOME/logs"
     * JAVA_OPTS=$JAVA_OPTS" -XX:ParallelGCThreads=2 -XX:-UseConcMarkSweepGC"
     * JAVA_OPTS=$JAVA_OPTS" -XX:-PrintGC -XX:-PrintGCDetails -XX:-PrintGCTimeStamps -XX:-TraceClassUnloading -XX:-TraceClassLoading"
     *
     * JMX_OPTS=" -Dcom.sun.management.jmxremote \
     *                  -Dcom.sun.management.jmxremote.authenticate=false \
     *                  -Djava.rmi.server.hostname=${HOSTNAME} \
     *                 -Dcom.sun.management.jmxremote.ssl=false "
     * CATALINA_OPTS=" ${JMX_OPTS} ${CATALINA_OPTS}"
     * 출처: https://lucaskim.tistory.com/37 [Lucas Kim:티스토리]
     */
//    private final String SYSTEM_HOME_PATH = "/Users/ijong-ug/IdeaProjects/network-client/src/main/resources/static";
    private final String SYSTEM_HOME_PATH = "/Users/ijong-ug/IdeaProjects/network-client/src/main/resources/static";

    // AES 암호화
    public String encrypt_AES(String password){

        try {
            String result; // 암호화 결과 값을 담을 변수

            // 암호화/복호화 기능이 포함된 객체 생성
            Cipher cipher = Cipher.getInstance(algorithms);

            // 키로 비밀키 생성
            SecretKeySpec keySpec = new SecretKeySpec(getAESKey().getBytes(), "AES");

            // iv 로 spec 생성
            IvParameterSpec ivParamSpec = new IvParameterSpec(getAESIv().getBytes());
            // 매번 다른 IV를 생성하면 같은 평문이라도 다른 암호문을 생성할 수 있다.
            // 또한 IV는 암호를 복호화할 사람에게 미리 제공되어야 하고 키와 달리 공개되어도 상관없다

            // 암호화 적용
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);

            // 암호화 실행
            byte[] encrypted = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8)); // ID 암호화(인코딩 설정)
            result = Base64.getEncoder().encodeToString(encrypted); // 암호화 인코딩 후 저장

            return result;
        } catch (Exception e) {
            log.error("암호화 중 오류 발생하였습니다 : {}", e.getMessage());
            e.printStackTrace();
        }

        return "";
    }

    // AES 복호화
    public String decrypt_AES(String password){
        try {
            // 암호화/복호화 기능이 포함된 객체 생성
            Cipher cipher = Cipher.getInstance(algorithms);

            // 키로 비밀키 생성
            SecretKeySpec keySpec = new SecretKeySpec(getAESKey().getBytes(), "AES");

            // iv 로 spec 생성
            IvParameterSpec ivParamSpec = new IvParameterSpec(getAESIv().getBytes());
            // 매번 다른 IV를 생성하면 같은 평문이라도 다른 암호문을 생성할 수 있다.
            // 또한 IV는 암호를 복호화할 사람에게 미리 제공되어야 하고 키와 달리 공개되어도 상관없다

            // 암호화 적용
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);

            //암호 해석
            byte[] decodedBytes = Base64.getDecoder().decode(password);
            byte[] decrypted = cipher.doFinal(decodedBytes);

            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("복호화 중 오류 발생하였습니다.: {}", e.getMessage());
        }

        return "";
    }

    public String getAESKey() throws Exception {
        return FileUtility.readFile(SYSTEM_HOME_PATH + "/aes256/AESKey", "utf8"); //32byte
    }

    public String getAESIv() throws Exception {
        return FileUtility.readFile(SYSTEM_HOME_PATH + "/aes256/AESIv", "utf8"); //16byte;
    }

    /**
     * 특정 비트 사이즈의 랜덤한 문자열을 생성한다.
     *
     * @param byteLength 생성할 문자열 비트 수
     * @return
     */
    public String generateRandomStringWithByteLength(int byteLength) {
        boolean useLetters = true;
        boolean useNumbers = true;
        return RandomStringUtils.random(byteLength, useLetters, useNumbers);
    }
}

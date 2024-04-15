package org.httptest.networkclient.config.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * 참고 링크 : https://velog.io/@goniieee/%EB%82%B4%EA%B0%80-%EB%A7%8C%EB%93%A0-HTTP-%EC%BF%A0%ED%82%A4-%EB%84%88%EB%A5%BC-%EC%9C%84%ED%95%B4-%EA%B5%AC%EC%9B%A0%EC%A7%80
 *
 * 쿠키는 [이름, 도메인, 경로] 값으로 구분하기 때문에 같은 [이름, 도메인, 경로]를 가진 쿠키가 Set-Cookie로 새로 들어온다면 값은 덮어쓰여집니다.
 * 이를 이용해 서버는 기존의 쿠키를 삭제할 수 있습니다.
 *
 * Expires
 * 쿠키의 만료기한을 설정하는 속성입니다.
 * 만료기한은 날짜와 시간으로 설정할 수 있습니다.
 *
 * Max-Age
 * Expires와 마찬가지로 쿠키의 만료기한을 설정하는 속성인데, Expires와는 다르게 초 단위로 설정하게 됩니다.
 * 만료되게 하고 싶으면 setMaxAge(0)으로 해주세요.
 *
 * Domain
 * 해당 쿠키를 "특정 도메인으로 보내는 요청"에만 포함시키게 하는 속성입니다.
 * 만약 Set-Cookie 헤더에 Domain속성이 없더라도 `응답을 보낸 서버의 도메인을 Domain 값으로 인식`합니다.
 * 또한, 응답을 보낸 서버의 도메인과 Domain의 값이 동일한 쿠키만 저장한다는 특징이 있습니다.
 *
 * Path
 * Domain 과 비슷한 원리로 "특정 경로에만 쿠키를 포함시키고 싶을 경우" 해당 속성을 이용합니다.
 * "Path 정보가 누락된 경우에는 요청 URI의 경로 정보를 기본값으로 설정"합니다.
 * Path 를 "/" 로 설정한 경우 모든 path 에서 출력 가능함 (확인)
 *
 * Secure
 * Secure connection 에서만 쿠키를 포함시키고 싶은 경우 Secure을 설정하게 됩니다.
 * 쿠키는 HTTPS 요청에만 포함되며 HTTP 요청에는 포함되지 않습니다.
 * Secure 설정을 하게 되면 쿠키의 기밀성은 보장되어도, HTTP 요청으로 덮어쓰여질 수 있기 때문에 만능은 아닙니다.
 *
 * HttpOnly
 * 쿠키의 사용 범위를 HTTP 요청에만 국한시키는 설정입니다.
 * HttpOnly 쿠키는 non-HTTP API를 통해 접근이 불가능하게 됩니다.
 * 예를 들어, "자바 스크립트를 통해 쿠키의 값을 노출시키는 행위가 불가능"해집니다.
 * Secure과 HttpOnly는 독립된 속성이며, 하나의 쿠키가 두 속성 모두 가질 수 있다는 점을 유의해야 합니다.
 *
 * SameSite
 * cross-site 문맥에서 CSRF 공격을 막기 위해 사용됩니다.
 * enforcement의 기본값은 Default이며, None, Lax, Strict의 값을 가질 수 있습니다.
 * 흔히 SameSite의 기본값을 Lax로 알고 있는데, 브라우저마다 달라서 주의가 필요한 부분입니다. (RFC 6265bis에는 Lax라고 하지만 표준이 아니라서 생기는 문제인 것 같습니다.)
 *
 * Strict: "same-site" 요청에만 쿠키를 포함시키게 됩니다.
 * Lax: "same-site" 요청과 safe HTTP method를 사용하는 "cross-site" top-level navigation에서만 쿠키를 포함시키게 됩니다.
 * None: "same-site" 요청과 "cross-site" 요청 모두에 쿠키를 포함시킬 수 있습니다. 하지만, HTTPS에서만 가능한 속성으로 Secure 속성이 있어야 합니다.
 *
 * 아래 domain 이라고 하는 옵션이 있는데 해당 domain 옵션이 존재하면 해당 도메인에 쿠키를 굽겠다는 것을 의미한다.
 * 따라서 해당 도메인에서만 쿠키가 존재하게 됨.
 * response.addHeader("Set-Cookie", USER_URL + "=" + (JW_URL) + "; HTTPOnly;" + " Secure; SameSite=None;" + " domain=" + DOMAIN + ";" + "path=" + "/");
 * response.addHeader("Set-Cookie", R_JWM + "=" + (JW_A) + "; HTTPOnly;" + " Secure; SameSite=None;" + " domain=" + DOMAIN + ";" + "path=" + "/");
 */
@Slf4j
public class CookieManager {
    private static boolean isCookieSameSite = false;

    public static void setIsCookieSameSite(boolean isCookieSameSite) {
        CookieManager.isCookieSameSite = isCookieSameSite;
    }

    public static String getCookieValue(String cookieName, HttpServletRequest request) {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                String cookieValue = null;

                for (int i = 0; i < cookies.length; ++i) {
                    Cookie cookieInfo = cookies[i];
                    if (cookieInfo.getName().equals(cookieName)) {
                        cookieValue = cookieInfo.getValue();
                        if (cookieValue != null) {
                            cookieValue = URLDecoder.decode(cookieValue);
                        }
                        return cookieValue;
                    }
                }

            }

            return null;
        } catch (Exception e) {
            log.error("[ERROR] CookieManager getCookieValue message: {}", e.getMessage());
            return "";
        }
    }

    public static void addCookie(String name, String value, String domain, String path, HttpServletRequest request, HttpServletResponse response) {
        // response.setHeader("P3P", "CP=\"ALL CURa ADMa DEVa TAIa OUR BUS IND PHY ONL UNI PUR FIN COM NAV INT DEM CNT STA POL HEA PRE LOC OTC\"");
        String strSameSiteOption = "";
        if (isCookieSameSite) {
            strSameSiteOption = " Secure; SameSite=None;";
        }

        response.addHeader("Set-Cookie", name + "=" + URLEncoder.encode(value) + "; HTTPOnly;" + strSameSiteOption + " domain=" + domain + ";" + "path=" + path);
    }

    /**
     * 쿠키는 [이름, 도메인, 경로] 값으로 구분하기 때문에 같은 [이름, 도메인, 경로]를 가진 쿠키가 Set-Cookie로 새로 들어온다면 값은 덮어쓰여집니다.
     * 이를 이용해 서버는 기존의 쿠키를 삭제할 수 있습니다.
     *
     * @param strCookieName 쿠키 이름
     * @param strCookieValue 쿠키 값
     * @param strDomain 도메인
     * @param strPath URI 경로
     * @param req HttpServletRequest
     * @param res HttpServletResponse
     * @return
     */
    public static boolean removeCookie(String strCookieName, String strCookieValue, String strDomain, String strPath, HttpServletRequest req, HttpServletResponse res) {
        if (strCookieName != null) {
            Cookie cookieInfo = new Cookie(strCookieName, URLEncoder.encode(strCookieValue));
            if (strDomain != null && strDomain.length() != 0) {
                cookieInfo.setDomain(strDomain);
            }

            cookieInfo.setPath(strPath);
            cookieInfo.setMaxAge(0);
            res.addCookie(cookieInfo);
            return true;
        } else {
            return false;
        }
    }
}

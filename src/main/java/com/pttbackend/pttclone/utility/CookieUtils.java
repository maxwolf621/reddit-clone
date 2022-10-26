package com.pttbackend.pttclone.utility;

import org.springframework.util.SerializationUtils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Optional;

/**
 * <p> Cookie Utils to handle the cookies in 
 *    {@code HttpServletRequest} 
 *    and {@code HttpServerResponse} </p>
 */
@Slf4j
@UtilityClass
public class CookieUtils {

    /** 
     * <p> Get the cookie from {@code HttpServletRequest}  
     *     sent by user agent 
     *     (e.g Application, Client or Browser ...) </p>
     * @param request {@link HttpServletRequest}
     * @param cookieName {@link Cookie}'s name
     * @return {@code Optional<Cookie>} or {@code Optional.empty()}
     */
    public static Optional<Cookie> getCookie(HttpServletRequest request, String cookieName) {
        
        log.info("\n*-----CookieUtils getCookie from Request : " + request.getRequestURI());
        
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    log.info("  '___Get Cookie Name : " + cookieName);
                    
                    log.info("      '___Cookie's value :" + cookie.getValue());
                    return Optional.of(cookie);
                }
            }
        }
        /**
         * return {@code null} if there is no cookies in {@code HttpServletRequest}
         */
        log.info("__There are no cookies in this HttpServletRequest");
        return Optional.empty();
    }


    /**
     *  Set up a cookie and add it in the response payload via
     * @param response {@link HttpServletResponse}
     * @param cookieName {@link Cookie}'s name
     * @param cookieValue {@link Cookie}'s value
     * @param maxAge the cookie's life cycle
     * @see HttpServletResponse#addCookie(Cookie)
     * @see javax.servlet.http.Cookie
     */
    public static void addCookie(HttpServletResponse response, String cookieName, String cookieValue, int maxAge) {
        log.info("\n*-----CookieUtils addCookie : " +
                "\nName: " + cookieName + 
                "\nCookie Value: " + cookieValue);

        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }


    
    /**
     * Delete Cookie via
     * @param request {@link HttpServletResponse}
     * @param response {@link HttpServletRequest}
     * @param cookieName Cookie's name
     * @see javax.servlet.http.Cookie
     * @see HttpServletResponse#addCookie(Cookie)   
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        log.info("  *----CookieUtils deleteCookie");
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie: cookies) {
                /**
                 *  response Payload will send back to client 
                 *  {@code cookie.setMaxAge(0)}
                 *      Once client receives the response. 
                 *      It deletes the cookie stored in client.
                 */
                if (cookie.getName().equals(cookieName)) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

    /**
     * Serialize the data 
     * @param object the object to be serialized
     * @return String of Base64
     * @see SerializationUtils#serialize(Object)
     * @see Base64.Encoder#encodeToString(byte[])
     * @see Base64#getEncoder() 
     *
     */
     public static String serialize(Object object) {
        log.info("*----serialize data : " + object.toString());
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(object));
    }

    /**
     * <p> Deserialize the cookies via </p>
     * @param <T> Type that {@code cls} will be casted
     * @param cookie {@link Cookie}
     * @param cls object that will be casted 
     * @return {@code <T>} 
     * @see Cookie#getValue()
     * @see Base64.Decoder#decode(String)
     * @see Base64#getUrlDecoder()
     * @see SerializationUtils#deserialize(byte[])
     * @see java.lang.Class#cast(Object)
     */
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        log.info("  *----deserialize data");
        return cls.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())));
    }
}
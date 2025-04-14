package com.ahicode.TextMe.service.impl;

import com.ahicode.TextMe.service.CookieService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

@Service
public class CookieServiceImpl implements CookieService {

    @Override
    public void updateCookie(HttpServletResponse response, String cookieValue, String cookieName, int cookieAge) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(cookieAge);
        response.addCookie(cookie);
    }

    @Override
    public String extractCookieValueFromCookieByName(HttpServletRequest request, String cookieName) {
        Cookie cookie = WebUtils.getCookie(request, cookieName);

        return (cookie != null) ? cookie.getValue() : null;
    }
}

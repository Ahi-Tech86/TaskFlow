package com.ahicode.TextMe.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface CookieService {
    String extractCookieValueFromCookieByName(HttpServletRequest request, String cookieName);
    void updateCookie(HttpServletResponse response, String cookieValue, String cookieName, int cookieAge);
}

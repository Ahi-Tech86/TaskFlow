package com.ahicode.TextMe.service;

import com.ahicode.TextMe.service.impl.CookieServiceImpl;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CookieServiceTest {

    private final CookieServiceImpl service = new CookieServiceImpl();

    @Test
    void testUpdateCookie() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        String cookieName = "cookie";
        String cookieValue = "value";
        int cookieAge = 360;

        service.updateCookie(response, cookieValue, cookieName, cookieAge);

        Cookie cookie = response.getCookie(cookieName);
        assertThat(cookie).isNotNull();
        assertThat(cookie.getName()).isEqualTo(cookieName);
        assertThat(cookie.getValue()).isEqualTo(cookieValue);
        assertThat(cookie.getMaxAge()).isEqualTo(cookieAge);
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.isHttpOnly()).isTrue();
    }

    @Test
    void testExtractCookieValueFromCookieByName_existingCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String cookieName = "cookie";
        String cookieValue = "value";

        request.setCookies(new Cookie(cookieName, cookieValue));

        String extractedValue = service.extractCookieValueFromCookieByName(request, cookieName);

        assertThat(extractedValue).isNotNull();
        assertThat(extractedValue).isEqualTo(cookieValue);
    }

    @Test
    void testExtractCookieValueFromCookieByName_nonExistingCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String cookieName = "nonExistingCookie";

        String extractedValue = service.extractCookieValueFromCookieByName(request, cookieName);

        assertThat(extractedValue).isNull();
    }
}

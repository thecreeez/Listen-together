package com.tcz.listentogether;

import javax.servlet.http.Cookie;

public class TokenCookie {
    private Cookie cookie;

    public TokenCookie(String token) {
        cookie = new Cookie("token", token);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
    }

    public Cookie getCookie() {
        return cookie;
    }

    public void setCookie(Cookie cookie) {
        this.cookie = cookie;
    }
}

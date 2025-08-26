package com.wakeb.yusradmin.services;


public class AuthSession {
    private static String jwt;

    public static void setJwt(String token) { jwt = token; }
    public static String getJwt() { return jwt; }
    public static void clear() { jwt = null; }
}

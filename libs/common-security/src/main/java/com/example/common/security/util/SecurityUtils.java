package com.example.common.security.util;

import com.example.common.security.CurrentUser;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static CurrentUser getCurrentUser() {
        return (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    public static Long getUserId() {
        return getCurrentUser().userId();
    }

    public static String getUsername() {
        return getCurrentUser().username();
    }
}
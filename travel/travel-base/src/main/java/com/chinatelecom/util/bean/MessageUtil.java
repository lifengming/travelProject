package com.chinatelecom.util.bean;

import org.springframework.context.MessageSource;

import java.util.Locale;

public class MessageUtil {
    private MessageUtil() {}
    public static String getMessage(String key, MessageSource messageSource) {
        try {
            return messageSource.getMessage(key,null, Locale.getDefault()) ;
        } catch (Exception e) {
            return null ;
        }
    }
}

package com.chinatelecom.exception;

/**
 * Description: travelProject
 * Created by leizhaoyuan on 20/2/29 下午8:20
 */
public class LevelNotEnoughException extends RuntimeException {
    public LevelNotEnoughException(String message) {
        super(message);
    }
}

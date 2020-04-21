package com.chinatelecom.exception;

/**
 * Description: travelProject
 * Created by leizhaoyuan on 20/2/28 下午11:36
 */
public class DeptManagerExistException extends RuntimeException {
    public DeptManagerExistException(String message) {
        super(message);
    }
}

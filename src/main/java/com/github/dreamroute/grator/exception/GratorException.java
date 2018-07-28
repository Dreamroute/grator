package com.github.dreamroute.grator.exception;

/**
 * 
 * @author 342252328@qq.com
 * @version 1.0
 * @date 2018-05-01
 *
 */
public class GratorException extends RuntimeException {

    private static final long serialVersionUID = 6782791065610438263L;

    public GratorException() {}

    public GratorException(String message) {
        super(message);
    }

    public GratorException(Throwable cause) {
        super(cause);
    }

    public GratorException(String message, Throwable cause) {
        super(message, cause);
    }

}

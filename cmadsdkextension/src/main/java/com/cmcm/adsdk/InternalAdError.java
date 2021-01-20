package com.cmcm.adsdk;


/**
 * Created by chenhao on 16/5/11.
 */
public enum InternalAdError {

    NETWORK_OTHER_ERROR(200, "network other error"),
    NETWORK_ENCODING_ERROR(201, "network encoding error"),
    NETWORK_PROTOCOL_ERROR(202, "network protocol error"),
    NETWORK_REDIRECT_ERROR(203, "network redirect error"),
    NETWORK_RESPONSE_ERROR(204, "network response error"),
    NETWORK_TIMEOUT_ERROR(205, "network timeout error"),
    NETWORK_MAX_SIZE_ERROR(206, "network max size error"),
    NETWORK_DISK_SPACE_ERROR(207, "network_disk_space_error"),
    NETWORK_URL_ERROR(208, "network_url_error");
    private int errorCode;
    private String errorMessage;
    private String exceptionName;

    InternalAdError(int code, String message) {
        this.errorCode = code;
        this.errorMessage = message;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public InternalAdError withMessage(String message) {
        errorMessage = message;
        return this;
    }

    public InternalAdError withExceptionName(Throwable ex){
        if(null != ex){
            exceptionName = ex.getClass().getName();
        }
        return this;
    }

    public String getExceptionName(){
        return exceptionName;
    }

    public InternalAdError withException(Throwable e) {
        errorMessage = e.getMessage();
        return this;
    }
}

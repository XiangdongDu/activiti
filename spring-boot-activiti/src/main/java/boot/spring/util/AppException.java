package boot.spring.util;

/**
 * @Author duxiangdong
 * @Date 2022/1/25 10:42
 * @Version 1.0
 */
public class AppException extends Exception {

    private static final long serialVersionUID = 1L;
    private String errorCode;
    private String errorMessage;
    private String errorDetailMessage;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorDetailMessage() {
        return errorDetailMessage;
    }

    public void setErrorDetailMessage(String errorDetailMessage) {
        this.errorDetailMessage = errorDetailMessage;
    }

    public AppException(String errorCode, Throwable throwable) {
        super(throwable);
        if (throwable instanceof AppException) {
            this.errorCode = ((AppException) throwable).getErrorCode();
            this.errorMessage = ((AppException) throwable).getErrorMessage();
            this.errorDetailMessage = ((AppException) throwable).getErrorDetailMessage();
        } else {
            this.errorCode = errorCode;
            this.errorMessage = throwable.getMessage();
            this.errorDetailMessage = throwable.getMessage();
        }
    }

    public AppException(String errorCode, String errorDetail) {
        this.errorCode = errorCode;
        this.errorMessage = errorDetail;
        this.errorDetailMessage = errorDetail;
    }

}

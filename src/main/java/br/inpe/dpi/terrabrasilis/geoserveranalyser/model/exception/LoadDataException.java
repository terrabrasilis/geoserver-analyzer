package br.inpe.dpi.terrabrasilis.geoserveranalyser.model.exception;

import java.io.IOException;

public class LoadDataException extends IOException {
    

    private int httpCode;
    private String errorResponseContent;

    public LoadDataException(String message, int httpCode, String errorResponseContent)
    {
        super(message);
        this.httpCode = httpCode;
        this.errorResponseContent = errorResponseContent;
    }



    public int getHttpCode() {
        return this.httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public String getErrorResponseContent() {
        return this.errorResponseContent;
    }

    public void setErrorResponseContent(String errorResponseContent) {
        this.errorResponseContent = errorResponseContent;
    }
}

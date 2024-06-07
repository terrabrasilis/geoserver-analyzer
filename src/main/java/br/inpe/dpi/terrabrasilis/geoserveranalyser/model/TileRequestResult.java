package br.inpe.dpi.terrabrasilis.geoserveranalyser.model;

public class TileRequestResult
{
    private boolean sucess;
    private double size;
    private double duration;
    private String GWCResult; 
    private int httpCode; 
    private String url;
    

    public TileRequestResult() 
    {
        this.sucess = false;
    }
    
    public double getSize() {
        return this.size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public double getDuration() {
        return this.duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getGWCResult() {
        return this.GWCResult;
    }

    public void setGWCResult(String GWCResult) {
        this.GWCResult = GWCResult;
    }


    public int getHttpCode() {
        return this.httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }


    public boolean isSucess() {
        return this.sucess;
    }

    public boolean getSucess() {
        return this.sucess;
    }

    public void setSucess(boolean sucess) {
        this.sucess = sucess;
    }

    public String getURL() {
        return this.url;
    }

    public void setURL(String url) {
        this.url = url;
    }


}
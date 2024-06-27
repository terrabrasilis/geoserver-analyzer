package br.inpe.dpi.terrabrasilis.geoserveranalyser.model;

public class TileRequestResult
{
    private boolean sucess;
    private long size;
    private long duration;
    private String GWCResult=""; 
    private String GWCResultMissReason="";         
    private int httpCode; 
    private String url;
    private String contentType="";
    private String xmlContent="";
    

    public TileRequestResult() 
    {
        this.sucess = false;
    }
    
    public long getSize() {
        return this.size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long duration) {
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

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }


    public String getXmlContent() {
        return this.xmlContent;
    }

    public void setXmlContent(String xmlContent) {
        this.xmlContent = xmlContent;
    }

    public String getGWCResultMissReason() {
        return this.GWCResultMissReason;
    }

    public void setGWCResultMissReason(String GWCResultMissReason) {
        this.GWCResultMissReason = GWCResultMissReason;
    }

}
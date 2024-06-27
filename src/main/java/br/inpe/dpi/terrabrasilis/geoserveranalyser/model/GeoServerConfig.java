package br.inpe.dpi.terrabrasilis.geoserveranalyser.model;

import java.util.ArrayList;

public class GeoServerConfig 
{
    private String url;
    private String username;
    private String password;
    private ArrayList<String> businessAPIURLs;


    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean checkValid() throws Exception
    {
        if(this.url==null || this.url.isEmpty())
        {
            throw new Exception("Wrong --geoserver-url parameter");
        }
        if(this.username==null || this.username.isEmpty())
        {
            throw new Exception("Wrong --geoserver-username parameter");
        }
        if(this.password==null || this.password.isEmpty())
        {
            throw new Exception("Wrong --geoserver-password parameter");
        }
        return true;
    }


    public ArrayList<String> getBusinessAPIURLs() {
        return this.businessAPIURLs;
    }

    public void addBusinessAPIURL(String businessAPIURL) 
    {
        if(this.businessAPIURLs==null)
        {
            this.businessAPIURLs = new ArrayList<String>();
        }
        this.businessAPIURLs.add(businessAPIURL);
        
    }


}
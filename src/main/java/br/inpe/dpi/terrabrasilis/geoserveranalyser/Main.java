package br.inpe.dpi.terrabrasilis.geoserveranalyser;

import br.inpe.dpi.terrabrasilis.geoserveranalyser.model.GeoServerConfig;
import br.inpe.dpi.terrabrasilis.geoserveranalyser.service.LoadData;

public class Main 
{
    public static void main(String args[]) throws Exception
    {
        
        GeoServerConfig geoserverConfig = getGeoserverConfig(args);
        System.out.println("GeoServer URL:" + geoserverConfig.getUrl());
        //GeoService.teste(geoserverConfig);
        LoadData.loadData(geoserverConfig);
    }
    private static GeoServerConfig getGeoserverConfig(String args[]) throws Exception
    {
        if(args.length == 6)
        {
            String errorMsg = "Missing arguments. Minimal example: \n --geoserver-url=http://localhost:8080/geoserver/, \n --geoserver-username=admin and \n --geoserver-password=geoserver ";
            
            throw new Exception(errorMsg);
        }
        GeoServerConfig geoserverConfig = new GeoServerConfig();
        for (String arg : args)
        {
            if(arg.contains("=")==false)
            {
                throw new Exception("Invalid argument: " + arg);
            }
            String argValue = null;         
            if(arg.contains("--geoserver-url="))
            {
               argValue = arg.split("=")[1];
               geoserverConfig.setUrl(argValue);
               continue;
            }
            if(arg.contains("--geoserver-username="))
            {
                argValue = arg.split("=")[1];
                geoserverConfig.setUsername(argValue);
                continue;
            }
            if(arg.contains("--geoserver-password="))
            {
                argValue = arg.split("=")[1];
                geoserverConfig.setPassword(argValue);
                continue;
            }
            System.err.println("Argument ignored: " + arg);
        }
        
        geoserverConfig.checkValid();

        return geoserverConfig;    
    }
}

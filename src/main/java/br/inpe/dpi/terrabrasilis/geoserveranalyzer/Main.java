package br.inpe.dpi.terrabrasilis.geoserveranalyzer;

import java.util.ArrayList;

import br.inpe.dpi.terrabrasilis.geoserveranalyzer.model.BusinessAPILayer;
import br.inpe.dpi.terrabrasilis.geoserveranalyzer.model.GeoServerConfig;
import br.inpe.dpi.terrabrasilis.geoserveranalyzer.model.restfeaturetype.RestFeatureTypeLayerRoot;
import br.inpe.dpi.terrabrasilis.geoserveranalyzer.service.BusinessAPIService;
import br.inpe.dpi.terrabrasilis.geoserveranalyzer.service.CSVService;
import br.inpe.dpi.terrabrasilis.geoserveranalyzer.service.LoadData;

public class Main 
{
    public static void main(String args[]) throws Exception
    {
        
        GeoServerConfig geoserverConfig = getGeoserverConfig(args);
        System.out.println("GeoServer URL:" + geoserverConfig.getUrl());
        //GeoService.teste(geoserverConfig);
        ArrayList<RestFeatureTypeLayerRoot> singleRestLayerList = LoadData.loadData(geoserverConfig);

        if(geoserverConfig.getBusinessAPIURLs().isEmpty()==false)
        {
            ArrayList<BusinessAPILayer> baLayersList = BusinessAPIService.getLayers(geoserverConfig);

            for (BusinessAPILayer baLayer : baLayersList) 
            {
                for (RestFeatureTypeLayerRoot singleRestLayer : singleRestLayerList) 
                {
                    if(singleRestLayer.coverage==null)
                    {     
                        if(baLayer.getName().equalsIgnoreCase(singleRestLayer.featureType.name)
                        && baLayer.getWorkspace().equalsIgnoreCase(singleRestLayer.featureType.namespace.name))
                        {
                            singleRestLayer.isBALayer = true;
                        }
                    }
                    else
                    {
                        if(baLayer.getName().equalsIgnoreCase(singleRestLayer.coverage.name)
                        && baLayer.getWorkspace().equalsIgnoreCase(singleRestLayer.coverage.namespace.name))
                        {
                            singleRestLayer.isBALayer = true;
                        }
                    }
                }
                
            }

        }

        CSVService.writeResultToCSV(singleRestLayerList, geoserverConfig);
    }
    private static GeoServerConfig getGeoserverConfig(String args[]) throws Exception
    {
        if(args.length < 4)
        {
            String errorMsg = "Missing arguments. Minimal example: \n --geoserver-url=http://localhost:8080/geoserver/, \n --geoserver-username=admin and \n --geoserver-password=geoserver \n --output-file=/tmp/geoserver-layers.csv \n";
            errorMsg+="Optional argument: --businessapi-url=https://terrabrasilis.dpi.inpe.br/business/ (cross with business api layers)";
            
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
            if(arg.contains("--businessapi-url="))
            {
                argValue = arg.split("=")[1];
                geoserverConfig.addBusinessAPIURL(argValue);
                continue;
            }
            if(arg.contains("--output-file="))
            {
                argValue = arg.split("=")[1];
                geoserverConfig.setOutputFile(argValue);
                continue;
            }
            System.err.println("Argument ignored: " + arg);
        }
        
        geoserverConfig.checkValid();

        return geoserverConfig;    
    }
}

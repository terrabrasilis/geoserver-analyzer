package br.inpe.dpi.terrabrasilis.geoserveranalyser.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;

import br.inpe.dpi.terrabrasilis.geoserveranalyser.model.GeoServerConfig;
import br.inpe.dpi.terrabrasilis.geoserveranalyser.model.exception.LoadDataException;
import br.inpe.dpi.terrabrasilis.geoserveranalyser.model.restfeaturetype.RestFeatureTypeLayerRoot;
import br.inpe.dpi.terrabrasilis.geoserveranalyser.model.restlayer.RestLayerRoot;
import br.inpe.dpi.terrabrasilis.geoserveranalyser.model.restlayers.RestLayer;
import br.inpe.dpi.terrabrasilis.geoserveranalyser.model.restlayers.RestLayersRoot;

public class LoadData 
{

    private static int restLayersCount = 0;
    private static int restLayerFTCount = 0;

    public static ArrayList<RestFeatureTypeLayerRoot> loadData(GeoServerConfig config) throws Exception
    {

        //Loads list of layers
        RestLayersRoot restLayers = LoadData.loadRestLayers(config);

        restLayersCount = restLayers.layers.getLayers().size();        

        ArrayList<RestFeatureTypeLayerRoot> singleRestLayerList = new ArrayList<RestFeatureTypeLayerRoot>();

        for (RestLayer restLayer : restLayers.layers.getLayers()) 
        {

            // if("prodes-pantanal-nb:accumulated_deforestation_2000".equalsIgnoreCase(restLayer.name)==false)
            // {
            //      continue;
            // }            

            //Load single layer info
            RestFeatureTypeLayerRoot restLayerFeatureType = loadRestLayer(config, restLayer);

            if(restLayerFeatureType.restSingleLayer==null)
            {
                restLayerFeatureType = loadRestLayer(config, restLayer);
            }

            if(restLayerFeatureType.requestStatus == true && restLayerFeatureType.restSingleLayer!=null)
            {
                //Load layer feature type
                restLayerFeatureType = loadRestLayerFeatureType(config, restLayerFeatureType.restSingleLayer, restLayer);
            }           

            singleRestLayerList.add(restLayerFeatureType);
        }
        restLayerFTCount = singleRestLayerList.size();   
        
        for (RestFeatureTypeLayerRoot restFeatureType : singleRestLayerList) 
        {
            GeoService.getMap(config, restFeatureType);    
        }        
        System.out.println("Rest Layers Count: " + restLayersCount);
        System.out.println("Rest Layers FT Count: " + restLayerFTCount);

        return singleRestLayerList;
    }

    /**
     * Getting layer list from rest api
     * @param config
     * @return
     * @throws Exception
     */
    private static RestLayersRoot loadRestLayers(GeoServerConfig config) throws Exception    
    {
        String geoserverRestURL = config.getUrl() + "/rest/layers.json";
        
        String restLayerJSON = LoadData.fetchData(geoserverRestURL, config.getUsername(), config.getPassword());
        
        RestLayersRoot restLayers = (RestLayersRoot) MappingService.mapObjectFromJSON(restLayerJSON, RestLayersRoot.class);

        return restLayers;
    }

    /**
     * Getting single layer configuration from rest api
     * @param config
     * @return
     * @throws Exception
     */
    private static RestFeatureTypeLayerRoot loadRestLayer(GeoServerConfig config, RestLayer restLayer) throws Exception    
    {
        String geoserverLayerRestURL = restLayer.href;
        
        String restLayerJSON = "";

        RestLayerRoot restSingleLayer = new RestLayerRoot();

        RestFeatureTypeLayerRoot restLayerFeatureType = new RestFeatureTypeLayerRoot();

        try {

            restLayerJSON = LoadData.fetchData(geoserverLayerRestURL, config.getUsername(), config.getPassword());

            restSingleLayer = (RestLayerRoot) MappingService.mapObjectFromJSON(restLayerJSON, RestLayerRoot.class);

            restLayerFeatureType.requestStatus = true;

            System.out.println("Sucess fetching layer data: " + geoserverLayerRestURL);

        } catch(IOException e)
        {
            System.out.println("Failed to fetch layer data: " + geoserverLayerRestURL);

            //handle by writing an informative layer informing the error
            restLayerFeatureType = handleFailedSingleLayerRequest(restLayer, e, geoserverLayerRestURL);

            
        }       

        restLayerFeatureType.restSingleLayer = restSingleLayer;

        return restLayerFeatureType;
    }

    /**
     * Getting layer featuretype configuration from rest api
     * @param config
     * @return
     * @throws Exception
     */

    private static RestFeatureTypeLayerRoot loadRestLayerFeatureType(GeoServerConfig config, RestLayerRoot restLayerRoot, RestLayer restLayer) throws Exception    
    {
       
        String restLayerJSON = "";

        RestFeatureTypeLayerRoot restSingleLayer;

        String geoserverLayerRestURL="";

        try {            

            geoserverLayerRestURL = MappingService.getFeatureTypeURL(restLayerRoot);

            restLayerJSON = LoadData.fetchData(geoserverLayerRestURL, config.getUsername(), config.getPassword());

            restSingleLayer = (RestFeatureTypeLayerRoot) MappingService.mapObjectFromJSON(restLayerJSON, RestFeatureTypeLayerRoot.class);

            restSingleLayer.requestStatus = true;

            restSingleLayer.restSingleLayer = restLayerRoot;


            System.out.println("Sucess fetching layer featuretype data: " + geoserverLayerRestURL);

        } catch(Exception e)
        {
            System.out.println("Failed to fetch layer featuretype data: " + restLayerRoot.layer.name);
            //handle by writing an informative layer informing the error
            restSingleLayer = handleFailedLayerFeatureTypeRequest(restLayer, e, geoserverLayerRestURL);
        }       

        return restSingleLayer;
    }

    private static RestFeatureTypeLayerRoot handleFailedSingleLayerRequest(RestLayer restSingleLayer, Exception e, String requestURL)
    {   
        
        String namespace = "";
        String layerName = "";

        if(restSingleLayer.name.contains(":"))
        {
            namespace = restSingleLayer.name.split(":")[0];
            layerName = restSingleLayer.name.split(":")[1];
        }
        else
        {
            layerName = restSingleLayer.name;
        }

        RestFeatureTypeLayerRoot restFeatureTypeLayer = new RestFeatureTypeLayerRoot();
        restFeatureTypeLayer.requestURL = requestURL;
        restFeatureTypeLayer.featureType.namespace.name = namespace;
        restFeatureTypeLayer.featureType.name = layerName;
        restFeatureTypeLayer.requestStatus = false;
        
        if(e instanceof LoadDataException)
        {
            restFeatureTypeLayer.errorResponseContent = ((LoadDataException)e).getErrorResponseContent();
            restFeatureTypeLayer.httpCode = ((LoadDataException)e).getHttpCode();
        }       

        restFeatureTypeLayer.errorMessage = e.getMessage();

        return restFeatureTypeLayer;
    }

    private static RestFeatureTypeLayerRoot handleFailedLayerFeatureTypeRequest(RestLayer restLayer, Exception e, String requestURL)
    {   
        String namespace = "";
        String layerName = "";

        if(restLayer.name.contains(":"))
        {
            namespace = restLayer.name.split(":")[0];
            layerName = restLayer.name.split(":")[1];
        }
        else
        {
            layerName = restLayer.name;
        }

        RestFeatureTypeLayerRoot restFeatureTypeLayer = new RestFeatureTypeLayerRoot();
        restFeatureTypeLayer.requestURL = requestURL;
        restFeatureTypeLayer.featureType.namespace.name = namespace;
        restFeatureTypeLayer.featureType.name = layerName;
        restFeatureTypeLayer.requestStatus = false;

        if(e instanceof LoadDataException)
        {
            restFeatureTypeLayer.errorResponseContent = ((LoadDataException)e).getErrorResponseContent();
            restFeatureTypeLayer.httpCode = ((LoadDataException)e).getHttpCode();
        }     

        restFeatureTypeLayer.errorMessage = e.getMessage();

        return restFeatureTypeLayer;
    }

    private static String fetchData(String urlStr, String username, String password) throws IOException
    {       
        HttpURLConnection uc =null;
        try
        {
            URL url = new URL(urlStr);
            uc = (HttpURLConnection) url.openConnection();
            String userpass = username + ":" + password;
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
            uc.setRequestProperty ("Authorization", basicAuth);
            InputStream in = uc.getInputStream();                 
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder json = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                json.append((char) c);
            }

            return json.toString();    

        } catch(IOException e)
        {
            if(uc != null)
            {                    
                InputStreamReader isr = new InputStreamReader(uc.getErrorStream());
                BufferedReader reader = new BufferedReader(isr);
                StringBuilder errorMessage = new StringBuilder();
                int c;
                while ((c = reader.read()) != -1) {
                    errorMessage.append((char) c);
                }               
                throw new LoadDataException("Failed while loading data. ", uc.getResponseCode(), errorMessage.toString());
            }
            throw new IOException(e);
        }
                   
    }
}

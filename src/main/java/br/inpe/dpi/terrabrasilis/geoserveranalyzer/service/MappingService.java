package br.inpe.dpi.terrabrasilis.geoserveranalyzer.service;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.inpe.dpi.terrabrasilis.geoserveranalyzer.model.restlayer.RestLayerRoot;

public class MappingService {
    
    public static <T> Object mapObjectFromJSON(String json, Class<T> classReference) throws Exception
    {
        json = replaceInvalidFieldName(json);
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);        
        try 
        {
            return om.readValue(json, classReference);
        } catch (JsonProcessingException e) 
        {
            throw new Exception("Failed parsing JSON to Object", e);
        }        
    }

    private static String replaceInvalidFieldName(String json)
    {
        HashMap<String, String> invalidFieldNames = new HashMap<String, String>();
        
        invalidFieldNames.put("@key","key");
        invalidFieldNames.put("@class","class_name");

        for (Map.Entry<String, String> entry : invalidFieldNames.entrySet()) 
        {
            json=json.replaceAll(entry.getKey(), entry.getValue());
        }
        return json;
    }

    public static String getFeatureTypeURL(RestLayerRoot restLayerRoot) throws Exception
    {
        if("featureType".equalsIgnoreCase(restLayerRoot.layer.resource.class_name) || 
        "coverage".equalsIgnoreCase(restLayerRoot.layer.resource.class_name) || 
        "wmsLayer".equalsIgnoreCase(restLayerRoot.layer.resource.class_name))
        {
            return restLayerRoot.layer.resource.href;
        }
        
        throw new Exception("Failed to get featureType URL.");
    }

   
            
    
    
}

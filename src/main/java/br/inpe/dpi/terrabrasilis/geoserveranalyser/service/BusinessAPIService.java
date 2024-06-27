package br.inpe.dpi.terrabrasilis.geoserveranalyser.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.inpe.dpi.terrabrasilis.geoserveranalyser.model.BusinessAPILayer;
import br.inpe.dpi.terrabrasilis.geoserveranalyser.model.GeoServerConfig;

public class BusinessAPIService 
{

    public static ArrayList<BusinessAPILayer> getLayers(GeoServerConfig geoserverConfig) throws JsonParseException, JsonMappingException, IOException
    {
        ArrayList<BusinessAPILayer> layersList = new ArrayList<BusinessAPILayer>();

        if(geoserverConfig.getBusinessAPIURLs().isEmpty()==false)
        {
            for (String bussinessAPIURL : geoserverConfig.getBusinessAPIURLs()) 
            {
                String json = fetchData(bussinessAPIURL);

                List<Map<String, Object>> result = new ObjectMapper().readValue(json, new TypeReference<List<Map<String, Object>>>(){});
    
                if(result.size()>0)
                {
                    ArrayList<Map<String, Object>> visions = (ArrayList<Map<String, Object>>)result.get(0).get("visions");
    
                    for (Map<String, Object> vision : visions) 
                    {
                        ArrayList<Map<String, Object>> layers = (ArrayList<Map<String, Object>>) vision.get("layers");
    
                        for (Map<String, Object> layer : layers) 
                        {
                            String name = (String) layer.get("name");
                            String workspace = (String) layer.get("workspace");
                            String nameAuthenticated = (String) layer.get("nameAuthenticated");
    
                            BusinessAPILayer l = new BusinessAPILayer();
                            l.setName(name);
                            l.setWorkspace(workspace);
    
                            layersList.add(l);
    
                            if(nameAuthenticated!=null && nameAuthenticated.isEmpty()==false)
                            {
                                BusinessAPILayer la = new BusinessAPILayer();
                                la.setName(name);
                                la.setWorkspace(workspace);
                                
                                layersList.add(la);
                            }
                        }
                    }
                }
                
            }


            

        }
        return layersList;   
    }

    private static String fetchData(String urlStr) throws IOException
    {       
        HttpURLConnection uc =null;

        URL url = new URL(urlStr);
        uc = (HttpURLConnection) url.openConnection();
        InputStream in = uc.getInputStream();                 
        InputStreamReader isr = new InputStreamReader(in);
        BufferedReader reader = new BufferedReader(isr);
        StringBuilder json = new StringBuilder();
        int c;
        while ((c = reader.read()) != -1) {
            json.append((char) c);
        }

        return json.toString();    
    }
}

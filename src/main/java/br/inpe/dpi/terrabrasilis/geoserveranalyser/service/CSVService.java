package br.inpe.dpi.terrabrasilis.geoserveranalyser.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import br.inpe.dpi.terrabrasilis.geoserveranalyser.model.GeoServerConfig;
import br.inpe.dpi.terrabrasilis.geoserveranalyser.model.restfeaturetype.RestFeatureTypeLayerRoot;

public class CSVService 
{
    public static void writeResultToCSV(ArrayList<RestFeatureTypeLayerRoot> layerList, GeoServerConfig geoserverConfig) throws Exception    
    {
        String[] headers = { "workspace", "name", "webmap_layer" , "layer_type" ,"request_status", "request_url", "http_code", "error_message", "error_response_content", "native_name", 
        "title", "external_metadata_count", "srs", "enabled", "geoserver_metadata", "datastore", "tilerequest_count", "tilerequest_sucess", "tilerequest_duration", "tilerequest_httpcode", "tilerequest_contenttype", "tilerequest_gwcresult", "tilerequest_gwcresult_missreason", "gwcresultallmiss","tilerequest_datalength"
        , "tilerequest_url", "tilerequest_xmlcontent"};

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
        .setHeader(headers)
        .build();

        try  {

            StringWriter sw = new StringWriter();
            
            CSVPrinter csvPrinter = new CSVPrinter(sw, csvFormat);

            for (RestFeatureTypeLayerRoot restSingleLayerRoot : layerList) 
            {
                String layerType = "NONE";
                if(restSingleLayerRoot.restSingleLayer!=null 
                && restSingleLayerRoot.restSingleLayer.layer!=null
                && restSingleLayerRoot.restSingleLayer.layer.type!=null)
                {
                    layerType = restSingleLayerRoot.restSingleLayer.layer.type;
                }
                //"tilerequest_sucess", "tilerequest_duration", "tilerequest_httpcode", "tilerequest_gwcresult", "tilerequest_datalength"
                
                if(restSingleLayerRoot.coverage==null)
                {                    
                    csvPrinter.printRecord(
                        restSingleLayerRoot.featureType.namespace.name, //workspace
                        restSingleLayerRoot.featureType.name, //name
                        restSingleLayerRoot.isBALayer,
                        layerType, //type
                        restSingleLayerRoot.requestStatus, //request_status
                        restSingleLayerRoot.requestURL, //request_url
                        restSingleLayerRoot.httpCode, //httpCode
                        restSingleLayerRoot.errorMessage, //error_message
                        restSingleLayerRoot.errorResponseContent, //errorResponseContent
                        restSingleLayerRoot.featureType.nativeName, //native_name
                        restSingleLayerRoot.featureType.title, //title
                        restSingleLayerRoot.featureType.metadataLinks.metadataLink.size(), //external_metadata_count
                        restSingleLayerRoot.featureType.srs, //srs
                        restSingleLayerRoot.featureType.enabled, //enabled
                        restSingleLayerRoot.featureType.getGeoserverMetadata(), //geoserver_metadata
                        restSingleLayerRoot.featureType.store.name, //geoserver_metadata                    
                        restSingleLayerRoot.getTilesRequestCount(),
                        restSingleLayerRoot.getTilesRequestAttr("sucess"),
                        restSingleLayerRoot.getTilesRequestAttr("duration"),
                        restSingleLayerRoot.getTilesRequestAttr("httpcode"),
                        restSingleLayerRoot.getTilesRequestAttr("contenttype"),
                        restSingleLayerRoot.getTilesRequestAttr("gwcresult"),                        
                        restSingleLayerRoot.getTilesRequestAttr("gwcresultmissreason"),
                        restSingleLayerRoot.isAllTilesMissGWC(),
                        restSingleLayerRoot.getTilesRequestAttr("datalength"),
                        restSingleLayerRoot.getTilesRequestAttr("url"),
                        restSingleLayerRoot.getTilesRequestAttr("xmlcontent")
                    );
                }
                else
                {
                    csvPrinter.printRecord(
                        restSingleLayerRoot.coverage.namespace.name, //workspace
                        restSingleLayerRoot.coverage.name, //name
                        restSingleLayerRoot.isBALayer,
                        layerType, //type
                        restSingleLayerRoot.requestStatus, //request_status
                        restSingleLayerRoot.requestURL, //request_url
                        restSingleLayerRoot.httpCode, //httpCode
                        restSingleLayerRoot.errorMessage, //error_message
                        restSingleLayerRoot.errorResponseContent, //errorResponseContent
                        restSingleLayerRoot.coverage.nativeName, //native_name
                        restSingleLayerRoot.coverage.title, //title
                        restSingleLayerRoot.coverage.metadataLinks.metadataLink.size(), //external_metadata_count
                        restSingleLayerRoot.coverage.srs, //srs
                        restSingleLayerRoot.coverage.enabled, //enabled
                        restSingleLayerRoot.coverage.getGeoserverMetadata(), //geoserver_metadata
                        restSingleLayerRoot.coverage.store.name, //geoserver_metadata                    
                        restSingleLayerRoot.getTilesRequestCount(),
                        restSingleLayerRoot.getTilesRequestAttr("sucess"),
                        restSingleLayerRoot.getTilesRequestAttr("duration"),
                        restSingleLayerRoot.getTilesRequestAttr("httpcode"),
                        restSingleLayerRoot.getTilesRequestAttr("contenttype"),
                        restSingleLayerRoot.getTilesRequestAttr("gwcresult"),
                        restSingleLayerRoot.getTilesRequestAttr("gwcresultmissreason"),
                        restSingleLayerRoot.isAllTilesMissGWC(),
                        restSingleLayerRoot.getTilesRequestAttr("datalength"),
                        restSingleLayerRoot.getTilesRequestAttr("url"),
                        restSingleLayerRoot.getTilesRequestAttr("xmlcontent")
                    );
                }

            }

            //System.out.println(sw.toString());

            PrintWriter writer = new PrintWriter(geoserverConfig.getOutputFile(), "UTF-8");
            writer.println(sw.toString());
            writer.close();

        } catch(Exception e)
        {
            throw new Exception("Failed while trying to write to CSV", e);
        }


    }
}

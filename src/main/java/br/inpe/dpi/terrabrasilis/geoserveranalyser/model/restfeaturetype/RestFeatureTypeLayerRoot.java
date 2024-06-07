package br.inpe.dpi.terrabrasilis.geoserveranalyser.model.restfeaturetype;

import java.util.ArrayList;
import java.util.List;

import br.inpe.dpi.terrabrasilis.geoserveranalyser.model.TileRequestResult;
import br.inpe.dpi.terrabrasilis.geoserveranalyser.model.restfeaturetype.coverage.Coverage;
import br.inpe.dpi.terrabrasilis.geoserveranalyser.model.restlayer.RestLayerRoot;

public class RestFeatureTypeLayerRoot
{
    public String requestURL;
    public boolean requestStatus;
    public int httpCode=200;
    public String errorResponseContent;
    public String errorMessage;
    public FeatureType featureType = new FeatureType();
    public Coverage coverage;
    public RestLayerRoot restSingleLayer;
    public List<TileRequestResult> tilesRequestResult = new ArrayList<TileRequestResult>();

    public String getTilesRequestAttr(String attr)
    {
        String attrValue = "";
        for (TileRequestResult tileRequestResult : tilesRequestResult) 
        {
            if(attr.equals("duration"))
            {
                attrValue+=":"+tileRequestResult.getDuration(); 
            } else if(attr.equals("gwcresult"))
            {
                attrValue+=":"+tileRequestResult.getGWCResult(); 
            } else if(attr.equals("httpcode"))
            {
                attrValue+=":"+tileRequestResult.getHttpCode(); 
            } else if(attr.equals("datalength"))
            {
                attrValue+=":"+tileRequestResult.getSize(); 
            }
            else if(attr.equals("sucess"))
            {
                attrValue+=":"+tileRequestResult.getSucess(); 
            } else if(attr.equals("url"))
            {
                attrValue+=":"+tileRequestResult.getURL(); 
            }             
        }
        attrValue=attrValue.replaceFirst(":", "");
        return attrValue;
    }
    public int getTilesRequestCount()
    {
        return tilesRequestResult.size();
    }
}

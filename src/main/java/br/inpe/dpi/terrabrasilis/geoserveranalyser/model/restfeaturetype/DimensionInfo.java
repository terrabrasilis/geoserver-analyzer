package br.inpe.dpi.terrabrasilis.geoserveranalyser.model.restfeaturetype;

import java.util.LinkedHashMap;

public class DimensionInfo{
    public boolean enabled;
    public String attribute;
    public String presentation;
    public String units;
    public DefaultValue defaultValue;
    public boolean nearestMatchEnabled;
    public boolean rawNearestMatchEnabled;
    public String startValue;
    public String endValue;

    public void setDefaultValue(Object defaultValue) 
    {

        if(defaultValue instanceof LinkedHashMap)
        {
            this.defaultValue = new DefaultValue();
            this.defaultValue.referenceValue = ((LinkedHashMap<String, String>) defaultValue).get("referenceValue");
            this.defaultValue.strategy = ((LinkedHashMap<String, String>) defaultValue).get("strategy");
        }
        else if(defaultValue instanceof String)
        {
            this.defaultValue = new DefaultValue();
            this.defaultValue.referenceValue = (String) defaultValue; 
        }
        
    }
}

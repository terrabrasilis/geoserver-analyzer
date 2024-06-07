package br.inpe.dpi.terrabrasilis.geoserveranalyser.model.restfeaturetype;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FeatureType{
    public String name;
    public String nativeName;
    public Namespace namespace = new Namespace();
    public String title;
    @JsonProperty("abstract") 
    public String myabstract;
    public Keywords keywords;
    public MetadataLinks metadataLinks = new MetadataLinks();
    public String nativeCRS;
    public String srs;
    public NativeBoundingBox nativeBoundingBox;
    public LatLonBoundingBox latLonBoundingBox;
    public String projectionPolicy;
    public boolean enabled;
    public Metadata metadata = new Metadata();
    public Store store = new Store();
    public boolean serviceConfiguration;
    public String internationalTitle;
    public String internationalAbstract;
    public int maxFeatures;
    public int numDecimals;
    public boolean padWithZeros;
    public boolean forcedDecimal;
    public boolean overridingServiceSRS;
    public boolean skipNumberMatched;
    public boolean circularArcPresent;
    public Attributes attributes;


    public String getGeoserverMetadata()
    {
        String dimensions = "";
        for (Entry entry : metadata.entry) 
        {
            if(dimensions.isEmpty()==false)
            {
                dimensions+=";";
            }
            dimensions+=entry.key;
        }
        return dimensions;
    } 
}

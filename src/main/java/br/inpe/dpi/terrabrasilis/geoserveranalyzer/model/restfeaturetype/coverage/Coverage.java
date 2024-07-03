package br.inpe.dpi.terrabrasilis.geoserveranalyzer.model.restfeaturetype.coverage; 
import com.fasterxml.jackson.annotation.JsonProperty; 
public class Coverage{
    public String name;
    public String nativeName;
    public Namespace namespace;
    public String title;
    public String description;
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
    public boolean advertised;
    public Metadata metadata;
    public Store store;
    public boolean serviceConfiguration;
    public String nativeFormat;
    public Grid grid;
    public SupportedFormats supportedFormats;
    public InterpolationMethods interpolationMethods;
    public String defaultInterpolationMethod;
    public Dimensions dimensions;
    public RequestSRS requestSRS;
    public ResponseSRS responseSRS;
    public Parameters parameters;

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

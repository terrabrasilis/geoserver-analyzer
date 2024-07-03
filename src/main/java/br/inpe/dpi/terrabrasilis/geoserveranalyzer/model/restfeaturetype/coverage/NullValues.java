package br.inpe.dpi.terrabrasilis.geoserveranalyzer.model.restfeaturetype.coverage; 
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty; 
public class NullValues{
    @JsonProperty("double") 
    public ArrayList<String> mydouble;
}

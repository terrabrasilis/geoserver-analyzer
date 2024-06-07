package br.inpe.dpi.terrabrasilis.geoserveranalyser.model.restlayers;

import java.util.ArrayList;

public class RestLayers
{
    private ArrayList<RestLayer> layer;

    public ArrayList<RestLayer> getLayers() {
        return this.layer;
    }

    public void setLayer(ArrayList<RestLayer> layer) {
        this.layer = layer;
    }

}

package br.inpe.dpi.terrabrasilis.geoserveranalyzer.service;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.tile.TileFactory;
import org.geotools.tile.TileService;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class TilesService extends TileService{

    protected TilesService(String name, String baseURL) 
    {
        super(name, baseURL);
    }

    @Override
    public double[] getScaleList() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getScaleList'");
    }

    @Override
    public ReferencedEnvelope getBounds() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBounds'");
    }

    @Override
    public CoordinateReferenceSystem getProjectedTileCrs() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProjectedTileCrs'");
    }

    @Override
    public TileFactory getTileFactory() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTileFactory'");
    }

}

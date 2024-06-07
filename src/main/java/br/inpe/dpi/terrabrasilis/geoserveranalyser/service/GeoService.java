package br.inpe.dpi.terrabrasilis.geoserveranalyser.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Set;

import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.ows.ServiceException;
import org.geotools.ows.wms.Layer;
import org.geotools.ows.wms.WMSCapabilities;
import org.geotools.ows.wms.WebMapServer;
import org.geotools.referencing.CRS;
import org.geotools.renderer.lite.RendererUtilities;
import org.geotools.tile.Tile;
import org.geotools.tile.TileService;
import org.geotools.tile.impl.osm.OSMService;
import org.locationtech.jts.geom.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import br.inpe.dpi.terrabrasilis.geoserveranalyser.model.GeoServerConfig;
import br.inpe.dpi.terrabrasilis.geoserveranalyser.model.TileRequestResult;
import br.inpe.dpi.terrabrasilis.geoserveranalyser.model.restfeaturetype.RestFeatureTypeLayerRoot;

public class GeoService 
{

    public static void getMap(GeoServerConfig config, RestFeatureTypeLayerRoot layerRoot) throws NoSuchAuthorityCodeException, FactoryException, TransformException, ServiceException, IOException
    {
        String layerType = "NONE";
        if(layerRoot.restSingleLayer!=null 
        && layerRoot.restSingleLayer.layer!=null
        && layerRoot.restSingleLayer.layer.type!=null)
        {
            layerType = layerRoot.restSingleLayer.layer.type;
        }

        if("VECTOR".equalsIgnoreCase(layerType) || "RASTER".equalsIgnoreCase(layerType) )
        {
            String workspace = "";
            String name = "";
            Double minx = 0.;
            Double miny = 0.;
            Double maxx = 0.;
            Double maxy = 0.;
            String inCRS = "";

            if ("VECTOR".equalsIgnoreCase(layerType))
            {
                inCRS = layerRoot.featureType.latLonBoundingBox.crs;                    
                minx = layerRoot.featureType.latLonBoundingBox.minx;
                miny = layerRoot.featureType.latLonBoundingBox.miny;
                maxx = layerRoot.featureType.latLonBoundingBox.maxx;
                maxy = layerRoot.featureType.latLonBoundingBox.maxy;
                name = layerRoot.featureType.name;
                workspace = layerRoot.featureType.namespace.name;
            }
            else if ("RASTER".equalsIgnoreCase(layerType))
            {
                inCRS = layerRoot.coverage.latLonBoundingBox.crs;                    
                minx = layerRoot.coverage.latLonBoundingBox.minx;
                miny = layerRoot.coverage.latLonBoundingBox.miny;
                maxx = layerRoot.coverage.latLonBoundingBox.maxx;
                maxy = layerRoot.coverage.latLonBoundingBox.maxy;
                name = layerRoot.coverage.name;
                workspace = layerRoot.coverage.namespace.name;
            }

            if(!inCRS.isEmpty() && minx!=0.0 && miny!=0.0 && maxx!=0.0 && maxy!=0.0 && !workspace.isEmpty() && !name.isEmpty())
            {
                ReferencedEnvelope layerBBox = new ReferencedEnvelope(minx, maxx, miny,  maxy, CRS.decode(inCRS));

                Set<Tile> tiles = getTiles(config, layerBBox);

                String layerCompleteName = workspace + ":" + name;

                getMapTiles(config, tiles, layerCompleteName, layerRoot);
            }
            {
                System.out.println("");
                // System.out.println(e.getMinX());
                // System.out.println(e.getMinY());
                // System.out.println(e.getMaxX());
                // System.out.println(e.getMaxY());
            }
           
        }
    }

    private static void getMapTiles(GeoServerConfig config, Set<Tile> tiles, String layerName, RestFeatureTypeLayerRoot layerRoot) throws NoSuchAuthorityCodeException, FactoryException, TransformException, IOException
    {

        for (Tile tile : tiles) {
            String outCRS = "EPSG:3857";                       
            
            MathTransform transform = CRS.findMathTransform(tile.getExtent().getCoordinateReferenceSystem(), CRS.decode(outCRS), false );
            Envelope res = JTS.transform(tile.getExtent(), transform);

            String minxStr = BigDecimal.valueOf(res.getMinX()).toPlainString();
            String minyStr = BigDecimal.valueOf(res.getMinY()).toPlainString();
            String maxxStr = BigDecimal.valueOf(res.getMaxX()).toPlainString();
            String maxyStr = BigDecimal.valueOf(res.getMaxY()).toPlainString();

            
            String getMapURL = config.getUrl();
            getMapURL += "ows?service=WMS&request=GetMap";
            getMapURL += "&layers=" + layerName;
            getMapURL += "&styles=";
            getMapURL += "&format=image/png";
            getMapURL += "&transparent=true";        
            getMapURL += "&version=1.1.1";
            getMapURL += "&tiled=true";
            getMapURL += "&width=256";
            getMapURL += "&height=256";
            getMapURL += "&srs=" + outCRS;
            getMapURL += "&bbox="+minyStr+","+minxStr+","+maxyStr+","+maxxStr;
    
            System.out.println(getMapURL);

            TileRequestResult tileRequestResult = fetchTileData(getMapURL, config.getUsername(), config.getPassword());
            
            layerRoot.tilesRequestResult.add(tileRequestResult);
        }

               
    }

    public static Set<Tile> getTiles(GeoServerConfig config, ReferencedEnvelope env) throws ServiceException, IOException, TransformException, FactoryException
    {

        String baseURL = "http://tile.openstreetmap.org/";

        TileService service = new OSMService("OSM", baseURL);

        int tileWidth = 500; //px

        Double heightValue = ((tileWidth*env.getHeight())/env.getWidth());

        int tileHeight = heightValue.intValue();
        
        Double scale = RendererUtilities.calculateScale(env, tileWidth, tileHeight, 92.);

        Set<Tile> tiles = service.findTilesInExtent(env, scale.intValue(), false, 1000);

        System.out.println(tiles);

        return tiles;

        // //osmTileLayer.getCoverage().getSources().get(0);

        // String getMapURL = config.getUrl() + "/ows?service=WMS&request=getcapabilities";
        // URL capabilitiesURL = new URL(getMapURL);

        // WebMapServer wms = new WebMapServer(capabilitiesURL);
        // wms.createGetMapRequest();
    }

    public static void teste(GeoServerConfig config) throws ServiceException, IOException, NoSuchAuthorityCodeException, FactoryException
    {
        // TileService ts = new OSMService("teste", "http://localhost/geoserver/ows?");
        // Set<Tile> tiles = ts.findTilesInExtent(env, 0, false, 0);

        // System.out.println(tiles);



        String getMapURL = config.getUrl() + "/ows?service=WMS&request=getcapabilities";
        URL capabilitiesURL = new URL(getMapURL);

        WebMapServer wms = new WebMapServer(capabilitiesURL);
        wms.createGetMapRequest();
        
        WMSCapabilities cap =  wms.getCapabilities();

        for (Layer layer : cap.getLayerList()) 
        {
            if("prodes-legal-amz:temporal_mosaic_legal_amazon".equalsIgnoreCase(layer.getName())==false)
            {
                continue;
            }         
            System.out.println(layer.getName());    
            System.out.println(layer.getEnvelope(CRS.decode("EPSG:3857")));    
            ReferencedEnvelope e = new ReferencedEnvelope(layer.getEnvelope(CRS.decode("EPSG:4326")));
            System.out.println(e.getMinX());
            System.out.println(e.getMinY());
            System.out.println(e.getMaxX());
            System.out.println(e.getMaxY());

            
        }
        
        

    }


    private static TileRequestResult fetchTileData(String urlStr, String username, String password) throws IOException
    {       
        long duration = 0;
        long startTime = System.nanoTime();
        HttpURLConnection uc =null;
        TileRequestResult tileRequestResult = new TileRequestResult();
        tileRequestResult.setURL(urlStr);
        try
        {

            URL url = new URL(urlStr);
            uc = (HttpURLConnection) url.openConnection();
            String userpass = username + ":" + password;
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
            uc.setRequestProperty ("Authorization", basicAuth);

            //uc.getRequestProperties().entrySet()
            
            tileRequestResult.setHttpCode(uc.getResponseCode());
            
            tileRequestResult.setSize(uc.getInputStream().available());
            tileRequestResult.setSucess(true);
            long endTime = System.nanoTime();
            duration = (endTime - startTime)/1000000000; //seconds
            tileRequestResult.setDuration(duration);         


            if(uc.getHeaderField("geowebcache-cache-result")!=null)
            {
                tileRequestResult.setGWCResult(uc.getHeaderField("geowebcache-cache-result"));
            }else
            {
                tileRequestResult.setGWCResult("MISSING-PROPERTY");
            }

        } catch(IOException e)
        {
            long endTime = System.nanoTime();

            duration = (endTime - startTime)/1000000000; //seconds

            tileRequestResult.setSucess(false);
            tileRequestResult.setDuration(duration);
            tileRequestResult.setHttpCode(uc.getResponseCode());
            
            
        }
        return tileRequestResult;
                   
    }

}

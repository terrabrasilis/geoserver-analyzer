package br.inpe.dpi.terrabrasilis.geoserveranalyzer.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashSet;
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
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.ProjCoordinate;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import br.inpe.dpi.terrabrasilis.geoserveranalyzer.model.GeoServerConfig;
import br.inpe.dpi.terrabrasilis.geoserveranalyzer.model.TileRequestResult;
import br.inpe.dpi.terrabrasilis.geoserveranalyzer.model.restfeaturetype.RestFeatureTypeLayerRoot;

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

                String outCRS = "EPSG:3857";  

                ReferencedEnvelope reprojectedEnvelope = reprojectEnvelope(layerBBox, inCRS, outCRS);                   

                {

                    //For debug purpose only

                    // String minxStr = BigDecimal.valueOf(reprojectedEnvelope.getMinX()).toPlainString();
                    // String minyStr = BigDecimal.valueOf(reprojectedEnvelope.getMinY()).toPlainString();
                    // String maxxStr = BigDecimal.valueOf(reprojectedEnvelope.getMaxX()).toPlainString();
                    // String maxyStr = BigDecimal.valueOf(reprojectedEnvelope.getMaxY()).toPlainString();
                    // String layerCompleteName = workspace + ":" + name;
           
                    // String getMapURL = config.getUrl();
                    // getMapURL += "ows?service=WMS&request=GetMap";
                    // getMapURL += "&layers=" + layerCompleteName;
                    // getMapURL += "&styles=";
                    // getMapURL += "&format=image/png";
                    // getMapURL += "&transparent=true";        
                    // getMapURL += "&version=1.1.1";
                    // getMapURL += "&tiled=true";
                    // getMapURL += "&width=256";
                    // getMapURL += "&height=256";
                    // getMapURL += "&srs=" + outCRS;
                    // getMapURL += "&bbox="+minxStr+","+minyStr+","+maxxStr+","+maxyStr;

                    // System.out.println(getMapURL);
                }

                Set<Tile> tiles = new HashSet<Tile>();

                String layerCompleteName = workspace + ":" + name;

                try
                {
                    tiles = getTiles(config, reprojectedEnvelope);

                } catch(IllegalArgumentException e)
                {
                    String msg = "Unable to get tiles for layer: " + layerCompleteName + " with envelope: " + reprojectedEnvelope;
                    System.out.println(msg);

                    if(layerRoot.errorMessage!=null && layerRoot.errorMessage.isEmpty() == false)
                    {
                        layerRoot.errorMessage = "\n";    
                    }
                    layerRoot.errorMessage += msg;
                }


                getMapTiles(config, tiles, layerCompleteName, inCRS, outCRS, layerRoot);            
            }

           
        }
    }

    private static void getMapTiles(GeoServerConfig config, Set<Tile> tiles, String layerName, String inCRS, String outCRS, RestFeatureTypeLayerRoot layerRoot) throws NoSuchAuthorityCodeException, FactoryException, TransformException, IOException
    {

        for (Tile tile : tiles) {
            
            MathTransform transform = CRS.findMathTransform(tile.getExtent().getCoordinateReferenceSystem(), CRS.decode(outCRS), false );
            Envelope res = JTS.transform(tile.getExtent(), transform);

            ReferencedEnvelope reprojectedEnvelope = reprojectEnvelope(tile.getExtent(), inCRS,  outCRS);

            String minxStr = BigDecimal.valueOf(reprojectedEnvelope.getMinX()).toPlainString();
            String minyStr = BigDecimal.valueOf(reprojectedEnvelope.getMinY()).toPlainString();
            String maxxStr = BigDecimal.valueOf(reprojectedEnvelope.getMaxX()).toPlainString();
            String maxyStr = BigDecimal.valueOf(reprojectedEnvelope.getMaxY()).toPlainString();            

   
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
            getMapURL += "&bbox="+minxStr+","+minyStr+","+maxxStr+","+maxyStr;
    
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

        Double scale = 0.;

        try {
            scale = RendererUtilities.calculateScale(env, tileWidth, tileHeight, 92.);
        } 
        catch(IllegalArgumentException e)
        {            
            throw new IllegalArgumentException("Unable to get scale, invalid envelope or dimension.", e);
        }

        Set<Tile> tiles = service.findTilesInExtent(env, scale.intValue(), false, 1000);

        System.out.println(tiles);

        return tiles;

    }

    public static void teste(GeoServerConfig config) throws ServiceException, IOException, NoSuchAuthorityCodeException, FactoryException
    {

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
            
            long endTime = System.nanoTime();
            duration = (endTime - startTime)/1000000000; //seconds
            tileRequestResult.setDuration(duration); 
            tileRequestResult.setContentType(uc.getContentType());  
            
            long size = 0;
		    // int chunk = 0;
            // byte[] buffer = new byte[1024];
			// while((chunk = uc.getInputStream().read(buffer)) != -1){
			// 	size += chunk;
            // }


            int bufferSize = 1024;
            char[] buffer = new char[bufferSize];
            StringBuilder out = new StringBuilder();
            Reader in = new InputStreamReader(uc.getInputStream(), StandardCharsets.UTF_8);
            for (int numRead; (numRead = in.read(buffer, 0, buffer.length)) > 0; ) 
            {
                size += numRead;
                out.append(buffer, 0, numRead);
            }

            tileRequestResult.setSize(size);

            if("image/png".equalsIgnoreCase(uc.getContentType()))
            {
                tileRequestResult.setSucess(true);
                if(uc.getHeaderField("geowebcache-cache-result")!=null)
                {
                    tileRequestResult.setGWCResult(uc.getHeaderField("geowebcache-cache-result"));                    
                    if("MISS".equalsIgnoreCase(tileRequestResult.getGWCResult()))
                    {
                        tileRequestResult.setGWCResultMissReason(uc.getHeaderField("geowebcache-miss-reason"));                                            

                        if((tileRequestResult.getGWCResultMissReason() == null || tileRequestResult.getGWCResultMissReason().isEmpty()) && tileRequestResult.getSize()<=1800.0)
                        {
                            tileRequestResult.setGWCResultMissReason("empty-tile");
                        }
                        
                        
                    }
                }
                else
                {
                    tileRequestResult.setGWCResult("MISSING-PROPERTY");
                }
            }
            else
            {
                tileRequestResult.setSucess(false);
                if(tileRequestResult.getContentType().contains("application/vnd.ogc.se_xml"))
                {
                    tileRequestResult.setXmlContent(out.toString());
                }                
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

    private static ReferencedEnvelope reprojectEnvelope(ReferencedEnvelope inEnv, String inCRSStr, String outCRSStr) throws MismatchedDimensionException, NoSuchAuthorityCodeException, FactoryException
    {  
        ProjCoordinate min = reprojectCoordinate(inEnv.getMinX(),inEnv.getMinY(), inCRSStr, outCRSStr);
        ProjCoordinate max = reprojectCoordinate(inEnv.getMaxX(),inEnv.getMaxY(), inCRSStr, outCRSStr);   

        ReferencedEnvelope layerBBox = new ReferencedEnvelope(min.x, max.x, min.y,  max.y, CRS.decode(outCRSStr));

        return layerBBox;
    }

    private static ProjCoordinate reprojectCoordinate(double x, double y, String inCRSStr, String outCRSStr)
    {        
        CRSFactory crsFactory = new CRSFactory();
        org.locationtech.proj4j.CoordinateReferenceSystem inCRS = crsFactory.createFromName(inCRSStr);
        org.locationtech.proj4j.CoordinateReferenceSystem outCRS =  crsFactory.createFromName(outCRSStr);
        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CoordinateTransform wgsToUtm = ctFactory.createTransform(inCRS, outCRS);
        ProjCoordinate result = new ProjCoordinate();
        wgsToUtm.transform(new ProjCoordinate(x, y), result);

        return result;        
    }

}

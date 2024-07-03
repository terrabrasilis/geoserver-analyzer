## GeoServer Analyser

This project is a tool to to extract data from GeoServer layers/featuretypes to be able to analyse, fix and clear a big geoserver configuration.

## Tool Output

The ouput of this tool is a sheet with some information about each layer (CSV format).

- workspace (String: Layer workspace/namespace name)
- name (String: Layer name)
- webmap_layer (Boolean: Is and webmap layer?)
- layer_type (String: VECTOR, RASTER or WMS)
- request_status (Boolean: Is the request to REST layer configuration  sucessful)
- request_url (String: Layer configuration REST url)
- http_code (Integer: Layer configuration REST request http code)
- error_message (String: Layer configuration REST request exception message)
- error_response_content (String: Layer configuration REST request exception response content)
- native_name   (String: Layer native name)
- title (String: Layer title name)
- external_metadata_count (String: List of Metadata Links)
- srs (String: Layer SRS)
- enabled (String: If the layer is enabled)
- geoserver_metadata (String: List of extra configuration enabled on the layer: JDBC_VIRTUAL_TABLE, TIME, ELEVATION, CACHING ENABLED)
- datastore (String: Name of the DataStore)
- tilerequest_count (Integer: Number of tiles OSM-EPSG:3857 will be requested for the layer Bounding Box)
- tilerequest_sucess (Boolean: If the tile request was sucessful)
- tilerequest_duration (Integer: Time to request the tile in seconds)
- tilerequest_httpcode (Integer: Tile request responde http code)
- tilerequest_contenttype (String: Tile request content type)
- tilerequest_gwcresult (String: Tile request GWC result MISS or HIT)
- tilerequest_gwcresult_missreason (String: Tile request GWC result reason)
- gwcresultallmiss (Boolean: If all Tile request GWC result is MISS)
- tilerequest_datalength (Integer: Tile request response length)
- tilerequest_url (String: Tile request url)
- tilerequest_xmlcontent (String: If response is not an image and is a XML, this option is the response. The error response is always a XML )

## Additional information

This project also allow to check if each layer is configured on Business API Service (webmap_layer output attribute).

## Runing environment variables

- BUSINESS_API_URL - URLs to Business API (Values separeted by ";"). This is not a required parameter, it fill the webmap_layer attribute. 
- GEOSERVER_URL - Base URL for GeoServer 
- GEOSERVER_USERNAME - Path to Secret with GeoServer username 
- GEOSERVER_PASSWORD - Path to Secret with GeoServer password 

## Runing on docker

`docker run \`
`-e "BUSINESS_API_URL=https://terrabrasilis.dpi.inpe.br/business/api/v1/vision/name/deforestation/all;https://terrabrasilis.dpi.inpe.br/business/api/v1/vision/name/alerts/all;https://terrabrasilis.dpi.inpe.br/business/api/v1/vision/name/vegetation/all" \`
`-e GEOSERVER_URL="https://terrabrasilis.dpi.inpe.br/geoserver/"  \`
`-e GEOSERVER_USERNAME="/run/secrets/GEOSERVER_USERNAME" \`
`-e GEOSERVER_PASSWORD="/run/secrets/GEOSERVER_PASSWORD" \`
`-t terrabrasilis/geoserver-analyzer:v1.0.0`

## Clone and install the dependencies

To clone this project, execute: 

1. `https://github.com/terrabrasilis/geoserver-analyzer.git`
2. `cd geoserver-analyzer`
3. `git fetch --all`
4. `git pull --all`
5. `git checkout <branch> (e.g: master)`
6. `mvn clean install`

After all statements above was executed, the project will be prepare to run the steps bellow.

## Runing as dev

`mvn exec:java -Dexec.mainClass="br.inpe.dpi.terrabrasilis.geoserveranalyser.Main" \`
`-Dexec.args="--businessapi-url=https://terrabrasilis.dpi.inpe.br/business/api/v1/vision/name/deforestation/all --businessapi-url=https://terrabrasilis.dpi.inpe.br/business/api/v1/vision/name/alerts/all --businessapi-url=https://terrabrasilis.dpi.inpe.br/business/api/v1/vision/name/vegetation/all --geoserver-url=https://terrabrasilis.dpi.inpe.br/geoserver/ --geoserver-username=admin --geoserver-password=geoserver --output-file=/tmp/geoserver-analyser.csv`


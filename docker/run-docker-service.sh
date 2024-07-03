echo "'admin'" | docker secret create GEOSERVER_PASSWORD -
echo "'admin'" | docker secret create GEOSERVER_USERNAME -
docker service create \
    --secret source=GEOSERVER_PASSWORD,target=GEOSERVER_PASSWORD \
    --secret source=GEOSERVER_USERNAME,target=GEOSERVER_USERNAME \
    -e GEOSERVER_USERNAME='/run/secrets/GEOSERVER_USERNAME' \
    -e GEOSERVER_PASSWORD='/run/secrets/GEOSERVER_PASSWORD' \
    -e "BUSINESS_API_URL=https://terrabrasilis.dpi.inpe.br/business/api/v1/vision/name/deforestation/all;https://terrabrasilis.dpi.inpe.br/business/api/v1/vision/name/alerts/all;https://terrabrasilis.dpi.inpe.br/business/api/v1/vision/name/vegetation/all" \
    -e GEOSERVER_URL="https://terrabrasilis.dpi.inpe.br/geoserver/" \
    terrabrasilis/geoserver-analyzer:v1.0.0

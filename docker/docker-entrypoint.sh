#!/bin/sh
cd /usr/local/geoserver-analyser

#source .env

NOW_TIMESTAMP=`date +%Y-%d-%m_%H-%M-%S`
OUTPUTFILE="/usr/local/geoserver-analyser/output"
mkdir $OUTPUTFILE
OUTPUTFILE="$OUTPUTFILE/geoserver-analyser-report_$NOW_TIMESTAMP.csv"

# Secrets
GEOSERVER_USERNAME=`cat /run/secrets/GEOSERVER_USERNAME`
GEOSERVER_PASSWORD=`cat /run/secrets/GEOSERVER_PASSWORD`

# Java Main Arguments
OUTPUTFILE_ARG="--output-file=$OUTPUTFILE"
GEOSERVER_URL_ARG="--geoserver-url=$GEOSERVER_URL"
GEOSERVER_USERNAME_ARG="--geoserver-username=$GEOSERVER_USERNAME"
GEOSERVER_PASSWORD_ARG="--geoserver-password=$GEOSERVER_PASSWORD"
BUSINESS_URL_ARG=""

if [ ! -z "$BUSINESS_API_URL" ]
then
    URLS=$(echo $BUSINESS_API_URL | tr ";" "\n")

    for URL in $URLS
    do
        BUSINESS_URL_ARG="$BUSINESS_URL_ARG --businessapi-url=$URL"
    done
fi


MAIN_ARGS="$GEOSERVER_URL_ARG $GEOSERVER_USERNAME_ARG $GEOSERVER_PASSWORD_ARG $BUSINESS_URL_ARG $OUTPUTFILE_ARG"
echo "Current Main Argument: $MAIN_ARGS"

mvn exec:java -Dexec.mainClass="br.inpe.dpi.terrabrasilis.geoserveranalyser.Main" -Dexec.args="$MAIN_ARGS"
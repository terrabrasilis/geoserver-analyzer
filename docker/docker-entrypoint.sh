#!/bin/sh
cd /usr/local/geoserver-analyzer

#source .env

NOW_TIMESTAMP=`date +%Y-%d-%m_%H-%M-%S`
OUTPUTFILE="/usr/local/geoserver-analyzer/output"
mkdir $OUTPUTFILE
OUTPUTFILE="$OUTPUTFILE/geoserver-analyzer-report_$NOW_TIMESTAMP.csv"

# Java Main Arguments
OUTPUTFILE_ARG="--output-file=$OUTPUTFILE"
GEOSERVER_URL_ARG="--geoserver-url=$GEOSERVER_URL"

#GeoServer Username and Password
GEOSERVER_USERNAME_ARG="--geoserver-username=$GEOSERVER_USERNAME"
if [ ! -z "$GEOSERVER_USERNAME_FILE" ]
then
    GEOSERVER_USERNAME_ARG="--geoserver-username=`cat $GEOSERVER_USERNAME_FILE`"
fi
GEOSERVER_PASSWORD_ARG="--geoserver-password=$GEOSERVER_PASSWORD"
if [ ! -z "$GEOSERVER_PASSWORD_FILE" ]
then
    GEOSERVER_PASSWORD_ARG="--geoserver-password=`cat $GEOSERVER_PASSWORD_FILE`"
fi

#Business API URLs
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
#echo "Current Main Argument: $MAIN_ARGS"

mvn install
mvn exec:java -Dexec.mainClass="br.inpe.dpi.terrabrasilis.geoserveranalyzer.Main" -Dexec.args="$MAIN_ARGS"
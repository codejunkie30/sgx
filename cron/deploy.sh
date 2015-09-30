#!/bin/bash

### arguments
REMOTE_PATH=$1
ENVIRONMENT_NAME=$2

###  local variables
BASE_PATH=/root
BUILDS_PATH=$BASE_PATH/builds
BUILDS_ARCHIVE_PATH=$BUILDS_PATH/archives
CURRENT_BUILD_FILE=$BUILDS_PATH/current.build
CURRENT_BUILD=`cat $CURRENT_BUILD_FILE`

### remote variables
LAST_BUILD=`ssh -i ssh-key.pem ec2-user@54.196.98.231 "cat $REMOTE_PATH/last.build"`

## logging date/time
logCommandToOut() {
	echo `date +"%D %T"` $1
}

logCommandToOut "existing build $CURRENT_BUILD -> server build $LAST_BUILD"

if [[ "$CURRENT_BUILD" != "$LAST_BUILD" ]]; then

	shopt -s nullglob
	
	## sending email
	aws sns publish --topic-arn arn:aws:sns:us-east-1:266865979071:QA_BUILD_NOTIFICATION --message "A new build ($LAST_BUILD) has been discovered and is being deployed." --subject	"$ENVIRONMENT_NAME stopping"

	## archive as last
	for file in `$BUILDS_CURRENT/*.war`; do
		logCommandToOut "moving $file to $BUILDS_ARCHIVE_PATH/${file##*/}"
		mv $file $BUILDS_ARCHIVE_PATH/${file##*/}
	done
	
	## pull the latest build
	logCommandToOut "Pulling latest build"
	scp -i ssh-key.pem ec2-user@54.196.98.231:$REMOTE_PATH/$LAST_BUILD $BUILDS_PATH 
	
	## mark the current build
	logCommandToOut "Updating current build"
	echo $LAST_BUILD > $CURRENT_BUILD_FILE
	
	## stopping server
	logCommandToOut "Stopping server"
	/sbin/service tomcat7 stop
	echo "\n"
	
	## cleaning up resources
	logCommandToOut "Cleaning up resources"
	cd /var/lib/tomcat7/webapps
	rm -fR sgx*
	
	## copy latest war
	logCommandToOut "Deploying latest war"
	cp $BUILDS_PATH/$LAST_BUILD sgx.war
	
	## starting server
	logCommandToOut "Starting server"
	/sbin/service tomcat7 start
	echo "\n"
	
	## sending email
	logCommandToOut "Sending email"
	aws sns publish --topic-arn arn:aws:sns:us-east-1:266865979071:QA_BUILD_NOTIFICATION --message "The server has been restarted, please give 5 minutes before testing." --subject	"$ENVIRONMENT_NAME starting"
	
else

	logCommandToOut "No build to deploy"

fi
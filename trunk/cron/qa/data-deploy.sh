## dynamic (per server) variables
REMOTE_PATH="/mnt/100g/sgx-builds/data-premium"
ENVIRONMENT_NAME="SGX Premium Data (QA)"
WAR_NAME="sgx-data.war"

## move to cron directory
SCRIPT_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd $SCRIPT_DIR

../deploy.sh "$REMOTE_PATH" "$ENVIRONMENT_NAME" "$WAR_NAME"
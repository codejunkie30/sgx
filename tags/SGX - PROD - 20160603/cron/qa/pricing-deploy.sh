## dynamic (per server) variables
REMOTE_PATH="/mnt/100g/sgx-builds/pricing-premium"
ENVIRONMENT_NAME="SGX Premium Pricing (QA)"
WAR_NAME="sgx-pricing.war"

## move to cron directory
SCRIPT_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd $SCRIPT_DIR

../deploy.sh "$REMOTE_PATH" "$ENVIRONMENT_NAME" "$WAR_NAME"
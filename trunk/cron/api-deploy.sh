## dynamic (per server) variables
REMOTE_PATH="/mnt/100g/sgx-builds/api-premium"
ENVIRONMENT_NAME="SGX Premium API (QA)"

## move to cron directory
SCRIPT_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd $SCRIPT_DIR

./deploy.sh
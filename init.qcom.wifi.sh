#!/system/bin/sh

LOG_TAG="qcom-wifi"
LOG_NAME="${0}:"

loge ()
{
  /system/bin/log -t $LOG_TAG -p e "$LOG_NAME $@"
}

logi ()
{
  /system/bin/log -t $LOG_TAG -p i "$LOG_NAME $@"
}

# cleanup old files
if [ -d /data/misc/wifi/prima ]; then
    rm -r /data/misc/wifi/prima
fi

WCNSS_CFG_FILE="/system/etc/firmware/wlan/prima/WCNSS_qcom_cfg.ini"

# delete so that we can check if patching was sucessfull
rm $WCNSS_CFG_FILE

# Configure the hardware. fail if nvitems aren't available yet.
/system/bin/logwrapper /system/bin/mac-update

# Wait for nvitems and reconfigure if necessary (first boot)
if [ ! -e $WCNSS_CFG_FILE ]; then
    while [ ! -e $WCNSS_CFG_FILE ]; do
        logi "MAC patching failed - retry"
    
        /system/bin/logwrapper /system/bin/mac-update
        sleep 2;
    done
fi



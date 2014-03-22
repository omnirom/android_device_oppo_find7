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

# Configure the hardware. fail if nvitems aren't available yet.
/system/bin/logwrapper /system/bin/mac-update

# Wait for nvitems and reconfigure if necessary (first boot)
if [ ! "$(ls /data/opponvitems)" ]; then
    while [ ! "$(ls /data/opponvitems)" ]; do
        logi "waiting for /data/opponvitems"
        sleep 1;
    done
    
    /system/bin/logwrapper /system/bin/mac-update
fi



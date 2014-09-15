#!/sbin/bb sh
set +x
_PATH="$PATH"
export PATH=/sbin

#Same deal as lvm_init
#This MIGHT be something that can be run in early-init within
#with lvm_init.sh, but this stuff is in "on init" within the init.rc
#so let's keep it there for now
if [ -e /dev/lvpool/userdata ]; then
    bb ln -s /storage/emulated/legacy /sdcard
    bb ln -s /storage/emulated/legacy /mnt/sdcard
    bb ln -s /storage/emulated/legacy /storage/sdcard0
    bb ln -s /mnt/shell/emulated/0 /storage/emulated/legacy
else
    bb mkdir -p /storage/sdcard0
    bb chown root:root /storage/sdcard0
    bb chmod 0775 /storage/sdcard0
    bb ln -s /storage/sdcard0 /sdcard
    bb ln -s /storage/sdcard0 /mnt/sdcard
fi

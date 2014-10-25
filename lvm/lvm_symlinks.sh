#!/sbin/static/busybox sh
set +x
_PATH="$PATH"
export PATH=/sbin

#Same deal as lvm_init
#This MIGHT be something that can be run in early-init within
#with lvm_init.sh, but this stuff is in "on init" within the init.rc
#so let's keep it there for now
if [ -e /dev/lvpool/userdata ] ||
    # For the unified layout we can take the same emulated storage symlinks as for lvm
    # If the userdata partion is larger than 4GB we probably have Coldbird's unified layout
    [ `/sbin/static/busybox fdisk -l /dev/block/platform/msm_sdcc.1/by-name/userdata | /sbin/static/busybox sed -e 's/Disk .*userdata:.*, \([0-9]*\)[0-9]\{6\} bytes/\1/' | /sbin/static/busybox grep -Eo '^[0-9]+$'` -gt 4000 ]; then
    /sbin/static/busybox ln -s /storage/emulated/legacy /sdcard
    /sbin/static/busybox ln -s /storage/emulated/legacy /mnt/sdcard
    /sbin/static/busybox ln -s /storage/emulated/legacy /storage/sdcard0
    /sbin/static/busybox ln -s /mnt/shell/emulated/0 /storage/emulated/legacy
else
    /sbin/static/busybox mkdir -p /storage/sdcard0
    /sbin/static/busybox chown root:root /storage/sdcard0
    /sbin/static/busybox chmod 0775 /storage/sdcard0
    /sbin/static/busybox ln -s /storage/sdcard0 /sdcard
    /sbin/static/busybox ln -s /storage/sdcard0 /mnt/sdcard
fi

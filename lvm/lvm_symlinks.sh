#!/sbin/static/busybox sh
set +x
_PATH="$PATH"
export PATH=/sbin

#Same deal as lvm_init
#This MIGHT be something that can be run in early-init within
#with lvm_init.sh, but this stuff is in "on init" within the init.rc
#so let's keep it there for now
if [ -e /dev/lvpool/userdata ]; then
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

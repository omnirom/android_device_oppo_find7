#!/sbin/busybox sh
set +x
_PATH="$PATH"
export PATH=/sbin

if [ -e /dev/lvpool/userdata ]; then
    busybox ln -s /storage/emulated/legacy /sdcard
    busybox ln -s /storage/emulated/legacy /mnt/sdcard
    busybox ln -s /storage/emulated/legacy /storage/sdcard0
    busybox ln -s /mnt/shell/emulated/0 /storage/emulated/legacy
else
    busybox mkdir -p /storage/sdcard0
    busybox chown root:root /storage/sdcard0
    busybox chmod 0775 /storage/sdcard0
    busybox ln -s /storage/sdcard0 /sdcard
    busybox ln -s /storage/sdcard0 /mnt/sdcard
fi

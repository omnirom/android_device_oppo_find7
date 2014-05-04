#!/system/bin/sh

/system/xbin/busybox chown -R media_rw:media_rw /mnt/media_rw/sdcard0
/system/xbin/busybox chmod -R u+rwX,g+rwX,o+rX,o-w /mnt/media_rw/sdcard0

/system/bin/sdcard -u 1023 -g 1023 -w 1023 /mnt/media_rw/sdcard0 /storage/sdcard0

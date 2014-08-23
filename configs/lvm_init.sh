#!/sbin/busybox sh
set +x
_PATH="$PATH"
export PATH=/sbin

if [ -e /dev/lvpool/userdata ]; then
    busybox cp /fstab.qcom.lvm /fstab.qcom
    busybox cp /etc/twrp.fstab.lvm /etc/twrp.fstab
    busybox cp /init.fs.rc.lvm /init.fs.rc
else
    busybox cp /fstab.qcom.std /fstab.qcom
    busybox cp /etc/twrp.fstab.std /etc/twrp.fstab
    busybox cp /init.fs.rc.std /init.fs.rc
fi

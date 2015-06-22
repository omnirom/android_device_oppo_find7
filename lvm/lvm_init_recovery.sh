#!/sbin/static/busybox sh
set +x
_PATH="$PATH"
export PATH=/sbin

/sbin/lvm vgscan --mknodes --ignorelockingfailure
/sbin/lvm vgchange -aly --ignorelockingfailure

#don't use -f here - this is not a regular file
#Check if an LV for userdata exists, if it does
#this is an LVM configuration and copy LVM fstabs
#and env.  If it doesn't exist, assume a standard config
if [ -e /dev/lvpool/userdata ]; then
    /sbin/static/busybox cp /fstab.qcom.lvm /fstab.qcom
    /sbin/static/busybox cp /etc/twrp.fstab.lvm /etc/recovery.fstab
    /sbin/static/busybox cp /init.fs.rc.lvm /init.fs.rc
elif [ -e /dev/block/platform/msm_sdcc.1/by-name/sdcard ]; then
    /sbin/static/busybox cp /fstab.qcom.std /fstab.qcom
    /sbin/static/busybox cp /etc/twrp.fstab.std /etc/recovery.fstab
    /sbin/static/busybox cp /init.fs.rc.std /init.fs.rc
else
    /sbin/static/busybox cp /fstab.qcom.unified /fstab.qcom
    /sbin/static/busybox cp /etc/twrp.fstab.unified /etc/recovery.fstab
    /sbin/static/busybox cp /init.fs.rc.lvm /init.fs.rc
fi

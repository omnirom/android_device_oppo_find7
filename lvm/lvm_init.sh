#!/sbin/busybox sh
set +x
_PATH="$PATH"
export PATH=/sbin

#don't use -f here - this is not a regular file
#Check if an LV for userdata exists, if it does
#this is an LVM configuration and copy LVM fstabs
#and env.  If it doesn't exist, assume a standard config
if [ -e /dev/lvpool/userdata ]; then
    busybox cp /fstab.qcom.lvm /fstab.qcom
    busybox cp /etc/twrp.fstab.lvm /etc/twrp.fstab
    busybox cp /init.fs.rc.lvm /init.fs.rc
else
    busybox cp /fstab.qcom.std /fstab.qcom
    busybox cp /etc/twrp.fstab.std /etc/twrp.fstab
    busybox cp /init.fs.rc.std /init.fs.rc
fi

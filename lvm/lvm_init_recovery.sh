#!/sbin/static/busybox sh
set +x
_PATH="$PATH"
export PATH=/sbin

/sbin/static/busybox touch /path0.txt
/sbin/lvm vgscan --mknodes --ignorelockingfailure
/sbin/lvm vgchange -aly --ignorelockingfailure

#don't use -f here - this is not a regular file
#Check if an LV for userdata exists, if it does
#this is an LVM configuration and copy LVM fstabs
#and env.  If it doesn't exist, assume a standard config
if [ -e /dev/lvpool/userdata ]; then
    /sbin/static/busybox cp /fstab.qcom.lvm /fstab.qcom
    /sbin/static/busybox cp /etc/twrp.fstab.lvm /etc/twrp.fstab
    /sbin/static/busybox cp /init.fs.rc.lvm /init.fs.rc
    /sbin/static/busybox touch /path1.txt
else
    /sbin/static/busybox cp /fstab.qcom.std /fstab.qcom
    /sbin/static/busybox cp /etc/twrp.fstab.std /etc/twrp.fstab
    /sbin/static/busybox cp /init.fs.rc.std /init.fs.rc
    /sbin/static/busybox touch /path2.txt
fi

#!/system/bin/sh
PATH=/sbin:/system/sbin:/system/bin:/system/xbin
export PATH

if [ -e /dev/lvpool/userdata ]; then
    setprop ro.lvm_storage 1
else
    setprop ro.lvm_storage 0
fi

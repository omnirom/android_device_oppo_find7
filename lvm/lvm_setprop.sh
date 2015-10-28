#!/system/bin/sh

#Just like lvm_init.sh and lvm_symlinks, this makes decisions based on whether
#LVM is in use.  You will notice that here we are using the system shell and
#setprop.

#Note that this must be run in "on boot" or later - see main() in system/core/init/init.c
#specifically where property_service_init_action happens
#TODO: Consider making this usable from a ramdisk for recovery.  We haven't decided
#whether to make TWRP property-driven or to automatically detect that emulated storage
#is in use based on the fstab.  CWM automatically detects from fstab, unsure about
#Oppo recovery

echo "Setprop start!"
if [ -e /dev/lvpool/userdata ]; then
    /system/bin/setprop ro.crypto.fuse_sdcard true
else
    /system/bin/setprop ro.vold.primary_physical 1
fi

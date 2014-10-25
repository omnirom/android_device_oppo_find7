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

#These properties are used by https://gerrit.omnirom.org/#/c/9273/ to determine
#the storage configuration to use
echo "Setprop start!"
if [ -e /dev/lvpool/userdata ]; then
    echo "Setting lvm_storage to 1!"
    /system/bin/setprop ro.lvm_storage 1
    echo "Set lvm_storage to 1!"

    # **** HACK *****
    # As currently ro.lvm_storage is only used in
    # frameworks/base/services/java/com/android/server/MountService.java
    # to detect emulated storage, we'll use this flag for now also for
    # non-lvm'ed but emulated storage on Coldbird's unified layout. To
    # make it distinguishable, we introduce an additional property
    # ro.unified_storage.
    #
    # If the userdata partion is larger than 4GB we probably have Coldbird's unified layout
elif [ `/sbin/static/busybox fdisk -l /dev/block/platform/msm_sdcc.1/by-name/userdata | /sbin/static/busybox sed -e 's/Disk .*userdata:.*, \([0-9]*\)[0-9]\{6\} bytes/\1/' | /sbin/static/busybox grep -Eo '^[0-9]+$'` -gt 4000 ]; then
    echo "Setting lvm_storage to 1 even though we are not lvm'ed!"
    /system/bin/setprop ro.lvm_storage 1
    echo "Set lvm_storage to 1!"
    echo "Setting unified_storage to 1!"
    /system/bin/setprop ro.unified_storage 1
    echo "Set unified_storage to 1!"
else
    echo "Setting lvm_storage to 0"
    /system/bin/setprop ro.lvm_storage 0
    echo "Set lvm_storage to 0"
    echo "Setting unified_storage to 0!"
    /system/bin/setprop ro.unified_storage 0
    echo "Set unified_storage to 0!"
fi

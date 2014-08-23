#!/system/bin/sh

echo "Setprop start!"
if [ -e /dev/lvpool/userdata ]; then
    echo "Setting lvm_storage to 1!"
    /system/bin/setprop ro.lvm_storage 1
    echo "Set lvm_storage to 1!"
else
    echo "Setting lvm_storage to 0"
    /system/bin/setprop ro.lvm_storage 0
    echo "Set lvm_storage to 0"
fi

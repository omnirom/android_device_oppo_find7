#!/sbin/bb sh
set +x
_PATH="$PATH"
export PATH=/sbin

#don't use -f here - this is not a regular file
#Check if an LV for userdata exists, if it does
#this is an LVM configuration and copy LVM fstabs
#and env.  If it doesn't exist, assume a standard config
if [ -e /dev/lvpool/userdata ]; then
    bb cp /fstab.qcom.lvm /fstab.qcom
    bb cp /etc/twrp.fstab.lvm /etc/twrp.fstab
    bb cp /init.fs.rc.lvm /init.fs.rc
else
    bb cp /fstab.qcom.std /fstab.qcom
    bb cp /etc/twrp.fstab.std /etc/twrp.fstab
    bb cp /init.fs.rc.std /init.fs.rc
fi

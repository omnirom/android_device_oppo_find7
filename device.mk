# This file includes all definitions that apply to ALL find7a devices, and
# are also specific to find7a devices
#
# Everything in this directory will become public

# Include common makefile
$(call inherit-product, device/oppo/msm8974-common/common.mk)

$(call inherit-product, vendor/omni/config/phone-xxxhdpi-3072-dalvik-heap.mk)
$(call inherit-product, vendor/omni/config/phone-xxxhdpi-3072-hwui-memory.mk)

LOCAL_PATH := device/oppo/find7

DEVICE_PACKAGE_OVERLAYS += $(LOCAL_PATH)/overlay

# Ramdisk
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/configs/init.find7.rc:root/init.qcom.rc \
    $(LOCAL_PATH)/configs/init.find7.power.rc:root/init.qcom.power.rc \
    $(LOCAL_PATH)/configs/fstab.find7.lvm:root/fstab.qcom \
    $(LOCAL_PATH)/configs/init.recovery.find7.rc:root/init.recovery.qcom.rc \
    $(LOCAL_PATH)/configs/twrp.fstab.lvm:recovery/root/etc/twrp.fstab.lvm \
    $(LOCAL_PATH)/configs/twrp.fstab.std:recovery/root/etc/twrp.fstab.std

# Audio config files
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/mixer_paths.xml:/system/etc/mixer_paths.xml

# LVM
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/lvm/lvm_init_recovery.sh:recovery/root/sbin/lvm_init_recovery.sh \
    $(LOCAL_PATH)/lvm/lvm:root/sbin/lvm \
    $(LOCAL_PATH)/lvm/lvm.conf:root/lvm/etc/lvm.conf

# nfc
PRODUCT_PACKAGES += \
    libnfc \
    libnfc_jni \
    Nfc

# Sensor configuration from Oppo
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/sensor/sensor_def_qcomdev.conf:system/etc/sensor_def_qcomdev.conf

# Properties
PRODUCT_PROPERTY_OVERRIDES += \
    persist.camera.4k2k.enable=1 \
    ro.qti.sensors.ir_proximity=true \
    persist.audio.fluence.voicecall=true

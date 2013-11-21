# This file includes all definitions that apply to ALL find5 devices, and
# are also specific to find5 devices
#
# Everything in this directory will become public

DEVICE_PACKAGE_OVERLAYS := device/oppo/n1/overlay

# This device is xhdpi.  However the platform doesn't
# currently contain all of the bitmaps at xhdpi density so
# we do this little trick to fall back to the hdpi version
# if the xhdpi doesn't exist.
PRODUCT_AAPT_CONFIG := normal hdpi xhdpi xxhdpi
PRODUCT_AAPT_PREF_CONFIG := xxhdpi

PRODUCT_PACKAGES += \
    charger_res_images \
    charger

# Live Wallpapers
PRODUCT_PACKAGES += \
        LiveWallpapers \
        LiveWallpapersPicker \
        VisualizationWallpapers \
        librs_jni

# Ramdisk
PRODUCT_COPY_FILES += \
        device/oppo/n1/configs/init.n1.rc:root/init.qcom.rc \
        device/oppo/n1/configs/init.n1.usb.rc:root/init.n1.usb.rc \
        device/oppo/n1/configs/ueventd.qcom.rc:root/ueventd.qcom.rc \
        device/oppo/n1/configs/fstab.n1:root/fstab.qcom \
        device/oppo/n1/configs/twrp.fstab:recovery/root/etc/twrp.fstab

# Properties
PRODUCT_PROPERTY_OVERRIDES += \
        ro.opengles.version=131072

PRODUCT_PROPERTY_OVERRIDES += \
        ro.sf.lcd_density=480

$(call inherit-product, frameworks/native/build/phone-xxhdpi-2048-dalvik-heap.mk)
$(call inherit-product, frameworks/native/build/phone-xxhdpi-2048-hwui-memory.mk)


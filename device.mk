# This file includes all definitions that apply to ALL n1 devices, and
# are also specific to n1 devices
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

# Omni Packages
PRODUCT_PACKAGES += \
	OmniTorch

# Ramdisk
PRODUCT_COPY_FILES += \
        device/oppo/n1/configs/init.n1.rc:root/init.qcom.rc \
        device/oppo/n1/configs/init.n1.usb.rc:root/init.n1.usb.rc \
        device/oppo/n1/configs/ueventd.qcom.rc:root/ueventd.qcom.rc \
        device/oppo/n1/configs/fstab.n1:root/fstab.qcom \
        device/oppo/n1/configs/init.qcom.wcnss3660.sh:root/init.qcom.wcnss3660.sh \
        device/oppo/n1/configs/twrp.fstab:recovery/root/etc/twrp.fstab

# Config files for touch and input
PRODUCT_COPY_FILES += \
	device/oppo/n1/configs/keylayout/keypad_8960_liquid.kl:system/usr/keylayout/keypad_8960_liquid.kl \
	device/oppo/n1/configs/keylayout/keypad_8960.kl:system/usr/keylayout/keypad_8960.kl \
	device/oppo/n1/configs/keylayout/Button_Jack.kl:system/usr/keylayout/Button_Jack.kl \
	device/oppo/n1/configs/keylayout/gpio-keys.kl:system/usr/keylayout/gpio-keys.kl \
	device/oppo/n1/configs/keylayout/cyttsp-i2c.kl:system/usr/keylayout/cyttsp-i2c.kl \
	device/oppo/n1/configs/keylayout/atmel_mxt_ts.kl:system/usr/keylayout/atmel_mxt_ts.kl \
	device/oppo/n1/configs/keylayout/ue_rf4ce_remote.kl:system/usr/keylayout/ue_rf4ce_remote.kl \
	device/oppo/n1/configs/keylayout/Vendor_046d_Product_c216.kl:system/usr/keylayout/Vendor_046d_Product_c216.kl \
	device/oppo/n1/configs/keylayout/Vendor_05ac_Product_0239.kl:system/usr/keylayout/Vendor_05ac_Product_0239.kl \
	device/oppo/n1/configs/idc/y8c20x66a-rmi-ts.idc:system/usr/idc/y8c20x66a-rmi-ts.idc

# Audio config files
PRODUCT_COPY_FILES += \
	device/oppo/n1/configs/audio_policy.conf:system/etc/audio_policy.conf \
	device/oppo/n1/media_codecs.xml:system/etc/media_codecs.xml \
	device/oppo/n1/snd_soc_msm/snd_soc_msm_I2SFusion:system/etc/snd_soc_msm/snd_soc_msm_I2SFusion \
	device/oppo/n1/snd_soc_msm/snd_soc_msm_Sitar:system/etc/snd_soc_msm/snd_soc_msm_Sitar \
	device/oppo/n1/snd_soc_msm/snd_soc_msm_auxpcm:system/etc/snd_soc_msm/snd_soc_msm_auxpcm \
	device/oppo/n1/snd_soc_msm/snd_soc_msm_2x:system/etc/snd_soc_msm/snd_soc_msm_2x \
	device/oppo/n1/snd_soc_msm/snd_soc_msm_Sitar_Sglte:system/etc/snd_soc_msm/snd_soc_msm_Sitar_Sglte \
	device/oppo/n1/snd_soc_msm/snd_soc_msm_2x_Fusion3_auxpcm:system/etc/snd_soc_msm/snd_soc_msm_2x_Fusion3_auxpcm \
	device/oppo/n1/snd_soc_msm/snd_soc_msm_Sitar_auxpcm:system/etc/snd_soc_msm/snd_soc_msm_Sitar_auxpcm \
	device/oppo/n1/snd_soc_msm/snd_soc_msm_2x_Fusion3:system/etc/snd_soc_msm/snd_soc_msm_2x_Fusion3 \
	device/oppo/n1/snd_soc_msm/snd_soc_msm_I2S:system/etc/snd_soc_msm/snd_soc_msm_I2S

# qcom init stuff
PRODUCT_COPY_FILES += \
	device/oppo/n1/init.qcom.post_fs.sh:system/etc/init.qcom.post_fs.sh \
	device/oppo/n1/init.qcom.mdm_links.sh:system/etc/init.qcom.mdm_links.sh \
	device/oppo/n1/init.qcom.modem_links.sh:system/etc/init.qcom.modem_links.sh \
	device/oppo/n1/init.qcom.wifi.sh:system/etc/init.qcom.wifi.sh

# Hardware modules to build
PRODUCT_PACKAGES += \
	hwcomposer.msm8960 \
	gralloc.msm8960 \
	audio.primary.msm8960 \
	audio_policy.msm8960 \
	lights.n1


# Properties
# Graphics
PRODUCT_PROPERTY_OVERRIDES += \
        ro.opengles.version=131072 \
        ro.sf.lcd_density=480 \
	persist.hwc.mdpcomp.enable=true

# Ril
PRODUCT_PROPERTY_OVERRIDES += \
	rild.libpath=/system/lib/libril-qc-qmi-1.so \
	rild.libargs=-d /dev/smd0

# Oppo-specific
PRODUCT_DEFAULT_PROPERTY_OVERRIDES += \
	ro.oppo.version=US \
	ro.xxversion=V1.0 \
	ro.bootversion=V1.1

PRODUCT_CHARACTERISTICS := nosdcard

$(call inherit-product, frameworks/native/build/phone-xxhdpi-2048-dalvik-heap.mk)
$(call inherit-product, frameworks/native/build/phone-xxhdpi-2048-hwui-memory.mk)


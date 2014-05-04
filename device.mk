# This file includes all definitions that apply to ALL find7a devices, and
# are also specific to find7a devices
#
# Everything in this directory will become public

DEVICE_PACKAGE_OVERLAYS := device/oppo/find7a/overlay

# This device is xhdpi.  However the platform doesn't
# currently contain all of the bitmaps at xhdpi density so
# we do this little trick to fall back to the hdpi version
# if the xhdpi doesn't exist.
PRODUCT_AAPT_CONFIG := normal hdpi xhdpi xxhdpi
PRODUCT_AAPT_PREF_CONFIG := xxhdpi

PRODUCT_CHARACTERISTICS := nosdcard

PRODUCT_PACKAGES += \
    charger_res_images_find7a \
    charger_find7a

PRODUCT_PACKAGES += \
    com.android.future.usb.accessory

#Default USB mount
PRODUCT_DEFAULT_PROPERTY_OVERRIDES += \
    persist.sys.usb.config=mtp

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
    device/oppo/find7a/configs/init.find7a.rc:root/init.qcom.rc \
    device/oppo/find7a/configs/init.find7a.usb.rc:root/init.find7a.usb.rc \
    device/oppo/find7a/configs/ueventd.qcom.rc:root/ueventd.qcom.rc \
    device/oppo/find7a/configs/fstab.find7a:root/fstab.qcom \
    device/oppo/find7a/configs/twrp.fstab:recovery/root/etc/twrp.fstab

# Config files for touch and input
PRODUCT_COPY_FILES += \
    device/oppo/find7a/configs/keylayout/gpio-keys.kl:system/usr/keylayout/gpio-keys.kl \
    device/oppo/find7a/configs/keylayout/atmel_mxt_ts.kl:system/usr/keylayout/atmel_mxt_ts.kl \
    device/oppo/find7a/configs/keylayout/Vendor_046d_Product_c216.kl:system/usr/keylayout/Vendor_046d_Product_c216.kl \
    device/oppo/find7a/configs/keylayout/Vendor_05ac_Product_0239.kl:system/usr/keylayout/Vendor_05ac_Product_0239.kl

# Media config files
PRODUCT_COPY_FILES += \
    device/oppo/find7a/media_codecs.xml:system/etc/media_codecs.xml \
    device/oppo/find7a/media_profiles.xml:system/etc/media_profiles.xml

# Audio config files
PRODUCT_COPY_FILES += \
    device/oppo/find7a/configs/audio_policy.conf:system/etc/audio_policy.conf \
    device/oppo/find7a/snd_soc_msm/snd_soc_apq_Taiko_DB:system/etc/snd_soc_msm/snd_soc_apq_Taiko_DB \
    device/oppo/find7a/snd_soc_msm/snd_soc_msm_Taiko:system/etc/snd_soc_msm/snd_soc_msm_Taiko \
    device/oppo/find7a/snd_soc_msm/snd_soc_msm_Taiko_CDP:system/etc/snd_soc_msm/snd_soc_msm_Taiko_CDP \
    device/oppo/find7a/snd_soc_msm/snd_soc_msm_Taiko_ES325:system/etc/snd_soc_msm/snd_soc_msm_Taiko_ES325 \
    device/oppo/find7a/snd_soc_msm/snd_soc_msm_Taiko_Fluid:system/etc/snd_soc_msm/snd_soc_msm_Taiko_Fluid \
    device/oppo/find7a/snd_soc_msm/snd_soc_msm_Taiko_liquid:system/etc/snd_soc_msm/snd_soc_msm_Taiko_liquid \
    device/oppo/find7a/snd_soc_msm/snd_soc_msm_Taiko_OnePlus:system/etc/snd_soc_msm/snd_soc_msm_Taiko_OnePlus

# MSM IPC Router security configuration
PRODUCT_COPY_FILES += \
    device/oppo/find7a/configs/sec_config:system/etc/sec_config

# qcom init stuff
PRODUCT_COPY_FILES += \
    device/oppo/find7a/init.qcom.syspart_fixup.sh:system/etc/init.qcom.syspart_fixup.sh \
    device/oppo/find7a/init.qcom.modem_links.sh:system/etc/init.qcom.modem_links.sh \
    device/oppo/find7a/init.qcom.wifi.sh:system/etc/init.qcom.wifi.sh

#thermal-engine
PRODUCT_COPY_FILES += \
        device/oppo/find7a/configs/thermal-engine-8974.conf:system/etc/thermal-engine-8974.conf

# Wifi config
PRODUCT_COPY_FILES += \
    device/oppo/find7a/configs/p2p_supplicant_overlay.conf:system/etc/wifi/p2p_supplicant_overlay.conf \
    device/oppo/find7a/configs/wpa_supplicant_overlay.conf:system/etc/wifi/wpa_supplicant_overlay.conf

# These are the hardware-specific features
PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/handheld_core_hardware.xml:system/etc/permissions/handheld_core_hardware.xml \
    frameworks/native/data/etc/android.hardware.camera.flash-autofocus.xml:system/etc/permissions/android.hardware.camera.flash-autofocus.xml \
    frameworks/native/data/etc/android.hardware.camera.front.xml:system/etc/permissions/android.hardware.camera.front.xml \
    frameworks/native/data/etc/android.hardware.location.gps.xml:system/etc/permissions/android.hardware.location.gps.xml \
    frameworks/native/data/etc/android.hardware.wifi.xml:system/etc/permissions/android.hardware.wifi.xml \
    frameworks/native/data/etc/android.hardware.wifi.direct.xml:system/etc/permissions/android.hardware.wifi.direct.xml \
    frameworks/native/data/etc/android.hardware.sensor.proximity.xml:system/etc/permissions/android.hardware.sensor.proximity.xml \
    frameworks/native/data/etc/android.hardware.sensor.light.xml:system/etc/permissions/android.hardware.sensor.light.xml \
    frameworks/native/data/etc/android.hardware.sensor.gyroscope.xml:system/etc/permissions/android.hardware.sensor.gyroscope.xml \
    frameworks/native/data/etc/android.hardware.sensor.barometer.xml:system/etc/permissions/android.hardware.sensor.barometer.xml \
    frameworks/native/data/etc/android.hardware.touchscreen.multitouch.jazzhand.xml:system/etc/permissions/android.hardware.touchscreen.multitouch.jazzhand.xml \
    frameworks/native/data/etc/android.software.sip.voip.xml:system/etc/permissions/android.software.sip.voip.xml \
    frameworks/native/data/etc/android.hardware.usb.accessory.xml:system/etc/permissions/android.hardware.usb.accessory.xml \
    frameworks/native/data/etc/android.hardware.telephony.gsm.xml:system/etc/permissions/android.hardware.telephony.gsm.xml \
    frameworks/native/data/etc/android.hardware.audio.low_latency.xml:system/etc/permissions/android.hardware.audio.low_latency.xml \
    frameworks/native/data/etc/android.hardware.bluetooth.xml:system/etc/permissions/android.hardware.bluetooth.xml \
    frameworks/native/data/etc/android.hardware.bluetooth_le.xml:system/etc/permissions/android.hardware.bluetooth_le.xml

# Hardware modules to build
PRODUCT_PACKAGES += \
    hwcomposer.msm8974 \
    gralloc.msm8974 \
    copybit.msm8974 \
    memtrack.msm8974 \
    audio.primary.msm8974 \
    audio_policy.msm8974 \
    lights.qcom \
    audio.a2dp.default \
    audio.usb.default \
    audio.r_submix.default \
    camera-wrapper.msm8974 \
    libaudio-resampler

PRODUCT_PACKAGES += \
    libmm-omxcore \
    libdivxdrmdecrypt \
    libOmxVdec \
    libOmxVenc \
    libOmxCore \
    libOmxAacEnc \
    libOmxAmrEnc \
    libOmxEvrcEnc \
    libOmxQcelp13Enc \
    libstagefrighthw \
    libc2dcolorconvert

# wifi
PRODUCT_PACKAGES += \
    mac-update \
    wcnss_service

PRODUCT_COPY_FILES += \
    device/oppo/find7a/configs/init.find7a.bt.sh:system/etc/init.find7a.bt.sh

# NFC packages
PRODUCT_PACKAGES += \
    libnfc \
    libnfc_jni \
    Nfc \
    Tag

# NFC feature files
PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.hardware.nfc.xml:system/etc/permissions/android.hardware.nfc.xml

# GPS configuration
PRODUCT_COPY_FILES += \
    device/oppo/find7a/configs/gps.conf:system/etc/gps.conf

# Properties

# bluetooth
PRODUCT_PROPERTY_OVERRIDES += \
    ro.qualcomm.bt.hci_transport=smd

# Graphics
PRODUCT_PROPERTY_OVERRIDES += \
    ro.opengles.version=131072 \
    ro.sf.lcd_density=480 \
    persist.hwc.mdpcomp.enable=true

# Do not power down SIM card when modem is sent to Low Power Mode.
PRODUCT_PROPERTY_OVERRIDES += \
    persist.radio.apm_sim_not_pwdn=1 \
    persist.radio.eons.enabled=false

# Ril sends only one RIL_UNSOL_CALL_RING, so set call_ring.multiple to false
PRODUCT_PROPERTY_OVERRIDES += \
    ro.telephony.call_ring.multiple=0

# Ril
PRODUCT_PROPERTY_OVERRIDES += \
    rild.libpath=/system/lib/libril-qc-qmi-1.so \
    ril.subscription.types=NV,RUIM \
    ro.telephony.ril_class=OppoRIL

# Cell Broadcasts
PRODUCT_PROPERTY_OVERRIDES += \
    ro.cellbroadcast.emergencyids=0-65534

PRODUCT_PROPERTY_OVERRIDES += \
    telephony.lteOnCdmaDevice=0

PRODUCT_PROPERTY_OVERRIDES += \
    drm.service.enabled=true

PRODUCT_PROPERTY_OVERRIDES += \
    wifi.interface=wlan0 \
    wifi.supplicant_scan_interval=15

# Enable AAC 5.1 output
PRODUCT_PROPERTY_OVERRIDES += \
    media.aac_51_output_enabled=true

PRODUCT_PROPERTY_OVERRIDES += \
    debug.egl.recordable.rgba8888=1

# Oppo-specific
PRODUCT_DEFAULT_PROPERTY_OVERRIDES += \
    ro.oppo.version=US \
    ro.xxversion=V1.0 \
    ro.bootversion=V1.1

# qcom
PRODUCT_PROPERTY_OVERRIDES += \
    ro.qc.sdk.audio.ssr=false \
    ro.qc.sdk.audio.fluencetype=fluence \
    ro.qc.sdk.sensors.gestures=false \
    ro.qc.sdk.camera.facialproc=true \
    ro.qc.sdk.gestures.camera=false

# Audio Configuration
PRODUCT_PROPERTY_OVERRIDES += \
    persist.audio.lowlatency.rec=false \
    af.resampler.quality=4 \
    lpa.decode=false \
    tunnel.decode=false \
    tunnel.audiovideo.decode=true

# QCOM
PRODUCT_PROPERTY_OVERRIDES += \
    com.qc.hardware=true

# QC Perf
PRODUCT_PROPERTY_OVERRIDES += \
    ro.vendor.extension_library=/vendor/lib/libqc-opt.so

# gps
#system prop for switching gps driver to qmi
PRODUCT_PROPERTY_OVERRIDES += \
    persist.gps.qmienabled=true

$(call inherit-product, frameworks/native/build/phone-xxhdpi-2048-dalvik-heap.mk)
$(call inherit-product, frameworks/native/build/phone-xxhdpi-2048-hwui-memory.mk)

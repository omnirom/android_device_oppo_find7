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

# Wifi config
PRODUCT_COPY_FILES += \
	device/oppo/n1/configs/p2p_supplicant_overlay.conf:system/etc/wifi/p2p_supplicant_overlay.conf \
	device/oppo/n1/configs/wpa_supplicant_overlay.conf:system/etc/wifi/wpa_supplicant_overlay.conf

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
        frameworks/native/data/etc/android.hardware.audio.low_latency.xml:system/etc/permissions/android.hardware.audio.low_latency.xml

# Hardware modules to build
PRODUCT_PACKAGES += \
	hwcomposer.msm8960 \
	gralloc.msm8960 \
	audio.primary.msm8960 \
	audio_policy.msm8960 \
	lights.qcom

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


# Properties
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
	ril.subscription.types=NV,RUIM

# Oppo-specific
PRODUCT_DEFAULT_PROPERTY_OVERRIDES += \
	ro.oppo.version=US \
	ro.xxversion=V1.0 \
	ro.bootversion=V1.1

PRODUCT_CHARACTERISTICS := nosdcard

# qcom
PRODUCT_PROPERTY_OVERRIDES += \
	ro.qc.sdk.audio.ssr=false \
	ro.qc.sdk.audio.fluencetype=fluence \
	ro.qc.sdk.sensors.gestures=false

# Audio Configuration
PRODUCT_PROPERTY_OVERRIDES += \
	persist.audio.handset.mic=dmic \
	persist.audio.fluence.mode=endfire \
	persist.audio.lowlatency.rec=false \
	af.resampler.quality=4 \
	lpa.decode=false \
	tunnel.decode=false \
	tunnel.audiovideo.decode=true

$(call inherit-product, frameworks/native/build/phone-xxhdpi-2048-dalvik-heap.mk)
$(call inherit-product, frameworks/native/build/phone-xxhdpi-2048-hwui-memory.mk)


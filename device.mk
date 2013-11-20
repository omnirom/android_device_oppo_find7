$(call inherit-product, $(SRC_TARGET_DIR)/product/languages_full.mk)

# The gps config appropriate for this device
$(call inherit-product, device/common/gps/gps_us_supl.mk)

$(call inherit-product-if-exists, vendor/oppo/N1/N1-vendor.mk)

DEVICE_PACKAGE_OVERLAYS += device/oppo/N1/overlay

LOCAL_PATH := device/oppo/N1
ifeq ($(TARGET_PREBUILT_KERNEL),)
	LOCAL_KERNEL := $(LOCAL_PATH)/kernAl
else
	LOCAL_KERNEL := $(TARGET_PREBUILT_KERNEL)
endif

PRODUCT_COPY_FILES += \
    $(LOCAL_KERNEL):kernel

$(call inherit-product, build/target/product/full.mk)

PRODUCT_NAME := omni_N1
PRODUCT_DEVICE := N1
PRODUCT_BRAND := Oppo
PRODUCT_MODEL := N1

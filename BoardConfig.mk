# inherit common device tree
-include device/oppo/msm8974-common/BoardConfigCommon.mk

LOCAL_PATH := device/oppo/find7

TARGET_NO_BOOTLOADER := true
TARGET_BOOTLOADER_BOARD_NAME := find7
TARGET_OTA_ASSERT_DEVICE := none

# Partition info
BOARD_BOOTIMAGE_PARTITION_SIZE := 0x00F00000
BOARD_RECOVERYIMAGE_PARTITION_SIZE := 0x00F00000
BOARD_SYSTEMIMAGE_PARTITION_SIZE := 1388314624
BOARD_USERDATAIMAGE_PARTITION_SIZE := 3221225472
BOARD_FLASH_BLOCK_SIZE := 131072

# Bluetooth
BOARD_BLUETOOTH_BDROID_BUILDCFG_INCLUDE_DIR := $(LOCAL_PATH)/bluetooth

#Libinit - handle find7a vs find7s
TARGET_INIT_VENDOR_LIB := libinit_find7

# MUST NOT USE LOCAL_PATH
BOARD_SEPOLICY_DIRS += \
    device/oppo/find7/sepolicy

# Recovery:Start

#TODO: Need to determine just how this is used.  There's a slight
#chance this could cause some small issues on LVM configs
#but overall, TWRP doesn't use this and the rest of the build system
#does
TARGET_RECOVERY_FSTAB := $(LOCAL_PATH)/configs/fstab.find7.lvm

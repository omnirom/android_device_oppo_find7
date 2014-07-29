# this creates a regular ramdisk that has copies of both the 1080x1920 and 1440x2560
# TWRP themes in it for a combined recovery that works for both
# Initial work by Dees_Troy
FIND7_RECOVERY_ROOT := out/target/product/find7/recovery/root/

$(INSTALLED_RECOVERYIMAGE_TARGET): $(MKBOOTIMG) \
		$(recovery_ramdisk) \
		$(recovery_kernel)
	@echo ----- Copying 1440x2560 resources ------
	$(hide) rm -rf $(FIND7_RECOVERY_ROOT)qhdres
	$(hide) cp -R bootable/recovery/gui/devices/1440x2560/res $(FIND7_RECOVERY_ROOT)/qhdres
	@echo ----- Making recovery ramdisk ------
	$(hide) (cd $(FIND7_RECOVERY_ROOT) && find * | sort | cpio -o -H newc) | gzip > $(recovery_ramdisk)
	@echo ----- Making recovery image ------
	$(MKBOOTIMG) $(INTERNAL_RECOVERYIMAGE_ARGS) --output $@
	@echo ----- Made recovery image -------- $@
	$(hide) $(call assert-max-image-size,$@,$(BOARD_RECOVERYIMAGE_PARTITION_SIZE),raw)


$(INSTALLED_BOOTIMAGE_TARGET): $(MKBOOTIMG) $(INTERNAL_BOOTIMAGE_FILES)
	$(call pretty,"Target boot image: $@")
	$(hide) $(MKBOOTIMG) $(INTERNAL_BOOTIMAGE_ARGS) --output $@
	$(hide) $(call assert-max-image-size,$@,$(BOARD_BOOTIMAGE_PARTITION_SIZE),raw)

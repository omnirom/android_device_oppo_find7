ifneq ($(filter N1,$(TARGET_DEVICE)),)
    include $(all-subdir-makefiles)
endif

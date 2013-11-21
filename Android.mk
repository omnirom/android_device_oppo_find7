ifneq ($(filter n1,$(TARGET_DEVICE)),)
    include $(all-subdir-makefiles)
endif

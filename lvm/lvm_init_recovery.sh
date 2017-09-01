#!/sbin/busybox sh
/sbin/lvm vgscan --mknodes --ignorelockingfailure
/sbin/lvm vgchange -aly --ignorelockingfailure

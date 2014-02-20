/*
 *  Copyright (C) 2014 The OmniROM Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

/* this will patch the MAC address in the wlan config file */

#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>

static char mac_buf[6];
static char mac_string[256];

#define NV_IN "/system/etc/wifi/WCNSS_qcom_cfg.ini"
#define NV_OUT "/system/etc/firmware/wlan/prima/WCNSS_qcom_cfg.ini"

#define MAC_FILE "/data/opponvitems/4678"
#define MAC_STRING_TAG "Intf0MacAddress="

int read_mac(const char *filename)
{
    int ret;
    int i = 5;
    FILE *f = fopen(filename, "r");
    if (!f)
        return -ENOENT;

     while ((ret = getc(f)) > 0 && i >= 0) {
        mac_buf[i] = ret;
        i--;
    }
    fclose(f);
    sprintf(mac_string, "%02X%02X%02X%02X%02X%02X", mac_buf[0], mac_buf[1], mac_buf[2], mac_buf[3], mac_buf[4], mac_buf[5]);

    return 0;
}

int main(int argc, char **argv)
{
    struct stat statbuf;
    int ret;
    size_t actual;
    FILE *f;
    char *buf;

    if (read_mac(MAC_FILE)) {
        perror("Failed to read MAC");
        exit(EINVAL);
    }

    ret = stat(NV_IN, &statbuf);
    if (ret) {
        perror("Failed to stat " NV_IN);
        exit(EINVAL);
    }
    f = fopen(NV_IN, "r");
    if (!f) {
        perror("Failed to open " NV_IN);
        exit(EINVAL);
    }

    buf = malloc(statbuf.st_size);
    if (!buf) {
        perror("malloc failed");
        exit(ENOMEM);
    }

    actual = fread(buf, 1, statbuf.st_size, f);
    if (actual != statbuf.st_size) {
        perror("Failed to read from nv");
        exit(EINVAL);
    }
    fclose(f);

    char *p = strstr(buf, MAC_STRING_TAG);
    if (p == NULL){
        perror("Failed to locate " MAC_STRING_TAG);
        exit(EINVAL);
    }

    strncpy(p + strlen(MAC_STRING_TAG), mac_string, 12);

    f = fopen(NV_OUT, "w");
    if (!f) {
        perror("Failed to open " NV_OUT);
        exit(EINVAL);
    }

    actual = fwrite(buf, 1, statbuf.st_size, f);
    if (actual != statbuf.st_size) {
        perror("Failed to write to nv");
        exit(EINVAL);
    }

    fclose(f);

    return 0;
}

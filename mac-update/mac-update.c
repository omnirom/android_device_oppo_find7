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

static char mac_string[256];

#define NV_IN "/system/etc/wifi/WCNSS_qcom_cfg.ini"
#define NV_OUT "/system/etc/firmware/wlan/prima/WCNSS_qcom_cfg.ini"

#define MAC_FILE "/data/opponvitems/4678"
#define EMPTY_MAC "000000000000"

/*
 * Station Mode MAC Address
 */
#define WFC_UTIL_CFG_TAG_MAC_ADDRESS    "Intf0MacAddress="
#define WFC_UTIL_CFG_TAG_P2P_ADDRESS    "Intf1MacAddress="
/*
 * AP Mode MAC Address
 */
#define WFC_UTIL_CFG_TAG_AP_MAC_ADDRESS "gAPMacAddr="

int read_mac(const char *filename)
{
    char raw[6];
    char mac[6];
    FILE *fp = NULL;
    int ret;

    memset(raw, 0, 6);
    memset(mac, 0, 6);

    fp = fopen(filename, "r");
    if (fp == NULL)
        return ENOENT;

    ret = fread(raw, 6, 1, fp);

    // swap bytes
    mac[0] = raw[5];
    mac[1] = raw[4];
    mac[2] = raw[3];
    mac[3] = raw[2];
    mac[4] = raw[1];
    mac[5] = raw[0];

    sprintf(mac_string, "%02X%02X%02X%02X%02X%02X", mac[0], mac[1], mac[2], mac[3], mac[4], mac[5]);

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

    if (!strcmp(mac_string, EMPTY_MAC)) {
        perror("MAC empty");
        exit(EINVAL);
    }
    
    fprintf(stderr, "Found MAC address %s\n", mac_string);
    
    if (argc == 2 && !strcmp(argv[1], "-v")){
        exit(0);
    }

    ret = stat(NV_IN, &statbuf);
    if (ret) {
        perror("Failed to stat " NV_IN);
        exit(ENOENT);
    }

    f = fopen(NV_IN, "r");
    if (!f) {
        perror("Failed to open " NV_IN);
        exit(ENOENT);
    }

    buf = malloc(statbuf.st_size);
    if (!buf) {
        perror("malloc failed");
        exit(ENOMEM);
    }

    actual = fread(buf, 1, statbuf.st_size, f);
    if (actual != statbuf.st_size) {
        perror("Failed to read from nv");
        exit(ENOENT);
    }
    fclose(f);

    char *p = strstr(buf, WFC_UTIL_CFG_TAG_MAC_ADDRESS);
    if (p == NULL){
        perror("Failed to locate " WFC_UTIL_CFG_TAG_MAC_ADDRESS);
        exit(EINVAL);
    }
    strncpy(p + strlen(WFC_UTIL_CFG_TAG_MAC_ADDRESS), mac_string, 12);

    p = strstr(buf, WFC_UTIL_CFG_TAG_P2P_ADDRESS);
    if (p == NULL){
        perror("Failed to locate " WFC_UTIL_CFG_TAG_P2P_ADDRESS);
        exit(EINVAL);
    }
    strncpy(p + strlen(WFC_UTIL_CFG_TAG_P2P_ADDRESS), mac_string, 12);

    p = strstr(buf, WFC_UTIL_CFG_TAG_AP_MAC_ADDRESS);
    if (p == NULL){
        perror("Failed to locate " WFC_UTIL_CFG_TAG_AP_MAC_ADDRESS);
        exit(EINVAL);
    }
    strncpy(p + strlen(WFC_UTIL_CFG_TAG_AP_MAC_ADDRESS), mac_string, 12);

    f = fopen(NV_OUT, "w");
    if (!f) {
        perror("Failed to open " NV_OUT);
        exit(ENOENT);
    }

    actual = fwrite(buf, 1, statbuf.st_size, f);
    if (actual != statbuf.st_size) {
        perror("Failed to write to nv");
        exit(ENOENT);
    }

    fclose(f);

    return 0;
}

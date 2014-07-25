/*
 * Copyright (C) 2008 The Android Open Source Project
 * Copyright (C) 2013 The CyanogenMod Project
 * Copyright (C) 2013 The OmniROM Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


#define LOG_NDEBUG 0
#define LOG_TAG "lights"

#include <cutils/log.h>
#include <cutils/properties.h>
#include <stdint.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <fcntl.h>
#include <pthread.h>

#include <sys/ioctl.h>
#include <sys/types.h>

#include <hardware/lights.h>

/******************************************************************************/

static pthread_once_t g_init = PTHREAD_ONCE_INIT;
static pthread_mutex_t g_lock = PTHREAD_MUTEX_INITIALIZER;
static struct light_state_t g_notification;
static struct light_state_t g_battery;
static struct light_state_t g_attention;
static int g_is_find7a = 0;

char const*const LCD_FILE
        = "/sys/class/leds/lcd-backlight/brightness";

char const*const BUTTONS_FILE
        = "/sys/class/leds/button-backlight/brightness";

char const*const RGB_BLUE_BLINK_FILE
        = "/sys/class/leds/rgb_blue/blink";

char const*const RGB_BLUE_LED_FILE
        = "/sys/class/leds/rgb_blue/brightness";

char const*const BLUE_BLINK_FILE
        = "/sys/class/leds/blue/device/blink";

char const*const BLUE_LED_FILE
        = "/sys/class/leds/blue/brightness";

#define LED_BRIGHTNESS 128

/**
 * device methods
 */

void init_globals(void)
{
    // init the mutex
    pthread_mutex_init(&g_lock, NULL);
}

static int
is_find7a()
{
    char value[PROPERTY_VALUE_MAX] = {'\0'};

    if (property_get("ro.oppo.device", value, NULL)) {
        if (!strcmp(value, "find7a")) {
            return 1;
        }
    }
    return 0;
}

static int
read_int(char const* path)
{
    int fd;
    fd = open(path, O_RDONLY);
    if (fd >= 0) {
        char buffer[5] = {0,0,0,0,0};
        read(fd, buffer, 5);
        close(fd);

        return atoi(buffer);
    } else {
        ALOGE("Error reading path %s", path);
        return 0;
    }
}

static int
write_int(char const* path, int value)
{
    int fd;
    static int already_warned = 0;

    fd = open(path, O_RDWR);
    if (fd >= 0) {
        char buffer[20];
        int bytes = sprintf(buffer, "%d\n", value);
#if 0
        ALOGD("write_int %s %d\n", path, value);
#endif
        int amt = write(fd, buffer, bytes);
        close(fd);
        return amt == -1 ? -errno : 0;
    } else {
        if (already_warned == 0) {
            ALOGE("write_int failed to open %s\n", path);
            already_warned = 1;
        }
        return -errno;
    }
}

static int
is_lit(struct light_state_t const* state)
{
    return state->color & 0x00ffffff;
}

static int
rgb_to_brightness(struct light_state_t const* state)
{
    int color = state->color & 0x00ffffff;
    return ((77*((color>>16)&0x00ff))
            + (150*((color>>8)&0x00ff)) + (29*(color&0x00ff))) >> 8;
}

static int
set_light_backlight(struct light_device_t* dev,
        struct light_state_t const* state)
{
    int err = 0;
    int brightness = rgb_to_brightness(state);

    pthread_mutex_lock(&g_lock);
    err = write_int(LCD_FILE, brightness);
    pthread_mutex_unlock(&g_lock);

    return err;
}

static int
set_speaker_light_locked(struct light_device_t* dev,
        struct light_state_t const* state)
{
    int blue = 0;
    int blink = 0;
    int onMS = 0;
    int offMS = 0;
    unsigned int colorRGB = 0;

    if(state != NULL) {
        switch (state->flashMode) {
            case LIGHT_FLASH_TIMED:
                onMS = state->flashOnMS;
                offMS = state->flashOffMS;
                break;
            case LIGHT_FLASH_NONE:
            default:
                break;
        }

        colorRGB = state->color;

#if 0
        ALOGD("set_speaker_light_locked mode %d, colorRGB=%08X, onMS=%d, offMS=%d\n",
                state->flashMode, colorRGB, onMS, offMS);
#endif

        if (colorRGB)
            blue = LED_BRIGHTNESS;
        else
            blue = 0;

        if (onMS > 0 && offMS > 0) {
            blink = 1;
            blue = 0;
        } else {
            blink = 0;
        }
    }
#if 0
    ALOGD("set_speaker_light_locked %d %d\n", blue, blink);
#endif
    // blink
    if (g_is_find7a)
        write_int(BLUE_BLINK_FILE, blink);
    else
        write_int(RGB_BLUE_BLINK_FILE, blink);

    // solid
    if (!blink && blue)
        if (g_is_find7a)
            write_int(BLUE_LED_FILE, blue);
        else
            write_int(RGB_BLUE_LED_FILE, blue);
    return 0;
}

static void
handle_speaker_battery_locked(struct light_device_t* dev,
    struct light_state_t const* state, int state_type)
{
    if(is_lit(&g_attention)) {
        set_speaker_light_locked(dev, NULL);
        set_speaker_light_locked(dev, &g_attention);
    } else {
        if(is_lit(&g_battery) && is_lit(&g_notification)) {
            set_speaker_light_locked(dev, NULL);
            set_speaker_light_locked(dev, &g_notification);
        } else if(is_lit(&g_battery)) {
            set_speaker_light_locked(dev, NULL);
            set_speaker_light_locked(dev, &g_battery);
        } else {
            set_speaker_light_locked(dev, &g_notification);
        }
    }

}

static int
set_light_battery(struct light_device_t* dev,
        struct light_state_t const* state)
{
    pthread_mutex_lock(&g_lock);
    g_battery = *state;
    handle_speaker_battery_locked(dev, state, 0);
    pthread_mutex_unlock(&g_lock);
    return 0;
}

static int
set_light_notifications(struct light_device_t* dev,
        struct light_state_t const* state)
{
    pthread_mutex_lock(&g_lock);
    g_notification = *state;
    handle_speaker_battery_locked(dev, state, 1);
    pthread_mutex_unlock(&g_lock);
    return 0;
}

static int
set_light_attention(struct light_device_t* dev,
        struct light_state_t const* state)
{
    pthread_mutex_lock(&g_lock);
    g_attention = *state;
    /*
     * attention logic tweaks from:
     * https://github.com/CyanogenMod/android_device_samsung_d2-common/commit/6886bdbbc2417dd605f9818af2537c7b58491150
    */
    if (state->flashMode == LIGHT_FLASH_HARDWARE) {
        if (g_attention.flashOnMS > 0 && g_attention.flashOffMS == 0) {
            g_attention.flashMode = LIGHT_FLASH_NONE;
        }
    } else if (state->flashMode == LIGHT_FLASH_NONE) {
        g_attention.color = 0;
    }
    handle_speaker_battery_locked(dev, state, 2);
    pthread_mutex_unlock(&g_lock);
    return 0;
}

static int
set_light_touchkeys(struct light_device_t* dev,
        struct light_state_t const* state)
{
    int err = 0;
    int brightness = rgb_to_brightness(state);

    pthread_mutex_lock(&g_lock);
    write_int(BUTTONS_FILE, brightness);
    pthread_mutex_unlock(&g_lock);
    return err;
}


/** Close the lights device */
static int
close_lights(struct light_device_t *dev)
{
    if (dev) {
        free(dev);
    }
    return 0;
}


/******************************************************************************/

/**
 * module methods
 */

/** Open a new instance of a lights device using name */
static int open_lights(const struct hw_module_t* module, char const* name,
        struct hw_device_t** device)
{
    int (*set_light)(struct light_device_t* dev,
            struct light_state_t const* state);

    if (0 == strcmp(LIGHT_ID_BACKLIGHT, name))
        set_light = set_light_backlight;
    else if (0 == strcmp(LIGHT_ID_NOTIFICATIONS, name))
        set_light = set_light_notifications;
    else if (0 == strcmp(LIGHT_ID_BATTERY, name))
        set_light = set_light_battery;
    else if (0 == strcmp(LIGHT_ID_ATTENTION, name))
        set_light = set_light_attention;
    else if (0 == strcmp(LIGHT_ID_BUTTONS, name))
        set_light = set_light_touchkeys;
    else
        return -EINVAL;

    pthread_once(&g_init, init_globals);

    struct light_device_t *dev = malloc(sizeof(struct light_device_t));
    memset(dev, 0, sizeof(*dev));

    dev->common.tag = HARDWARE_DEVICE_TAG;
    dev->common.version = 0;
    dev->common.module = (struct hw_module_t*)module;
    dev->common.close = (int (*)(struct hw_device_t*))close_lights;
    dev->set_light = set_light;

    *device = (struct hw_device_t*)dev;

    g_is_find7a = is_find7a();

    return 0;
}

static struct hw_module_methods_t lights_module_methods = {
    .open =  open_lights,
};

/*
 * The lights Module
 */
struct hw_module_t HAL_MODULE_INFO_SYM = {
    .tag = HARDWARE_MODULE_TAG,
    .version_major = 1,
    .version_minor = 0,
    .id = LIGHTS_HARDWARE_MODULE_ID,
    .name = "find7 lights module",
    .author = "Google, Inc., OmniROM",
    .methods = &lights_module_methods,
};

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
package org.omnirom.omniclick;

import java.util.UUID;

public class OClickGattAttributes {

    public static UUID LINK_LOSS_UUID = UUID
            .fromString("00001803-0000-1000-8000-00805f9b34fb");
    public static UUID LINK_LOSS_CHAR_UUID = UUID
            .fromString("00001803-0000-1000-8000-00805f9b34fb");
    public static UUID OPPO_OTOUCH_UUID = UUID
            .fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    public static UUID OPPO_OTOUCH_CLICK1_UUID = UUID
            .fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    public static UUID OPPO_OTOUCH_CLICK2_UUID = UUID
            .fromString("f000ffe1-0451-4000-b000-000000000000");
    public static UUID IMMEDIATE_ALERT_UUID = UUID
            .fromString("00001802-0000-1000-8000-00805f9b34fb"); // 0-2
    public static UUID IMMEDIATE_ALERT_CHAR_UUID = UUID
            .fromString("00002a06-0000-1000-8000-00805f9b34fb");
}

/*
 * Copyright (c) 2016 - 2018 Rui Zhao <renyuneyun@gmail.com>
 *
 * This file is part of Easer.
 *
 * Easer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Easer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Easer.  If not, see <http://www.gnu.org/licenses/>.
 */

package ryey.easer.plugins.event.bluetooth_device;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import ryey.easer.R;
import ryey.easer.Utils;
import ryey.easer.commons.local_plugin.IllegalStorageDataException;
import ryey.easer.commons.local_plugin.dynamics.Dynamics;
import ryey.easer.plugin.PluginDataFormat;
import ryey.easer.plugins.event.AbstractEventData;


public class BTDeviceEventData extends AbstractEventData {
    private List<String> hwaddresses = new ArrayList<>();

    BTDeviceEventData(String[] hardware_addresses) {
        setMultiple(hardware_addresses);
    }

    BTDeviceEventData(@NonNull String data, @NonNull PluginDataFormat format, int version) throws IllegalStorageDataException {
        parse(data, format, version);
    }

    private void setMultiple(String[] hardware_addresses) {
        for (String hardware_address : hardware_addresses) {
            if (!Utils.isBlank(hardware_address))
                this.hwaddresses.add(hardware_address.trim());
        }
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        boolean is_first = true;
        for (String hwaddress : hwaddresses) {
            if (!is_first)
                text.append("\n");
            text.append(hwaddress);
            is_first = false;
        }
        return text.toString();
    }

    @SuppressWarnings({"SimplifiableIfStatement", "RedundantIfStatement"})
    @Override
    public boolean isValid() {
        if (hwaddresses.size() == 0)
            return false;
        return true;
    }

    @Nullable
    @Override
    public Dynamics[] dynamics() {
        return new Dynamics[]{new DeviceNameDynamics(), new DeviceAddressDynamics()};
    }

    @SuppressWarnings({"SimplifiableIfStatement", "RedundantIfStatement"})
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof BTDeviceEventData))
            return false;
        if (!hwaddresses.equals(((BTDeviceEventData) obj).hwaddresses))
            return false;
        return true;
    }

    public void parse(@NonNull String data, @NonNull PluginDataFormat format, int version) throws IllegalStorageDataException {
        hwaddresses.clear();
        switch (format) {
            default:
                try {
                    JSONArray jsonArray = new JSONArray(data);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String hwaddr = jsonArray.getString(i);
                        hwaddresses.add(hwaddr);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new IllegalStorageDataException(e);
                }
        }
    }

    @NonNull
    @Override
    public String serialize(@NonNull PluginDataFormat format) {
        String res;
        switch (format) {
            default:
                JSONArray jsonArray = new JSONArray();
                for (String hwaddr : hwaddresses) {
                    jsonArray.put(hwaddr);
                }
                res = jsonArray.toString();
        }
        return res;
    }

    public boolean match(@NonNull Object obj) {
        if (obj instanceof String) {
            return hwaddresses.contains(((String) obj).trim());
        }
        return equals(obj);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(hwaddresses);
    }

    public static final Parcelable.Creator<BTDeviceEventData> CREATOR
            = new Parcelable.Creator<BTDeviceEventData>() {
        public BTDeviceEventData createFromParcel(Parcel in) {
            return new BTDeviceEventData(in);
        }

        public BTDeviceEventData[] newArray(int size) {
            return new BTDeviceEventData[size];
        }
    };

    private BTDeviceEventData(Parcel in) {
        in.readStringList(hwaddresses);
    }

    static class DeviceNameDynamics implements Dynamics {

        static final String id = "ryey.easer.plugins.event.bluetooth_device.device_name";

        @Override
        public String id() {
            return id;
        }

        @Override
        public int nameRes() {
            return R.string.ebtdevice_dynamics_device_name;
        }
    }

    static class DeviceAddressDynamics implements Dynamics {

        static final String id = "ryey.easer.plugins.event.bluetooth_device.device_address";

        @Override
        public String id() {
            return id;
        }

        @Override
        public int nameRes() {
            return R.string.ebtdevice_dynamics_device_address;
        }
    }
}

package me.mrgazdag.hibiscus.defaultplugin;

import me.mrgazdag.hibiscus.library.ui.property.filters.DevicePropertyFilter;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;

import java.util.Map;
import java.util.WeakHashMap;

public class TextHandler implements DevicePropertyFilter<String> {
    private final Map<ConnectedDevice, String> inputs;

    public TextHandler() {
        this.inputs = new WeakHashMap<>();
    }

    public void put(ConnectedDevice device, String text) {
        this.inputs.put(device, text);
    }

    @Override
    public boolean test(ConnectedDevice device) {
        return inputs.containsKey(device);
    }

    @Override
    public String apply(ConnectedDevice device) {
        return inputs.get(device);
    }
}

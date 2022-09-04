package me.mrgazdag.hibiscus.defaultplugin;

import me.mrgazdag.hibiscus.library.ui.component.BlockLayoutComponent;
import me.mrgazdag.hibiscus.library.ui.component.ButtonComponent;
import me.mrgazdag.hibiscus.library.ui.component.TextBoxComponent;
import me.mrgazdag.hibiscus.library.ui.component.TextInputComponent;
import me.mrgazdag.hibiscus.library.ui.property.filters.DevicePropertyFilter;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

public class InputTester {
    private final Map<ConnectedDevice, InputTesterInstance> instances;
    private final ButtonComponent button1;
    private final ButtonComponent button2;
    private final ButtonComponent button3;

    private final TextBoxComponent modeText;
    private final TextBoxComponent valueText;

    private final TextInputComponent textInput;

    public InputTester(ButtonComponent button1, ButtonComponent button2, ButtonComponent button3, TextBoxComponent modeText, TextBoxComponent valueText, TextInputComponent textInput) {
        this.button1 = button1;
        this.button2 = button2;
        this.button3 = button3;
        this.modeText = modeText;
        this.valueText = valueText;
        this.textInput = textInput;

        this.instances = new WeakHashMap<>();

        button1.onPress().setHandler((device, unused)->setMode(device, TextInputComponent.UpdateEventType.EVERY_KEY));
        button2.onPress().setHandler((device, unused)->setMode(device, TextInputComponent.UpdateEventType.AFTER_CHANGE));
        button3.onPress().setHandler((device, unused)->setMode(device, TextInputComponent.UpdateEventType.AFTER_CHANGE_TIMEOUT));

        button1.getButtonText().setDefaultValue("Every Key");
        button2.getButtonText().setDefaultValue("After Changes");
        button3.getButtonText().setDefaultValue("After Changes (with timeout)");

        modeText.getText().setDefaultValue("Default mode: AFTER_CHANGES");
        modeText.getText().addDeviceFilter(new DevicePropertyFilter<>() {
            @Override
            public boolean test(ConnectedDevice device) {
                return instances.containsKey(device) && instances.get(device).getMode() != null;
            }

            @Override
            public String apply(ConnectedDevice device) {
                return "Current mode: " + instances.get(device).getMode().name();
            }
        });

        valueText.getText().setDefaultValue("No input received.");
        valueText.getText().addDeviceFilter(new DevicePropertyFilter<>() {
            @Override
            public boolean test(ConnectedDevice device) {
                return instances.containsKey(device) && instances.get(device).getInput() != null;
            }

            @Override
            public String apply(ConnectedDevice device) {
                return instances.get(device).getInput();
            }
        });

        textInput.getUpdateEventType().addDeviceFilter(new DevicePropertyFilter<>() {
            @Override
            public boolean test(ConnectedDevice device) {
                return instances.containsKey(device) && instances.get(device).getMode() != null;
            }

            @Override
            public TextInputComponent.UpdateEventType apply(ConnectedDevice device) {
                return instances.get(device).getMode();
            }
        });
        textInput.onInputChange().setHandler(this::setText);
    }

    private InputTesterInstance getOrCreate(ConnectedDevice device) {
        return this.instances.computeIfAbsent(device, InputTesterInstance::new);
    }

    public void setMode(ConnectedDevice device, TextInputComponent.UpdateEventType mode) {
        System.out.println("Setting mode " + device + " " + mode);
        getOrCreate(device).setMode(mode);
    }

    public void setText(ConnectedDevice device, String text) {
        System.out.println("Setting text " + device + " " + text);
        getOrCreate(device).setText(text);
    }

    public void layout(BlockLayoutComponent layout) {
        layout.addChild(modeText);
        layout.addChild(button1);
        layout.addChild(button2);
        layout.addChild(button3);
        layout.addChild(valueText);
        layout.addChild(textInput);
    }

    private class InputTesterInstance {
        private final WeakReference<ConnectedDevice> device;
        private TextInputComponent.UpdateEventType mode;
        private String input;
        private InputTesterInstance(ConnectedDevice device) {
            this.device = new WeakReference<>(device);
        }

        public void setText(String input) {
            this.input = input;
            valueText.getText().sendUpdate(device.get());
        }

        public void setMode(TextInputComponent.UpdateEventType mode) {
            this.mode = mode;
            textInput.getUpdateEventType().sendUpdate(device.get());
            modeText.getText().sendUpdate(device.get());
        }

        public String getInput() {
            return input;
        }

        public TextInputComponent.UpdateEventType getMode() {
            return mode;
        }
    }
}

package org.example2.onegai.client;

public final class ClientFeatureToggle {
    private static boolean enabled = true;

    private ClientFeatureToggle() {
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static boolean toggle() {
        enabled = !enabled;
        return enabled;
    }

    public static void setEnabled(boolean value) {
        enabled = value;
    }
}

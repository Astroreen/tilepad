package me.astroreen.tilepad.model;

import java.util.ArrayList;
import java.util.List;

public class AppConfig {
    private String configPath = "";
    private String activeProfile = "default";
    private List<ProfileConfig> profiles = new ArrayList<>();
    private double windowX = 0;
    private double windowY = 0;
    private double windowWidth = 1280;
    private double windowHeight = 800;

    public AppConfig() {}

    /**
     * Creates a default AppConfig with one empty "default" profile.
     * Called when no config file exists yet.
     */
    public static AppConfig createDefault() {
        AppConfig config = new AppConfig();
        config.activeProfile = "default";
        config.windowWidth = 1280;
        config.windowHeight = 800;
        ProfileConfig defaultProfile = new ProfileConfig("default");
        defaultProfile.setGridColumns(6);
        List<ProfileConfig> profiles = new ArrayList<>();
        profiles.add(defaultProfile);
        config.profiles = profiles;
        return config;
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public String getActiveProfile() {
        return activeProfile;
    }

    public void setActiveProfile(String activeProfile) {
        this.activeProfile = activeProfile;
    }

    public List<ProfileConfig> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<ProfileConfig> profiles) {
        this.profiles = profiles;
    }

    public double getWindowX() {
        return windowX;
    }

    public void setWindowX(double windowX) {
        this.windowX = windowX;
    }

    public double getWindowY() {
        return windowY;
    }

    public void setWindowY(double windowY) {
        this.windowY = windowY;
    }

    public double getWindowWidth() {
        return windowWidth;
    }

    public void setWindowWidth(double windowWidth) {
        this.windowWidth = windowWidth;
    }

    public double getWindowHeight() {
        return windowHeight;
    }

    public void setWindowHeight(double windowHeight) {
        this.windowHeight = windowHeight;
    }
}

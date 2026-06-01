package me.astroreen.tilepad.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AppConfig {
    private String configPath = "";
    private String activeProfile = "default";
    private List<ProfileConfig> profiles = new ArrayList<>();
    private double windowX = 0;
    private double windowY = 0;
    private double windowWidth = 1280;
    private double windowHeight = 800;

    /**
     * Creates a default AppConfig with one empty "default" profile.
     * Called when no config file exists yet.
     */
    public static AppConfig createDefault() {
        final String DEFAULT_PROFILE_NAME = "default";
        AppConfig config = new AppConfig();
        config.activeProfile = DEFAULT_PROFILE_NAME;
        config.windowWidth = 1280;
        config.windowHeight = 800;
        ProfileConfig defaultProfile = new ProfileConfig(DEFAULT_PROFILE_NAME);
        defaultProfile.setGridColumns(6);
        List<ProfileConfig> profiles = new ArrayList<>();
        profiles.add(defaultProfile);
        config.profiles = profiles;
        return config;
    }
}

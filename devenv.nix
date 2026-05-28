{ pkgs, lib, ... }:

{
  # 1. Ensure the correct JDK and Maven are present
  languages.java = {
    enable = true;
    jdk.package = pkgs.jdk25;
  };

  packages = with pkgs; [
    maven
  ];

  # 2. Define the library path for JavaFX's unpatched binaries
  env.LD_LIBRARY_PATH = "${lib.makeLibraryPath (
    with pkgs;
    [
      libXxf86vm
      libXtst
      libX11
      libXrender
      gtk3
      libGL
      glib
      fontconfig
      freetype
    ]
  )}:${pkgs.javaPackages.openjfx25}/modules_libs";
  env.JAVA_MODULE_PATH = "${pkgs.javaPackages.openjfx25}/modules";
  env.JAVA_LIBRARY_PATH = "${pkgs.javaPackages.openjfx25}/modules_libs";

  # Optional: Print a nice message when you enter the shell
  enterShell = ''
    echo "🚀 JavaFX Development Environment (NixOS) Loaded"
    echo "LD_LIBRARY_PATH is set for native JFX libraries."
    mkdir -p .devenv
    ln -sfn ${pkgs.javaPackages.openjfx25}/modules .devenv/javafx-modules
  '';
}

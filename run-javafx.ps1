$ErrorActionPreference = "Stop"

$javaHome = "C:\Program Files\JetBrains\IntelliJ IDEA 2025.3.2\jbr\bin"
$javaFxModulePath = @(
    "$env:USERPROFILE\.m2\repository\org\openjfx\javafx-base\17\javafx-base-17-win.jar",
    "$env:USERPROFILE\.m2\repository\org\openjfx\javafx-graphics\17\javafx-graphics-17-win.jar",
    "$env:USERPROFILE\.m2\repository\org\openjfx\javafx-controls\17\javafx-controls-17-win.jar"
) -join ";"

& "$javaHome\javac.exe" `
    --module-path $javaFxModulePath `
    --add-modules javafx.controls `
    -d out `
    src\*.java

& "$javaHome\java.exe" `
    --module-path $javaFxModulePath `
    --add-modules javafx.controls `
    -cp out `
    Main

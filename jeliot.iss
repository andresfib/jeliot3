; -- Example1.iss --

; SEE THE DOCUMENTATION FOR DETAILS ON CREATING .ISS SCRIPT FILES!

[Setup]
AppName=Jeliot 3
AppVerName=Jeliot 3-2P2
DefaultDirName={pf}\Jeliot3
DefaultGroupName=Jeliot3
;UninstallDisplayIcon={app}\MyProg.exe
OutputDir=Release
[Dirs]
Name: "{app}\images"
Name: "{app}\examples"
Name: "{app}\docs"
Name: "{app}\docs\images"
[Files]
Source: "Jeliot3\images\*"; DestDir: "{app}\images\"
Source: "Jeliot3\docs\*"; DestDir: "{app}\docs\"
Source: "Jeliot3\docs\images\*"; DestDir: "{app}\docs\images\"
Source: "Jeliot3\examples\*"; DestDir: "{app}\examples\"
Source: "Jeliot3\*"; DestDir: "{app}"
; Source: "Readme.txt"; DestDir: "{app}"; Flags: isreadme

[Tasks]
Name: "desktopicon"; Description: "Create a &Desktop Icon shortcut"; GroupDescription: "Shortcuts"; Flags: unchecked
Name: "starticon"; Description: "Create a shortcut in the &Start Menu"; GroupDescription: "Shortcuts"; Flags: unchecked


[Icons]
Name: "{group}\Jeliot 3"; Filename: "{app}\jeliot.bat"; IconFilename: "{app}\jeliot.ico"; WorkingDir: "{app}"; Tasks:starticon
Name: "{userdesktop}\Jeliot 3"; Filename: "{app}\jeliot.bat"; IconFilename: "{app}\jeliot.ico"; WorkingDir: "{app}"; Tasks:desktopicon
Name: "{app}\Jeliot 3"; Filename: "{app}\jeliot.bat"; IconFilename: "{app}\jeliot.ico"; WorkingDir: "{app}"

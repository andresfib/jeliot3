; -- Jeliot 3 + JRE --

; SEE THE DOCUMENTATION FOR DETAILS ON CREATING .ISS SCRIPT FILES!

[Setup]
AppName=Jeliot 3
AppVerName=Jeliot 3.4.1 + Java Runtime Environment
DefaultDirName={pf}\Jeliot3
DefaultGroupName=Jeliot3
;UninstallDisplayIcon={app}\MyProg.exe
OutputDir=Release
SetupIconFile=src/jeliot.ico
[Dirs]
Name: "{app}\examples"
Name: "{app}\docs"
Name: "{app}\docs\images"
;jre
Name: "{app}\jre"
Name: "{app}\jre\bin"
Name: "{app}\jre\bin\client"
Name: "{app}\jre\bin\server"
Name: "{app}\jre\lib"
Name: "{app}\jre\lib\applet"
Name: "{app}\jre\lib\audio"
Name: "{app}\jre\lib\cmm"
Name: "{app}\jre\lib\ext"
Name: "{app}\jre\lib\fonts"
Name: "{app}\jre\lib\i386"
Name: "{app}\jre\lib\im"
Name: "{app}\jre\lib\images"
Name: "{app}\jre\lib\images\cursors"
Name: "{app}\jre\lib\security"
Name: "{app}\jre\lib\zi"

Name: "{app}\jre\lib\zi\Africa"
Name: "{app}\jre\lib\zi\America"
Name: "{app}\jre\lib\zi\America\Indiana"
Name: "{app}\jre\lib\zi\America\Kentucky"
Name: "{app}\jre\lib\zi\America\North_Dakota"
Name: "{app}\jre\lib\zi\Antarctica"
Name: "{app}\jre\lib\zi\Asia"
Name: "{app}\jre\lib\zi\Atlantic"
Name: "{app}\jre\lib\zi\Etc"
Name: "{app}\jre\lib\zi\Europe"
Name: "{app}\jre\lib\zi\Indian"
Name: "{app}\jre\lib\zi\Pacific"

[Files]
Source: "Jeliot3\docs\*"; DestDir: "{app}\docs\"
Source: "Jeliot3\docs\images\*"; DestDir: "{app}\docs\images\"
Source: "Jeliot3\examples\*"; DestDir: "{app}\examples\"
Source: "Jeliot3\*"; DestDir: "{app}"
; Source: "Readme.txt"; DestDir: "{app}"; Flags: isreadme
;Java Runtime Env Files
;Source: "Jeliot3\jre\*"; DestDir: "{app}\jre"
Source: "jre\bin\*"; DestDir: "{app}\jre\bin"
Source: "jre\bin\client\*"; DestDir: "{app}\jre\bin\client"
Source: "jre\bin\server\*"; DestDir: "{app}\jre\bin\server"
Source: "jre\lib\*"; DestDir: "{app}\jre\lib"
;Source: "jre\lib\applet\*"; DestDir: "{app}\jre\lib\applet"
Source: "jre\lib\audio\*"; DestDir: "{app}\jre\lib\audio"
Source: "jre\lib\cmm\*"; DestDir: "{app}\jre\lib\cmm"
;Source: "jre\lib\ext\*"; DestDir: "{app}\jre\lib\ext"
Source: "jre\lib\fonts\*"; DestDir: "{app}\jre\lib\fonts"
Source: "jre\lib\i386\*"; DestDir: "{app}\jre\lib\i386"
Source: "jre\lib\im\*"; DestDir: "{app}\jre\lib\im"
Source: "jre\lib\cursors\images\*"; DestDir: "{app}\jre\lib\cursors\images"
Source: "jre\lib\security\*"; DestDir: "{app}\jre\lib\security"
Source: "jre\lib\zi\*"; DestDir: "{app}\jre\lib\zi"
Source: "jre\lib\zi\Africa\*"; DestDir : "{app}\jre\lib\zi\Africa"
Source: "jre\lib\zi\America\*"; DestDir : "{app}\jre\lib\zi\America"
Source: "jre\lib\zi\America\Indiana\*"; DestDir : "{app}\jre\lib\zi\America\Indiana"
Source: "jre\lib\zi\America\Kentucky\*"; DestDir : "{app}\jre\lib\zi\America\Kentucky"
Source: "jre\lib\zi\America\North_Dakota\*"; DestDir : "{app}\jre\lib\zi\America\North_Dakota"
Source: "jre\lib\zi\Antarctica\*"; DestDir : "{app}\jre\lib\zi\Antarctica"
Source: "jre\lib\zi\Asia\*"; DestDir : "{app}\jre\lib\zi\Asia"
Source: "jre\lib\zi\Atlantic\*"; DestDir : "{app}\jre\lib\zi\Atlantic"
Source: "jre\lib\zi\Etc\*"; DestDir : "{app}\jre\lib\zi\Etc"
Source: "jre\lib\zi\Europe\*"; DestDir : "{app}\jre\lib\zi\Europe"
Source: "jre\lib\zi\Indian\*"; DestDir : "{app}\jre\lib\zi\Indian"
Source: "jre\lib\zi\Pacific\*"; DestDir : "{app}\jre\lib\zi\Pacific"
[Tasks]
Name: "desktopicon"; Description: "Create a &Desktop Icon shortcut"; GroupDescription: "Shortcuts"; Flags: unchecked
Name: "starticon"; Description: "Create a shortcut in the &Start Menu"; GroupDescription: "Shortcuts"; Flags: unchecked


[Icons]
Name: "{group}\Jeliot 3"; Filename: "{app}\jeliot.bat"; IconFilename: "{app}\jeliot.ico"; WorkingDir: "{app}"; Tasks:starticon
Name: "{userdesktop}\Jeliot 3"; Filename: "{app}\jeliot.bat"; IconFilename: "{app}\jeliot.ico"; WorkingDir: "{app}"; Tasks:desktopicon
Name: "{app}\Jeliot 3"; Filename: "{app}\jeliot.bat"; IconFilename: "{app}\jeliot.ico"; WorkingDir: "{app}"

; -- Example1.iss --

; SEE THE DOCUMENTATION FOR DETAILS ON CREATING .ISS SCRIPT FILES!

[Setup]
AppName=Jeliot 3
AppVerName=Jeliot 3.5.0
DefaultDirName={pf}\Jeliot3
DefaultGroupName=Jeliot3
;UninstallDisplayIcon={app}\MyProg.exe
OutputDir=Release
[Dirs]
Name: "{app}\examples"
Name: "{app}\docs"
Name: "{app}\docs\images"
[Files]
Source: "Jeliot3\docs\*"; DestDir: "{app}\docs\"
Source: "Jeliot3\docs\images\*"; DestDir: "{app}\docs\images\"
Source: "Jeliot3\examples\*"; DestDir: "{app}\examples\"
Check: GetJavaPath; Source: "Jeliot3\*"; DestDir: "{app}"
; Source: "Readme.txt"; DestDir: "{app}"; Flags: isreadme

[Tasks]
Name: "desktopicon"; Description: "Create a &Desktop Icon shortcut"; GroupDescription: "Shortcuts"; Flags: unchecked
Name: "starticon"; Description: "Create a shortcut in the &Start Menu"; GroupDescription: "Shortcuts"; Flags: unchecked
;Name: "addJavaPath"; Description:"Add Java executble to Path variable (useful when Java is not properly installed)"; GroupDescription: "System Configuration"; Flags: unchecked

[Icons]
Name: "{group}\Jeliot 3";  Filename: "{app}\jeliot.bat"; IconFilename: "{app}\jeliot.ico"; WorkingDir: "{app}"; Tasks:starticon
Name: "{group}\User Guide"; Filename: "{app}\userguide.pdf"; Tasks:starticon
Name: "{group}\Quick Tutorial"; Filename: "{app}\quicktutorial.pdf"; Tasks:starticon
Name: "{userdesktop}\Jeliot 3"; Filename: "{app}\jeliot.bat"; IconFilename: "{app}\jeliot.ico"; WorkingDir: "{app}"; Tasks:desktopicon
Name: "{app}\Jeliot 3"; Filename: "{app}\jeliot.bat"; IconFilename: "{app}\jeliot.ico"; WorkingDir: "{app}"

[Registry]


[Code]
var
  version: String;
  test: Boolean;
  key,  javaPath, currentPath: String;
function GetPathFromKey(subkey: String; var path: String) : Boolean;
begin
  key := 'SOFTWARE\JavaSoft\';
  Insert(subkey,key,Length(key)+1);
  SaveStringToFile('c:\javainstalls.txt', key, true);
  if RegKeyExists(HKLM, key) then
  begin
     RegQueryStringValue(HKLM, key, 'CurrentVersion', version);
     Insert(version, key,Length(key)+1);
     Result := RegQueryStringValue (HKLM, key, 'JavaHome', path);
   end;
end;

function GetJavaPath(): Boolean;
begin

  RegQueryStringValue(HKCU, 'Environment\', 'PATH', currentPath);
  test := RegKeyExists(HKCU, 'Environment');
  if  GetPathFromKey ('Java Development Kit\', javaPath)
      or GetPathFromKey ('Java Runtime Environment\', javaPath) then
  begin
     Insert('\bin\',javapath,Length(javaPath)+1);
     StringChange(currentPath, javaPath, '');
     Insert(';',javapath,1);
     Insert(javaPath,currentPath,Length(currentPath)+1);
     StringChange(currentPath, ';;', ';');
     //Insert path into registry
     RegWriteStringValue (HKCU, 'Environment\', 'PATH', currentPath);

  end;
  Result := true;
//  for (i :=0; i<javaInstallations; i++)
end;

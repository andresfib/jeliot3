IF EXIST jre\bin\java.exe GOTO JeliotJRE

:JeliotCommon
java -jar jeliot.jar %1 %2 %3
GOTO END

:JeliotJRE
jre\bin\java -jar jeliot.jar %1 %2 %3

:END
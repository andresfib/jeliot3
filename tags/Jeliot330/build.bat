@echo off
:: -----------------------------------------------------------------------------
:: build.bat - Win32 Build Script for DynamicJava
::
:: $Id$
:: -----------------------------------------------------------------------------

:: ----- Verify and Set Required Environment Variables -------------------------


set JAVA_HOME=c:\j2sdk1.4.0_01
if not "%JAVA_HOME%" == "" goto gotJavaHome
echo %JAVA_HOME%
echo You must set JAVA_HOME to point at your Java Development Kit installation
goto cleanup
:gotJavaHome

if not "%ANT_HOME%" == "" goto gotAntHome
set ANT_HOME=.
:gotAntHome

:: ----- Set Up The Runtime Classpath ------------------------------------------


set CP=%JAVA_HOME%\lib\tools.jar;%ANT_HOME%\lib\build\ant.jar;%ANT_HOME%\lib\build\crimson.jar;%ANT_HOME%\lib\build\jaxp.jar
 

:: ----- Execute The Requested Build -------------------------------------------

%JAVA_HOME%\bin\java.exe %ANT_OPTS% -classpath %CP% org.apache.tools.ant.Main -Dant.home=%ANT_HOME% %1 %2 %3 %4 %5 %6 %7 %8 %9

:: ----- Cleanup the environment -----------------------------------------------

:cleanup
set CP=


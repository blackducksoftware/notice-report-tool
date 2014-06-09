@REM ----------------------------------------------------------------------------
@REM  Copyright 2001-2006 The Apache Software Foundation.
@REM
@REM  Licensed under the Apache License, Version 2.0 (the "License");
@REM  you may not use this file except in compliance with the License.
@REM  You may obtain a copy of the License at
@REM
@REM       http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM  Unless required by applicable law or agreed to in writing, software
@REM  distributed under the License is distributed on an "AS IS" BASIS,
@REM  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM  See the License for the specific language governing permissions and
@REM  limitations under the License.
@REM ----------------------------------------------------------------------------
@REM
@REM   Copyright (c) 2001-2006 The Apache Software Foundation.  All rights
@REM   reserved.

@echo off

set ERROR_CODE=0

:init
@REM Decide how to startup depending on the version of windows

@REM -- Win98ME
if NOT "%OS%"=="Windows_NT" goto Win9xArg

@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" @setlocal

@REM -- 4NT shell
if "%eval[2+2]" == "4" goto 4NTArgs

@REM -- Regular WinNT shell
set CMD_LINE_ARGS=%*
goto WinNTGetScriptDir

@REM The 4NT Shell from jp software
:4NTArgs
set CMD_LINE_ARGS=%$
goto WinNTGetScriptDir

:Win9xArg
@REM Slurp the command line arguments.  This loop allows for an unlimited number
@REM of arguments (up to the command line limit, anyway).
set CMD_LINE_ARGS=
:Win9xApp
if %1a==a goto Win9xGetScriptDir
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto Win9xApp

:Win9xGetScriptDir
set SAVEDIR=%CD%
%0\
cd %0\..\.. 
set BASEDIR=%CD%
cd %SAVEDIR%
set SAVE_DIR=
goto repoSetup

:WinNTGetScriptDir
set BASEDIR=%~dp0\..

:repoSetup


if "%JAVACMD%"=="" set JAVACMD=java

if "%REPO%"=="" set REPO=%BASEDIR%\lib

set CLASSPATH="%BASEDIR%"\.\properties;"%REPO%"\commons-collections-3.2.1.jar;"%REPO%"\commons-lang-2.4.jar;"%REPO%"\httpclient-4.0.1.jar;"%REPO%"\httpcore-4.0.1.jar;"%REPO%"\commons-codec-1.4.jar;"%REPO%"\xml-apis-1.3.04.jar;"%REPO%"\commons-logging-1.1.1.jar;"%REPO%"\common-framework-1.2.0-SNAPSHOT.jar;"%REPO%"\mockito-all-1.9.5.jar;"%REPO%"\slf4j-api-1.7.5.jar;"%REPO%"\slf4j-simple-1.7.5.jar;"%REPO%"\xstream-1.4.1.jar;"%REPO%"\xmlpull-1.1.3.1.jar;"%REPO%"\xpp3_min-1.1.4c.jar;"%REPO%"\snakeyaml-1.13.jar;"%REPO%"\gson-1.7.1.jar;"%REPO%"\poi-3.10-beta2.jar;"%REPO%"\poi-ooxml-3.10-beta2.jar;"%REPO%"\poi-ooxml-schemas-3.10-beta2.jar;"%REPO%"\xmlbeans-2.3.0.jar;"%REPO%"\stax-api-1.0.1.jar;"%REPO%"\dom4j-1.6.1.jar;"%REPO%"\protex-sdk-client-6.4.2.jar;"%REPO%"\cxf-rt-frontend-jaxws-2.7.6.jar;"%REPO%"\xml-resolver-1.2.jar;"%REPO%"\asm-3.3.1.jar;"%REPO%"\cxf-rt-bindings-soap-2.7.6.jar;"%REPO%"\cxf-rt-databinding-jaxb-2.7.6.jar;"%REPO%"\cxf-rt-bindings-xml-2.7.6.jar;"%REPO%"\cxf-rt-frontend-simple-2.7.6.jar;"%REPO%"\cxf-rt-ws-addr-2.7.6.jar;"%REPO%"\cxf-rt-ws-policy-2.7.6.jar;"%REPO%"\neethi-3.0.2.jar;"%REPO%"\cxf-rt-transports-http-2.7.6.jar;"%REPO%"\cxf-rt-ws-security-2.7.6.jar;"%REPO%"\ehcache-core-2.5.1.jar;"%REPO%"\wss4j-1.6.11.jar;"%REPO%"\xmlsec-1.5.5.jar;"%REPO%"\opensaml-2.5.1-1.jar;"%REPO%"\openws-1.4.2-1.jar;"%REPO%"\xmltooling-1.3.2-1.jar;"%REPO%"\joda-time-1.6.2.jar;"%REPO%"\protex-sdk-utilities-6.4.2.jar;"%REPO%"\codecenter-sdk-client-6.6.0.jar;"%REPO%"\codecenter-sdk-utilities-6.6.0.jar;"%REPO%"\blackduck-cxf-utilities-1.1.jar;"%REPO%"\cxf-tools-common-2.7.6.jar;"%REPO%"\velocity-1.7.jar;"%REPO%"\wsdl4j-1.6.3.jar;"%REPO%"\jaxb-xjc-2.2.6.jar;"%REPO%"\jaxb-impl-2.2.6.jar;"%REPO%"\cxf-api-2.7.6.jar;"%REPO%"\woodstox-core-asl-4.2.0.jar;"%REPO%"\stax2-api-3.1.1.jar;"%REPO%"\xmlschema-core-2.0.3.jar;"%REPO%"\geronimo-javamail_1.4_spec-1.7.1.jar;"%REPO%"\cxf-rt-core-2.7.6.jar;"%REPO%"\opencsv-2.0.jar;"%REPO%"\jsoup-1.7.3.jar;"%REPO%"\log4j-1.2.14.jar;"%REPO%"\slf4j-log4j12-1.7.5.jar;"%REPO%"\commons-io-2.0.jar;"%REPO%"\rendersnake-1.7.jar;"%REPO%"\commons-lang3-3.1.jar;"%REPO%"\proserv-protex-utils-0.0.1.jar;"%REPO%"\reflections-0.9.9-RC1.jar;"%REPO%"\guava-11.0.2.jar;"%REPO%"\jsr305-1.3.9.jar;"%REPO%"\javassist-3.16.1-GA.jar;"%REPO%"\proserv-protex-report-utils-0.0.3.jar;"%REPO%"\NoticeReportTool-0.6.1-SNAPSHOT.jar
goto endInit

@REM Reaching here means variables are defined and arguments have been captured
:endInit

%JAVACMD% %JAVA_OPTS% -Xmx1024m -Xms1024M -classpath %CLASSPATH_PREFIX%;%CLASSPATH% -Dapp.name="NoticeReportTool" -Dapp.repo="%REPO%" -Dapp.home="%BASEDIR%" -Dbasedir="%BASEDIR%" com.blackducksoftware.sdk.notice.NoticeReportTool %CMD_LINE_ARGS%
if %ERRORLEVEL% NEQ 0 goto error
goto end

:error
if "%OS%"=="Windows_NT" @endlocal
set ERROR_CODE=%ERRORLEVEL%

:end
@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" goto endNT

@REM For old DOS remove the set variables from ENV - we assume they were not set
@REM before we started - at least we don't leave any baggage around
set CMD_LINE_ARGS=
goto postExec

:endNT
@REM If error code is set to 1 then the endlocal was done already in :error.
if %ERROR_CODE% EQU 0 @endlocal


:postExec

if "%FORCE_EXIT_ON_ERROR%" == "on" (
  if %ERROR_CODE% NEQ 0 exit %ERROR_CODE%
)

exit /B %ERROR_CODE%

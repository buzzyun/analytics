@echo off
rem ------------------------------------------------------------------------------- 
rem  Copyright (C) 2011 WebSquared Inc. http://websqrd.com                         
rem                                                                                 
rem  This program is free software; you can redistribute it and/or                  
rem  modify it under the terms of the GNU General Public License                    
rem  as published by the Free Software Foundation; either version 2                 
rem  of the License, or (at your option) any later version.                         
rem                                                                                 
rem  This program is distributed in the hope that it will be useful,                
rem  but WITHOUT ANY WARRANTY; without even the implied warranty of                 
rem  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                  
rem  GNU General Public License for more details.                                   
rem                                                                                 
rem  You should have received a copy of the GNU General Public License              
rem  along with this program; if not, write to the Free Software                    
rem  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
rem ------------------------------------------------------------------------------- 

setlocal enabledelayedexpansion

set IR_HOME=%~dp0\..\
chdir /d %IR_HOME%
set CONF=%IR_HOME%\conf
set LIB=%IR_HOME%\lib
set FASTCAT_CLASSPATH=.

for /f "tokens=*" %%x in ('dir /s /b %LIB%\*.jar') do (set FASTCAT_CLASSPATH=!FASTCAT_CLASSPATH!;%%x)

echo fastcatsearch-analytics start. see logs/system.log file.

java -Xmx512m -Dlogback.configurationFile=%CONF%\logback.xml -Dderby.stream.error.file=logs/db.log -classpath %FASTCAT_CLASSPATH% org.fastcatsearch.server.CatServer %IR_HOME% >>logs/output.log 2>&1

endlocal
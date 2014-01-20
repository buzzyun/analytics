#!/bin/sh
#-------------------------------------------------------------------------------
# Copyright (C) 2011 WebSquared Inc. http://websqrd.com
# 
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
#-------------------------------------------------------------------------------
#Fastcat start script
cd `dirname $0`/../
SERVER_HOME=`pwd`;

CONF=$SERVER_HOME/conf
LIB=$SERVER_HOME/lib
LOGS=$SERVER_HOME/logs
OUTPUT_LOG=$LOGS/output.log

HEAP_MEMORY_SIZE=512m
JVM_OPTS="-Xms$HEAP_MEMORY_SIZE -Xmx$HEAP_MEMORY_SIZE -XX:+HeapDumpOnOutOfMemoryError"
JAVA_OPTS="-server -Dfile.encoding=UTF-8 -Dlogback.configurationFile=$CONF/logback.xml -Dderby.stream.error.file=logs/db.log"
ADDITIONAL_OPTS=

trap '' 1 2 

java -Dserver.home=$SERVER_HOME $JVM_OPTS $JAVA_OPTS $ADDITIONAL_OPTS -classpath $LIB/analytics-server-bootstrap.jar org.fastcatgroup.analytics.server.Bootstrap > $OUTPUT_LOG 2>&1 & 

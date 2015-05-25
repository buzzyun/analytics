#!/bin/bash

echo '++++++++++ Fastcat-analytics Environment ++++++++++'

heap_memory_size=768m
java_path=
daemon_account=fastcat

current=$( dirname "$0" )

cd $current/../
server_home=$(pwd)
# return to original folder
cd "$current"

export heap_memory_size
export server_home
export java_path
export daemon_account

echo server_home = "$server_home"


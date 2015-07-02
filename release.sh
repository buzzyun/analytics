#!/bin/bash

mvn clean

mvn release:prepare

cd target

name=`ls |grep analytics`

tar czvf "$name".tar.gz "$name"/

cd .. 

mvn release:clean
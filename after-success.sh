#!/bin/sh
codecov
ssh -v -oStrictHostKeyChecking=no deployuser@178.62.252.23 "cd Scala-collaborative-worksheets; git pull origin master; ./restartApp.sh"
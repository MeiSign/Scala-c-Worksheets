#!/bin/sh
codecov
ssh -oStrictHostKeyChecking=no deployuser@178.62.252.23 "cd Scala-collaborative-worksheets; git pull origin master; ./scripts/restartApp.sh; exit 0"
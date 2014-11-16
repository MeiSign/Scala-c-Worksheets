#!/bin/sh
codecov
ssh -v -oStrictHostKeyChecking=no deployuser@178.62.252.23 "./restartApp.sh"
#!/bin/sh
codecov
ssh deploy@178.62.252.23
cd Scala-collaborative-worksheets
git pull origin master
./sbt run
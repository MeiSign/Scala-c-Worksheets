#!/bin/sh
if [ -f "../target/universal/stage/RUNNING_PID" ]
then
  kill $(cat ../target/universal/stage/RUNNING_PID)
  echo "Stopping App"
fi
echo "Starting App"
../sbt clean
../sbt stage
nohup ../target/universal/stage/bin/scala-collaborative-worksheets > start.out 2> start.err < /dev/null &
exit 0
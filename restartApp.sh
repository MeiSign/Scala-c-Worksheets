#!/bin/sh
if [ -f "target/universal/stage/RUNNING_PID" ]
then
  kill $(cat target/universal/stage/RUNNING_PID)
  echo "Stopping App"
fi
echo "Starting App"
nohup ./target/universal/stage/bin/Scala/Collaborative-worksheets 0<&- &>/dev/null &
exit 0
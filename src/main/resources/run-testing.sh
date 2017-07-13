#!/bin/sh
echo "RUNNING MAPREDUCE JOB - Testing"
/Users/Shared/hadoop/bin/hadoop jar /Users/mczal/Projects/MapReduce-workspace/mapreduce-naivebayes-testing/target/testing-1.0-SNAPSHOT.jar App /bayes/demo

echo "DONE"
#!/bin/sh
echo "RUNNING MAPREDUCE JOB - Training"
/Users/Shared/hadoop/bin/hadoop jar /Users/mczal/Projects/MapReduce-workspace/mapreduce-naivebayes-training/target/mapreduce-1.0-SNAPSHOT.jar mczal.bayes.App /bayes/demo

echo "DONE"
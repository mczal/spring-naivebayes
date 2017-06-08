#!/bin/sh
echo "RUNNING MAPREDUCE JOB - Testing"
/usr/local/hadoop/bin/hadoop jar /home/hduser1/Projects/mapreduce-naivebayes-testing/target/testing-1.0-SNAPSHOT.jar App /bayes/demo

echo "DONE"
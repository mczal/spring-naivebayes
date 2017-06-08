#!/bin/sh
echo "RUNNING MAPREDUCE JOB - Training"
/usr/local/hadoop/bin/hadoop jar /home/hduser1/Projects/mapreduce-naivebayes-training/target/mapreduce-1.0-SNAPSHOT.jar mczal.bayes.App /bayes/demo

echo "DONE"
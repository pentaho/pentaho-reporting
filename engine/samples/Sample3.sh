#!/bin/sh

cp=./dist/pentaho-reporting-engine-classic-samples-TRUNK-SNAPSHOT.jar
for jar in `ls -1 lib/*.jar`
do
    cp=${cp}:${jar}
done

java -cp ${cp} org.pentaho.reporting.engine.classic.samples.Sample3


#!/bin/sh

# Workaround for compatibility with recent Git versions. Without it, only snapshots will be published.
mvn org.apache.maven.plugins:maven-release-plugin:2.5:prepare org.apache.maven.plugins:maven-release-plugin:2.5:perform

#!/bin/bash

export CLASSPATH=".:build:lib/*"

java ece454.A3Client localhost:2181 /$USER 8 10000 100

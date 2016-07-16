#!/bin/bash

export CLASSPATH=".:build:lib/*"

java ece454.A3Client ecelinux3.uwaterloo.ca:2815 /$USER 8 10000 100

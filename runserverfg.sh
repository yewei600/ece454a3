#!/bin/bash

export CLASSPATH=".:build:lib/*"

java ece454.StorageNode localhost 2913 ecelinux3.uwaterloo.ca:2815 /$USER


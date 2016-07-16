#!/bin/bash

export CLASSPATH=".:build:lib/*"

java ece454.StorageNode localhost 5567 ecelinux3.uwaterloo.ca:2815 /$USER


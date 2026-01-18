#!/bin/bash

set -e

GitDir=~/NetBeansProjects
#GitDir="/c/Users/sgold/My Documents/NetBeansProjects"

S1="$GitDir/sport-jolt"
D1="$GitDir/sport"

S2="$GitDir/sport-jolt/library/src/main/java/com/github/stephengold/sportjolt"
D2="$GitDir/sport/lib/src/main/java/com/github/stephengold/sport"

S3="$GitDir/sport-jolt/java-apps/src/main/java/com/github/stephengold/sportjolt/javaapp"
D3="$GitDir/sport/apps/src/main/java/com/github/stephengold/sport"

S4="$GitDir/V-Sport"
D4="$GitDir/sport"

Meld="/usr/bin/meld"
#Meld="/c/Program Files/Meld/meld"

"$Meld" --diff "$S1" "$D1" --diff "$S2" "$D2" --diff "$S3" "$D3" --diff "$S4" "$D4"

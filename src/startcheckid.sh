#!/bin/bash

if [ "$1" = "" ]
then
	echo Usage: $0 \<directory_path\>
else 
	SAVEIFS=$IFS
	IFS=$(echo -en "\n\b")
	for f in $(ls $1)
	do
		echo "$f"
		java capture.CheckId $1/$f
	done
	IFS=$SAVEIFS
fi

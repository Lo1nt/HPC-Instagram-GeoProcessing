#!/bin/bash
#SBATCH -p physical
#SBATCH --time=00:20:00
#SBATCH --nodes=2
#SBATCH --ntasks-per-node=4
#SBATCH --output=2n8c.out
module load Java/9.0.4 
module load MPJ-Express/0.44-goolf-2015a-Java-9.0.4
javac -cp .:gson-2.8.2.jar:log4j-1.2.11.jar:$MPJ_HOME/lib/mpj.jar cloud_fin.jar
time mpjrun.sh -cp gson-2.8.2.jar cloud_fin.jar

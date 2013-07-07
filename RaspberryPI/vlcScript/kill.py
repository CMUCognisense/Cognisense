import os
import signal
import sys

killmap = {}

f = open("vlcScript/portnumber-pid","r")
line = f.readline()
while(line != ''):
	killmap[line.split(':')[0]] = line.split(':')[1]
	line = f.readline()
print killmap
pid = int(killmap[sys.argv[1]])

os.killpg(pid, signal.SIGTERM)  

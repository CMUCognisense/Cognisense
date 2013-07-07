import os
import signal

f = open('pid', 'r')
pid = f.readline()
os.killpg(int(pid), signal.SIGTERM)


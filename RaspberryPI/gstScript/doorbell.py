import sys
import os
import signal
import subprocess

command = "gst-launch-0.10 playbin2 uri=file:///home/pi/parth/gstScript/doorbell.mp3"
pro = subprocess.Popen(command, stdout=subprocess.PIPE,shell=True, preexec_fn=os.setsid)
print command


import sys
import os
import signal
import subprocess

# The os.setsid() is passed in the argument preexec_fn so
# it's run after the fork() and before  exec() to run the shell.
ip = sys.argv[1]
port = sys.argv[2]
command = "gst-launch-0.10 playbin2 uri=rtsp://" + ip + ":" + port
pro = subprocess.Popen(command, stdout=subprocess.PIPE,shell=True, preexec_fn=os.setsid)
print command
f = open('pid', 'w')
f.write(str(pro.pid))
#os.killpg(pro.pid, signal.SIGTERM)


import sys
import signal 
import os
import subprocess

cmd = "cvlc /media/data/Music/"+ sys.argv[1] +" --sout '#transcode{vcodec=none,acodec=s16l,ab=128,channels=2,samplerate=44100}:rtp{sdp=rtsp://:"+sys.argv[2]+"/}'"
print cmd
pro = subprocess.Popen(cmd, stdout=subprocess.PIPE, 
                      shell=True, preexec_fn=os.setsid)
f = open("vlcScript/portnumber-pid","a")
f.write(sys.argv[2]+':'+str(pro.pid)+'\n')

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <time.h>

static void daemonise(void) {
	pid_t pid, sid;

	// already a daemon
	if ( getppid() == 1 ) return;

	// Fork off the parent process
	pid = fork();

	// check if fork was successful. then check if we are the parent process. if so we just exit
	// straight away.
	if (pid < 0) {
	    exit(1);
	} else if (pid > 0) {
	    exit(0);
	}

	// from here onwards it is the forked child process which is executing
	umask(0);

	// Create a new SID for the child process
	sid = setsid();
	if (sid < 0) {
	    exit(1);
	}

	// redirect the program input/output/error pipes to /dev/null.
	freopen( "/dev/null", "r", stdin);
	freopen( "/dev/null", "w", stdout);
	freopen( "/dev/null", "w", stderr);
}

int main( int argc, char *argv[] ) {
	// fork the process and make it indepentent of the client launch thread
	daemonise();
	// we now launch the server
	char cmd[22] = "java ClassifierServer";
	system(cmd);
	return 0;
}

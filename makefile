build: clean build-server-launcher
	javac -Werror -Xlint:unchecked *.java

build-server-launcher:
	gcc launch.c -o launch-server

clean:
	rm -f *.class launch-server

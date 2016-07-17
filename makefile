JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Server.java \
	SessionThread.java \
	Client.java \
	ChatHistory.java \
	ColorManagement.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
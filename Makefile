#
# Warning: this is a gmake makefile.  Compile with "gmake". 
#
# This makefile only works under Unix (tested on Linux and Solaris).
#

#
# Configuration
#

# Jikes
#JC=jikes
#JFLAGS=+E -g

# Javac
JC=javac
JFLAGS=-g

# Path for WEBSPHINX classes and the classes it depends on, 
# relative to root websphinx directory.
# Separate with semicolons on Windows platform.
CLASSPATH:=src:lib:/usr/lib/netscape/java/classes/java40.jar:$(CLASSPATH)
export CLASSPATH



#
# End configuration
#
 

all:: java jar


# Building Java classes
java::
	$(JC) $(JFLAGS) `find src -name '*.java' -print`

# Building API documentation
doc::
	javadoc -d doc -sourcepath src websphinx websphinx.workbench websphinx.searchengine rcm.awt rcm.enum rcm.util org.apache.regexp
	zip -r websphinx-doc.zip doc

# Deleting all object files
clean::
	rm -rf `find src -name '*.class' -print`


#
# Release
#

# Build websphinx.jar
jar::
	cd src ; jar c0mf MANIFEST.INF ../websphinx.jar `find . '(' -name '*.class' -o -name '*.properties' ')' -print`
	cd lib ; jar u0f ../websphinx.jar `find . -name '*.class' -print`


# Build the release ZIP file
zip::
	-mkdir websphinx
	-rm -rf websphinx/* websphinx.zip
	cp -r \
		README Websphinx-LICENSE Apache-LICENSE ChangeLog Makefile *.html images lib src \
		websphinx
	zip -r websphinx.zip \
	    websphinx \
	    -x "*CVS*" "*~" "todo.html"



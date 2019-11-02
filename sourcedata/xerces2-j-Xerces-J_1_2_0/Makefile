# Top Makefile

all:: compile jars docs apidocs package

TOP = .
include $(TOP)/src/Makefile.incl

compile:: compile_src compile_samples
package:: package_bin package_src 

compile_src::
	@echo Building Source
	${MAKE} -C src

compile_samples:: compile_src
	@echo Building Samples
	${MAKE} -C samples

jars:: compile
	@echo Building Jar files in bin directory
	${MKDIR} class
	${MKDIR} bin
	${MAKE} -C src jars

docs:: ./src/classfiles_updated
	@echo Building Stylebook docs in docs directory
	${MKDIR} docs/html
	$(STYLEBOOK) "targetDirectory=docs/html" docs/docs-book.xml tools/style-apachexml.jar

apidocs::
	@echo Building apiDocs in docs directory.
	${MKDIR} docs/apiDocs
	${MAKE} -C src apidocs

package_bin:: jars apidocs ${BINZIPFILE}
${BINZIPFILE}:: ./src/classfiles_updated

	@echo Building the binary release package
	${MKDIR} bin
	${MKDIR} bin/samples
	${MKDIR} bin/samples/dom
	${CP} samples/dom/*.java bin/samples/dom
	${MKDIR} bin/samples/dom/traversal
	${CP} samples/dom/traversal/*.java bin/samples/dom/traversal
	${MKDIR} bin/samples/dom/wrappers
	${CP} samples/dom/wrappers/*.java bin/samples/dom/wrappers
	${MKDIR} bin/samples/sax
	${CP} samples/sax/*.java bin/samples/sax
	${MKDIR} bin/samples/sax/helpers
	${CP} samples/sax/helpers/*.java bin/samples/sax/helpers
	${MKDIR} bin/samples/ui
	${CP} samples/ui/*.java bin/samples/ui
	${MKDIR} bin/samples/util
	${CP} samples/util/*.java bin/samples/util
	${CP} -r docs bin
	${RM} -r bin/docs/CVS
	${RM} -r bin/docs/dtd/CVS
	${RM} -r bin/docs/*.xml bin/docs/dtd/*.dtd bin/docs/dtd/*.ent
	${CP} -r data bin
	${RM} -r bin/data/CVS
	${CP} LICENSE bin
	$(MV) bin xerces-${PRODUCTVERSION}
	$(JAR) cvfM ${BINZIPFILE} xerces-${PRODUCTVERSION} 
	$(MV) xerces-${PRODUCTVERSION} bin

package_src:: ${SRCZIPFILE}
${SRCZIPFILE}: ./src/classfiles_updated
	@echo Building the source release package
	${MAKE} -C src package_src
	${CP} -r data source
	${RM} -r source/data/CVS
	${MKDIR} source/docs
	${MKDIR} source/docs/dtd
	${CP} docs/*.xml source/docs
	${CP} LICENSE source
	${CP} docs/dtd/*.dtd source/docs/dtd
	${CP} docs/dtd/*.ent source/docs/dtd
	$(MV) source xerces-${PRODUCTVERSION}
	$(JAR) cvfM ${SRCZIPFILE} xerces-${PRODUCTVERSION} 
	$(MV) xerces-${PRODUCTVERSION} source

clean::
	${MAKE} -C src clean
	${MAKE} -C samples clean
	${RM} -rf bin class source docs/apiDocs docs/html 
	${RM} ${BINZIPFILE} ${SRCZIPFILE}

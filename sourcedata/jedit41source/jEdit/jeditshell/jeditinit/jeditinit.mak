# Microsoft Developer Studio Generated NMAKE File, Based on jeditinit.dsp
!IF "$(CFG)" == ""
CFG=jeditinit - Win32 Debug
!MESSAGE No configuration specified. Defaulting to jeditinit - Win32 Debug.
!ENDIF 

!IF "$(CFG)" != "jeditinit - Win32 Release" && "$(CFG)" != "jeditinit - Win32 Debug"
!MESSAGE Invalid configuration "$(CFG)" specified.
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "jeditinit.mak" CFG="jeditinit - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "jeditinit - Win32 Release" (based on "Win32 (x86) Application")
!MESSAGE "jeditinit - Win32 Debug" (based on "Win32 (x86) Application")
!MESSAGE 
!ERROR An invalid configuration is specified.
!ENDIF 

!IF "$(OS)" == "Windows_NT"
NULL=
!ELSE 
NULL=nul
!ENDIF 

!IF  "$(CFG)" == "jeditinit - Win32 Release"

OUTDIR=.\Release
INTDIR=.\Release
# Begin Custom Macros
OutDir=.\Release
# End Custom Macros

ALL : "$(OUTDIR)\jedit.exe" "G:\Program Files\jEdit\jedit.exe"


CLEAN :
	-@erase "$(INTDIR)\jeditinit.obj"
	-@erase "$(INTDIR)\jeditinit.pch"
	-@erase "$(INTDIR)\jeditinit.res"
	-@erase "$(INTDIR)\jeditinitDlg.obj"
	-@erase "$(INTDIR)\StdAfx.obj"
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(OUTDIR)\jedit.exe"
	-@erase "G:\Program Files\jEdit\jedit.exe"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

CPP=cl.exe
CPP_PROJ=/nologo /MT /W3 /O1 /Ob0 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /D "_ATL_MIN_CRT" /Fp"$(INTDIR)\jeditinit.pch" /Yu"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 

.c{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cpp{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cxx{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.c{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cpp{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cxx{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

MTL=midl.exe
MTL_PROJ=/nologo /D "NDEBUG" /mktyplib203 /win32 
RSC=rc.exe
RSC_PROJ=/l 0x409 /fo"$(INTDIR)\jeditinit.res" /d "NDEBUG" 
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\jeditinit.bsc" 
BSC32_SBRS= \
	
LINK32=link.exe
LINK32_FLAGS=kernel32.lib user32.lib gdi32.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib /nologo /subsystem:windows /incremental:no /pdb:"$(OUTDIR)\jedit.pdb" /machine:I386 /out:"$(OUTDIR)\jedit.exe" 
LINK32_OBJS= \
	"$(INTDIR)\jeditinit.obj" \
	"$(INTDIR)\jeditinitDlg.obj" \
	"$(INTDIR)\StdAfx.obj" \
	"$(INTDIR)\jeditinit.res"

"$(OUTDIR)\jedit.exe" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

TargetPath=.\Release\jedit.exe
InputPath=.\Release\jedit.exe
SOURCE="$(InputPath)"

"G:\Program Files\jEdit\jedit.exe" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	<<tempfile.bat 
	@echo off 
	rem copy $(TargetPath) "G:\Program Files\jEdit\jedit.exe" 
	echo copy not performed 
<< 
	

!ELSEIF  "$(CFG)" == "jeditinit - Win32 Debug"

OUTDIR=.\Debug
INTDIR=.\Debug
# Begin Custom Macros
OutDir=.\Debug
# End Custom Macros

ALL : "$(OUTDIR)\jedit.exe"


CLEAN :
	-@erase "$(INTDIR)\jeditinit.obj"
	-@erase "$(INTDIR)\jeditinit.pch"
	-@erase "$(INTDIR)\jeditinit.res"
	-@erase "$(INTDIR)\jeditinitDlg.obj"
	-@erase "$(INTDIR)\StdAfx.obj"
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(INTDIR)\vc60.pdb"
	-@erase "$(OUTDIR)\jedit.exe"
	-@erase "$(OUTDIR)\jedit.ilk"
	-@erase "$(OUTDIR)\jedit.pdb"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

CPP=cl.exe
CPP_PROJ=/nologo /MLd /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_MBCS" /D "_ATL_MIN_CRT" /Fp"$(INTDIR)\jeditinit.pch" /Yu"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /GZ /c 

.c{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cpp{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cxx{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.c{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cpp{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cxx{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

MTL=midl.exe
MTL_PROJ=/nologo /D "_DEBUG" /mktyplib203 /win32 
RSC=rc.exe
RSC_PROJ=/l 0x409 /fo"$(INTDIR)\jeditinit.res" /d "_DEBUG" 
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\jeditinit.bsc" 
BSC32_SBRS= \
	
LINK32=link.exe
LINK32_FLAGS=kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /subsystem:windows /incremental:yes /pdb:"$(OUTDIR)\jedit.pdb" /debug /machine:I386 /out:"$(OUTDIR)\jedit.exe" /pdbtype:sept 
LINK32_OBJS= \
	"$(INTDIR)\jeditinit.obj" \
	"$(INTDIR)\jeditinitDlg.obj" \
	"$(INTDIR)\StdAfx.obj" \
	"$(INTDIR)\jeditinit.res"

"$(OUTDIR)\jedit.exe" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

!ENDIF 


!IF "$(NO_EXTERNAL_DEPS)" != "1"
!IF EXISTS("jeditinit.dep")
!INCLUDE "jeditinit.dep"
!ELSE 
!MESSAGE Warning: cannot find "jeditinit.dep"
!ENDIF 
!ENDIF 


!IF "$(CFG)" == "jeditinit - Win32 Release" || "$(CFG)" == "jeditinit - Win32 Debug"
SOURCE=.\jeditinit.cpp

"$(INTDIR)\jeditinit.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditinit.pch"


SOURCE=.\jeditinit.idl
SOURCE=.\jeditinit.rc

"$(INTDIR)\jeditinit.res" : $(SOURCE) "$(INTDIR)"
	$(RSC) $(RSC_PROJ) $(SOURCE)


SOURCE=.\jeditinitDlg.cpp

"$(INTDIR)\jeditinitDlg.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditinit.pch"


SOURCE=.\StdAfx.cpp

!IF  "$(CFG)" == "jeditinit - Win32 Release"

CPP_SWITCHES=/nologo /MT /W3 /O1 /Ob0 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /D "_ATL_MIN_CRT" /Fp"$(INTDIR)\jeditinit.pch" /Yc"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 

"$(INTDIR)\StdAfx.obj"	"$(INTDIR)\jeditinit.pch" : $(SOURCE) "$(INTDIR)"
	$(CPP) @<<
  $(CPP_SWITCHES) $(SOURCE)
<<


!ELSEIF  "$(CFG)" == "jeditinit - Win32 Debug"

CPP_SWITCHES=/nologo /MLd /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_MBCS" /D "_ATL_MIN_CRT" /Fp"$(INTDIR)\jeditinit.pch" /Yc"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /GZ /c 

"$(INTDIR)\StdAfx.obj"	"$(INTDIR)\jeditinit.pch" : $(SOURCE) "$(INTDIR)"
	$(CPP) @<<
  $(CPP_SWITCHES) $(SOURCE)
<<


!ENDIF 


!ENDIF 


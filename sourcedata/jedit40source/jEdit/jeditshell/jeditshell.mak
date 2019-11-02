# Microsoft Developer Studio Generated NMAKE File, Based on jeditshell.dsp
!IF "$(CFG)" == ""
CFG=jeditshell - Win32 Debug
!MESSAGE No configuration specified. Defaulting to jeditshell - Win32 Debug.
!ENDIF 

!IF "$(CFG)" != "jeditshell - Win32 Debug" && "$(CFG)" != "jeditshell - Win32 Unicode Debug" && "$(CFG)" != "jeditshell - Win32 Release MinSize" && "$(CFG)" != "jeditshell - Win32 Release MinDependency" && "$(CFG)" != "jeditshell - Win32 Unicode Release MinSize" && "$(CFG)" != "jeditshell - Win32 Unicode Release MinDependency"
!MESSAGE Invalid configuration "$(CFG)" specified.
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "jeditshell.mak" CFG="jeditshell - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "jeditshell - Win32 Debug" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE "jeditshell - Win32 Unicode Debug" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE "jeditshell - Win32 Release MinSize" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE "jeditshell - Win32 Release MinDependency" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE "jeditshell - Win32 Unicode Release MinSize" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE "jeditshell - Win32 Unicode Release MinDependency" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE 
!ERROR An invalid configuration is specified.
!ENDIF 

!IF "$(OS)" == "Windows_NT"
NULL=
!ELSE 
NULL=nul
!ENDIF 

CPP=cl.exe
MTL=midl.exe
RSC=rc.exe

!IF  "$(CFG)" == "jeditshell - Win32 Debug"

OUTDIR=.\Debug
INTDIR=.\Debug
# Begin Custom Macros
OutDir=.\Debug
# End Custom Macros

ALL : "$(OUTDIR)\jeshlstb.dll" ".\jeditshell.tlb" ".\jeditshell.h" ".\jeditshell_i.c" ".\Debug\regsvr32.trg"


CLEAN :
	-@erase "$(INTDIR)\JEditCtxMenu.obj"
	-@erase "$(INTDIR)\jeditshell.obj"
	-@erase "$(INTDIR)\jeditshell.pch"
	-@erase "$(INTDIR)\jeditshell.res"
	-@erase "$(INTDIR)\StdAfx.obj"
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(INTDIR)\vc60.pdb"
	-@erase "$(OUTDIR)\jeshlstb.dll"
	-@erase "$(OUTDIR)\jeshlstb.exp"
	-@erase "$(OUTDIR)\jeshlstb.ilk"
	-@erase "$(OUTDIR)\jeshlstb.lib"
	-@erase "$(OUTDIR)\jeshlstb.pdb"
	-@erase ".\jeditshell.h"
	-@erase ".\jeditshell.tlb"
	-@erase ".\jeditshell_i.c"
	-@erase ".\Debug\regsvr32.trg"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

CPP_PROJ=/nologo /MDd /W3 /Gm /ZI /Od /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "_ATL_MIN_CRT" /Fp"$(INTDIR)\jeditshell.pch" /Yu"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /GZ /c 
RSC_PROJ=/l 0x409 /fo"$(INTDIR)\jeditshell.res" /d "_DEBUG" 
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\jeditshell.bsc" 
BSC32_SBRS= \
	
LINK32=link.exe
LINK32_FLAGS=shlwapi.lib kernel32.lib user32.lib gdi32.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib ws2_32.lib /nologo /version:3.2 /subsystem:windows /dll /incremental:yes /pdb:"$(OUTDIR)\jeshlstb.pdb" /debug /machine:I386 /def:".\jeditshell.def" /out:"$(OUTDIR)\jeshlstb.dll" /implib:"$(OUTDIR)\jeshlstb.lib" /pdbtype:sept /out:""G:\Progra~1\jEdit\jeshlstb.dll"" 
DEF_FILE= \
	".\jeditshell.def"
LINK32_OBJS= \
	"$(INTDIR)\JEditCtxMenu.obj" \
	"$(INTDIR)\jeditshell.obj" \
	"$(INTDIR)\StdAfx.obj" \
	"$(INTDIR)\jeditshell.res"

"$(OUTDIR)\jeshlstb.dll" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

OutDir=.\Debug
TargetPath=.\Debug\jeshlstb.dll
InputPath=.\Debug\jeshlstb.dll
SOURCE="$(InputPath)"

"$(OUTDIR)\regsvr32.trg" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	<<tempfile.bat 
	@echo off 
	echo regsvr32 /s /c "$(TargetPath)" 
	echo regsvr32 exec. time > "$(OutDir)\regsvr32.trg" 
	echo Registration not performed 
<< 
	

!ELSEIF  "$(CFG)" == "jeditshell - Win32 Unicode Debug"

OUTDIR=.\DebugU
INTDIR=.\DebugU
# Begin Custom Macros
OutDir=.\DebugU
# End Custom Macros

ALL : "$(OUTDIR)\jeshlstb.dll" ".\jeditshell.tlb" ".\jeditshell.h" ".\jeditshell_i.c" ".\DebugU\regsvr32.trg"


CLEAN :
	-@erase "$(INTDIR)\JEditCtxMenu.obj"
	-@erase "$(INTDIR)\jeditshell.obj"
	-@erase "$(INTDIR)\jeditshell.pch"
	-@erase "$(INTDIR)\jeditshell.res"
	-@erase "$(INTDIR)\StdAfx.obj"
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(INTDIR)\vc60.pdb"
	-@erase "$(OUTDIR)\jeshlstb.dll"
	-@erase "$(OUTDIR)\jeshlstb.exp"
	-@erase "$(OUTDIR)\jeshlstb.ilk"
	-@erase "$(OUTDIR)\jeshlstb.lib"
	-@erase "$(OUTDIR)\jeshlstb.pdb"
	-@erase ".\jeditshell.h"
	-@erase ".\jeditshell.tlb"
	-@erase ".\jeditshell_i.c"
	-@erase ".\DebugU\regsvr32.trg"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

CPP_PROJ=/nologo /MTd /W3 /Gm /ZI /Od /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_USRDLL" /D "_UNICODE" /Fp"$(INTDIR)\jeditshell.pch" /Yu"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /GZ /c 
RSC_PROJ=/l 0x409 /fo"$(INTDIR)\jeditshell.res" /d "_DEBUG" 
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\jeditshell.bsc" 
BSC32_SBRS= \
	
LINK32=link.exe
LINK32_FLAGS=shlwapi.lib kernel32.lib user32.lib gdi32.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib ws2_32.lib userenv.lib /nologo /version:1.0 /subsystem:windows /dll /incremental:yes /pdb:"$(OUTDIR)\jeshlstb.pdb" /debug /machine:I386 /def:".\jeditshell.def" /out:"$(OUTDIR)\jeshlstb.dll" /implib:"$(OUTDIR)\jeshlstb.lib" /pdbtype:sept 
DEF_FILE= \
	".\jeditshell.def"
LINK32_OBJS= \
	"$(INTDIR)\JEditCtxMenu.obj" \
	"$(INTDIR)\jeditshell.obj" \
	"$(INTDIR)\StdAfx.obj" \
	"$(INTDIR)\jeditshell.res"

"$(OUTDIR)\jeshlstb.dll" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

OutDir=.\DebugU
TargetPath=.\DebugU\jeshlstb.dll
InputPath=.\DebugU\jeshlstb.dll
SOURCE="$(InputPath)"

"$(OUTDIR)\regsvr32.trg" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	<<tempfile.bat 
	@echo off 
	if "%OS%"=="" goto NOTNT 
	if not "%OS%"=="Windows_NT" goto NOTNT 
	regsvr32 /s /c "$(TargetPath)" 
	echo regsvr32 exec. time > "$(OutDir)\regsvr32.trg" 
	goto end 
	:NOTNT 
	echo Warning : Cannot register Unicode DLL on Windows 95 
	:end 
<< 
	

!ELSEIF  "$(CFG)" == "jeditshell - Win32 Release MinSize"

OUTDIR=.\ReleaseMinSize
INTDIR=.\ReleaseMinSize
# Begin Custom Macros
OutDir=.\ReleaseMinSize
# End Custom Macros

ALL : "$(OUTDIR)\jeshlstb.dll" ".\jeditshell.tlb" ".\jeditshell.h" ".\jeditshell_i.c" ".\ReleaseMinSize\regsvr32.trg"


CLEAN :
	-@erase "$(INTDIR)\JEditCtxMenu.obj"
	-@erase "$(INTDIR)\jeditshell.obj"
	-@erase "$(INTDIR)\jeditshell.pch"
	-@erase "$(INTDIR)\jeditshell.res"
	-@erase "$(INTDIR)\StdAfx.obj"
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(OUTDIR)\jeshlstb.dll"
	-@erase "$(OUTDIR)\jeshlstb.exp"
	-@erase "$(OUTDIR)\jeshlstb.lib"
	-@erase ".\jeditshell.h"
	-@erase ".\jeditshell.tlb"
	-@erase ".\jeditshell_i.c"
	-@erase ".\ReleaseMinSize\regsvr32.trg"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

CPP_PROJ=/nologo /MD /W3 /O1 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "_ATL_DLL" /D "_ATL_MIN_CRT" /Fp"$(INTDIR)\jeditshell.pch" /Yu"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 
RSC_PROJ=/l 0x409 /fo"$(INTDIR)\jeditshell.res" /d "NDEBUG" 
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\jeditshell.bsc" 
BSC32_SBRS= \
	
LINK32=link.exe
LINK32_FLAGS=shlwapi.lib kernel32.lib user32.lib gdi32.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib ws2_32.lib /nologo /version:1.0 /subsystem:windows /dll /incremental:no /pdb:"$(OUTDIR)\jeshlstb.pdb" /machine:I386 /def:".\jeditshell.def" /out:"$(OUTDIR)\jeshlstb.dll" /implib:"$(OUTDIR)\jeshlstb.lib" 
DEF_FILE= \
	".\jeditshell.def"
LINK32_OBJS= \
	"$(INTDIR)\JEditCtxMenu.obj" \
	"$(INTDIR)\jeditshell.obj" \
	"$(INTDIR)\StdAfx.obj" \
	"$(INTDIR)\jeditshell.res"

"$(OUTDIR)\jeshlstb.dll" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

OutDir=.\ReleaseMinSize
TargetPath=.\ReleaseMinSize\jeshlstb.dll
InputPath=.\ReleaseMinSize\jeshlstb.dll
SOURCE="$(InputPath)"

"$(OUTDIR)\regsvr32.trg" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	<<tempfile.bat 
	@echo off 
	echo regsvr32 /s /c "$(TargetPath)" 
	echo regsvr32 exec. time > "$(OutDir)\regsvr32.trg" 
	echo Registration not performed 
<< 
	

!ELSEIF  "$(CFG)" == "jeditshell - Win32 Release MinDependency"

OUTDIR=.\ReleaseMinDependency
INTDIR=.\ReleaseMinDependency
# Begin Custom Macros
OutDir=.\ReleaseMinDependency
# End Custom Macros

ALL : "$(OUTDIR)\jeshlstb.dll" ".\jeditshell.tlb" ".\jeditshell.h" ".\jeditshell_i.c" ".\ReleaseMinDependency\regsvr32.trg" "G:\Program Files\jEdit\jeshlstb.dll"


CLEAN :
	-@erase "$(INTDIR)\JEditCtxMenu.obj"
	-@erase "$(INTDIR)\jeditshell.obj"
	-@erase "$(INTDIR)\jeditshell.pch"
	-@erase "$(INTDIR)\jeditshell.res"
	-@erase "$(INTDIR)\StdAfx.obj"
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(OUTDIR)\jeshlstb.dll"
	-@erase "$(OUTDIR)\jeshlstb.exp"
	-@erase "$(OUTDIR)\jeshlstb.lib"
	-@erase ".\jeditshell.h"
	-@erase ".\jeditshell.tlb"
	-@erase ".\jeditshell_i.c"
	-@erase ".\ReleaseMinDependency\regsvr32.trg"
	-@erase "G:\Program Files\jEdit\jeshlstb.dll"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

CPP_PROJ=/nologo /MD /W3 /O1 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "_ATL_STATIC_REGISTRY" /Fp"$(INTDIR)\jeditshell.pch" /Yu"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 
RSC_PROJ=/l 0x409 /fo"$(INTDIR)\jeditshell.res" /d "NDEBUG" 
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\jeditshell.bsc" 
BSC32_SBRS= \
	
LINK32=link.exe
LINK32_FLAGS=odbc32.lib odbccp32.lib kernel32.lib user32.lib gdi32.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib userenv.lib /nologo /version:3.3 /subsystem:windows /dll /incremental:no /pdb:"$(OUTDIR)\jeshlstb.pdb" /machine:I386 /def:".\jeditshell.def" /out:"$(OUTDIR)\jeshlstb.dll" /implib:"$(OUTDIR)\jeshlstb.lib" 
DEF_FILE= \
	".\jeditshell.def"
LINK32_OBJS= \
	"$(INTDIR)\JEditCtxMenu.obj" \
	"$(INTDIR)\jeditshell.obj" \
	"$(INTDIR)\StdAfx.obj" \
	"$(INTDIR)\jeditshell.res"

"$(OUTDIR)\jeshlstb.dll" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

OutDir=.\ReleaseMinDependency
TargetPath=.\ReleaseMinDependency\jeshlstb.dll
InputPath=.\ReleaseMinDependency\jeshlstb.dll
SOURCE="$(InputPath)"

"$(OUTDIR)\regsvr32.trg"	"G:\Program Files\jEdit\jeshlstb.dll" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	<<tempfile.bat 
	@echo off 
	echo regsvr32 /s /c "$(TargetPath)" 
	echo regsvr32 exec. time > "$(OutDir)\regsvr32.trg" 
	echo Registration not performed 
	rem echo Copying to jEdit directory... 
	rem copy $(TargetPath) "G:\Program Files\jEdit"
<< 
	

!ELSEIF  "$(CFG)" == "jeditshell - Win32 Unicode Release MinSize"

OUTDIR=.\ReleaseUMinSize
INTDIR=.\ReleaseUMinSize
# Begin Custom Macros
OutDir=.\ReleaseUMinSize
# End Custom Macros

ALL : "$(OUTDIR)\jeshlstb.dll" ".\jeditshell.tlb" ".\jeditshell.h" ".\jeditshell_i.c" ".\ReleaseUMinSize\regsvr32.trg"


CLEAN :
	-@erase "$(INTDIR)\JEditCtxMenu.obj"
	-@erase "$(INTDIR)\jeditshell.obj"
	-@erase "$(INTDIR)\jeditshell.pch"
	-@erase "$(INTDIR)\jeditshell.res"
	-@erase "$(INTDIR)\StdAfx.obj"
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(OUTDIR)\jeshlstb.dll"
	-@erase "$(OUTDIR)\jeshlstb.exp"
	-@erase "$(OUTDIR)\jeshlstb.lib"
	-@erase ".\jeditshell.h"
	-@erase ".\jeditshell.tlb"
	-@erase ".\jeditshell_i.c"
	-@erase ".\ReleaseUMinSize\regsvr32.trg"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

CPP_PROJ=/nologo /MT /W3 /O1 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_USRDLL" /D "_UNICODE" /D "_ATL_DLL" /D "_ATL_MIN_CRT" /Fp"$(INTDIR)\jeditshell.pch" /Yu"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 
RSC_PROJ=/l 0x409 /fo"$(INTDIR)\jeditshell.res" /d "NDEBUG" 
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\jeditshell.bsc" 
BSC32_SBRS= \
	
LINK32=link.exe
LINK32_FLAGS=shlwapi.lib kernel32.lib user32.lib gdi32.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib ws2_32.lib userenv.lib /nologo /version:1.0 /subsystem:windows /dll /incremental:no /pdb:"$(OUTDIR)\jeshlstb.pdb" /machine:I386 /def:".\jeditshell.def" /out:"$(OUTDIR)\jeshlstb.dll" /implib:"$(OUTDIR)\jeshlstb.lib" 
DEF_FILE= \
	".\jeditshell.def"
LINK32_OBJS= \
	"$(INTDIR)\JEditCtxMenu.obj" \
	"$(INTDIR)\jeditshell.obj" \
	"$(INTDIR)\StdAfx.obj" \
	"$(INTDIR)\jeditshell.res"

"$(OUTDIR)\jeshlstb.dll" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

OutDir=.\ReleaseUMinSize
TargetPath=.\ReleaseUMinSize\jeshlstb.dll
InputPath=.\ReleaseUMinSize\jeshlstb.dll
SOURCE="$(InputPath)"

"$(OUTDIR)\regsvr32.trg" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	<<tempfile.bat 
	@echo off 
	if "%OS%"=="" goto NOTNT 
	if not "%OS%"=="Windows_NT" goto NOTNT 
	echo regsvr32 /s /c "$(TargetPath)" 
	echo regsvr32 exec. time > "$(OutDir)\regsvr32.trg" 
	goto end 
	:NOTNT 
	echo Warning : Cannot register Unicode DLL on Windows 95 
	:end 
	echo Registration not performed 
<< 
	

!ELSEIF  "$(CFG)" == "jeditshell - Win32 Unicode Release MinDependency"

OUTDIR=.\ReleaseUMinDependency
INTDIR=.\ReleaseUMinDependency
# Begin Custom Macros
OutDir=.\ReleaseUMinDependency
# End Custom Macros

ALL : "$(OUTDIR)\jeshlstb.dll" ".\jeditshell.tlb" ".\jeditshell.h" ".\jeditshell_i.c" ".\ReleaseUMinDependency\regsvr32.trg"


CLEAN :
	-@erase "$(INTDIR)\JEditCtxMenu.obj"
	-@erase "$(INTDIR)\jeditshell.obj"
	-@erase "$(INTDIR)\jeditshell.pch"
	-@erase "$(INTDIR)\jeditshell.res"
	-@erase "$(INTDIR)\StdAfx.obj"
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(OUTDIR)\jeshlstb.dll"
	-@erase "$(OUTDIR)\jeshlstb.exp"
	-@erase "$(OUTDIR)\jeshlstb.lib"
	-@erase ".\jeditshell.h"
	-@erase ".\jeditshell.tlb"
	-@erase ".\jeditshell_i.c"
	-@erase ".\ReleaseUMinDependency\regsvr32.trg"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

CPP_PROJ=/nologo /MT /W3 /O1 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_USRDLL" /D "_UNICODE" /D "_ATL_STATIC_REGISTRY" /D "_ATL_MIN_CRT" /Fp"$(INTDIR)\jeditshell.pch" /Yu"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 
RSC_PROJ=/l 0x409 /fo"$(INTDIR)\jeditshell.res" /d "NDEBUG" 
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\jeditshell.bsc" 
BSC32_SBRS= \
	
LINK32=link.exe
LINK32_FLAGS=shlwapi.lib kernel32.lib user32.lib gdi32.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib ws2_32.lib userenv.lib /nologo /version:3.1 /subsystem:windows /dll /incremental:no /pdb:"$(OUTDIR)\jeshlstb.pdb" /machine:I386 /def:".\jeditshell.def" /out:"$(OUTDIR)\jeshlstb.dll" /implib:"$(OUTDIR)\jeshlstb.lib" 
DEF_FILE= \
	".\jeditshell.def"
LINK32_OBJS= \
	"$(INTDIR)\JEditCtxMenu.obj" \
	"$(INTDIR)\jeditshell.obj" \
	"$(INTDIR)\StdAfx.obj" \
	"$(INTDIR)\jeditshell.res"

"$(OUTDIR)\jeshlstb.dll" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

OutDir=.\ReleaseUMinDependency
TargetPath=.\ReleaseUMinDependency\jeshlstb.dll
InputPath=.\ReleaseUMinDependency\jeshlstb.dll
SOURCE="$(InputPath)"

"$(OUTDIR)\regsvr32.trg" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	<<tempfile.bat 
	@echo off 
	if "%OS%"=="" goto NOTNT 
	if not "%OS%"=="Windows_NT" goto NOTNT 
	echo regsvr32 /s /c "$(TargetPath)" 
	echo regsvr32 exec. time > "$(OutDir)\regsvr32.trg" 
	goto end 
	:NOTNT 
	echo Warning : Cannot register Unicode DLL on Windows 95 
	:end 
	echo Registration not performed 
<< 
	

!ENDIF 

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

MTL_PROJ=

!IF "$(NO_EXTERNAL_DEPS)" != "1"
!IF EXISTS("jeditshell.dep")
!INCLUDE "jeditshell.dep"
!ELSE 
!MESSAGE Warning: cannot find "jeditshell.dep"
!ENDIF 
!ENDIF 


!IF "$(CFG)" == "jeditshell - Win32 Debug" || "$(CFG)" == "jeditshell - Win32 Unicode Debug" || "$(CFG)" == "jeditshell - Win32 Release MinSize" || "$(CFG)" == "jeditshell - Win32 Release MinDependency" || "$(CFG)" == "jeditshell - Win32 Unicode Release MinSize" || "$(CFG)" == "jeditshell - Win32 Unicode Release MinDependency"
SOURCE=.\JEditCtxMenu.cpp

"$(INTDIR)\JEditCtxMenu.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditshell.pch"


SOURCE=.\jeditshell.cpp

"$(INTDIR)\jeditshell.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditshell.pch"


SOURCE=.\jeditshell.idl
MTL_SWITCHES=/tlb ".\jeditshell.tlb" /h "jeditshell.h" /iid "jeditshell_i.c" /Oicf 

".\jeditshell.tlb"	".\jeditshell.h"	".\jeditshell_i.c" : $(SOURCE) "$(INTDIR)"
	$(MTL) @<<
  $(MTL_SWITCHES) $(SOURCE)
<<


SOURCE=.\jeditshell.rc

"$(INTDIR)\jeditshell.res" : $(SOURCE) "$(INTDIR)"
	$(RSC) $(RSC_PROJ) $(SOURCE)


SOURCE=.\StdAfx.cpp

!IF  "$(CFG)" == "jeditshell - Win32 Debug"

CPP_SWITCHES=/nologo /MDd /W3 /Gm /ZI /Od /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "_ATL_MIN_CRT" /Fp"$(INTDIR)\jeditshell.pch" /Yc"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /GZ /c 

"$(INTDIR)\StdAfx.obj"	"$(INTDIR)\jeditshell.pch" : $(SOURCE) "$(INTDIR)"
	$(CPP) @<<
  $(CPP_SWITCHES) $(SOURCE)
<<


!ELSEIF  "$(CFG)" == "jeditshell - Win32 Unicode Debug"

CPP_SWITCHES=/nologo /MTd /W3 /Gm /ZI /Od /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_USRDLL" /D "_UNICODE" /Fp"$(INTDIR)\jeditshell.pch" /Yc"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /GZ /c 

"$(INTDIR)\StdAfx.obj"	"$(INTDIR)\jeditshell.pch" : $(SOURCE) "$(INTDIR)"
	$(CPP) @<<
  $(CPP_SWITCHES) $(SOURCE)
<<


!ELSEIF  "$(CFG)" == "jeditshell - Win32 Release MinSize"

CPP_SWITCHES=/nologo /MD /W3 /O1 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "_ATL_DLL" /D "_ATL_MIN_CRT" /Fp"$(INTDIR)\jeditshell.pch" /Yc"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 

"$(INTDIR)\StdAfx.obj"	"$(INTDIR)\jeditshell.pch" : $(SOURCE) "$(INTDIR)"
	$(CPP) @<<
  $(CPP_SWITCHES) $(SOURCE)
<<


!ELSEIF  "$(CFG)" == "jeditshell - Win32 Release MinDependency"

CPP_SWITCHES=/nologo /MD /W3 /O1 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "_ATL_STATIC_REGISTRY" /Fp"$(INTDIR)\jeditshell.pch" /Yc"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 

"$(INTDIR)\StdAfx.obj"	"$(INTDIR)\jeditshell.pch" : $(SOURCE) "$(INTDIR)"
	$(CPP) @<<
  $(CPP_SWITCHES) $(SOURCE)
<<


!ELSEIF  "$(CFG)" == "jeditshell - Win32 Unicode Release MinSize"

CPP_SWITCHES=/nologo /MT /W3 /O1 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_USRDLL" /D "_UNICODE" /D "_ATL_DLL" /D "_ATL_MIN_CRT" /Fp"$(INTDIR)\jeditshell.pch" /Yc"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 

"$(INTDIR)\StdAfx.obj"	"$(INTDIR)\jeditshell.pch" : $(SOURCE) "$(INTDIR)"
	$(CPP) @<<
  $(CPP_SWITCHES) $(SOURCE)
<<


!ELSEIF  "$(CFG)" == "jeditshell - Win32 Unicode Release MinDependency"

CPP_SWITCHES=/nologo /MT /W3 /O1 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_USRDLL" /D "_UNICODE" /D "_ATL_STATIC_REGISTRY" /D "_ATL_MIN_CRT" /Fp"$(INTDIR)\jeditshell.pch" /Yc"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 

"$(INTDIR)\StdAfx.obj"	"$(INTDIR)\jeditshell.pch" : $(SOURCE) "$(INTDIR)"
	$(CPP) @<<
  $(CPP_SWITCHES) $(SOURCE)
<<


!ENDIF 


!ENDIF 


# Microsoft Developer Studio Generated NMAKE File, Based on jeditlauncher.dsp
!IF "$(CFG)" == ""
CFG=jeditlauncher - Win32 Debug
!MESSAGE No configuration specified. Defaulting to jeditlauncher - Win32 Debug.
!ENDIF 

!IF "$(CFG)" != "jeditlauncher - Win32 Debug" && "$(CFG)" != "jeditlauncher - Win32 Unicode Debug" && "$(CFG)" != "jeditlauncher - Win32 Release MinSize" && "$(CFG)" != "jeditlauncher - Win32 Release MinDependency" && "$(CFG)" != "jeditlauncher - Win32 Unicode Release MinSize" && "$(CFG)" != "jeditlauncher - Win32 Unicode Release MinDependency"
!MESSAGE Invalid configuration "$(CFG)" specified.
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "jeditlauncher.mak" CFG="jeditlauncher - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "jeditlauncher - Win32 Debug" (based on "Win32 (x86) Application")
!MESSAGE "jeditlauncher - Win32 Unicode Debug" (based on "Win32 (x86) Application")
!MESSAGE "jeditlauncher - Win32 Release MinSize" (based on "Win32 (x86) Application")
!MESSAGE "jeditlauncher - Win32 Release MinDependency" (based on "Win32 (x86) Application")
!MESSAGE "jeditlauncher - Win32 Unicode Release MinSize" (based on "Win32 (x86) Application")
!MESSAGE "jeditlauncher - Win32 Unicode Release MinDependency" (based on "Win32 (x86) Application")
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

!IF  "$(CFG)" == "jeditlauncher - Win32 Debug"

OUTDIR=.\Debug
INTDIR=.\Debug
# Begin Custom Macros
OutDir=.\Debug
# End Custom Macros

ALL : "$(OUTDIR)\jeditsrv.exe" ".\jeditlauncher.tlb" ".\jeditlauncher.h" ".\jeditlauncher_i.c" "$(OUTDIR)\jeditlauncher.bsc" ".\Debug\regsvr32.trg"


CLEAN :
	-@erase "$(INTDIR)\FileList.obj"
	-@erase "$(INTDIR)\FileList.sbr"
	-@erase "$(INTDIR)\jeditlauncher.obj"
	-@erase "$(INTDIR)\jeditlauncher.pch"
	-@erase "$(INTDIR)\jeditlauncher.res"
	-@erase "$(INTDIR)\jeditlauncher.sbr"
	-@erase "$(INTDIR)\JELauncher.obj"
	-@erase "$(INTDIR)\JELauncher.sbr"
	-@erase "$(INTDIR)\LauncherLog.obj"
	-@erase "$(INTDIR)\LauncherLog.sbr"
	-@erase "$(INTDIR)\RegistryParser.obj"
	-@erase "$(INTDIR)\RegistryParser.sbr"
	-@erase "$(INTDIR)\ScriptServer.obj"
	-@erase "$(INTDIR)\ScriptServer.sbr"
	-@erase "$(INTDIR)\ScriptWriter.obj"
	-@erase "$(INTDIR)\ScriptWriter.sbr"
	-@erase "$(INTDIR)\ServConn.obj"
	-@erase "$(INTDIR)\ServConn.sbr"
	-@erase "$(INTDIR)\StdAfx.obj"
	-@erase "$(INTDIR)\StdAfx.sbr"
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(INTDIR)\vc60.pdb"
	-@erase "$(OUTDIR)\jeditlauncher.bsc"
	-@erase "$(OUTDIR)\jeditsrv.exe"
	-@erase "$(OUTDIR)\jeditsrv.ilk"
	-@erase "$(OUTDIR)\jeditsrv.pdb"
	-@erase ".\jeditlauncher.h"
	-@erase ".\jeditlauncher.tlb"
	-@erase ".\jeditlauncher_i.c"
	-@erase ".\Debug\regsvr32.trg"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

CPP_PROJ=/nologo /MLd /W3 /Gm /ZI /Od /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_MBCS" /FR"$(INTDIR)\\" /Fp"$(INTDIR)\jeditlauncher.pch" /Yu"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /GZ /c 
RSC_PROJ=/l 0x409 /fo"$(INTDIR)\jeditlauncher.res" /d "_DEBUG" 
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\jeditlauncher.bsc" 
BSC32_SBRS= \
	"$(INTDIR)\FileList.sbr" \
	"$(INTDIR)\jeditlauncher.sbr" \
	"$(INTDIR)\JELauncher.sbr" \
	"$(INTDIR)\LauncherLog.sbr" \
	"$(INTDIR)\RegistryParser.sbr" \
	"$(INTDIR)\ScriptServer.sbr" \
	"$(INTDIR)\ScriptWriter.sbr" \
	"$(INTDIR)\ServConn.sbr" \
	"$(INTDIR)\StdAfx.sbr"

"$(OUTDIR)\jeditlauncher.bsc" : "$(OUTDIR)" $(BSC32_SBRS)
    $(BSC32) @<<
  $(BSC32_FLAGS) $(BSC32_SBRS)
<<

LINK32=link.exe
LINK32_FLAGS=kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib ws2_32.lib /nologo /subsystem:windows /incremental:yes /pdb:"$(OUTDIR)\jeditsrv.pdb" /debug /machine:I386 /out:"$(OUTDIR)\jeditsrv.exe" /pdbtype:sept 
LINK32_OBJS= \
	"$(INTDIR)\FileList.obj" \
	"$(INTDIR)\jeditlauncher.obj" \
	"$(INTDIR)\JELauncher.obj" \
	"$(INTDIR)\LauncherLog.obj" \
	"$(INTDIR)\RegistryParser.obj" \
	"$(INTDIR)\ScriptServer.obj" \
	"$(INTDIR)\ScriptWriter.obj" \
	"$(INTDIR)\ServConn.obj" \
	"$(INTDIR)\StdAfx.obj" \
	"$(INTDIR)\jeditlauncher.res"

"$(OUTDIR)\jeditsrv.exe" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

OutDir=.\Debug
TargetPath=.\Debug\jeditsrv.exe
InputPath=.\Debug\jeditsrv.exe
SOURCE="$(InputPath)"

"$(OUTDIR)\regsvr32.trg" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	<<tempfile.bat 
	@echo off 
	copy "$(TargetPath)" "G:\Program Files\jEdit\jeditsrv.exe" 
	G:\Progra~1\jEdit\jeditsrv.exe  /RegServer 
	echo regsvr32 exec. time > "$(OutDir)\regsvr32.trg" 
	echo Server registration done! 
<< 
	

!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Unicode Debug"

OUTDIR=.\DebugU
INTDIR=.\DebugU
# Begin Custom Macros
OutDir=.\DebugU
# End Custom Macros

ALL : "$(OUTDIR)\jeditlauncher.exe" ".\jeditlauncher.tlb" ".\jeditlauncher.h" ".\jeditlauncher_i.c" ".\DebugU\regsvr32.trg"


CLEAN :
	-@erase "$(INTDIR)\FileList.obj"
	-@erase "$(INTDIR)\jeditlauncher.obj"
	-@erase "$(INTDIR)\jeditlauncher.pch"
	-@erase "$(INTDIR)\jeditlauncher.res"
	-@erase "$(INTDIR)\JELauncher.obj"
	-@erase "$(INTDIR)\LauncherLog.obj"
	-@erase "$(INTDIR)\RegistryParser.obj"
	-@erase "$(INTDIR)\ScriptServer.obj"
	-@erase "$(INTDIR)\ScriptWriter.obj"
	-@erase "$(INTDIR)\ServConn.obj"
	-@erase "$(INTDIR)\StdAfx.obj"
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(INTDIR)\vc60.pdb"
	-@erase "$(OUTDIR)\jeditlauncher.exe"
	-@erase "$(OUTDIR)\jeditlauncher.ilk"
	-@erase "$(OUTDIR)\jeditlauncher.pdb"
	-@erase ".\jeditlauncher.h"
	-@erase ".\jeditlauncher.tlb"
	-@erase ".\jeditlauncher_i.c"
	-@erase ".\DebugU\regsvr32.trg"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

CPP_PROJ=/nologo /MLd /W3 /Gm /ZI /Od /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_UNICODE" /Fp"$(INTDIR)\jeditlauncher.pch" /Yu"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /GZ /c 
RSC_PROJ=/l 0x409 /fo"$(INTDIR)\jeditlauncher.res" /d "_DEBUG" 
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\jeditlauncher.bsc" 
BSC32_SBRS= \
	
LINK32=link.exe
LINK32_FLAGS=kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib ws2_32.lib /nologo /entry:"wWinMainCRTStartup" /subsystem:windows /incremental:yes /pdb:"$(OUTDIR)\jeditlauncher.pdb" /debug /machine:I386 /out:"$(OUTDIR)\jeditlauncher.exe" /pdbtype:sept 
LINK32_OBJS= \
	"$(INTDIR)\FileList.obj" \
	"$(INTDIR)\jeditlauncher.obj" \
	"$(INTDIR)\JELauncher.obj" \
	"$(INTDIR)\LauncherLog.obj" \
	"$(INTDIR)\RegistryParser.obj" \
	"$(INTDIR)\ScriptServer.obj" \
	"$(INTDIR)\ScriptWriter.obj" \
	"$(INTDIR)\ServConn.obj" \
	"$(INTDIR)\StdAfx.obj" \
	"$(INTDIR)\jeditlauncher.res"

"$(OUTDIR)\jeditlauncher.exe" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

OutDir=.\DebugU
TargetPath=.\DebugU\jeditlauncher.exe
InputPath=.\DebugU\jeditlauncher.exe
SOURCE="$(InputPath)"

"$(OUTDIR)\regsvr32.trg" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	<<tempfile.bat 
	@echo off 
	if "%OS%"=="" goto NOTNT 
	if not "%OS%"=="Windows_NT" goto NOTNT 
	"$(TargetPath)" /RegServer 
	echo regsvr32 exec. time > "$(OutDir)\regsvr32.trg" 
	echo Server registration done! 
	goto end 
	:NOTNT 
	echo Warning : Cannot register Unicode EXE on Windows 95 
	:end 
<< 
	

!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Release MinSize"

OUTDIR=.\ReleaseMinSize
INTDIR=.\ReleaseMinSize
# Begin Custom Macros
OutDir=.\ReleaseMinSize
# End Custom Macros

ALL : "$(OUTDIR)\jeditlauncher.exe" ".\ReleaseMinSize\regsvr32.trg"


CLEAN :
	-@erase "$(INTDIR)\FileList.obj"
	-@erase "$(INTDIR)\jeditlauncher.obj"
	-@erase "$(INTDIR)\jeditlauncher.pch"
	-@erase "$(INTDIR)\jeditlauncher.res"
	-@erase "$(INTDIR)\JELauncher.obj"
	-@erase "$(INTDIR)\LauncherLog.obj"
	-@erase "$(INTDIR)\RegistryParser.obj"
	-@erase "$(INTDIR)\ScriptServer.obj"
	-@erase "$(INTDIR)\ScriptWriter.obj"
	-@erase "$(INTDIR)\ServConn.obj"
	-@erase "$(INTDIR)\StdAfx.obj"
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(OUTDIR)\jeditlauncher.exe"
	-@erase ".\jeditlauncher.h"
	-@erase ".\jeditlauncher.tlb"
	-@erase ".\jeditlauncher_i.c"
	-@erase ".\ReleaseMinSize\regsvr32.trg"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

CPP_PROJ=/nologo /ML /W3 /O1 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /D "_ATL_DLL" /D "_ATL_MIN_CRT" /Fp"$(INTDIR)\jeditlauncher.pch" /Yu"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 
RSC_PROJ=/l 0x409 /fo"$(INTDIR)\jeditlauncher.res" /d "NDEBUG" 
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\jeditlauncher.bsc" 
BSC32_SBRS= \
	
LINK32=link.exe
LINK32_FLAGS=kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib ws2_32.lib /nologo /subsystem:windows /incremental:no /pdb:"$(OUTDIR)\jeditlauncher.pdb" /machine:I386 /out:"$(OUTDIR)\jeditlauncher.exe" 
LINK32_OBJS= \
	"$(INTDIR)\FileList.obj" \
	"$(INTDIR)\jeditlauncher.obj" \
	"$(INTDIR)\JELauncher.obj" \
	"$(INTDIR)\LauncherLog.obj" \
	"$(INTDIR)\RegistryParser.obj" \
	"$(INTDIR)\ScriptServer.obj" \
	"$(INTDIR)\ScriptWriter.obj" \
	"$(INTDIR)\ServConn.obj" \
	"$(INTDIR)\StdAfx.obj" \
	"$(INTDIR)\jeditlauncher.res"

"$(OUTDIR)\jeditlauncher.exe" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

OutDir=.\ReleaseMinSize
TargetPath=.\ReleaseMinSize\jeditlauncher.exe
InputPath=.\ReleaseMinSize\jeditlauncher.exe
SOURCE="$(InputPath)"

"$(OUTDIR)\regsvr32.trg" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	<<tempfile.bat 
	@echo off 
	"$(TargetPath)" /RegServer 
	echo regsvr32 exec. time > "$(OutDir)\regsvr32.trg" 
	echo Server registration done! 
<< 
	

!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Release MinDependency"

OUTDIR=.\ReleaseMinDependency
INTDIR=.\ReleaseMinDependency
# Begin Custom Macros
OutDir=.\ReleaseMinDependency
# End Custom Macros

ALL : "$(OUTDIR)\jeditsrv.exe" "$(OUTDIR)\jeditlauncher.bsc" ".\ReleaseMinDependency\regsvr32.trg"


CLEAN :
	-@erase "$(INTDIR)\FileList.obj"
	-@erase "$(INTDIR)\FileList.sbr"
	-@erase "$(INTDIR)\jeditlauncher.obj"
	-@erase "$(INTDIR)\jeditlauncher.pch"
	-@erase "$(INTDIR)\jeditlauncher.res"
	-@erase "$(INTDIR)\jeditlauncher.sbr"
	-@erase "$(INTDIR)\JELauncher.obj"
	-@erase "$(INTDIR)\JELauncher.sbr"
	-@erase "$(INTDIR)\LauncherLog.obj"
	-@erase "$(INTDIR)\LauncherLog.sbr"
	-@erase "$(INTDIR)\RegistryParser.obj"
	-@erase "$(INTDIR)\RegistryParser.sbr"
	-@erase "$(INTDIR)\ScriptServer.obj"
	-@erase "$(INTDIR)\ScriptServer.sbr"
	-@erase "$(INTDIR)\ScriptWriter.obj"
	-@erase "$(INTDIR)\ScriptWriter.sbr"
	-@erase "$(INTDIR)\ServConn.obj"
	-@erase "$(INTDIR)\ServConn.sbr"
	-@erase "$(INTDIR)\StdAfx.obj"
	-@erase "$(INTDIR)\StdAfx.sbr"
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(OUTDIR)\jeditlauncher.bsc"
	-@erase "$(OUTDIR)\jeditsrv.exe"
	-@erase ".\jeditlauncher.h"
	-@erase ".\jeditlauncher.tlb"
	-@erase ".\jeditlauncher_i.c"
	-@erase ".\ReleaseMinDependency\regsvr32.trg"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

CPP_PROJ=/nologo /MT /W3 /O1 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /D "_ATL_STATIC_REGISTRY" /D "_ATL_MIN_CRT" /D "SPECIAL_BUILD" /FR"$(INTDIR)\\" /Fp"$(INTDIR)\jeditlauncher.pch" /Yu"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 
RSC_PROJ=/l 0x409 /fo"$(INTDIR)\jeditlauncher.res" /d "NDEBUG" 
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\jeditlauncher.bsc" 
BSC32_SBRS= \
	"$(INTDIR)\FileList.sbr" \
	"$(INTDIR)\jeditlauncher.sbr" \
	"$(INTDIR)\JELauncher.sbr" \
	"$(INTDIR)\LauncherLog.sbr" \
	"$(INTDIR)\RegistryParser.sbr" \
	"$(INTDIR)\ScriptServer.sbr" \
	"$(INTDIR)\ScriptWriter.sbr" \
	"$(INTDIR)\ServConn.sbr" \
	"$(INTDIR)\StdAfx.sbr"

"$(OUTDIR)\jeditlauncher.bsc" : "$(OUTDIR)" $(BSC32_SBRS)
    $(BSC32) @<<
  $(BSC32_FLAGS) $(BSC32_SBRS)
<<

LINK32=link.exe
LINK32_FLAGS=kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib ws2_32.lib /nologo /subsystem:windows /incremental:no /pdb:"$(OUTDIR)\jeditsrv.pdb" /machine:I386 /out:"$(OUTDIR)\jeditsrv.exe" 
LINK32_OBJS= \
	"$(INTDIR)\FileList.obj" \
	"$(INTDIR)\jeditlauncher.obj" \
	"$(INTDIR)\JELauncher.obj" \
	"$(INTDIR)\LauncherLog.obj" \
	"$(INTDIR)\RegistryParser.obj" \
	"$(INTDIR)\ScriptServer.obj" \
	"$(INTDIR)\ScriptWriter.obj" \
	"$(INTDIR)\ServConn.obj" \
	"$(INTDIR)\StdAfx.obj" \
	"$(INTDIR)\jeditlauncher.res"

"$(OUTDIR)\jeditsrv.exe" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

OutDir=.\ReleaseMinDependency
TargetPath=.\ReleaseMinDependency\jeditsrv.exe
InputPath=.\ReleaseMinDependency\jeditsrv.exe
SOURCE="$(InputPath)"

"$(OUTDIR)\regsvr32.trg" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	<<tempfile.bat 
	@echo off 
	copy "$(TargetPath)" "G:\Program Files\jEdit\jeditsrv.exe" 
	G:\Progra~1\jEdit\jeditsrv.exe  /RegServer 
	echo regsvr32 exec. time > "$(OutDir)\regsvr32.trg" 
	echo Server registration done! 
<< 
	

!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Unicode Release MinSize"

OUTDIR=.\ReleaseUMinSize
INTDIR=.\ReleaseUMinSize
# Begin Custom Macros
OutDir=.\ReleaseUMinSize
# End Custom Macros

ALL : "$(OUTDIR)\jeditlauncher.exe" ".\jeditlauncher.tlb" ".\jeditlauncher.h" ".\jeditlauncher_i.c" ".\ReleaseUMinSize\regsvr32.trg"


CLEAN :
	-@erase "$(INTDIR)\FileList.obj"
	-@erase "$(INTDIR)\jeditlauncher.obj"
	-@erase "$(INTDIR)\jeditlauncher.pch"
	-@erase "$(INTDIR)\jeditlauncher.res"
	-@erase "$(INTDIR)\JELauncher.obj"
	-@erase "$(INTDIR)\LauncherLog.obj"
	-@erase "$(INTDIR)\RegistryParser.obj"
	-@erase "$(INTDIR)\ScriptServer.obj"
	-@erase "$(INTDIR)\ScriptWriter.obj"
	-@erase "$(INTDIR)\ServConn.obj"
	-@erase "$(INTDIR)\StdAfx.obj"
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(OUTDIR)\jeditlauncher.exe"
	-@erase ".\jeditlauncher.h"
	-@erase ".\jeditlauncher.tlb"
	-@erase ".\jeditlauncher_i.c"
	-@erase ".\ReleaseUMinSize\regsvr32.trg"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

CPP_PROJ=/nologo /ML /W3 /O1 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_UNICODE" /D "_ATL_DLL" /D "_ATL_MIN_CRT" /Fp"$(INTDIR)\jeditlauncher.pch" /Yu"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 
RSC_PROJ=/l 0x409 /fo"$(INTDIR)\jeditlauncher.res" /d "NDEBUG" 
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\jeditlauncher.bsc" 
BSC32_SBRS= \
	
LINK32=link.exe
LINK32_FLAGS=kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib ws2_32.lib /nologo /subsystem:windows /incremental:no /pdb:"$(OUTDIR)\jeditlauncher.pdb" /machine:I386 /out:"$(OUTDIR)\jeditlauncher.exe" 
LINK32_OBJS= \
	"$(INTDIR)\FileList.obj" \
	"$(INTDIR)\jeditlauncher.obj" \
	"$(INTDIR)\JELauncher.obj" \
	"$(INTDIR)\LauncherLog.obj" \
	"$(INTDIR)\RegistryParser.obj" \
	"$(INTDIR)\ScriptServer.obj" \
	"$(INTDIR)\ScriptWriter.obj" \
	"$(INTDIR)\ServConn.obj" \
	"$(INTDIR)\StdAfx.obj" \
	"$(INTDIR)\jeditlauncher.res"

"$(OUTDIR)\jeditlauncher.exe" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

OutDir=.\ReleaseUMinSize
TargetPath=.\ReleaseUMinSize\jeditlauncher.exe
InputPath=.\ReleaseUMinSize\jeditlauncher.exe
SOURCE="$(InputPath)"

"$(OUTDIR)\regsvr32.trg" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	<<tempfile.bat 
	@echo off 
	if "%OS%"=="" goto NOTNT 
	if not "%OS%"=="Windows_NT" goto NOTNT 
	"$(TargetPath)" /RegServer 
	echo regsvr32 exec. time > "$(OutDir)\regsvr32.trg" 
	echo Server registration done! 
	goto end 
	:NOTNT 
	echo Warning : Cannot register Unicode EXE on Windows 95 
	:end 
<< 
	

!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Unicode Release MinDependency"

OUTDIR=.\ReleaseUMinDependency
INTDIR=.\ReleaseUMinDependency
# Begin Custom Macros
OutDir=.\ReleaseUMinDependency
# End Custom Macros

ALL : "$(OUTDIR)\jeditlauncher.exe" ".\jeditlauncher.tlb" ".\jeditlauncher.h" ".\jeditlauncher_i.c" ".\ReleaseUMinDependency\regsvr32.trg"


CLEAN :
	-@erase "$(INTDIR)\FileList.obj"
	-@erase "$(INTDIR)\jeditlauncher.obj"
	-@erase "$(INTDIR)\jeditlauncher.pch"
	-@erase "$(INTDIR)\jeditlauncher.res"
	-@erase "$(INTDIR)\JELauncher.obj"
	-@erase "$(INTDIR)\LauncherLog.obj"
	-@erase "$(INTDIR)\RegistryParser.obj"
	-@erase "$(INTDIR)\ScriptServer.obj"
	-@erase "$(INTDIR)\ScriptWriter.obj"
	-@erase "$(INTDIR)\ServConn.obj"
	-@erase "$(INTDIR)\StdAfx.obj"
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(OUTDIR)\jeditlauncher.exe"
	-@erase ".\jeditlauncher.h"
	-@erase ".\jeditlauncher.tlb"
	-@erase ".\jeditlauncher_i.c"
	-@erase ".\ReleaseUMinDependency\regsvr32.trg"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

CPP_PROJ=/nologo /ML /W3 /O1 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_UNICODE" /D "_ATL_STATIC_REGISTRY" /D "_ATL_MIN_CRT" /Fp"$(INTDIR)\jeditlauncher.pch" /Yu"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 
RSC_PROJ=/l 0x409 /fo"$(INTDIR)\jeditlauncher.res" /d "NDEBUG" 
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\jeditlauncher.bsc" 
BSC32_SBRS= \
	
LINK32=link.exe
LINK32_FLAGS=kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib ws2_32.lib /nologo /subsystem:windows /incremental:no /pdb:"$(OUTDIR)\jeditlauncher.pdb" /machine:I386 /out:"$(OUTDIR)\jeditlauncher.exe" 
LINK32_OBJS= \
	"$(INTDIR)\FileList.obj" \
	"$(INTDIR)\jeditlauncher.obj" \
	"$(INTDIR)\JELauncher.obj" \
	"$(INTDIR)\LauncherLog.obj" \
	"$(INTDIR)\RegistryParser.obj" \
	"$(INTDIR)\ScriptServer.obj" \
	"$(INTDIR)\ScriptWriter.obj" \
	"$(INTDIR)\ServConn.obj" \
	"$(INTDIR)\StdAfx.obj" \
	"$(INTDIR)\jeditlauncher.res"

"$(OUTDIR)\jeditlauncher.exe" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

OutDir=.\ReleaseUMinDependency
TargetPath=.\ReleaseUMinDependency\jeditlauncher.exe
InputPath=.\ReleaseUMinDependency\jeditlauncher.exe
SOURCE="$(InputPath)"

"$(OUTDIR)\regsvr32.trg" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	<<tempfile.bat 
	@echo off 
	if "%OS%"=="" goto NOTNT 
	if not "%OS%"=="Windows_NT" goto NOTNT 
	"$(TargetPath)" /RegServer 
	echo regsvr32 exec. time > "$(OutDir)\regsvr32.trg" 
	echo Server registration done! 
	goto end 
	:NOTNT 
	echo Warning : Cannot register Unicode EXE on Windows 95 
	:end 
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
!IF EXISTS("jeditlauncher.dep")
!INCLUDE "jeditlauncher.dep"
!ELSE 
!MESSAGE Warning: cannot find "jeditlauncher.dep"
!ENDIF 
!ENDIF 


!IF "$(CFG)" == "jeditlauncher - Win32 Debug" || "$(CFG)" == "jeditlauncher - Win32 Unicode Debug" || "$(CFG)" == "jeditlauncher - Win32 Release MinSize" || "$(CFG)" == "jeditlauncher - Win32 Release MinDependency" || "$(CFG)" == "jeditlauncher - Win32 Unicode Release MinSize" || "$(CFG)" == "jeditlauncher - Win32 Unicode Release MinDependency"
SOURCE=.\FileList.cpp

!IF  "$(CFG)" == "jeditlauncher - Win32 Debug"


"$(INTDIR)\FileList.obj"	"$(INTDIR)\FileList.sbr" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Unicode Debug"


"$(INTDIR)\FileList.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Release MinSize"


"$(INTDIR)\FileList.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch" ".\jeditlauncher.h"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Release MinDependency"


"$(INTDIR)\FileList.obj"	"$(INTDIR)\FileList.sbr" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch" ".\jeditlauncher.h"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Unicode Release MinSize"


"$(INTDIR)\FileList.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Unicode Release MinDependency"


"$(INTDIR)\FileList.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ENDIF 

SOURCE=.\jeditlauncher.cpp

!IF  "$(CFG)" == "jeditlauncher - Win32 Debug"


"$(INTDIR)\jeditlauncher.obj"	"$(INTDIR)\jeditlauncher.sbr" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Unicode Debug"


"$(INTDIR)\jeditlauncher.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Release MinSize"


"$(INTDIR)\jeditlauncher.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch" ".\jeditlauncher_i.c" ".\jeditlauncher.h"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Release MinDependency"


"$(INTDIR)\jeditlauncher.obj"	"$(INTDIR)\jeditlauncher.sbr" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch" ".\jeditlauncher_i.c" ".\jeditlauncher.h"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Unicode Release MinSize"


"$(INTDIR)\jeditlauncher.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Unicode Release MinDependency"


"$(INTDIR)\jeditlauncher.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ENDIF 

SOURCE=.\jeditlauncher.idl
MTL_SWITCHES=/tlb ".\jeditlauncher.tlb" /h "jeditlauncher.h" /iid "jeditlauncher_i.c" /Oicf 

".\jeditlauncher.tlb"	".\jeditlauncher.h"	".\jeditlauncher_i.c" : $(SOURCE) "$(INTDIR)"
	$(MTL) @<<
  $(MTL_SWITCHES) $(SOURCE)
<<


SOURCE=.\jeditlauncher.rc

"$(INTDIR)\jeditlauncher.res" : $(SOURCE) "$(INTDIR)"
	$(RSC) $(RSC_PROJ) $(SOURCE)


SOURCE=.\JELauncher.cpp

!IF  "$(CFG)" == "jeditlauncher - Win32 Debug"


"$(INTDIR)\JELauncher.obj"	"$(INTDIR)\JELauncher.sbr" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Unicode Debug"


"$(INTDIR)\JELauncher.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Release MinSize"


"$(INTDIR)\JELauncher.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch" ".\jeditlauncher.h"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Release MinDependency"


"$(INTDIR)\JELauncher.obj"	"$(INTDIR)\JELauncher.sbr" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch" ".\jeditlauncher.h"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Unicode Release MinSize"


"$(INTDIR)\JELauncher.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Unicode Release MinDependency"


"$(INTDIR)\JELauncher.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ENDIF 

SOURCE=.\LauncherLog.cpp

!IF  "$(CFG)" == "jeditlauncher - Win32 Debug"


"$(INTDIR)\LauncherLog.obj"	"$(INTDIR)\LauncherLog.sbr" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Unicode Debug"


"$(INTDIR)\LauncherLog.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Release MinSize"


"$(INTDIR)\LauncherLog.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Release MinDependency"


"$(INTDIR)\LauncherLog.obj"	"$(INTDIR)\LauncherLog.sbr" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Unicode Release MinSize"


"$(INTDIR)\LauncherLog.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Unicode Release MinDependency"


"$(INTDIR)\LauncherLog.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ENDIF 

SOURCE=.\RegistryParser.cpp

!IF  "$(CFG)" == "jeditlauncher - Win32 Debug"


"$(INTDIR)\RegistryParser.obj"	"$(INTDIR)\RegistryParser.sbr" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Unicode Debug"


"$(INTDIR)\RegistryParser.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Release MinSize"


"$(INTDIR)\RegistryParser.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch" ".\jeditlauncher.h"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Release MinDependency"


"$(INTDIR)\RegistryParser.obj"	"$(INTDIR)\RegistryParser.sbr" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch" ".\jeditlauncher.h"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Unicode Release MinSize"


"$(INTDIR)\RegistryParser.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Unicode Release MinDependency"


"$(INTDIR)\RegistryParser.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ENDIF 

SOURCE=.\ScriptServer.cpp

!IF  "$(CFG)" == "jeditlauncher - Win32 Debug"


"$(INTDIR)\ScriptServer.obj"	"$(INTDIR)\ScriptServer.sbr" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Unicode Debug"


"$(INTDIR)\ScriptServer.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Release MinSize"


"$(INTDIR)\ScriptServer.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Release MinDependency"


"$(INTDIR)\ScriptServer.obj"	"$(INTDIR)\ScriptServer.sbr" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Unicode Release MinSize"


"$(INTDIR)\ScriptServer.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Unicode Release MinDependency"


"$(INTDIR)\ScriptServer.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ENDIF 

SOURCE=.\ScriptWriter.cpp

!IF  "$(CFG)" == "jeditlauncher - Win32 Debug"


"$(INTDIR)\ScriptWriter.obj"	"$(INTDIR)\ScriptWriter.sbr" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Unicode Debug"


"$(INTDIR)\ScriptWriter.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Release MinSize"


"$(INTDIR)\ScriptWriter.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch" ".\jeditlauncher.h"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Release MinDependency"


"$(INTDIR)\ScriptWriter.obj"	"$(INTDIR)\ScriptWriter.sbr" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch" ".\jeditlauncher.h"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Unicode Release MinSize"


"$(INTDIR)\ScriptWriter.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Unicode Release MinDependency"


"$(INTDIR)\ScriptWriter.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ENDIF 

SOURCE=.\ServConn.cpp

!IF  "$(CFG)" == "jeditlauncher - Win32 Debug"


"$(INTDIR)\ServConn.obj"	"$(INTDIR)\ServConn.sbr" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Unicode Debug"


"$(INTDIR)\ServConn.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Release MinSize"


"$(INTDIR)\ServConn.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Release MinDependency"


"$(INTDIR)\ServConn.obj"	"$(INTDIR)\ServConn.sbr" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch" ".\jeditlauncher.h"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Unicode Release MinSize"


"$(INTDIR)\ServConn.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Unicode Release MinDependency"


"$(INTDIR)\ServConn.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\jeditlauncher.pch"


!ENDIF 

SOURCE=.\StdAfx.cpp

!IF  "$(CFG)" == "jeditlauncher - Win32 Debug"

CPP_SWITCHES=/nologo /MLd /W3 /Gm /ZI /Od /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_MBCS" /FR"$(INTDIR)\\" /Fp"$(INTDIR)\jeditlauncher.pch" /Yc"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /GZ /c 

"$(INTDIR)\StdAfx.obj"	"$(INTDIR)\StdAfx.sbr"	"$(INTDIR)\jeditlauncher.pch" : $(SOURCE) "$(INTDIR)"
	$(CPP) @<<
  $(CPP_SWITCHES) $(SOURCE)
<<


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Unicode Debug"

CPP_SWITCHES=/nologo /MLd /W3 /Gm /ZI /Od /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_UNICODE" /Fp"$(INTDIR)\jeditlauncher.pch" /Yc"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /GZ /c 

"$(INTDIR)\StdAfx.obj"	"$(INTDIR)\jeditlauncher.pch" : $(SOURCE) "$(INTDIR)"
	$(CPP) @<<
  $(CPP_SWITCHES) $(SOURCE)
<<


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Release MinSize"

CPP_SWITCHES=/nologo /ML /W3 /O1 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /D "_ATL_DLL" /D "_ATL_MIN_CRT" /Fp"$(INTDIR)\jeditlauncher.pch" /Yc"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 

"$(INTDIR)\StdAfx.obj"	"$(INTDIR)\jeditlauncher.pch" : $(SOURCE) "$(INTDIR)"
	$(CPP) @<<
  $(CPP_SWITCHES) $(SOURCE)
<<


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Release MinDependency"

CPP_SWITCHES=/nologo /MT /W3 /O1 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /D "_ATL_STATIC_REGISTRY" /D "_ATL_MIN_CRT" /D "SPECIAL_BUILD" /FR"$(INTDIR)\\" /Fp"$(INTDIR)\jeditlauncher.pch" /Yc"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 

"$(INTDIR)\StdAfx.obj"	"$(INTDIR)\StdAfx.sbr"	"$(INTDIR)\jeditlauncher.pch" : $(SOURCE) "$(INTDIR)"
	$(CPP) @<<
  $(CPP_SWITCHES) $(SOURCE)
<<


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Unicode Release MinSize"

CPP_SWITCHES=/nologo /ML /W3 /O1 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_UNICODE" /D "_ATL_DLL" /D "_ATL_MIN_CRT" /Fp"$(INTDIR)\jeditlauncher.pch" /Yc"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 

"$(INTDIR)\StdAfx.obj"	"$(INTDIR)\jeditlauncher.pch" : $(SOURCE) "$(INTDIR)"
	$(CPP) @<<
  $(CPP_SWITCHES) $(SOURCE)
<<


!ELSEIF  "$(CFG)" == "jeditlauncher - Win32 Unicode Release MinDependency"

CPP_SWITCHES=/nologo /ML /W3 /O1 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_UNICODE" /D "_ATL_STATIC_REGISTRY" /D "_ATL_MIN_CRT" /Fp"$(INTDIR)\jeditlauncher.pch" /Yc"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 

"$(INTDIR)\StdAfx.obj"	"$(INTDIR)\jeditlauncher.pch" : $(SOURCE) "$(INTDIR)"
	$(CPP) @<<
  $(CPP_SWITCHES) $(SOURCE)
<<


!ENDIF 


!ENDIF 


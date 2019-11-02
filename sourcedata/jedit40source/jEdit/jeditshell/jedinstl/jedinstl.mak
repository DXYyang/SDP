# Microsoft Developer Studio Generated NMAKE File, Based on jedinstl.dsp
!IF "$(CFG)" == ""
CFG=jedinstl - Win32 Debug
!MESSAGE No configuration specified. Defaulting to jedinstl - Win32 Debug.
!ENDIF 

!IF "$(CFG)" != "jedinstl - Win32 Release" && "$(CFG)" != "jedinstl - Win32 Debug"
!MESSAGE Invalid configuration "$(CFG)" specified.
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "jedinstl.mak" CFG="jedinstl - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "jedinstl - Win32 Release" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE "jedinstl - Win32 Debug" (based on "Win32 (x86) Dynamic-Link Library")
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

!IF  "$(CFG)" == "jedinstl - Win32 Release"

OUTDIR=.\Release
INTDIR=.\Release
# Begin Custom Macros
OutDir=.\Release
# End Custom Macros

ALL : "$(OUTDIR)\jedinstl.dll" "$(OUTDIR)\jedinstl.pch"


CLEAN :
	-@erase "$(INTDIR)\DeferFileOps.obj"
	-@erase "$(INTDIR)\InstallData.obj"
	-@erase "$(INTDIR)\InstallerLog.obj"
	-@erase "$(INTDIR)\jedinstl.obj"
	-@erase "$(INTDIR)\jedinstl.pch"
	-@erase "$(INTDIR)\jedinstl.res"
	-@erase "$(INTDIR)\JELInstaller.obj"
	-@erase "$(INTDIR)\JELRegInstaller.obj"
	-@erase "$(INTDIR)\StdAfx.obj"
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(OUTDIR)\jedinstl.dll"
	-@erase "$(OUTDIR)\jedinstl.exp"
	-@erase "$(OUTDIR)\jedinstl.lib"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

CPP_PROJ=/nologo /MT /W3 /GX /O1 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "JEDINSTL_EXPORTS" /Fp"$(INTDIR)\jedinstl.pch" /YX"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 
MTL_PROJ=/nologo /D "NDEBUG" /mktyplib203 /win32 
RSC_PROJ=/l 0x409 /fo"$(INTDIR)\jedinstl.res" /d "NDEBUG" 
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\jedinstl.bsc" 
BSC32_SBRS= \
	
LINK32=link.exe
LINK32_FLAGS=version.lib kernel32.lib user32.lib gdi32.lib comdlg32.lib shell32.lib advapi32.lib ole32.lib oleaut32.lib msvcrt.lib /nologo /dll /incremental:no /pdb:"$(OUTDIR)\jedinstl.pdb" /machine:I386 /nodefaultlib /def:".\jedinstl.def" /out:"$(OUTDIR)\jedinstl.dll" /implib:"$(OUTDIR)\jedinstl.lib" 
DEF_FILE= \
	".\jedinstl.def"
LINK32_OBJS= \
	"$(INTDIR)\DeferFileOps.obj" \
	"$(INTDIR)\InstallData.obj" \
	"$(INTDIR)\InstallerLog.obj" \
	"$(INTDIR)\jedinstl.obj" \
	"$(INTDIR)\JELInstaller.obj" \
	"$(INTDIR)\JELRegInstaller.obj" \
	"$(INTDIR)\StdAfx.obj" \
	"$(INTDIR)\jedinstl.res"

"$(OUTDIR)\jedinstl.dll" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

SOURCE="$(InputPath)"
PostBuild_Desc=Copy dll to jEdit installation
DS_POSTBUILD_DEP=$(INTDIR)\postbld.dep

ALL : $(DS_POSTBUILD_DEP)

# Begin Custom Macros
OutDir=.\Release
# End Custom Macros

$(DS_POSTBUILD_DEP) : "$(OUTDIR)\jedinstl.dll" "$(OUTDIR)\jedinstl.pch"
   copy "I:\cvsfiles\jeditshell\jedinstl\Release\jedinstl.dll" "G:\Program Files\jEdit"
	echo Helper for Post-build step > "$(DS_POSTBUILD_DEP)"

!ELSEIF  "$(CFG)" == "jedinstl - Win32 Debug"

OUTDIR=.\Debug
INTDIR=.\Debug
# Begin Custom Macros
OutDir=.\Debug
# End Custom Macros

ALL : "$(OUTDIR)\jedinstl.dll" ".\jedinstl.dll"


CLEAN :
	-@erase "$(INTDIR)\DeferFileOps.obj"
	-@erase "$(INTDIR)\InstallData.obj"
	-@erase "$(INTDIR)\InstallerLog.obj"
	-@erase "$(INTDIR)\jedinstl.obj"
	-@erase "$(INTDIR)\jedinstl.res"
	-@erase "$(INTDIR)\JELInstaller.obj"
	-@erase "$(INTDIR)\JELRegInstaller.obj"
	-@erase "$(INTDIR)\StdAfx.obj"
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(INTDIR)\vc60.pdb"
	-@erase "$(OUTDIR)\jedinstl.dll"
	-@erase "$(OUTDIR)\jedinstl.exp"
	-@erase "$(OUTDIR)\jedinstl.ilk"
	-@erase "$(OUTDIR)\jedinstl.lib"
	-@erase "$(OUTDIR)\jedinstl.pdb"
	-@erase ".\jedinstl.dll"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

CPP_PROJ=/nologo /MTd /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "JEDINSTL_EXPORTS" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /GZ /c 
MTL_PROJ=/nologo /D "_DEBUG" /mktyplib203 /win32 
RSC_PROJ=/l 0x409 /fo"$(INTDIR)\jedinstl.res" /d "_DEBUG" 
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\jedinstl.bsc" 
BSC32_SBRS= \
	
LINK32=link.exe
LINK32_FLAGS=winspool.lib uuid.lib odbc32.lib odbccp32.lib kernel32.lib user32.lib gdi32.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib version.lib /nologo /dll /incremental:yes /pdb:"$(OUTDIR)\jedinstl.pdb" /debug /machine:I386 /def:".\jedinstl.def" /out:"$(OUTDIR)\jedinstl.dll" /implib:"$(OUTDIR)\jedinstl.lib" /pdbtype:sept 
DEF_FILE= \
	".\jedinstl.def"
LINK32_OBJS= \
	"$(INTDIR)\DeferFileOps.obj" \
	"$(INTDIR)\InstallData.obj" \
	"$(INTDIR)\InstallerLog.obj" \
	"$(INTDIR)\jedinstl.obj" \
	"$(INTDIR)\JELInstaller.obj" \
	"$(INTDIR)\JELRegInstaller.obj" \
	"$(INTDIR)\StdAfx.obj" \
	"$(INTDIR)\jedinstl.res"

"$(OUTDIR)\jedinstl.dll" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

ProjDir=.
InputPath=.\Debug\jedinstl.dll
SOURCE="$(InputPath)"

".\jedinstl.dll" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	<<tempfile.bat 
	@echo off 
	copy  $(InputPath) $(ProjDir)
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


!IF "$(NO_EXTERNAL_DEPS)" != "1"
!IF EXISTS("jedinstl.dep")
!INCLUDE "jedinstl.dep"
!ELSE 
!MESSAGE Warning: cannot find "jedinstl.dep"
!ENDIF 
!ENDIF 


!IF "$(CFG)" == "jedinstl - Win32 Release" || "$(CFG)" == "jedinstl - Win32 Debug"
SOURCE=.\DeferFileOps.cpp

"$(INTDIR)\DeferFileOps.obj" : $(SOURCE) "$(INTDIR)"


SOURCE=.\InstallData.cpp

"$(INTDIR)\InstallData.obj" : $(SOURCE) "$(INTDIR)"


SOURCE=.\InstallerLog.cpp

"$(INTDIR)\InstallerLog.obj" : $(SOURCE) "$(INTDIR)"


SOURCE=.\jedinstl.cpp

"$(INTDIR)\jedinstl.obj" : $(SOURCE) "$(INTDIR)"


SOURCE=.\JELInstaller.cpp

"$(INTDIR)\JELInstaller.obj" : $(SOURCE) "$(INTDIR)"


SOURCE=.\JELRegInstaller.cpp

"$(INTDIR)\JELRegInstaller.obj" : $(SOURCE) "$(INTDIR)"


SOURCE=.\StdAfx.cpp

!IF  "$(CFG)" == "jedinstl - Win32 Release"

CPP_SWITCHES=/nologo /MT /W3 /GX /O1 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "JEDINSTL_EXPORTS" /Fp"$(INTDIR)\jedinstl.pch" /Yc /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 

"$(INTDIR)\StdAfx.obj"	"$(INTDIR)\jedinstl.pch" : $(SOURCE) "$(INTDIR)"
	$(CPP) @<<
  $(CPP_SWITCHES) $(SOURCE)
<<


!ELSEIF  "$(CFG)" == "jedinstl - Win32 Debug"

CPP_SWITCHES=/nologo /MTd /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "JEDINSTL_EXPORTS" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /GZ /c 

"$(INTDIR)\StdAfx.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) @<<
  $(CPP_SWITCHES) $(SOURCE)
<<


!ENDIF 

SOURCE=.\jedinstl.rc

"$(INTDIR)\jedinstl.res" : $(SOURCE) "$(INTDIR)"
	$(RSC) $(RSC_PROJ) $(SOURCE)



!ENDIF 


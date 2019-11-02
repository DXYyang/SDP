


jcmd.exe : jcmd.c
	cl /nologo /MLd /W4 /Gm /ZI /Od /D "WIN32" /D "DEBUG" /D "_MBCS" \
	jcmd.c /link user32.lib advapi32.lib /subsystem:windows \
	/version:1.2 /entry:mainCRTStartup


CLEAN :
	del jcmd2.exe
	del jcmd2.obj


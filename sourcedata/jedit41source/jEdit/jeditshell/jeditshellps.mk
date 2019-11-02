# $Id: jeditshellps.mk,v 1.1.1.1 2001/07/03 13:13:58 jgellene Exp $

jeditshellps.dll: dlldata.obj jeditshell_p.obj jeditshell_i.obj
	link /dll /out:jeditshellps.dll /def:jeditshellps.def /entry:DllMain dlldata.obj jeditshell_p.obj jeditshell_i.obj \
		kernel32.lib rpcndr.lib rpcns4.lib rpcrt4.lib oleaut32.lib uuid.lib \

.c.obj:
	cl /c /Ox /DWIN32 /D_WIN32_WINNT=0x0400 /DREGISTER_PROXY_DLL \
		$<

clean:
	@del jeditshellps.dll
	@del jeditshellps.lib
	@del jeditshellps.exp
	@del dlldata.obj
	@del jeditshell_p.obj
	@del jeditshell_i.obj

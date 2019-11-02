
jeservps.dll: dlldata.obj jeditlauncher_p.obj jeditlauncher_i.obj
	link /dll /out:jeservps.dll /def:jeditlauncherps.def /entry:DllMain dlldata.obj jeditlauncher_p.obj jeditlauncher_i.obj \
		kernel32.lib rpcndr.lib rpcns4.lib rpcrt4.lib oleaut32.lib uuid.lib \

.c.obj:
	cl /c /Ox /DWIN32 /D_WIN32_WINNT=0x0400 /DREGISTER_PROXY_DLL \
		$<

clean:
	@del jeservps.dll
	@del jeservps.lib
	@del jeservps.exp
	@del dlldata.obj
	@del jeditlauncher_p.obj
	@del jeditlauncher_i.obj

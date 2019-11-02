' * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
'
'  File Name:  get_registry.vbs
'
'  Author:     John Gellene (jgellene@nyc.rr.com)
'
'  Date:       Dec 7, 2001, 8:08 AM
'
'  Full path:  I:\cvsfiles\jeditshell\get_registry.vbs
'
'  Description:  VBScript diagnostic script to obtain
'				 Windows Registry entries for jEditLauncher
'				 module; writes output to both standard output
'				 and regdump.txt; does not make changes to
'                registry
'
'  $Id: get_registry.vbs,v 1.3 2002/01/22 20:07:56 jgellene Exp $
'
' * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

Option Explicit
On Error Resume Next
Public Shell, oFS, outPath, cout, textout

Set Shell = CreateObject("WScript.Shell")

' open output streams (standard output and output file)
Set oFS = CreateObject("Scripting.FileSystemObject")
outPath = ".\regdump.txt"
Const StdOut = 1
Set cout = oFS.GetStandardStream(StdOut)
Set textout = oFS.CreateTextFile(outPath, true)
If err <> 0 Then
	cout.WriteLine "Could not create file " & outPath & " for writing"
	cout.WriteLine err.description
	Set textout = Nothing
	err.clear
End If

' output send to both streams
writeLineTwice "Registry entires used by jEditLauncher module"
writeLineTwice "Prepared by get_registry.vbs on " & Now

writeRegEntries "HKEY_CLASSES_ROOT\Interface\{E53269FA-8A5C-42B0-B3BC-82254F4FCED4}",  _
	Array("\", "\NumMethods\", "\ProxyStubClsid\", "\TypeLib\", "\TypeLib\Version" )
writeRegEntries "HKEY_CLASSES_ROOT\CLSID\{E53269FA-8A5C-42B0-B3BC-82254F4FCED4}", _
	Array("\", "\InProcServer32\", "\InProcServer32\ThreadingModel")
writeRegEntries "HKEY_CLASSES_ROOT\JEdit.JEditLauncher", _
	Array("\", "\CLSID\", "\CurVer\")
writeRegEntries "HKEY_CLASSES_ROOT\JEdit.JEditLauncher.3.2", _
	Array("\", "\CLSID\")
writeRegEntries "HKEY_CLASSES_ROOT\JEdit.JEditLauncher.4.0", _
	Array("\", "\CLSID\")
writeRegEntries "HKEY_CLASSES_ROOT\CLSID\{2FA37216-6AFA-4299-9203-8A962384AA7E}", _
	Array("\", "\InProcServer32\", "\InProcServer32\jEditVersion", "\InProcServer32\ThreadingModel")
writeRegEntries	"HKEY_CLASSES_ROOT\CLSID\{6f58631e-d9c6-465c-b3a2-603f3f3d918c}", _
	Array("\", "\AppId", "\LocalServer32\", "\ProgID\", "\Programmable\", "\TypeLib\" , _
		"\Version\", "\VersionIndependentProgId\")
writeRegEntries "HKEY_CLASSES_ROOT\TypeLib\{C507245B-1B5E-4BFE-A9CB-4B21986375A8}\3.2", _
	Array("\", "\0\Win32\", "\FLAGS\", "\HELPDIR\")
writeRegEntries "HKEY_CLASSES_ROOT\TypeLib\{C507245B-1B5E-4BFE-A9CB-4B21986375A8}\4.0", _
	Array("\", "\0\Win32\", "\FLAGS\", "\HELPDIR\")
writeRegEntries	"HKEY_CURRENT_USER\Software\www.jedit.org\jEditLauncher\3.2\", _
	Array("Installation Directory", "Java Executable", "Java Options", "jEdit Options", _
		"jEdit Target", "jEdit Working Directory", "Launcher GUID")
writeRegEntries	"HKEY_CURRENT_USER\Software\www.jedit.org\jEditLauncher\4.0\", _
	Array("Installation Directory", "Java Executable", "Java Options", "jEdit Options", _
		"jEdit Target", "jEdit Working Directory", "Launcher GUID", "Launcher Log Level")
writeRegEntries "HKEY_CLASSES_ROOT\*\shellex\ContextMenuHandlers\Open with jEdit", _
	Array("\", "\jEditVersion")
writeRegEntries "HKEY_CURRENT_USER\Software\Microsoft\Windows\CurrentVersion\Uninstall\jEdit 3.2\", _
	Array("Comments", "DisplayIcon", "DisplayName", "InstallPath", "Publisher", _
		"UninstallString", "VersionMajor", "VersionMinor")
writeRegEntries "HKEY_CURRENT_USER\Software\Microsoft\Windows\CurrentVersion\Uninstall\jEdit 4.0\", _
	Array("Comments", "DisplayIcon", "DisplayName", "InstallPath", "Publisher", _
		"UninstallString", "VersionMajor", "VersionMinor")

cout.WriteLine "Output also written to " & outPath

' Procedure writes text to two streams
Sub writeLineTwice(text)
	If textout <> Nothing Then
		textout.WriteLine text
	End If
	cout.WriteLine text
End Sub

' Procedure prepares formatted dump of registry values,
' iterating through members of array in second parameter
Sub writeRegEntries(base, items)
	On Error Resume Next
	Dim value, item, key
	writeLineTwice "Entries for " & base & ":"
	For Each item in items
		err.Clear
		key = base & item
		value = Shell.RegRead(key)
		' A VBScript bug causes an error to be thrown when a registry key with an empty
		' default value is read. The error trapping routine provides a workaround.
		If err <> 0 Then
			If (err.number = 500) Or (Left(err.description, 14) = "Unable to open") Then
				writeLineTwice vbTab & item & " - Value: <value not set>"
			Else
				writeLineTwice vbTab & item & " - Error writing value: " & err.description
			End If
		Else
			writeLineTwice vbTab & item & " - Value: " & value
		End If
	Next
End Sub

' end get_registry.vbs


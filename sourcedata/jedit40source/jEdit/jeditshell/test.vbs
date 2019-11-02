' example VBScript demonstrating jEditLauncher interface
' change the file specifications as desired

' $Id: test.vbs,v 1.1.1.1 2001/07/03 13:13:58 jgellene Exp $

dim launcher
set launcher = CreateObject("JEdit.JEditLauncher")
a = Array("I:\\Source Code Files\\shellext\\jeditshell\\*.h", _
	"I:\\Source Code Files\\shellext\\jeditshell\\*.cpp")
MsgBox "The server authorization code is " + _
	FormatNumber(launcher.ServerKey, 0, 0, 0, 0) + ".", _
	vbOKOnly + vbInformation, "jEditLauncher"
launcher.openFiles(a)


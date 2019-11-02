// example JScript using jEditLauncher interface
// note: in contrast to VBScript, JScript does not
// support message boxes outside a browser window

// $Id: test.js,v 1.1.1.1 2001/07/03 13:13:58 jgellene Exp $

var launcher = WScript.createObject("JEdit.JEditLauncher");
var a = new Array("I:\\weather.html", "I:\\test.txt");
b = "I:\\*.pl";
launcher.openFiles(a);
launcher.openFile(b);


# * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
#
#  File Name:  regfix.pl
#
#  Author:     John Gellene (jgellene@nyc.rr.com)
#
#  Date:       Sep 21, 2001, 10:19 AM
#
#  Description:  Adds missing registry entires following
#                corrupted jEditLauncher installation.
#
#				 This is a model script and needs to customized
#                for particular situations.
#
#  $Id: regfix.pl,v 1.1 2001/09/21 21:05:42 jgellene Exp $
#
# * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

#	Missing keys installed by this version of the script:
#
#	Summary of key HKEY_CLASSES_ROOT/JEdit.JEditLauncher:
#   	 (default)                  :   JEditLauncher32 Class
#	There are 2 subkeys:
#   	 HKEY_CLASSES_ROOT/JEdit.JEditLauncher/CLSID:
#    	(default)                  :   {6f58631e-d9c6-465c-b3a2-603f3f3d918c}
#    	No subkeys.
#    	HKEY_CLASSES_ROOT/JEdit.JEditLauncher/CurVer:
#    	(default)                  :   JEdit.JEditLauncher.3.2
#    	No subkeys.
#
#	-- and these:
#
#	Summary of key HKEY_CURRENT_USER/Software/Microsoft/Windows/CurrentVersion/Uninstall/jEdit 3.2:
#    	UninstallString            :   D:\ARCHIV~1\JEDIT3~1.2\unlaunch.exe
#    	InstallPath                :   D:\ARCHIV~1\JEDIT3~1.2\jedit.exe /i
#    	DisplayIcon                :   D:\ARCHIV~1\JEDIT3~1.2\jedit.exe, 0


use strict;
my $Registry;
use Win32::TieRegistry 0.20 (
	TiedRef => \$Registry,  Delimiter => "/",  ArrayValues => 1,
	SplitMultis => 1,  AllowLoad => 1,
);

#
# class key repair
#

my($classKey) = $Registry->Open("HKEY_CLASSES_ROOT")->CreateKey("JEdit.JEditLauncher");

$classKey || print "No class key for HKEY_CLASSES_ROOT/JEdit.JEditLauncher created.\n";

$classKey->{"/"} = "JEditLauncher32 Class";
$classKey->CreateKey("CLSID")->{"/"} = "{6f58631e-d9c6-465c-b3a2-603f3f3d918c}";
$classKey->CreateKey("CurVer")->{"/"} = "JEdit.JEditLauncher.3.2";

#
# uninstall key repair
#

my($uninstallKeyName) = "HKEY_CURRENT_USER/Software/Microsoft" .
	"/Windows/CurrentVersion/Uninstall/jEdit 3.2";
my($uninstallKey) = $Registry->Open($uninstallKeyName);

$uninstallKey || print "Could not find $uninstallKeyName.\n";

$uninstallKey->{"/UninstallString"} = "D:\\ARCHIV~1\\JEDIT3~1.2\\unlaunch.exe";
$uninstallKey->{"/InstallPath"} 	= "D:\\ARCHIV~1\\JEDIT3~1.2\\jedit.exe /i";
$uninstallKey->{"/UninstallString"} = "D:\\ARCHIV~1\\JEDIT3~1.2\\jedit.exe, 0";

#
# report result
#

my $result;

if($classKey and $uninstallKey) { $result = "successful."; }
else { $result = "could not be completed."; }

print "Registry repair $result\n";

# end regfix.pl


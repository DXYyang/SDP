# * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
#
#  File Name:  get_registry.pl
#
#  Author:     John Gellene (jgellene@nyc.rr.com)
#
#  Description:  Diagnostic Perl script for extracting entries
#				 for jEditLauncher in Windows registry
#				 Note:  all registry keys are opened
#				 with read-only access
#
#  $Id: get_registry.pl,v 1.4 2001/08/01 14:57:21 jgellene Exp $
#
# * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

my $Registry;
use Win32::TieRegistry 0.20	(TiedRef=>\$Registry, Delimiter=>"/",
	ArrayValues=>1);

my($defaultValue, $noValue) = ("(default)", "(value not set)");
# global scope for use by format
my($kvName, $kvValue);


print "Windows registry data for jEditLauncher package\n",
	"Prepared by ".(substr $0, (rindex $0, "\\") + 1)." on ".(&mytime)."\n";

while(<DATA>) {
	/(?:^\#)|(?:^$)/ or (chomp and &dumpKey($_, 1, ""));
}

sub dumpKey
{
	my($keytext, $heading, $indent) = @_;
	my($swKey) = $Registry->Open($keytext, {Access=>"KEY_READ"}) ||
		(print "\nCould not find key $keytext\n" and return);
	print ($heading ? "\nSummary of key $keytext:\n" : "    $keytext:\n");
	foreach($swKey->ValueNames) {
		$kvName = ($_ or $defaultValue);
		$kvValue = $swKey->GetValue($_);
		$~ = KEYVALUE;
		write;
		$~ = STDOUT;
	}
	my(@names) = $swKey->SubKeyNames;
	my($numSubKeys) = scalar @names;
	if($numSubKeys != 0) {
		if($numSubKeys == 1) {
			print $indent."There is 1 subkey:\n";
		}
		else {
			print $indent."There are $numSubKeys subkeys:\n";
		}
		foreach(@names) {
			&dumpKey($keytext."/".$_, 0, $indent."  ");
		}
	}
	else {
		print "    No subkeys.\n";
	}
}

sub mytime {
  my (@mydate, @mytime, $apm);
  @mydate = split /\s+/, localtime;
  @mytime = split /:/, $mydate[3];
  $apm = $mytime[0] > 11 ? "PM" : "AM";
  $mytime[0] = (($mytime[0] + 11) % 12) + 1;
  join (" ", $mydate[0], $mydate[1], $mydate[2].",",
		$mydate[4], " ".$mytime[0].":".$mytime[1].$apm);
}

format KEYVALUE =
    @<<<<<<<<<<<<<<<<<<<<<<<<< :   ^<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    $kvName,                       $kvValue
~                                  ^<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                                   $kvValue
~                                  ^<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                                   $kvValue
~                                  ^<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                                   $kvValue
.

__DATA__
# keys to be examined
HKEY_CLASSES_ROOT/Interface/{E53269FA-8A5C-42B0-B3BC-82254F4FCED4}
HKEY_CLASSES_ROOT/CLSID/{E53269FA-8A5C-42B0-B3BC-82254F4FCED4}
HKEY_CLASSES_ROOT/JEdit.JEditLauncher
HKEY_CLASSES_ROOT/JEdit.JEditLauncher.3.1
HKEY_CLASSES_ROOT/JEdit.JEditLauncher.3.2
HKEY_CLASSES_ROOT/CLSID/{2FA37216-6AFA-4299-9203-8A962384AA7E}
HKEY_CLASSES_ROOT/CLSID/{5EAD3025-7852-472D-B840-93F61BB53B2B}
HKEY_CLASSES_ROOT/CLSID/{6f58631e-d9c6-465c-b3a2-603f3f3d918c}
HKEY_CLASSES_ROOT/TypeLib/{C507245B-1B5E-4BFE-A9CB-4B21986375A8}
HKEY_CURRENT_USER/Software/www.jedit.org/jEditLauncher
HKEY_CLASSES_ROOT/*/shellex/ContextMenuHandlers/Open with jEdit
HKEY_CURRENT_USER/Software/Microsoft/Windows/CurrentVersion/Uninstall/jEdit 3.2

# end get_registry.pl


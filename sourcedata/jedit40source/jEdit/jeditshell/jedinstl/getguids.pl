 #
 # getguids.pl - part of the jEditLauncher package
 # Copyright (C) 2001 John Gellene
 # jgellene@nyc.rr.com
 #
 # This program is free software; you can redistribute it and/or
 # modify it under the terms of the GNU General Public License
 # as published by the Free Software Foundation; either version 2
 # of the License, or any later version.
 #
 # This program is distributed in the hope that it will be useful,
 # but WITHOUT ANY WARRANTY; without even the implied warranty of
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 # GNU General Public License for more details.
 #
 # You should have received a copy of the GNU General Public License
 # along with this program; if not, write to the Free Software
 # Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 #
 # $Id: getguids.pl,v 1.1.1.1 2001/07/03 13:14:07 jgellene Exp $
 #
 # This script was used to format GUID's for the installer's resource file
 #

my($version) = 1;

while($version < 33)
{
	printf("#define IDS_JEL_VERSION_%02d\t\t\t%d\n",
		$version, $version + 1023);
	++$version;
}
print "\n\n";
$version = 17;
while(<DATA>)
{
	chomp;
	printf("IDS_JEL_VERSION_%02d\t\"\{%s\}\"\n", $version++, $_);
}

__DATA__
965cfab5-5045-4cbc-b4fd-653a6e0fd762
db497944-ab72-4e31-aa12-b27a3f1e5dfa
5ac5daeb-2919-4c69-b5f3-f94bd57a791f
69781480-e1a0-4b42-a33f-c5e00cd1df3f
400db48a-c9cc-447d-b6b1-10130c109908
539fb31f-8455-4583-9b10-e339d3db9736
794d084d-83c8-47bf-8a5c-ce4143d1d0bd
fc69c804-e68d-4767-871f-54effe2ac135
8be313d4-8fc0-4ef0-b1ef-af7358fa6e84
3c79d877-f242-43b9-845e-0b43420840cd
c32e45ce-f791-488b-ab5e-e48dfcacedf0
e9fda86c-094d-4d41-8c05-56621cff37a1
b1ab6cb3-0e9a-42a8-80ef-0037fadee447
44c3f586-c986-4bc7-828f-fcddf1264f49
5f4c293c-b4de-495b-9de4-280c59618ff8
e5b13f61-97d8-4e2c-935a-4f17816952be

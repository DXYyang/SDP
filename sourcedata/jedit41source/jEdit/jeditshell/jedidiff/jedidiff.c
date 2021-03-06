/*
 * jedidiff.c - part of jEditLauncher package
 * Copyright (C) 2002 John Gellene
 * jgellene@nyc.rr.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * Notwithstanding the terms of the General Public License, the author grants
 * permission to compile and link object code generated by the compilation of
 * this program with object code and libraries that are not subject to the
 * GNU General Public License, provided that the executable output of such
 * compilation shall be distributed with source code on substantially the
 * same basis as the jEditLauncher package of which this program is a part.
 * By way of example, a distribution would satisfy this condition if it
 * included a working makefile for any freely available make utility that
 * runs on the Windows family of operating systems. This condition does not
 * require a licensee of this software to distribute any proprietary software
 * (including header files and libraries) that is licensed under terms
 * prohibiting redistribution to third parties.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * $Id: jedidiff.c,v 1.2 2002/03/11 11:47:12 jgellene Exp $
 */

// jedidiff.c : Code for jeditdiff utility
//

#include <stdio.h>
#include "..\jeditlauncher\jeditlauncher_i.c"
#include "..\jeditlauncher\jeditlauncher.h"

char *szUsage = "jedidiff.exe:  creates graphical diff of two files in the jEdit text editor.\n"
				"Usage: jedidiff file1 file2  displays diff of file2 against file1\n"
				"       jedidiff /h           displays this message\n"
				"Part of the jEditLauncher package, released under GNU GPL\n"
				"Version 4.0.4, Copyright 2002 John Gellene (jgellene@nyc.rr.com)";

char *szMsg[] =	{
					"Could not create JEditLauncher object.\n",
					"File name missing!\n",
					"File %s does not exist!\n"
				};

char fullPath[MAX_PATH];
wchar_t wszFile[MAX_PATH];

BSTR MakeFileBSTR(const char* szFileName)
{
	BOOL bSuccess;
	CHAR *pFileName;
	BSTR bstrFile = 0;
	UINT nCodePage = AreFileApisANSI() ? CP_ACP : CP_OEMCP;
	if(szFileName == 0)
	{
		szMsg[2];
		return 0;
	}
	wszFile[0] = 0;
	GetFullPathName(szFileName, MAX_PATH, fullPath, &pFileName);
	if(GetFileAttributes(fullPath) == -1)
	{
		printf(szMsg[1], szFileName);
		return 0;
	}
	bSuccess = MultiByteToWideChar(nCodePage, 0, fullPath, -1,	wszFile, MAX_PATH);
	bstrFile = SysAllocString(wszFile);
	return bstrFile;
}

int main(int argc, char* argv[])
{
	HRESULT hr;
	BSTR bstrFile1, bstrFile2;
	IJEditLauncher *piJEdit = 0;
	if(argc != 3 || (argc > 1 && (strcmp(argv[1], "/h") == 0)))
	{
		printf(szUsage);
		return 0;
	}
	CoInitialize(0);
	hr = CoCreateInstance(&CLSID_JEditLauncher40, 0 , CLSCTX_LOCAL_SERVER,
		&IID_IJEditLauncher, (void**)&piJEdit);
	if(hr != S_OK)
	{
		printf(szMsg[0]);
	}
	else 
	{
		bstrFile1 = MakeFileBSTR(argv[1]);
		bstrFile2 = MakeFileBSTR(argv[2]);
		if(bstrFile1 && bstrFile2)
			hr = (piJEdit)->lpVtbl->RunDiff(piJEdit,bstrFile1,bstrFile2);
		SysFreeString(bstrFile1);
		SysFreeString(bstrFile2);
	}
	if(piJEdit != 0)
		(piJEdit)->lpVtbl->Release(piJEdit);
	CoUninitialize();
	return 0;
}


/*
 * Ltslog.h - part of jEditLauncher package
 * Copyright (C) 2001 John Gellene
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
 * $Id: ltslog.h,v 1.2 2001/12/30 08:46:02 jgellene Exp $
 */

#if !defined(__LTSLOG_H__)
#define __LTSLOG_H__

// NOTE: LTSLOG_EXPORTS should be defined in any module that exports
// NOTE: OpenLog(), CloseLog() and Log()
#ifdef LTSLOG_EXPORTS
#define LTSLOG_API __declspec(dllexport)
#else
#define LTSLOG_API __declspec(dllimport)
#endif


extern "C"
{

/*
 * These message level
 */
enum MsgLevel {
	Error = 0, Warning, Notice, Message, Debug
};

/*
 * This class logs printf style strings to a file named
 * in the static wrapper function OpenLog(). This version
 * opens the log in the directory in which ltslog is located.
 * Entries are always appended to an existing file.
 * If the bStamp parameter in the Log() function is set to true
 * the string written to the log file will be prefixed by the
 * label for the logging level ("Error", "Warning", and so on)
 * and a timestamp.
 */
class CLtslog {
public:
	CLtslog(LPCTSTR szPath);
	~CLtslog();

public:
	void SetLogLevel(MsgLevel level);
	void LogMessage();
	void Log(bool bStamp, MsgLevel level, const char* lpszFormat, ...);

protected:
	void InitLogEntry(bool bStart);

private:
	void LogImpl(bool bStamp, MsgLevel level, const char* lpszFormat, va_list args);

private:
	HANDLE hFile;
	DWORD dwWritten;
	MsgLevel msgLevel;
	char szMessage[1024];

	/* No copying */
private:
	CLtslog(const CLtslog&);
	CLtslog& operator=(const CLtslog&);

// exported log API
friend LTSLOG_API CLtslog* OpenLog(LPCTSTR szPath, MsgLevel level);
friend LTSLOG_API void CloseLog(CLtslog* pLog);
friend LTSLOG_API void SetLogLevel(CLtslog* pLog, MsgLevel level);
friend LTSLOG_API void Log(CLtslog* pLog, bool bStamp, MsgLevel level,
	const char* lpszFormat, ...);
friend LTSLOG_API void LogArgs(CLtslog* pLog, bool bStamp, MsgLevel level,
	const char* lpszFormat, va_list argList);

};

LTSLOG_API CLtslog* OpenLog(LPCTSTR szPath, MsgLevel level);
LTSLOG_API void CloseLog(CLtslog* pLog);
LTSLOG_API void SetLogLevel(CLtslog* pLog, MsgLevel level);
LTSLOG_API void Log(CLtslog* pLog, bool bStamp, MsgLevel level,
	const char* lpszFormat, ...);
LTSLOG_API void LogArgs(CLtslog* pLog, bool bStamp, MsgLevel level,
	const char* lpszFormat, va_list argList);

// typedef's for use when dynamically linking to logging module
typedef CLtslog* (*LogOpenFunc)(LPCTSTR, MsgLevel);
typedef void (*LogCloseFunc)(CLtslog*);
typedef void (*LogLevelFunc)(CLtslog*, MsgLevel);
typedef void (*LogFunc)(CLtslog*, bool, MsgLevel, const char*, ...);
typedef void (*LogArgsFunc)(CLtslog*, bool, MsgLevel, const char*, va_list);

}  // end extern "C" linkage block

#endif        //  #if !defined(__LTSLOG_H__)


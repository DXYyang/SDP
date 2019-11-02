/*
 * JcmdProcess.java - A Java class wrapping the jcmd.exe utility for
 * Windows; the utility is a command interpreter for use with the Java's
 * platform Runtime.exec, designed to prevent the display of console
 * windows when executing console-based programs
 * Copyright (C) 2001 John Gellene
 * jgellene@nyc.rr.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the jEdit program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A wrapper class for the <code>jcmd</code> command interpreter for Windows.
 * @author John Gellene
 * @version $Id: JcmdProcess.java,v 1.8 2001/09/16 22:57:42 jgellene Exp $
 * <p>
 * <b>Note:</b> this class is currently under development and has not
 * been fully tested.
 */
public class JcmdProcess extends Process
{
	private String jcmdDirectory;
	private Process jcmdProcess = null;
	private Integer returnValue = null;
	static private final int CTRL_C = 3;

	/**
	 * Constructs a <code>JcmdProcess</code> object.
	 * @param jcmdDirectory The directory in which <code>jcmd.exe</code>
	 * is located.
	 * <br>
	 * A <code>null</code> parameter indicates that <code>jcmd.exe</code> should
	 * be found in the current working directory of the JVM's process.
	 */
	protected JcmdProcess(String jcmdDirectory)
	{
		this.jcmdDirectory = jcmdDirectory;
	}


	/**
	 * Executes the <code>jcmd</code> command interpreter on a given command line.
	 * @param cmdLine The full command line to be passed to the interpreter.
	 * @param cwd The current working directory for the interpreter process.
	 * <br>
	 * <b>Note:</b> In this version use of the <code>cwd</code> parameter is not
	 * implemented.
	 * @throws IOException The exception thrown by the call to
	 * <code>Runtime.exec</code> that starts <code>jcmd</code>.
	 */
	static public JcmdProcess exec(String cmdLine, String cwd) throws IOException
	{
		return exec(cmdLine, cwd, null);
	}

	/**
	 * Executes the <code>jcmd</code> command interpreter on a given command line.
	 * @param cmdLine The full command line to be passed to the interpreter.
	 * @param cwd The current working directory for the interpreter process.
	 * <br>
	 * <b>Note:</b> In this version use of the <code>cwd</code> parameter is not
	 * implemented.
	 * @param jcmdDir The directory in which the <code>jcmd</code> interpreter
	 * may be found.
	 * <br>
	 * If the <code>jcmdDir</code> parameter is <code>null</code>, the current
	 * working directory of the JVM's process will be used as the location of
	 * <code>jcmd</code>.
	 * @throws IOException The exception thrown by the call to
	 * <code>Runtime.exec</code> that starts <code>jcmd</code>.
	 */
	static public JcmdProcess exec(String cmdLine, String cwd, String jcmdDir)
		throws IOException
	{
		JcmdProcess jp = new JcmdProcess(jcmdDir);
		// cwd not implemented in this version
		jp.doExec(cmdLine);
		return jp;
	}

	/**
	 * Destroys the process in which the command interpreter is running.
	 * <p>
	 * To terminate jcmd and any running child process in an orderly fashion,
	 * use <code>sendBreak</code>.
	 * @see sendBreak
	 */
	public void destroy()
	{
		if(jcmdProcess != null)
		{
			jcmdProcess.destroy();
		}
	}

	/**
	 * Terminates the <code>jcmd</code> process by sending it a Ctrl+C character.
	 * <p>
	 * Using <code>sendBreak</code> instead of <code>destroy</code> will
	 * cause <code>jcmd</code> to terminate any running child process.
	 * @throws IOException any exception thrown by call to
	 * <code>OutputStream.write</code>.
	 */
	public void sendBreak() throws IOException
	{
		String testString = new String("\003");
		byte[] bytes = testString.getBytes();
		if(jcmdProcess != null)
		{
			OutputStream os = jcmdProcess.getOutputStream();
			os.write(bytes);
			os.flush();
		}
	}

	/**
	 * Returns the exit value of the command executed by <code>jcmd</code>.
	 * @return The exit value of the command executed by <code>jcmd</code>,
	 * or -1 if the command passed to <code>jcmd</code> cannot be found or
	 * cannot be executed.
	 * <p>
	 * <b>Note:</b> Since a command executed by <code>jcmd</code> may itself
	 * return -1, this return value is ambiguous.  Use <code>hasLaunched</code>
	 * to test whether a command was launched successfully by <code>jcmd</code>.
	 * @see hasLaunched
	 */
	public int exitValue()
	{
		if(returnValue == null)
		{
			int rawReturnValue = -1;
			if(jcmdProcess != null)
			{
				rawReturnValue = jcmdProcess.exitValue();
			}
			returnValue = new Integer(rawReturnValue);
		}
		return returnValue.intValue();
	}

	/**
	 * Returns the error stream of the process launched by <code>jcmd</code>.
	 * @return An <code>InputStream</code> representing the error stream
	 * of both <code>jcmd</code> (for purposes of its own error messages)
	 * and the process it has launched.
	 */
	public InputStream getErrorStream()
	{
		return jcmdProcess.getErrorStream();
	}

	/**
	 * Returns the input stream of the process launched by <code>jcmd</code>.
	 * @return An <code>InputStream</code> representing the input stream
	 * of both <code>jcmd</code> and the process it has launched.
	 * <p>
	 * Currently, <code>jcmd</code> does not write to this stream, so
	 * the only data in the stream would come from the process launched by
	 * <code>jcmd</code>.
	 */
	public InputStream getInputStream()
	{
		return jcmdProcess.getInputStream();
	}

	/**
	 * Returns the output stream of the process launched by <code>jcmd</code>.
	 * @return An <code>OutputStream</code> representing the output stream
	 * of both <code>jcmd</code> and the process it has launched.
	 * <p>
	 * Currently, <code>jcmd</code> does not process data written to
	 * this stream and does not forward data to the process it launches,
	 * except for command line parameters passed upon launching.
	 */
	public OutputStream getOutputStream()
	{
		return jcmdProcess.getOutputStream();
	}

	/**
	 * Blocks execution until <code>jcmd</code> exits. Returns immediately
	 * if <code>jcmd</code> has already exited.
	 * @return The exit code returned by <code>jcmd</code>.
	 * <br>
	 * If <code>jcmd</code> has successfully launched another process, the
	 * exit code will be that of the launched process, otherwise this
	 * method will return -1.
	 * @throws InterruptedException An exception thrown by the
	 * <code>Process</code> object in which <code>jcmd</code> is running.
	 * @see exitValue
	 * @see hasLaunched
	 */
	public int waitFor() throws InterruptedException
	{
		if(jcmdProcess == null)
		{
			return exitValue();
		}
		return jcmdProcess.waitFor();
	}

	/**
	 * Tests whether or not <code>jcmd</code> has launched a child process.
	 * @return The value <code>true</code> if a process has been launched and
	 * <code>false</code> otherwise.
	 * <p>
	 * This method is more reliable than <code>waitFor</code> or
	 * <code>exitValue</code> to test whether <code>jcmd</code> successfully
	 * launched a process, because some native executables encountering their
	 * own errors will return -1, the exit code indicating an <code>jcmd</code>
	 * launch failure.
	 */
	public boolean hasLaunched()
	{
		return jcmdProcess != null;
	}

	private void doExec(String cmdLine) throws IOException
	{
		StringBuffer cmdBuffer = new StringBuffer(512);
		if(jcmdDirectory != null)
		{
			cmdBuffer.append(jcmdDirectory);
			if(!jcmdDirectory.endsWith(File.separator))
			{
				cmdBuffer.append(File.separator);
			}
		}
		cmdBuffer.append("jcmd.exe ").append(cmdLine);
		jcmdProcess = Runtime.getRuntime().exec(cmdBuffer.toString());
	}
}

// end JcmdProcess.java


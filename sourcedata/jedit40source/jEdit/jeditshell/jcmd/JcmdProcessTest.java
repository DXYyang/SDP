/*
 * JcmdProcessTest.java - A Java class to test the JcmdProcess class.
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * A test routine for the <code>JcmdProcess</code> class.
 * @author John Gellene
 * @version $Id: JcmdProcessTest.java,v 1.3 2001/09/17 21:11:33 jgellene Exp $
 * <br>
 * The class consists solely of a <code>main</code> method.
 */
public class JcmdProcessTest
{
	/**
	 * The main test routine.
	 * @param argv Command line parameters.
	 * <br>
	 * This method expects a conventional command line that will be passed as
	 * a parameter string to the Windows executable <code>jcmd.exe</code>.
	 */
	public static void main(String argv[])
	{
		System.out.println("Testing JcmdProcess class.");
		String cmdLine = null;
		JcmdProcess process = null;
		/* Get full command line */
		if(argv.length > 0)
		{
			StringBuffer sb = new StringBuffer(argv[0]);
			for(int i = 1; i < argv.length; ++i)
			{
				sb.append(' ').append(argv[i]);
			}
			cmdLine = sb.toString();
			System.out.println("Command line: " + cmdLine);
		}
		else
		{
			System.err.println("No command line provided.");
			System.exit(1);
		}
		try
		{
			process = JcmdProcess.exec(cmdLine, null);
			BufferedReader pInput = new BufferedReader(
										new InputStreamReader(
                						process.getInputStream()));
		    String s = pInput.readLine();
			/* call sendBreak after fixed number of
			 * lines of stdout are read
			 */
			int nRead = 0;
		    while (s != null)
			{
			    System.out.println(s);
				if(++nRead == 5)
					process.sendBreak();
				s = pInput.readLine();
			}
		}
		catch(IOException e)
		{
    		System.err.println("I/O error: " + e.toString());
		}
		catch(Exception e)
		{
			System.err.println("JcmdProcess error: " + e.toString());
		}
		System.out.println("Child process exited with code " +
			String.valueOf(process.exitValue()));
		System.exit(0);
	}
}

// end JcmdProcessTest.java


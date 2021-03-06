package fr.minibilles.basics.generation;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Some utility methods for mergers.
 * @author Jean-Charles Roger
 *
 */
public class MergerUtil {

	/** Writes given contents to destination file. */
	public static void writeFile(File destinationFile, String contents, String encoding) throws Exception {
		FileOutputStream stream = new FileOutputStream(destinationFile);
		stream.write(contents.getBytes(encoding));
		stream.close();
	}

	/**
	 * Loads a file contents as a String using platform encoding.
	 * @param file file to load.
	 * @return the file contents as a {@link String}.
	 * @throws Exception 
	 */
	public static String loadContents(File file) throws Exception {
		final StringBuilder contents = new StringBuilder();
		final BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
		try {
			final byte[] buffer = new byte[1024];
			int read = in.read(buffer);
			while ( read >= 0 ) {
				contents.append(new String(buffer, 0, read));
				read = in.read(buffer);
			}
		} finally {
			in.close();
		}
		return contents.toString();
	}
	
	/** Checks if file contents is equals to given contents. */
	public static boolean checkContents(File file, String contents, String encoding) throws IOException {
  		int contentsIndex = 0;
		final int contentsLength = contents.length();
		final BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(file));

		try { 
			byte[] buffer = new byte[256];
			int read = inStream.read(buffer);
			while ( read >= 0 ) {
				// creates string from file
				String readString = new String(buffer, 0, read, encoding);
				final int endIndex = contentsIndex+readString.length();
				
				// creates string from contents (and check size)
				if ( contentsIndex >= contentsLength || endIndex > contentsLength ) return false;
				String contentsLocalString = contents.substring(contentsIndex, endIndex);
				contentsIndex = endIndex +1;
				// tests equality
				if ( !readString.equals(contentsLocalString) ) return false;
				
				// until now contents of string and file is equal.
				read = inStream.read(buffer);
			}
		} finally {
			inStream.close();
		}
		return contentsIndex > contentsLength;
	}
}


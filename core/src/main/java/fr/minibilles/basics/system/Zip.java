package fr.minibilles.basics.system;

import fr.minibilles.basics.error.Diagnostic;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * <p>Zip contains a set of methods to compress and uncompress zip files. All
 * methods uses a {@link BasicShell} for error handling and logging.</p>
 * 
 * @author Jean-Charles Roger
 */
public class Zip {

	/**
	 * <p>Zips listed files to given archive file. It creates a new archive. 
	 * If an archive was already present, it's erased.</p>
	 * @param shell used shell for zip.
	 * @param archive target archive.
	 * @param filesToZip list of files to zip.
	 */
	public static void zip(BasicShell shell, File archive, List<File> filesToZip) {
		try {
			shell.printOut("Creating archive '" + archive.getPath() + "'.");
			final BufferedOutputStream archiveStream = new BufferedOutputStream(new FileOutputStream(archive));
			ZipOutputStream zipStream = new ZipOutputStream(archiveStream);
			zipStream.setLevel(7);
			
			String pwd = shell.pwd().getPath();
			for ( File file : filesToZip) {
				String name = file.getPath();
				if (name.equals(pwd)) continue;
				
				if ( name.startsWith(pwd) ) {
					name = name.substring(pwd.length()+1);
				}
				ZipEntry entry = new ZipEntry(name);
				zipStream.putNextEntry(entry);
				if ( file.isFile() ) {
					BufferedInputStream fileStream = new BufferedInputStream(new FileInputStream(file));
					byte[] buffer = new byte[2048];
					int count = fileStream.read(buffer);
					while ( count > -1 ) {
						zipStream.write(buffer, 0, count);
						count = fileStream.read(buffer);
					}
					fileStream.close();
				}
				shell.printOut("File '" + entry.getName() + "' added.");
			}
			zipStream.close();
		} catch (Exception e) {
			shell.getErrorHandler().handleError(Diagnostic.ERROR, e.getClass().getSimpleName() + ": "+ e.getMessage());
		}
	}
	
	/**
	 * <p>This methods zips to the archive file the result of 
	 * {@link BasicShell#ls(String, int)} performed on given shell with given 
	 * parameters.</p>
	 * @param shell used shell for zip.
	 * @param archive target archive.
	 * @param filter ls parameter.
	 * @param flags ls parameter.
	 */
	public static void zip(BasicShell shell, File archive, String filter, int flags) {
		List<File> filesToZip = shell.ls(filter, flags);
		zip(shell, archive, filesToZip);
	}
	
	/**
	 * <p>This methods zips to the archive file the result of 
	 * {@link BasicShell#ls(File, String, int)} performed on given shell with 
	 * given parameters.</p>
	 * @param shell used shell for zip.
	 * @param archive target archive.
	 * @param rootFile ls parameter
	 * @param filter ls parameter.
	 * @param flags ls parameter.
	 */
	public static void zip(BasicShell shell, File archive, File rootFile, String filter, int flags) {
		List<File> filesToZip = shell.ls(rootFile, filter, flags);
		zip(shell, archive, filesToZip);
	}
	
	/**
	 * <p>Unzip given archives file to given destination.</p>
	 * @param shell used shell for unzip.
	 * @param archive archive file.
	 * @param destination destination directory.
	 */
	public static void unzip(BasicShell shell, File archive, File destination) {
		try {
			shell.printOut("Extracting files from archive '" + archive.getPath() + "'.");
			ZipFile zipFile = new ZipFile(archive);
			Enumeration<? extends ZipEntry> e = zipFile.entries();
			while (e.hasMoreElements() ) {
				ZipEntry entry = e.nextElement();
				File entryFile = new File(destination, entry.getName());
				if ( entry.isDirectory() ) {
					entryFile.mkdirs();
				} else {
					entryFile.getParentFile().mkdirs();
					
					InputStream entryStream = zipFile.getInputStream(entry);
					BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(entryFile));
					byte[] buffer = new byte[2048];
					int count = entryStream.read(buffer);
					while ( count > -1 ) {
						outStream.write(buffer, 0, count);
						count = entryStream.read(buffer);
					}
					outStream.flush();
					outStream.close();
					entryStream.close();
				}
				shell.printOut("Extracted file '" + entry.getName() + "'.") ;
			}
			zipFile.close();
		} catch (Exception e) {
			shell.getErrorHandler().handleError(Diagnostic.ERROR, e.getClass().getSimpleName() + ": "+ e.getMessage());
		}

		
	}
}

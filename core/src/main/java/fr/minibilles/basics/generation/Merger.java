/**
 * 
 */
package fr.minibilles.basics.generation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Merger} is able to merge the content of a file with the new given 
 * content.
 * @author Jean-Charles Roger
 *
 */
public interface Merger {

	/**
	 * <p>
	 * Merge a file with the new contents for this file.
	 * </p>
	 * 
	 * @param destinationFile
	 *            destination file for new contents.
	 * @param contents
	 *            new contents.
	 * @param encoding 
	 * 			  encoding to use.
	 * @throws Exception
	 */
	void merge(File destinationFile, String contents, String encoding) throws Exception;
	
	List<File> mergedFileList();
	
	/** Simple merger that replace the old contents if it exits by the new one */
	public static class Stub implements Merger {
		private final List<File> fileList = new ArrayList<File>();
		
		public void merge(File destinationFile, String contents, String encoding) throws Exception {
			if ( !destinationFile.exists() || !MergerUtil.checkContents(destinationFile, contents, encoding) ) {
				MergerUtil.writeFile(destinationFile, contents, encoding);
			}
			fileList.add(destinationFile);
		}
		
		public List<File> mergedFileList() {
			return fileList;
		}
	}
	
	/** Default merger using UTF-8 encoding. */
	public static Merger DEFAULT = new Stub();
}

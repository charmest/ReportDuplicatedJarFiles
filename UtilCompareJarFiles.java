package com.charmest.reportDuplicatedJarFiles;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 
 * Compares the names of all the ".jar" files (librairies) of a given folder.
 * Informs the user if that folder contains multiple versions of one or more librairy.
 * Two librairies are considered the same if they share the same "LibName" but have different "X" in the following pattern :
 * "[LibName]-X.X.X.jar"
 * 
 * @author Thomas CHARMES
 *
 */
public class UtilCompareJarFiles {
    
    /**
     * This String represents the path of the execution environment directory.
     * From where the user execute the program.
     */
    private String executionFolderPath = "";
    
    /**
     * Folder scanned by the program
     */
    private File folder = null;
    
    /**
     * The file which contains the final stuff
     */
    private File logFile = null;

    public static void main(String[] args) {
	UtilCompareJarFiles utilCompareJarFiles = new UtilCompareJarFiles();
	try {
	    utilCompareJarFiles.initialize();
	    FileOutputStream fileOutputStream = new FileOutputStream(utilCompareJarFiles.getLogFile());
	    utilCompareJarFiles.writeNewLine("Program started \n", fileOutputStream);
	    File[] listJarFiles = utilCompareJarFiles.getSortedJarFiles(fileOutputStream);
	    if (listJarFiles.length == 0) {
		utilCompareJarFiles.writeNewLine("No jar file found \n", fileOutputStream);
	    }
	    else {
		utilCompareJarFiles.fillListDuplicatedJarFiles(listJarFiles, fileOutputStream);
	    }
	    utilCompareJarFiles.writeNewLine("Program successfully executed \n", fileOutputStream);
	    fileOutputStream.close();
	}
	catch (IOException ioe) {
	    String exceptionOriginalMessage = ioe.getMessage();
	    String exceptionPersonalizedMessage = "An error occured during initialization of app parameters : ";
	    System.out.println("UtilCompareJarFiles.main("+args+") EXCEPTION : "
		    + exceptionPersonalizedMessage + " : " + exceptionOriginalMessage);
	    return;
	}
	catch (SecurityException se) {
	    String exceptionOriginalMessage = se.getMessage();
	    String exceptionPersonalizedMessage = "An error occured during initialization of app parameters : ";
	    System.out.println("UtilCompareJarFiles.main("+args+") EXCEPTION : "
		    + exceptionPersonalizedMessage + " : " + exceptionOriginalMessage);
	    return;
	}
    }
    
    /**
     * Initializes the user context : set the folder and create the final log file
     * @throws IOException
     */
    public void initialize() throws IOException {
	this.setExecutionFolderPath(System.getProperty("user.dir"));
	this.setFolder(new File(this.getExecutionFolderPath()));
	this.setLogFile("CompareJarFilesLog.txt");
    }
    
    /**
     * Sorts alphabetically all the ".jar" files of the folder
     * @param fileOutputStream
     * @return an alphabetically sorted array of all the ".jar" files of the folder
     */
    public File[] getSortedJarFiles(FileOutputStream fileOutputStream) {
	JarFilesFilter jarFilesFilter = new JarFilesFilter();
	File[] listJarFiles = this.getFolder().listFiles(jarFilesFilter);
	Arrays.sort(listJarFiles);
	if (listJarFiles.length != 0) {
	    writeNewLine("Number of jar files scanned : " + listJarFiles.length + "\n", fileOutputStream);
	}
	return listJarFiles;
    }
    
    /**
     * Fills the log files with potential duplicated filenames
     * @param listJarFiles
     * @param fileOutputStream
     */
    public void fillListDuplicatedJarFiles(File[] listJarFiles, FileOutputStream fileOutputStream) {
	String previousValue = "AbsolutelyNoLibraryWearsThisName";
	List<String> markedFiles = new ArrayList<String>();
	Integer countSameLibrairies = 0;
	this.writeNewLine("\n", fileOutputStream);
	for (File jarFile : listJarFiles) {
	    List<String> areSameLibrairies = areSameLibrairies(jarFile.getName(), previousValue);
	    if (! areSameLibrairies.isEmpty() && ! markedFiles.contains(jarFile.getName())) {
		this.writeNewLine("WARNING : DUPLICATION OF THE FILE " + areSameLibrairies.get(0) + "\n", fileOutputStream);
		markedFiles.add(jarFile.getName());
		countSameLibrairies ++;
	    }
	    previousValue = jarFile.getName();
	}
	this.writeNewLine("\n", fileOutputStream);
	if (countSameLibrairies == 0) {
	    this.writeNewLine("No duplicated library detected \n", fileOutputStream);
	}
	else {
	    this.writeNewLine("Number of jar files duplicated : " + countSameLibrairies +  "\n", fileOutputStream);
	}
    }
    
    /**
     * Deletes the version part of the files and compares the left part 
     * @param firstFileName
     * @param secondFileName
     * @return a single element list, composed of the "versionpartless" name of the duplicated file, if it's shared by the two files
     * an empty list otherwise.
     */
    public List<String> areSameLibrairies(String firstFileName, String secondFileName) {
	List<String> sameLibrairies = new ArrayList<String>();
	firstFileName.replace(".jar", "");
	secondFileName.replace(".jar", "");
	String regex = "-((((\\d)(.)*))+(\\.)*)*$";
	String firstFileLibName = firstFileName.split(regex)[0];
	String secondFileLibName = secondFileName.split(regex)[0];
	if (firstFileLibName.equals(secondFileLibName)) {
	    sameLibrairies.add(firstFileLibName);
	}
	return sameLibrairies;
    }
    
    /**
     * @return a String which represents the current date matching the following pattern :
     * dd/MM/yyyy HH:mm:ss
     */
    public String getDate() {
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	Date date = new Date();
	return dateFormat.format(date);
    }
    
    /**
     * Writes a line in the log file. A line always begin by the current date.
     * @see getDate()
     * @param message
     * @param fileOutputStream
     */
    public void writeNewLine(String message, FileOutputStream fileOutputStream) {
	try {
	    String date = getDate();
	    String lineToWrite = date + " : " + message;    
	    byte b[] = lineToWrite.getBytes();
	    fileOutputStream.write(b);    
	} 
	catch (IOException e) {
	    System.out.println("ERROR DETECTED DURING THE WRITING INTO THE LOG FILE = " + e.getMessage());
	    e.printStackTrace();
	}
    }

    /**
     * @return the logFile
     */
    public File getLogFile() {
        return logFile;
    }

    /**
     * Creates the log file in the given directory
     * @param logFile
     * @throws IOException
     */
    public void setLogFile(String logFile) throws IOException {
	this.logFile = new File(this.getExecutionFolderPath()+"\\"+ logFile);
	this.getLogFile().createNewFile();
	this.getLogFile().setWritable(true);
    }
    
    /**
     * @return the execution folder path
     */
    public String getExecutionFolderPath() {
        return executionFolderPath;
    }

    /**
     * Sets the execution folder path
     * @param executionFolderPath
     */
    public void setExecutionFolderPath(String executionFolderPath) {
        this.executionFolderPath = executionFolderPath;
    }

    /**
     * @return the execution environment directory
     */
    public File getFolder() {
        return folder;
    }

    /**
     * Sets the execution environment directory
     * @param folder
     */
    public void setFolder(File folder) {
        this.folder = folder;
    }

}

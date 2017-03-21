/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dataverse.fileutility;

import edu.harvard.iq.dataverse.DataFile;
import edu.harvard.iq.dataverse.DataFileServiceBean;
import edu.harvard.iq.dataverse.dataaccess.DataFileIO;
import edu.harvard.iq.dataverse.util.BundleUtil;
import edu.harvard.iq.dataverse.util.FileUtil;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObjectBuilder;

/**
 * Given the iD of a local file, this utility will check the following:
 *  - Does the file exist?
 *  - Is the filesize correct?
 *  - Is the checksum correct
 * 
 * @author rmp553
 */
public class FileCoreAttributeChecker {
    
    private DataFileServiceBean fileService;
            
    private long fileId;
    private DataFile dataFile;

    // calculated attributes
    private boolean fileExists;
    private Long filesize;
    private String checksumValue;
    private String checksumType;

    private boolean errorFound;
    private String errorMessage;
    private String userMessage;
    
    private boolean fileUpdatedInDatabase = false;
    
    
   public FileCoreAttributeChecker(DataFileServiceBean fileService, Long fileId){
        if (fileId == null){
            throw new NullPointerException("fileId cannot be null");
        }
        if (fileService == null){
            throw new NullPointerException("fileService cannot be null");
        }
        this.fileId = fileId;
        this.fileService = fileService;
        this.retrieveFile();
        this.checkAttributes();
   }
   
   
    private boolean retrieveFile(){
        
        this.dataFile = fileService.find(this.fileId);

        if (dataFile == null){       
            String errMsg;
            errMsg = BundleUtil.getStringFromBundle("find.datafile.error.not_found.id", Arrays.asList(this.getFileId().toString()));            
            this.addError(errMsg);
            return false;
        } 
        return true;
    }

    
    /**
     * Check:
     *  (1) Does the file exist
     *  (2) Is the correct filesize in the database
     *  (3) Is the correct checksum in the database
     * 
     * @return 
     */
    private boolean checkAttributes(){
        if (this.hasError()){
            return false;
        }
        String errMsg;
        
        if (dataFile.isHarvested()){
            setUserMessage(BundleUtil.getStringFromBundle("datafile.checker.is_harvested"));
            return true;    // We can't check attributes on a harvested file.
        }        
        
        // Reset local variables
        //
        setFileExists(false);
        setFilesize(null);
        setChecksumValue(null);
        
        // -------------------------------------
        // Get access to the local file
        // -------------------------------------            
        DataFileIO dataFileAccess;
        try {
            dataFileAccess = dataFile.getAccessObject();
            // Check the size on disk
        } catch (IOException ex) {
            errMsg = BundleUtil.getStringFromBundle("datafile.checker.could_not_get_io_object", Arrays.asList(this.getFileId().toString()));
            addError(errMsg);
            return false;
        }
        
        if (dataFileAccess == null){
            errMsg = BundleUtil.getStringFromBundle("datafile.checker.could_not_get_io_object", Arrays.asList(this.getFileId().toString()));
            addError(errMsg);
            return false;
        }
        
        if (!dataFileAccess.isLocalFile()){
            errMsg = BundleUtil.getStringFromBundle("datafile.checker.not_local_file", Arrays.asList(this.getFileId().toString()));
            addError(errMsg);
            return false;
        }

        // Get the file path
        //
        Path filePath;
        try {
            filePath = dataFileAccess.getFileSystemPath();

        } catch (IOException ex) {
            errMsg = BundleUtil.getStringFromBundle("datafile.checker.failed_to_get_path", Arrays.asList(this.getFileId().toString()));
            addError(errMsg);
            return false;
        }
        
        if (filePath == null){            
            errMsg = BundleUtil.getStringFromBundle("datafile.checker.null_file_path", Arrays.asList(this.getFileId().toString()));
            addError(errMsg);
            return false;
        }
        
        // -------------------------------------            
        // Verify that the file exists
        // -------------------------------------
        File fileObject = new File(filePath.normalize().toString());
        if (!(fileObject.isFile())){
            errMsg = BundleUtil.getStringFromBundle("datafile.checker.not_a_file", Arrays.asList(this.getFileId().toString()));
            addError(errMsg);
            return false;
        }
        setFileExists(true);
        
        // -------------------------------------            
        // Get the file size
        // -------------------------------------            
        this.setFilesize(fileObject.length());
                
        // -------------------------------------            
        // Calculate the checksum
        // -------------------------------------            
        setChecksumType(dataFile.getChecksumType().toString());

        String newChecksum = FileUtil.CalculateCheckSum(fileObject.getAbsolutePath(), dataFile.getChecksumType());
        if (newChecksum == null){
            List<String> errStrings = Arrays.asList(this.getFileId().toString(), fileObject.getAbsolutePath());
            errMsg = BundleUtil.getStringFromBundle("datafile.checker.failed_calculate_checksum",
                                errStrings);
            addError(errMsg);
            return false;
        }
        setChecksumValue(newChecksum);
        
        return true;
    }
    
    
    /**
     * If the calculated filesize and checksum  
     * differ from what's in the database, then update 
     * the database DataFile record
     */
    public boolean updateDataFileObject(){
        
        if (this.fileUpdatedInDatabase){
            throw new UnsupportedOperationException("The datafile object has already been udpated");                        
        }
        if (this.hasError()){
            throw new UnsupportedOperationException("Make sure 'hasError()' is false before calling this method");
        }
        if (this.doCurrentAttributesMatch()){
            throw new UnsupportedOperationException("Make sure 'doCurrentAttributesMatch()' is false before calling this method");            
        }
        
        dataFile.setFilesize(this.getFilesize());
        dataFile.setChecksumValue(this.getChecksumValue());

        dataFile = fileService.save(dataFile);
        
        fileUpdatedInDatabase = true;
        
        return true;
    }
    
    
    /**
     * Check if the DataFile attributes match the actual file
     * 
     * @return 
     */
    public boolean doCurrentAttributesMatch(){
        
        if (this.hasError()){
            return false;
        }
    
        // Does file exist?
        //
        if (!getFileExists()){
            return false;
        }

        // Does actual file size match db?
        //
        if ((this.getFilesize()==null)||
           (dataFile.getFilesize() != this.getFilesize())){
            return false;
        }

        // Does actual checksum match db?
        //
        if (!dataFile.getChecksumValue().equals(this.getChecksumValue())){
            return false;
        }
    
        return true;
    }
    
    /**
     * Was an error found?
     * 
     * @return 
     */
    public boolean hasError(){
        return this.errorFound;
        
    }
    
    /**
     * Add error message
     * 
     * @param errMsg 
     */
    private void addError(String errMsg){
        
        if (errMsg == null){
            throw new NullPointerException("errMsg cannot be null");
        }
        this.errorFound = true;
 
        this.errorMessage = errMsg;
    }
    
    /**
     *  Set fileId
     *  @param fileId
     */
    public void setFileId(Long fileId){
        if (fileId == null){
            throw new NullPointerException("fileId cannot be null");
        }
        this.fileId = fileId;
    }

    /**
     *  Get for fileId
     *  @return Long
     */
    public Long getFileId(){
        return this.fileId;
    }
    

    /**
     *  Set filesize
     *  @param filesize
     */
    public void setFilesize(Long filesize){
        this.filesize = filesize;
    }

    /**
     *  Get for filesize
     *  @return Long
     */
    public Long getFilesize(){
        return this.filesize;
    }
    

    /**
     *  Set checksumValue
     *  @param checksumValue
     */
    public void setChecksumValue(String checksumValue){
        this.checksumValue = checksumValue;
    }

    /**
     *  Get for checksumValue
     *  @return String
     */
    public String getChecksumValue(){
        return this.checksumValue;
    }
    

    /**
     *  Set checksumType
     *  @param checksumType
     */
    public void setChecksumType(String checksumType){
        this.checksumType = checksumType;
    }

    /**
     *  Get for checksumType
     *  @return String
     */
    public String getChecksumType(){
        return this.checksumType;
    }
    

    /**
     *  Set dataFile
     *  @param dataFile
     */
    public void setDataFile(DataFile dataFile){
        this.dataFile = dataFile;
    }

    /**
     *  Get for dataFile
     *  @return DataFile
     */
    public DataFile getDataFile(){
        return this.dataFile;
    }
    

    /**
     *  Set errorFound
     *  @param errorFound
     */
    public void setErrorFound(boolean errorFound){
        this.errorFound = errorFound;
    }

    /**
     *  Get for errorFound
     *  @return boolean
     */
    public boolean getErrorFound(){
        return this.errorFound;
    }
    

    /**
     *  Set errorMessage
     *  @param errorMessage
     */
    public void setErrorMessage(String errorMessage){
        this.errorMessage = errorMessage;
    }

    /**
     *  Get for errorMessage
     *  @return String
     */
    public String getErrorMessage(){
        return this.errorMessage;
    }

    
    /**
     *  Set userMessage
     *  @param userMessage
     */
    public void setUserMessage(String userMessage){
        this.userMessage = userMessage;
    }

    /**
     *  Get for userMessage
     *  @return String
     */
    public String getUserMessage(){
        return this.userMessage;
    }

    /**
     *  Set fileExists
     *  @param fileExists
     */
    public void setFileExists(boolean fileExists){
        this.fileExists = fileExists;
    }

    /**
     *  Get for fileExists
     *  @return boolean
     */
    public boolean getFileExists(){
        return this.fileExists;
    }

    /**
     * get file information
     * 
     * @return 
     */
    public JsonObjectBuilder getFileInfo(){
        
        return Json.createObjectBuilder().add("id", this.getFileId())                                
                                         .add("isDatabaseAccurate", this.doCurrentAttributesMatch())
                                         .add("actualFilesize", this.getFilesize())
                                         .add("actualCheckSumValue", this.getChecksumValue())
                                         .add("actualCheckSumType", this.getChecksumType())
                                         .add("dbFilesize", dataFile.getFilesize())
                                         .add("dbCheckSumValue", dataFile.getChecksumValue())
                                         .add("dbCheckSumType", dataFile.getChecksumType().toString());
    }
            
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dataverse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
//import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author skraffmiller
 */
@Entity
public class DatasetVersion implements Serializable {

  // TODO: Determine the UI implications of various version states
    //IMPORTANT: If you add a new value to this enum, you will also have to modify the
    // StudyVersionsFragment.xhtml in order to display the correct value from a Resource Bundle
    public enum VersionState {

        DRAFT, IN_REVIEW, RELEASED, ARCHIVED, DEACCESSIONED
    };

    public DatasetVersion() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Version
    private Long version;

    public Long getVersion() {
        return this.version;
    }

    public void setVersion(Long version) {
    }

    private Long versionNumber;
    public static final int VERSION_NOTE_MAX_LENGTH = 1000;
    @Column(length = VERSION_NOTE_MAX_LENGTH)
    private String versionNote;

    @Enumerated(EnumType.STRING)
    private VersionState versionState;

    @ManyToOne
    private Dataset dataset;
    @OneToMany(mappedBy = "datasetVersion", cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("category") // this is not our preferred ordering, which is with the AlphaNumericComparator, but does allow the files to be grouped by category
    private List<FileMetadata> fileMetadatas;

    public List<FileMetadata> getFileMetadatas() {
        return fileMetadatas;
    }

    public void setFileMetadatas(List<FileMetadata> fileMetadatas) {
        this.fileMetadatas = fileMetadatas;
    }

    @OneToMany(mappedBy = "datasetVersion", cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("datasetField.displayOrder") 
    private List<DatasetFieldValue> datasetFieldValues = new ArrayList<>();
    public List<DatasetFieldValue> getDatasetFieldValues() {
        return datasetFieldValues;
    }
    public void setDatasetFieldValues(List<DatasetFieldValue> datasetFieldValues) {
        this.datasetFieldValues = datasetFieldValues;
    }
   
    /*
     @OneToMany(mappedBy="studyVersion", cascade={CascadeType.REMOVE, CascadeType.PERSIST})
     private List<VersionContributor> versionContributors;
     */
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date createTime;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date lastUpdateTime;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date releaseTime;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date archiveTime;
    public static final int ARCHIVE_NOTE_MAX_LENGTH = 1000;
    @Column(length = ARCHIVE_NOTE_MAX_LENGTH)
    private String archiveNote;
    private String deaccessionLink;

    public Date getArchiveTime() {
        return archiveTime;
    }

    public void setArchiveTime(Date archiveTime) {
        this.archiveTime = archiveTime;
    }

    public String getArchiveNote() {
        return archiveNote;
    }

    public void setArchiveNote(String note) {
        if (note.length() > ARCHIVE_NOTE_MAX_LENGTH) {
            throw new IllegalArgumentException("Error setting archiveNote: String length is greater than maximum (" + ARCHIVE_NOTE_MAX_LENGTH + ")."
                    + "  StudyVersion id=" + id + ", archiveNote=" + note);
        }
        this.archiveNote = note;
    }

    public String getDeaccessionLink() {
        return deaccessionLink;
    }

    public void setDeaccessionLink(String deaccessionLink) {
        this.deaccessionLink = deaccessionLink;
    }

    public GlobalId getDeaccessionLinkAsGlobalId() {
        return new GlobalId(deaccessionLink);
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        if (createTime == null) {
            createTime = lastUpdateTime;
        }
        this.lastUpdateTime = lastUpdateTime;
    }

    public Date getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Date releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getVersionNote() {
        return versionNote;
    }

    public void setVersionNote(String note) {
        if (note != null && note.length() > VERSION_NOTE_MAX_LENGTH) {
            throw new IllegalArgumentException("Error setting versionNote: String length is greater than maximum (" + VERSION_NOTE_MAX_LENGTH + ")."
                    + "  StudyVersion id=" + id + ", versionNote=" + note);
        }
        this.versionNote = note;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }

    public VersionState getVersionState() {
        return versionState;
    }

    public void setVersionState(VersionState versionState) {

        this.versionState = versionState;
    }

    public boolean isReleased() {
        return versionState.equals(VersionState.RELEASED);
    }

    public boolean isInReview() {
        return versionState.equals(VersionState.IN_REVIEW);
    }

    public boolean isDraft() {
        return versionState.equals(VersionState.DRAFT);
    }

    public boolean isWorkingCopy() {
        return (versionState.equals(VersionState.DRAFT) || versionState.equals(VersionState.IN_REVIEW));
    }

    public boolean isArchived() {
        return versionState.equals(VersionState.ARCHIVED);
    }

    public boolean isDeaccessioned() {
        return versionState.equals(VersionState.DEACCESSIONED);
    }

    public boolean isRetiredCopy() {
        return (versionState.equals(VersionState.ARCHIVED) || versionState.equals(VersionState.DEACCESSIONED));
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DatasetVersion)) {
            return false;
        }
        DatasetVersion other = (DatasetVersion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.harvard.iq.dataverse[id=" + id + "]";
    }

    public boolean isLatestVersion() {
        return true;
        //return this.equals( this.getDataset().getLatestVersion() );
    }

    public String getTitle() {
        String retVal = "Dataset Title";
        for (DatasetFieldValue dsfv: this.getDatasetFieldValues()){
            if (dsfv.getDatasetField().getName().equals(DatasetFieldConstant.title)){
                retVal = dsfv.getStrValue();
            }
        }
        return retVal;
    }

    public String getProductionDate() {
        //todo get "Production Date" from datasetfieldvalue table
        return "Production Date";
    }

    public List<DatasetAuthor> getDatasetAuthors() {
        //todo get "List of Authors" from datasetfieldvalue table
        return new ArrayList();
    }

    public String getDistributionDate() {
        //todo get dist date from datasetfieldvalue table
        return "Distribution Date";
    }

    public String getUNF() {
        //todo get dist date from datasetfieldvalue table
        return "UNF";
    }

    //TODO - make sure getCitation works
    private String getYearForCitation(String dateString) {
        //get date to first dash only
        if (dateString.indexOf("-") > -1) {
            return dateString.substring(0, dateString.indexOf("-"));
        }
        return dateString;
    }

    /**
     * @todo: delete this method? It seems to have been replaced by the version
     * in DatasetVersionUI
     */
    public String getCitation() {
        return getCitation(true);
    }

    /**
     * @todo: delete this method? It seems to have been replaced by the version
     * in DatasetVersionUI
     */
    public String getCitation(boolean isOnlineVersion) {

        Dataset dataset = getDataset();

        String str = "";

        boolean includeAffiliation = false;
        String authors = getAuthorsStr(includeAffiliation);
        if (!StringUtil.isEmpty(authors)) {
            str += authors;
        }

        if (!StringUtil.isEmpty(getDistributionDate())) {
            if (!StringUtil.isEmpty(str)) {
                str += ", ";
            }
            str += getYearForCitation(getDistributionDate());
        } else {
            if (!StringUtil.isEmpty(getProductionDate())) {
                if (!StringUtil.isEmpty(str)) {
                    str += ", ";
                }
                str += getYearForCitation(getProductionDate());
            }
        }
        if (!StringUtil.isEmpty(getTitle())) {
            if (!StringUtil.isEmpty(str)) {
                str += ", ";
            }
            str += "\"" + getTitle() + "\"";
        }
        if (!StringUtil.isEmpty(dataset.getIdentifier())) {
            if (!StringUtil.isEmpty(str)) {
                str += ", ";
            }
            if (isOnlineVersion) {
                str += "<a href=\"" + dataset.getPersistentURL() + "\">" + dataset.getIdentifier() + "</a>";
            } else {
                str += dataset.getPersistentURL();
            }

        }

        if (!StringUtil.isEmpty(getUNF())) {
            if (!StringUtil.isEmpty(str)) {
                str += " ";
            }
            str += getUNF();
        }
        String distributorNames = getDistributorNames();
        if (distributorNames.length() > 0) {
            str += " " + distributorNames;
            str += " [Distributor]";
        }

        if (this.getVersionNumber() != null) {
            str += " V" + this.getVersionNumber();
            str += " [Version]";
        }

        return str;
    }

    public List<DatasetDistributor> getDatasetDistributors() {
        //todo get distributors from DatasetfieldValues
        return new ArrayList();
    }

    public String getDistributorNames() {
        String str = "";
        for (DatasetDistributor sd : this.getDatasetDistributors()) {
            if (str.trim().length() > 1) {
                str += ";";
            }
            str += sd.getName();
        }
        return str;
    }

    public String getAuthorsStr() {
        return getAuthorsStr(true);
    }

    public String getAuthorsStr(boolean affiliation) {
        String str = "";
        for (DatasetAuthor sa : getDatasetAuthors()) {
            if (str.trim().length() > 1) {
                str += "; ";
            }
            str += sa.getName().getStrValue();
            if (affiliation) {
                if (!StringUtil.isEmpty(sa.getAffiliation().getStrValue())) {
                    str += " (" + sa.getAffiliation().getStrValue() + ")";
                }
            }
        }
        return str;
    }

    public List<DatasetFieldValue> initDatasetFieldValues() {
        //retList - Return List of values
        List<DatasetFieldValue> retList = new ArrayList();
        //if the datasetversion already has values add them here
        if (this.getDatasetFieldValues()!= null) {
           retList.addAll(this.getDatasetFieldValues());
        }
               
        //Test to see that there are values for 
        // all fields in this dataset via metadata blocks
        //only add if not added above
        for (MetadataBlock mdb : this.getDataset().getOwner().getMetadataBlocks()) {
            for (DatasetField dsf : mdb.getDatasetFields()) {
                boolean add = true;
                //don't add if already added as a val
                for (DatasetFieldValue dsfv : retList) {
                    if (dsf.equals(dsfv.getDatasetField())) {
                        add = false;
                    }
                }
                //don't add if it has a parent - it will be added as a child
                if (dsf.isHasParent()){
                    add = false;
                }
                if (add) {
                    DatasetFieldValue addDsfv = new DatasetFieldValue();
                    addDsfv.setDatasetField(dsf);
                    addDsfv.setDatasetVersion(this);  
                    dsf.getDatasetFieldValues().add(addDsfv);
                    //if there are children create link here
                    if (dsf.isHasChildren()) {
                        addDsfv.setChildDatasetFieldValues(new ArrayList());
                        for (DatasetField dsfc : dsf.getChildDatasetFields()) {
                            DatasetFieldValue dsfvc = new DatasetFieldValue();
                            dsfvc.setDatasetField(dsfc);
                            dsfvc.setParentDatasetFieldValue(addDsfv);
                            dsfvc.setDatasetVersion(this);
                            addDsfv.getChildDatasetFieldValues().add(dsfvc);
                            retList.add(dsfvc);
                        }
                    }
                    retList.add(addDsfv);
                }
            }
        }

        
        //sort via display order on dataset field
        Collections.sort(retList, new Comparator<DatasetFieldValue>(){
           public int compare (DatasetFieldValue d1, DatasetFieldValue d2){
               int a = d1.getDatasetField().getDisplayOrder();
               int b = d2.getDatasetField().getDisplayOrder();
               return Integer.valueOf(a).compareTo(Integer.valueOf(b));
           }
       });
        
        return retList;
    }
}

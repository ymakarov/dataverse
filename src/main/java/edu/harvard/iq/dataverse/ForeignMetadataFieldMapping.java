

package edu.harvard.iq.dataverse;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author Leonid Andreev
 */
/**
 * @todo The constraint
 * "@UniqueConstraint(columnNames={"foreignMetadataFormatMapping_id","foreignFieldXpath"})"
 * needs to be reexamined because https://github.com/IQSS/dataverse/issues/2187
 * introduces a requirement to support "agency" as an XML attribute for both
 * dcterms:isReferencedBy (existing and documented) and dcterms:identifier
 * (new). If you try to add the new "agency" for dcterms:identifier with...
 *
 * "INSERT INTO foreignmetadatafieldmapping (id, foreignfieldxpath,
 * datasetfieldname, isattribute, parentfieldmapping_id,
 * foreignmetadataformatmapping_id) VALUES (20, 'agency', 'otherIdAgency', TRUE,
 * 2, 1 );"
 *
 * ... you get this constraint violation:
 *
 * "ERROR: duplicate key value violates unique constraint
 * "unq_foreignmetadatafieldmapping_0" DETAIL: Key
 * (foreignmetadataformatmapping_id, foreignfieldxpath)=(1, agency) already
 * exists."
 *
 * What this means is that id 1 (dcterms, the only one we use so far) can only
 * have one "agency".
 */
@Table( uniqueConstraints = @UniqueConstraint(columnNames={"foreignMetadataFormatMapping_id","foreignFieldXpath"}) 
      , indexes = {@Index(columnList="foreignmetadataformatmapping_id")
		, @Index(columnList="foreignfieldxpath")
		, @Index(columnList="parentfieldmapping_id")})
@NamedQueries({
  @NamedQuery( name="ForeignMetadataFieldMapping.findByPath",
               query="SELECT fmfm FROM ForeignMetadataFieldMapping fmfm WHERE fmfm.foreignMetadataFormatMapping.name=:formatName AND fmfm.foreignFieldXPath=:xPath")  
})
@Entity
public class ForeignMetadataFieldMapping implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    @ManyToOne(cascade = CascadeType.MERGE)
    private ForeignMetadataFormatMapping foreignMetadataFormatMapping;

    @Column(name = "foreignFieldXPath", columnDefinition = "TEXT")
    private String foreignFieldXPath;
    
    @Column(name = "datasetfieldName", columnDefinition = "TEXT")
    private String datasetfieldName;    

    @OneToMany(mappedBy = "parentFieldMapping", cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private Collection<ForeignMetadataFieldMapping> childFieldMappings;
        
    @ManyToOne(cascade = CascadeType.MERGE)
    private ForeignMetadataFieldMapping parentFieldMapping;
    
    private boolean isAttribute;
    
    /* getters/setters: */

    public ForeignMetadataFormatMapping getForeignMetadataFormatMapping() {
        return foreignMetadataFormatMapping;
    }

    public void setForeignMetadataFormatMapping(ForeignMetadataFormatMapping foreignMetadataFormatMapping) {
        this.foreignMetadataFormatMapping = foreignMetadataFormatMapping;
    }
    
    public String getForeignFieldXPath() {
        return foreignFieldXPath;
    }

    public void setForeignFieldXPath(String foreignFieldXPath) {
        this.foreignFieldXPath = foreignFieldXPath;
    }
    
    public String getDatasetfieldName() {
        return datasetfieldName;
    }

    public void setDatasetfieldName(String datasetfieldName) {
        this.datasetfieldName = datasetfieldName;
    }
    
    
    public Collection<ForeignMetadataFieldMapping> getChildFieldMappings() {
        return this.childFieldMappings;
    }

    public void setChildFieldMappings(Collection<ForeignMetadataFieldMapping> childFieldMappings) {
        this.childFieldMappings = childFieldMappings;
    }
    
    /*
    public Collection<ForeignMetadataFieldMapping> getAttributeMappings() {
        return this.attributeMappings;
    }

    public void setAttributeMappings(Collection<ForeignMetadataFieldMapping> attributeMappings) {
        this.attributeMappings = attributeMappings;
    }
    */
    
    
    public ForeignMetadataFieldMapping getParentFieldMapping() {
        return parentFieldMapping;
    }

    public void setParentFieldMapping(ForeignMetadataFieldMapping parentFieldMapping) {
        this.parentFieldMapping = parentFieldMapping;
    }
    
    public boolean isAttribute() {
        return isAttribute;
    }

    public void setIsAttribute(boolean isAttribute) {
        this.isAttribute = isAttribute;
    }
    
    /* logical: */
    
    public boolean isChild() {
        return this.parentFieldMapping != null;        
    }    
    
    public boolean HasChildren() {
        return !this.childFieldMappings.isEmpty();
    }
    
    /*
    public boolean HasAttributes() {
        return !this.attributeMappings.isEmpty();
    }
    */

    public boolean HasParent() {
        return this.parentFieldMapping != null;
    }
    /* overrides: */ 

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ForeignMetadataFieldMapping)) {
            return false;
        }
        ForeignMetadataFieldMapping other = (ForeignMetadataFieldMapping) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "edu.harvard.iq.dataverse.ForeignMetadataFieldMapping[ id=" + id + " ]";
    }
    
}


package edu.harvard.iq.dataverse.export;

import java.io.OutputStream;
import java.util.Map;
import javax.json.JsonObject;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import com.google.auto.service.AutoService;
import edu.harvard.iq.dataverse.DataCitation;
import edu.harvard.iq.dataverse.Dataset;
import edu.harvard.iq.dataverse.DatasetAuthor;
import edu.harvard.iq.dataverse.DatasetVersion;
import edu.harvard.iq.dataverse.export.ddi.DdiExportUtil;
import edu.harvard.iq.dataverse.export.spi.Exporter;
import edu.harvard.iq.dataverse.util.BundleUtil;

@AutoService(Exporter.class)
public class CrossRefExporter implements Exporter {

    private static String DEFAULT_XML_NAMESPACE = "http://www.crossref.org/schema/4.3.7";
    private static String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";
    private static String DEFAULT_XML_SCHEMALOCATION = "http://www.crossref.org/schema/4.3.7 http://www.crossref.org/schemas/crossref4.3.7.xsd";
    private static String DEFAULT_XML_VERSION = "4.3.7";

    public static final String NAME = "Crossref";
    @Override
    public String getProviderName() {
        return NAME;
    }

    @Override
    public String getDisplayName() {
        return BundleUtil.getStringFromBundle("dataset.exportBtn.itemLabel.crossref") != null
                ? BundleUtil.getStringFromBundle("dataset.exportBtn.itemLabel.crossref")
                : "CrossRef";
    }

    @Override
    public void exportDataset(DatasetVersion version, JsonObject json, OutputStream outputStream)
            throws ExportException {
        try {
            DataCitation dc = new DataCitation(version);
            
            Map<String, String> metadata = dc.getDataCiteMetadata();
            XMLStreamWriter xmlw = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream);

            xmlw.writeStartElement("doi_batch"); // <doi_batch>

            xmlw.writeAttribute("xmlns:xsi", XSI_NAMESPACE);
            xmlw.writeAttribute("xmlns", DEFAULT_XML_NAMESPACE);
            xmlw.writeAttribute("xsi:schemaLocation", DEFAULT_XML_SCHEMALOCATION);
            xmlw.writeStartElement("head"); // <head>

            xmlw.writeStartElement("doi_batch_id"); // <doi_batch_id>
            xmlw.writeCharacters("2006-03-24-21-57-31-10023");
            xmlw.writeEndElement(); // <doi_batch_id>

            xmlw.writeStartElement("timestamp"); // <timestamp>
            xmlw.writeCharacters("20060324215731");
            xmlw.writeEndElement(); // <timestamp>

            xmlw.writeStartElement("depositor"); // <depositor>

            xmlw.writeStartElement("depositor_name"); // <depositor_name>
            xmlw.writeCharacters("Sample Master");
            xmlw.writeEndElement(); // <depositor_name>

            xmlw.writeStartElement("email_address"); // <email_address>
            xmlw.writeCharacters("support@crossref.org");
            xmlw.writeEndElement(); // <email_address>
            xmlw.writeEndElement(); // <depositor>

            xmlw.writeStartElement("registrant"); // <registrant>
            xmlw.writeCharacters("CrossRef");
            xmlw.writeEndElement(); // <registrant>
            xmlw.writeEndElement(); // <head>

            xmlw.writeStartElement("body"); // <body>

            xmlw.writeStartElement("database"); // <database>

            xmlw.writeStartElement("database_metadata"); // <database_metadata>
            xmlw.writeAttribute("language", "en");

            xmlw.writeStartElement("titles");
            xmlw.writeStartElement("title");
            xmlw.writeCharacters("Openlit Datasets");
            xmlw.writeEndElement(); // <title>

            xmlw.writeEndElement(); // <titles>

            xmlw.writeStartElement("institution");
            xmlw.writeStartElement("institution_name");
            xmlw.writeCharacters("Institute of Russian Literature, Russian Academy of Science");
            xmlw.writeEndElement(); // <institution_name>

            xmlw.writeStartElement("institution_acronym");
            xmlw.writeCharacters("IRLI RAN");
            xmlw.writeEndElement(); // <institution_actronym>
            xmlw.writeEndElement(); // <institution>


            xmlw.writeStartElement("doi_data");
            xmlw.writeStartElement("doi");
            xmlw.writeCharacters("10.1621/NURSA_dataset_home");
            xmlw.writeEndElement(); // <doi>

            xmlw.writeStartElement("resource");
            xmlw.writeCharacters("http://www.nursa.org/template.cfm?threadId=10222");
            xmlw.writeEndElement(); // <resource>

            xmlw.writeEndElement(); // <doi_data>
            xmlw.writeEndElement(); // <database_metadata>

            xmlw.writeStartElement("dataset");
            xmlw.writeAttribute("dataset_type", "collection");
            xmlw.writeStartElement("contributors");
            for (DatasetAuthor datasetAuthor : version.getDatasetAuthors()) {

                String authorName = datasetAuthor.getName().getDisplayValue();
                String[] parts = authorName.split(",");
                if (parts.length < 1) {
                    continue;
                }
                String surname = parts[0];
                String givenName = parts.length > 1 ? parts[1] : null;

                xmlw.writeStartElement("person_name");
                xmlw.writeAttribute("contributor_role", "author");
                xmlw.writeAttribute("sequence", "first");
                if (givenName != null) {
                    xmlw.writeStartElement("given_name");
                    xmlw.writeCharacters(givenName);
                    xmlw.writeEndElement(); // <given_name>
                }
                xmlw.writeStartElement("surname");
                xmlw.writeCharacters(surname);
                xmlw.writeEndElement(); // <surname>

                xmlw.writeEndElement(); // <person_name>
            }

            xmlw.writeEndElement(); // <contributors>

            xmlw.writeStartElement("titles");
            xmlw.writeStartElement("title");
            xmlw.writeCharacters(version.getTitle());
            xmlw.writeEndElement(); // <title>

            xmlw.writeEndElement(); // <titles>

            xmlw.writeStartElement("doi_data");
            xmlw.writeStartElement("doi");
            xmlw.writeCharacters(version.getDataset().getPersistentURL());
            xmlw.writeEndElement(); // <doi>

            xmlw.writeStartElement("resource");
            xmlw.writeCharacters(DdiExportUtil.getDataverseSiteUrl() + Dataset.TARGET_URL +
                    version.getDataset().getGlobalId().asString());
            xmlw.writeEndElement(); // <resource>

            xmlw.writeEndElement(); // <doi_data>

            xmlw.writeEndElement(); // <dataset>


            xmlw.writeEndElement(); // <database>
            xmlw.writeEndElement(); // <body>

            xmlw.writeEndElement(); // </doi_batch>

            xmlw.flush();
        } catch (XMLStreamException e) {
            throw new ExportException("Caught XMLStreamException performing Crossref export");
        }
    }

    @Override
    public Boolean isXMLFormat() {
        return true;
    }

    @Override
    public Boolean isHarvestable() {
        return true;
    }

    @Override
    public Boolean isAvailableToUsers() {
        return true;
    }

    @Override
    public String getXMLNameSpace() throws ExportException {
        return CrossRefExporter.DEFAULT_XML_NAMESPACE;
    }

    @Override
    public String getXMLSchemaLocation() throws ExportException {
        return CrossRefExporter.DEFAULT_XML_SCHEMALOCATION;
    }

    @Override
    public String getXMLSchemaVersion() throws ExportException {
        return CrossRefExporter.DEFAULT_XML_VERSION;
    }

    @Override
    public void setParam(String name, Object value) {
        // this exporter does not uses or supports any parameters as of now.
    }

}

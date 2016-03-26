/*
   Copyright (C) 2005-2016, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
*/

package edu.harvard.iq.dataverse.batch.processor;

import edu.harvard.iq.dataverse.DataFile;
import edu.harvard.iq.dataverse.Dataset;
import edu.harvard.iq.dataverse.batch.util.Utils;
import org.easybatch.core.processor.RecordProcessor;
import org.easybatch.core.record.StringRecord;
import org.easybatch.core.validator.RecordValidationException;

/**
 * Filesystem processor.
 *
 * @author Bill McKinney (mckinney@hkl.hms.harvard.edu)
 */

public class ChecksumProcessor implements RecordProcessor<StringRecord, StringRecord> {

    private Dataset dataset;

    public ChecksumProcessor(Dataset dataset) { this.dataset = dataset; }

    @Override
    public StringRecord processRecord(StringRecord record) throws RecordValidationException {

        String sha = record.getPayload().split("\\s+\\./")[0];
        String path = record.getPayload().split("\\s+\\./")[1];

        // locate the datafile
        DataFile dataFile = Utils.getDataFile(this.dataset, path);

        // set the checksum
        if (dataFile != null) {

            if (dataFile.getmd5().equalsIgnoreCase(sha)) {
                System.out.println("BEFORE=AFTER");
                return null; // skip it, it was already set
            } else {
                System.out.println("BEFORE!=AFTER : " + dataFile.getmd5() + " -> " + sha );
                dataFile.setmd5(sha);
                Utils.saveDataFile(dataFile); // commit change
                return record;
            }
        } else {
            throw new RecordValidationException("Can't find the DataFile for: " + path);
        }
    }

}
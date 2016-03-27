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

import edu.harvard.iq.dataverse.*;
import edu.harvard.iq.dataverse.batch.util.Utils;
import org.easybatch.core.processor.RecordProcessor;
import org.easybatch.core.record.FileRecord;

import java.util.*;

/**
 * Filesystem processor.
 *
 * @author Bill McKinney (mckinney@hkl.hms.harvard.edu)
 */

public class FilesystemProcessor implements RecordProcessor<FileRecord, FileRecord> {

    private Dataset dataset;

    public FilesystemProcessor(Dataset dataset) {
        this.dataset = dataset;
    }

    @Override
    public FileRecord processRecord(FileRecord record) {

        List<DataFile> datafiles = this.dataset.getFiles();

        // determine relative path to dataset folder
        String path = record.getPayload().getAbsolutePath();
        String dsid = this.dataset.getIdentifier();
        String relativePath = path.substring(path.indexOf(dsid) + dsid.length() + 1);

        // if we can't find it, create it
        if (!Utils.alreadyImported(this.dataset,relativePath )) {
            datafiles.add(Utils.createDataFile(this.dataset, record.getPayload()));
            dataset.getLatestVersion().getDataset().setFiles(datafiles);
            return record;
        } else {
            return null; // filtering it since we already have it
        }
    }
}
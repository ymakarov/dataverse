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

package edu.harvard.iq.dataverse.batch.filter;

import edu.harvard.iq.dataverse.Dataset;
import edu.harvard.iq.dataverse.batch.util.Utils;
import org.easybatch.core.filter.RecordFilter;
import org.easybatch.core.record.FileRecord;

public class DatafileExistsFilter implements RecordFilter<FileRecord> {

    private Dataset dataset;

    public DatafileExistsFilter(Dataset ds) { this.dataset = ds; }

    public FileRecord processRecord(final FileRecord record) {
        if (Utils.alreadyImported(dataset, record.toString())) {
            return null;
        } else {
            return record;
        }
    }

}

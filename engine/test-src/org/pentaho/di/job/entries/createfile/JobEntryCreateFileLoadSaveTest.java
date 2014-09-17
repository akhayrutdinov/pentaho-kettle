package org.pentaho.di.job.entries.createfile;

import org.pentaho.di.job.entry.loadSave.JobEntryLoadSaveTestSupport;

import java.util.Arrays;
import java.util.List;

public class JobEntryCreateFileLoadSaveTest extends JobEntryLoadSaveTestSupport<JobEntryCreateFile> {
  @Override protected Class<JobEntryCreateFile> getJobEntryClass() {
    return JobEntryCreateFile.class;
  }

  @Override protected List<String> listCommonAttributes() {
    return Arrays.asList( "filename", "failIfFileExists", "addfilenameresult" );
  }
}
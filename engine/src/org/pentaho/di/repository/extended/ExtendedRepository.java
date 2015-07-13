/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2015 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.di.repository.extended;

import org.pentaho.di.core.ProgressMonitorListener;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryElementInterface;
import org.pentaho.di.trans.TransMeta;

import java.util.Calendar;

/**
 * This interface extends {@linkplain Repository} for letting user to have more control over repository.
 *
 * @author Andrey Khayrutdinov
 */
public interface ExtendedRepository extends Repository {

  /**
   * Returns <tt>true</tt>, if the repository supports caching shared objects (databases, slave servers, partitions,
   * clusters).
   *
   * @return <tt>true</tt> or <tt>false</tt> depending on whether or not the repository supports the caching
   */
  boolean canCacheSharedObjects();

  /**
   * Forces the repository to cache shared objects. This method should do nothing and return <tt>false</tt> if the
   * repository does not support the caching.
   *
   * @return <tt>true</tt> if operation completed successfully and <tt>false</tt> otherwise
   * @throws KettleException if an error occurred
   */
  boolean prepareSharedObjectsCache() throws KettleException;


  /**
   * Saves the <tt>job</tt> in the <tt>repository</tt>. The difference of this method against {@linkplain
   * Repository#save(RepositoryElementInterface, String, Calendar, ProgressMonitorListener, boolean)} is that this
   * method lets user not to save shared objects.
   *
   * @param job            job meta
   * @param versionComment version comment, <tt>null</tt> is acceptable
   * @param versionDate    version date, <tt>null</tt> is acceptable
   * @param saveShared     save-or-not flag for shared objects attached to the <tt>job</tt>
   * @param overwrite      overwrite flag for the <tt>job</tt>
   * @throws KettleException if an error occurred
   */
  void saveJob( JobMeta job, String versionComment, Calendar versionDate, boolean saveShared, boolean overwrite )
    throws KettleException;

  /**
   * Saves the <tt>trans</tt> in the <tt>repository</tt>. The difference of this method against {@linkplain
   * Repository#save(RepositoryElementInterface, String, Calendar, ProgressMonitorListener, boolean)} is that this
   * method lets user not to save shared objects.
   *
   * @param trans           trans meta
   * @param versionComment  version comment, <tt>null</tt> is acceptable
   * @param versionDate     version date, <tt>null</tt> is acceptable
   * @param saveShared      save-or-not flag for shared objects attached to the <tt>trans</tt>
   * @param overwrite       overwrite flag for the <tt>trans</tt>
   * @throws KettleException if an error occurred
   */
  void saveTrans( TransMeta trans, String versionComment, Calendar versionDate, boolean saveShared, boolean overwrite )
    throws KettleException;
}

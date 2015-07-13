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

import org.pentaho.di.cluster.ClusterSchema;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.Condition;
import org.pentaho.di.core.ProgressMonitorListener;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleSecurityException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.partition.PartitionSchema;
import org.pentaho.di.repository.IRepositoryExporter;
import org.pentaho.di.repository.IRepositoryImporter;
import org.pentaho.di.repository.IRepositoryService;
import org.pentaho.di.repository.IUser;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryElementInterface;
import org.pentaho.di.repository.RepositoryElementMetaInterface;
import org.pentaho.di.repository.RepositoryMeta;
import org.pentaho.di.repository.RepositoryObject;
import org.pentaho.di.repository.RepositoryObjectType;
import org.pentaho.di.repository.RepositorySecurityManager;
import org.pentaho.di.repository.RepositorySecurityProvider;
import org.pentaho.di.shared.SharedObjects;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.metastore.api.IMetaStore;

import java.util.Calendar;
import java.util.List;

/**
 * This class is a simple wrapper to let {@linkplain Repository} instance suit {@linkplain ExtendedRepository}
 * interface.
 *
 * @author Andrey Khayrutdinov
 */
public class ExtendedRepositoryAdapter implements ExtendedRepository {

  private final Repository repository;

  public ExtendedRepositoryAdapter( Repository repository ) {
    this.repository = repository;
  }

  @Override
  public boolean canCacheSharedObjects() {
    return false;
  }

  @Override
  public boolean prepareSharedObjectsCache() throws KettleException {
    return false;
  }

  @Override
  public void saveJob( JobMeta job, String versionComment, Calendar versionDate, boolean saveShared, boolean overwrite )
    throws KettleException {
    save( job, versionComment, versionDate, null, overwrite );
  }

  @Override
  public void saveTrans( TransMeta trans, String versionComment, Calendar versionDate, boolean saveShared,
                         boolean overwrite ) throws KettleException {
    save( trans, versionComment, versionDate, null, overwrite );
  }

  // Delegate in other cases

  @Override public String getName() {
    return repository.getName();
  }

  @Override public String getVersion() {
    return repository.getVersion();
  }

  @Override public RepositoryMeta getRepositoryMeta() {
    return repository.getRepositoryMeta();
  }

  @Override public IUser getUserInfo() {
    return repository.getUserInfo();
  }

  @Override public RepositorySecurityProvider getSecurityProvider() {
    return repository.getSecurityProvider();
  }

  @Override public RepositorySecurityManager getSecurityManager() {
    return repository.getSecurityManager();
  }

  @Override public LogChannelInterface getLog() {
    return repository.getLog();
  }

  @Override public void connect( String username, String password ) throws KettleException, KettleSecurityException {
    repository.connect( username, password );
  }

  @Override public void disconnect() {
    repository.disconnect();
  }

  @Override public boolean isConnected() {
    return repository.isConnected();
  }

  @Override public void init( RepositoryMeta repositoryMeta ) {
    repository.init( repositoryMeta );
  }

  @Override public boolean exists( String name, RepositoryDirectoryInterface repositoryDirectory,
                                   RepositoryObjectType objectType ) throws KettleException {
    return repository.exists( name, repositoryDirectory, objectType );
  }

  @Override public ObjectId getTransformationID( String name,
                                                 RepositoryDirectoryInterface repositoryDirectory )
    throws KettleException {
    return repository.getTransformationID( name, repositoryDirectory );
  }

  @Override public ObjectId getJobId( String name,
                                      RepositoryDirectoryInterface repositoryDirectory ) throws KettleException {
    return repository.getJobId( name, repositoryDirectory );
  }

  @Override public void save( RepositoryElementInterface repositoryElement,
                              String versionComment, ProgressMonitorListener monitor ) throws KettleException {
    repository.save( repositoryElement, versionComment, monitor );
  }

  @Override public void save( RepositoryElementInterface repositoryElement,
                              String versionComment, ProgressMonitorListener monitor, boolean overwrite )
    throws KettleException {
    repository.save( repositoryElement, versionComment, monitor, overwrite );
  }

  @Override public void save( RepositoryElementInterface repositoryElement,
                              String versionComment, Calendar versionDate,
                              ProgressMonitorListener monitor, boolean overwrite ) throws KettleException {
    repository.save( repositoryElement, versionComment, versionDate, monitor, overwrite );
  }

  @Override public RepositoryDirectoryInterface getDefaultSaveDirectory(
    RepositoryElementInterface repositoryElement ) throws KettleException {
    return repository.getDefaultSaveDirectory( repositoryElement );
  }

  @Override public RepositoryDirectoryInterface getUserHomeDirectory() throws KettleException {
    return repository.getUserHomeDirectory();
  }

  @Override public void clearSharedObjectCache() {
    repository.clearSharedObjectCache();
  }

  @Override public TransMeta loadTransformation( String transname,
                                                 RepositoryDirectoryInterface repdir,
                                                 ProgressMonitorListener monitor,
                                                 boolean setInternalVariables, String revision )
    throws KettleException {
    return repository.loadTransformation( transname, repdir, monitor, setInternalVariables, revision );
  }

  @Override public TransMeta loadTransformation( ObjectId id_transformation,
                                                 String versionLabel ) throws KettleException {
    return repository.loadTransformation( id_transformation, versionLabel );
  }

  @Override public SharedObjects readTransSharedObjects( TransMeta transMeta ) throws KettleException {
    return repository.readTransSharedObjects( transMeta );
  }

  @Override public ObjectId renameTransformation( ObjectId id_transformation,
                                                  RepositoryDirectoryInterface newDirectory,
                                                  String newName ) throws KettleException {
    return repository.renameTransformation( id_transformation, newDirectory, newName );
  }

  @Override public ObjectId renameTransformation( ObjectId id_transformation,
                                                  String versionComment,
                                                  RepositoryDirectoryInterface newDirectory,
                                                  String newName ) throws KettleException {
    return repository.renameTransformation( id_transformation, versionComment, newDirectory, newName );
  }

  @Override public void deleteTransformation( ObjectId id_transformation ) throws KettleException {
    repository.deleteTransformation( id_transformation );
  }

  @Override public JobMeta loadJob( String jobname,
                                    RepositoryDirectoryInterface repdir,
                                    ProgressMonitorListener monitor,
                                    String revision ) throws KettleException {
    return repository.loadJob( jobname, repdir, monitor, revision );
  }

  @Override public JobMeta loadJob( ObjectId id_job, String versionLabel ) throws KettleException {
    return repository.loadJob( id_job, versionLabel );
  }

  @Override public SharedObjects readJobMetaSharedObjects( JobMeta jobMeta ) throws KettleException {
    return repository.readJobMetaSharedObjects( jobMeta );
  }

  @Override public ObjectId renameJob( ObjectId id_job,
                                       String versionComment,
                                       RepositoryDirectoryInterface newDirectory,
                                       String newName ) throws KettleException {
    return repository.renameJob( id_job, versionComment, newDirectory, newName );
  }

  @Override public ObjectId renameJob( ObjectId id_job,
                                       RepositoryDirectoryInterface newDirectory,
                                       String newName ) throws KettleException {
    return repository.renameJob( id_job, newDirectory, newName );
  }

  @Override public void deleteJob( ObjectId id_job ) throws KettleException {
    repository.deleteJob( id_job );
  }

  @Override public DatabaseMeta loadDatabaseMeta( ObjectId id_database,
                                                  String revision ) throws KettleException {
    return repository.loadDatabaseMeta( id_database, revision );
  }

  @Override public void deleteDatabaseMeta( String databaseName ) throws KettleException {
    repository.deleteDatabaseMeta( databaseName );
  }

  @Override public ObjectId[] getDatabaseIDs( boolean includeDeleted ) throws KettleException {
    return repository.getDatabaseIDs( includeDeleted );
  }

  @Override public String[] getDatabaseNames( boolean includeDeleted ) throws KettleException {
    return repository.getDatabaseNames( includeDeleted );
  }

  @Override public List<DatabaseMeta> readDatabases() throws KettleException {
    return repository.readDatabases();
  }

  @Override public ObjectId getDatabaseID( String name ) throws KettleException {
    return repository.getDatabaseID( name );
  }

  @Override public ClusterSchema loadClusterSchema( ObjectId id_cluster_schema,
                                                    List<SlaveServer> slaveServers,
                                                    String versionLabel ) throws KettleException {
    return repository.loadClusterSchema( id_cluster_schema, slaveServers, versionLabel );
  }

  @Override public ObjectId[] getClusterIDs( boolean includeDeleted ) throws KettleException {
    return repository.getClusterIDs( includeDeleted );
  }

  @Override public String[] getClusterNames( boolean includeDeleted ) throws KettleException {
    return repository.getClusterNames( includeDeleted );
  }

  @Override public ObjectId getClusterID( String name ) throws KettleException {
    return repository.getClusterID( name );
  }

  @Override public void deleteClusterSchema( ObjectId id_cluster ) throws KettleException {
    repository.deleteClusterSchema( id_cluster );
  }

  @Override public SlaveServer loadSlaveServer( ObjectId id_slave_server,
                                                String versionLabel ) throws KettleException {
    return repository.loadSlaveServer( id_slave_server, versionLabel );
  }

  @Override public ObjectId[] getSlaveIDs( boolean includeDeleted ) throws KettleException {
    return repository.getSlaveIDs( includeDeleted );
  }

  @Override public String[] getSlaveNames( boolean includeDeleted ) throws KettleException {
    return repository.getSlaveNames( includeDeleted );
  }

  @Override public List<SlaveServer> getSlaveServers() throws KettleException {
    return repository.getSlaveServers();
  }

  @Override public ObjectId getSlaveID( String name ) throws KettleException {
    return repository.getSlaveID( name );
  }

  @Override public void deleteSlave( ObjectId id_slave ) throws KettleException {
    repository.deleteSlave( id_slave );
  }

  @Override public PartitionSchema loadPartitionSchema(
    ObjectId id_partition_schema, String versionLabel ) throws KettleException {
    return repository.loadPartitionSchema( id_partition_schema, versionLabel );
  }

  @Override public ObjectId[] getPartitionSchemaIDs( boolean includeDeleted ) throws KettleException {
    return repository.getPartitionSchemaIDs( includeDeleted );
  }

  @Override public String[] getPartitionSchemaNames( boolean includeDeleted ) throws KettleException {
    return repository.getPartitionSchemaNames( includeDeleted );
  }

  @Override public ObjectId getPartitionSchemaID( String name ) throws KettleException {
    return repository.getPartitionSchemaID( name );
  }

  @Override public void deletePartitionSchema( ObjectId id_partition_schema ) throws KettleException {
    repository.deletePartitionSchema( id_partition_schema );
  }

  @Override public RepositoryDirectoryInterface loadRepositoryDirectoryTree() throws KettleException {
    return repository.loadRepositoryDirectoryTree();
  }

  @Override public RepositoryDirectoryInterface findDirectory( String directory ) throws KettleException {
    return repository.findDirectory( directory );
  }

  @Override public RepositoryDirectoryInterface findDirectory(
    ObjectId directory ) throws KettleException {
    return repository.findDirectory( directory );
  }

  @Override public void saveRepositoryDirectory( RepositoryDirectoryInterface dir ) throws KettleException {
    repository.saveRepositoryDirectory( dir );
  }

  @Override public void deleteRepositoryDirectory( RepositoryDirectoryInterface dir ) throws KettleException {
    repository.deleteRepositoryDirectory( dir );
  }

  @Override public ObjectId renameRepositoryDirectory( ObjectId id,
                                                       RepositoryDirectoryInterface newParentDir,
                                                       String newName ) throws KettleException {
    return repository.renameRepositoryDirectory( id, newParentDir, newName );
  }

  @Override public RepositoryDirectoryInterface createRepositoryDirectory(
    RepositoryDirectoryInterface parentDirectory, String directoryPath ) throws KettleException {
    return repository.createRepositoryDirectory( parentDirectory, directoryPath );
  }

  @Override public String[] getTransformationNames( ObjectId id_directory, boolean includeDeleted )
    throws KettleException {
    return repository.getTransformationNames( id_directory, includeDeleted );
  }

  @Override public List<RepositoryElementMetaInterface> getJobObjects(
    ObjectId id_directory, boolean includeDeleted ) throws KettleException {
    return repository.getJobObjects( id_directory, includeDeleted );
  }

  @Override public List<RepositoryElementMetaInterface> getTransformationObjects(
    ObjectId id_directory, boolean includeDeleted ) throws KettleException {
    return repository.getTransformationObjects( id_directory, includeDeleted );
  }

  @Override public List<RepositoryElementMetaInterface> getJobAndTransformationObjects(
    ObjectId id_directory, boolean includeDeleted ) throws KettleException {
    return repository.getJobAndTransformationObjects( id_directory, includeDeleted );
  }

  @Override public String[] getJobNames( ObjectId id_directory, boolean includeDeleted ) throws KettleException {
    return repository.getJobNames( id_directory, includeDeleted );
  }

  @Override public String[] getDirectoryNames( ObjectId id_directory ) throws KettleException {
    return repository.getDirectoryNames( id_directory );
  }

  @Override public ObjectId insertLogEntry( String description ) throws KettleException {
    return repository.insertLogEntry( description );
  }

  @Override public void insertStepDatabase( ObjectId id_transformation,
                                            ObjectId id_step,
                                            ObjectId id_database ) throws KettleException {
    repository.insertStepDatabase( id_transformation, id_step, id_database );
  }

  @Override public void insertJobEntryDatabase( ObjectId id_job,
                                                ObjectId id_jobentry,
                                                ObjectId id_database ) throws KettleException {
    repository.insertJobEntryDatabase( id_job, id_jobentry, id_database );
  }

  @Override public void saveConditionStepAttribute( ObjectId id_transformation,
                                                    ObjectId id_step, String code,
                                                    Condition condition ) throws KettleException {
    repository.saveConditionStepAttribute( id_transformation, id_step, code, condition );
  }

  @Override public Condition loadConditionFromStepAttribute( ObjectId id_step,
                                                             String code ) throws KettleException {
    return repository.loadConditionFromStepAttribute( id_step, code );
  }

  @Override public boolean getStepAttributeBoolean( ObjectId id_step, int nr, String code, boolean def )
    throws KettleException {
    return repository.getStepAttributeBoolean( id_step, nr, code, def );
  }

  @Override public boolean getStepAttributeBoolean( ObjectId id_step, int nr, String code ) throws KettleException {
    return repository.getStepAttributeBoolean( id_step, nr, code );
  }

  @Override public boolean getStepAttributeBoolean( ObjectId id_step, String code ) throws KettleException {
    return repository.getStepAttributeBoolean( id_step, code );
  }

  @Override public long getStepAttributeInteger( ObjectId id_step, int nr, String code ) throws KettleException {
    return repository.getStepAttributeInteger( id_step, nr, code );
  }

  @Override public long getStepAttributeInteger( ObjectId id_step, String code ) throws KettleException {
    return repository.getStepAttributeInteger( id_step, code );
  }

  @Override public String getStepAttributeString( ObjectId id_step, int nr, String code ) throws KettleException {
    return repository.getStepAttributeString( id_step, nr, code );
  }

  @Override public String getStepAttributeString( ObjectId id_step, String code ) throws KettleException {
    return repository.getStepAttributeString( id_step, code );
  }

  @Override public void saveStepAttribute( ObjectId id_transformation,
                                           ObjectId id_step, int nr, String code, String value )
    throws KettleException {
    repository.saveStepAttribute( id_transformation, id_step, nr, code, value );
  }

  @Override public void saveStepAttribute( ObjectId id_transformation,
                                           ObjectId id_step, String code, String value ) throws KettleException {
    repository.saveStepAttribute( id_transformation, id_step, code, value );
  }

  @Override public void saveStepAttribute( ObjectId id_transformation,
                                           ObjectId id_step, int nr, String code, boolean value )
    throws KettleException {
    repository.saveStepAttribute( id_transformation, id_step, nr, code, value );
  }

  @Override public void saveStepAttribute( ObjectId id_transformation,
                                           ObjectId id_step, String code, boolean value ) throws KettleException {
    repository.saveStepAttribute( id_transformation, id_step, code, value );
  }

  @Override public void saveStepAttribute( ObjectId id_transformation,
                                           ObjectId id_step, int nr, String code, long value ) throws KettleException {
    repository.saveStepAttribute( id_transformation, id_step, nr, code, value );
  }

  @Override public void saveStepAttribute( ObjectId id_transformation,
                                           ObjectId id_step, String code, long value ) throws KettleException {
    repository.saveStepAttribute( id_transformation, id_step, code, value );
  }

  @Override public void saveStepAttribute( ObjectId id_transformation,
                                           ObjectId id_step, int nr, String code, double value )
    throws KettleException {
    repository.saveStepAttribute( id_transformation, id_step, nr, code, value );
  }

  @Override public void saveStepAttribute( ObjectId id_transformation,
                                           ObjectId id_step, String code, double value ) throws KettleException {
    repository.saveStepAttribute( id_transformation, id_step, code, value );
  }

  @Override public int countNrStepAttributes( ObjectId id_step, String code ) throws KettleException {
    return repository.countNrStepAttributes( id_step, code );
  }

  @Override public int countNrJobEntryAttributes( ObjectId id_jobentry, String code ) throws KettleException {
    return repository.countNrJobEntryAttributes( id_jobentry, code );
  }

  @Override public boolean getJobEntryAttributeBoolean( ObjectId id_jobentry, String code ) throws KettleException {
    return repository.getJobEntryAttributeBoolean( id_jobentry, code );
  }

  @Override public boolean getJobEntryAttributeBoolean( ObjectId id_jobentry, int nr,
                                                        String code ) throws KettleException {
    return repository.getJobEntryAttributeBoolean( id_jobentry, nr, code );
  }

  @Override public boolean getJobEntryAttributeBoolean( ObjectId id_jobentry, String code, boolean def )
    throws KettleException {
    return repository.getJobEntryAttributeBoolean( id_jobentry, code, def );
  }

  @Override public long getJobEntryAttributeInteger( ObjectId id_jobentry, String code ) throws KettleException {
    return repository.getJobEntryAttributeInteger( id_jobentry, code );
  }

  @Override public long getJobEntryAttributeInteger( ObjectId id_jobentry, int nr, String code )
    throws KettleException {
    return repository.getJobEntryAttributeInteger( id_jobentry, nr, code );
  }

  @Override public String getJobEntryAttributeString( ObjectId id_jobentry, String code ) throws KettleException {
    return repository.getJobEntryAttributeString( id_jobentry, code );
  }

  @Override public String getJobEntryAttributeString( ObjectId id_jobentry, int nr,
                                                      String code ) throws KettleException {
    return repository.getJobEntryAttributeString( id_jobentry, nr, code );
  }

  @Override public void saveJobEntryAttribute( ObjectId id_job,
                                               ObjectId id_jobentry, int nr, String code,
                                               String value ) throws KettleException {
    repository.saveJobEntryAttribute( id_job, id_jobentry, nr, code, value );
  }

  @Override public void saveJobEntryAttribute( ObjectId id_job,
                                               ObjectId id_jobentry, String code, String value )
    throws KettleException {
    repository.saveJobEntryAttribute( id_job, id_jobentry, code, value );
  }

  @Override public void saveJobEntryAttribute( ObjectId id_job,
                                               ObjectId id_jobentry, int nr, String code, boolean value )
    throws KettleException {
    repository.saveJobEntryAttribute( id_job, id_jobentry, nr, code, value );
  }

  @Override public void saveJobEntryAttribute( ObjectId id_job,
                                               ObjectId id_jobentry, String code, boolean value )
    throws KettleException {
    repository.saveJobEntryAttribute( id_job, id_jobentry, code, value );
  }

  @Override public void saveJobEntryAttribute( ObjectId id_job,
                                               ObjectId id_jobentry, int nr, String code, long value )
    throws KettleException {
    repository.saveJobEntryAttribute( id_job, id_jobentry, nr, code, value );
  }

  @Override public void saveJobEntryAttribute( ObjectId id_job,
                                               ObjectId id_jobentry, String code, long value ) throws KettleException {
    repository.saveJobEntryAttribute( id_job, id_jobentry, code, value );
  }

  @Override public DatabaseMeta loadDatabaseMetaFromStepAttribute(
    ObjectId id_step, String code,
    List<DatabaseMeta> databases ) throws KettleException {
    return repository.loadDatabaseMetaFromStepAttribute( id_step, code, databases );
  }

  @Override public void saveDatabaseMetaStepAttribute( ObjectId id_transformation,
                                                       ObjectId id_step, String code,
                                                       DatabaseMeta database ) throws KettleException {
    repository.saveDatabaseMetaStepAttribute( id_transformation, id_step, code, database );
  }

  @Override public DatabaseMeta loadDatabaseMetaFromJobEntryAttribute(
    ObjectId id_jobentry, String nameCode, String idCode,
    List<DatabaseMeta> databases ) throws KettleException {
    return repository.loadDatabaseMetaFromJobEntryAttribute( id_jobentry, nameCode, idCode, databases );
  }

  @Override public DatabaseMeta loadDatabaseMetaFromJobEntryAttribute(
    ObjectId id_jobentry, String nameCode, int nr, String idCode,
    List<DatabaseMeta> databases ) throws KettleException {
    return repository.loadDatabaseMetaFromJobEntryAttribute( id_jobentry, nameCode, nr, idCode, databases );
  }

  @Override public void saveDatabaseMetaJobEntryAttribute( ObjectId id_job,
                                                           ObjectId id_jobentry,
                                                           String nameCode, String idCode,
                                                           DatabaseMeta database ) throws KettleException {
    repository.saveDatabaseMetaJobEntryAttribute( id_job, id_jobentry, nameCode, idCode, database );
  }

  @Override public void saveDatabaseMetaJobEntryAttribute( ObjectId id_job,
                                                           ObjectId id_jobentry, int nr,
                                                           String nameCode, String idCode,
                                                           DatabaseMeta database ) throws KettleException {
    repository.saveDatabaseMetaJobEntryAttribute( id_job, id_jobentry, nr, nameCode, idCode, database );
  }

  @Override public void undeleteObject( RepositoryElementMetaInterface repositoryObject ) throws KettleException {
    repository.undeleteObject( repositoryObject );
  }

  @Override public List<Class<? extends IRepositoryService>> getServiceInterfaces() throws KettleException {
    return repository.getServiceInterfaces();
  }

  @Override public IRepositoryService getService(
    Class<? extends IRepositoryService> clazz ) throws KettleException {
    return repository.getService( clazz );
  }

  @Override public boolean hasService( Class<? extends IRepositoryService> clazz ) throws KettleException {
    return repository.hasService( clazz );
  }

  @Override public RepositoryObject getObjectInformation( ObjectId objectId,
                                                          RepositoryObjectType objectType ) throws KettleException {
    return repository.getObjectInformation( objectId, objectType );
  }

  @Override public String getConnectMessage() {
    return repository.getConnectMessage();
  }

  @Override public String[] getJobsUsingDatabase( ObjectId id_database ) throws KettleException {
    return repository.getJobsUsingDatabase( id_database );
  }

  @Override public String[] getTransformationsUsingDatabase( ObjectId id_database ) throws KettleException {
    return repository.getTransformationsUsingDatabase( id_database );
  }

  @Override public IRepositoryImporter getImporter() {
    return repository.getImporter();
  }

  @Override public IRepositoryExporter getExporter() throws KettleException {
    return repository.getExporter();
  }

  @Override public IMetaStore getMetaStore() {
    return repository.getMetaStore();
  }
}

package org.pentaho.di.trans.steps.googleanalytics3;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.pentaho.di.core.encryption.Encr.encryptPassword;
import static org.pentaho.di.core.encryption.Encr.decryptPassword;
import static org.pentaho.di.core.xml.XMLHandler.addTagValue;
import static org.pentaho.di.core.xml.XMLHandler.getTagValue;

/**
 * @author Andrey Khayrutdinov
 */
public class Ga3InputStepMeta extends BaseStepMeta implements StepMetaInterface {
  private static final Class<?> PKG = Ga3InputStepMeta.class;

  public static final String F_APP_NAME = "applicationName";
  public static final String F_EMAIL = "accountEmail";
  public static final String F_KEY = "key";
  public static final String F_PROFILE_ID = "profileId";

  public static final String F_START_DATE = "startDate";
  public static final String F_END_DATE = "endDate";
  public static final String F_DIMENSIONS = "dimensions";
  public static final String F_METRICS = "metrics";
  public static final String F_FILTERS = "filters";
  public static final String F_SORTERS = "sorters";
  public static final String F_SEGMENT = "segment";

  public static final String F_MAX_RESULTS = "maxResults";


  public static final String DEFAULT_APP_NAME = "Kettle's Ga3InputStep";
  public static final String DATE_FORMAT = "yyyy-MM-dd";


  private String applicationName;
  private String accountEmail;
  private String key;
  private String profileId;

  private String startDate;
  private String endDate;
  private String dimensions;
  private String metrics;
  private String filters;
  private String sorters;
  private String segment;

  private String maxResults;

  private GaApi3Facade facade;

  @Override
  public void setDefault() {
    applicationName = DEFAULT_APP_NAME;
    accountEmail = "your.service-account.email-address@developer.gserviceaccount.com ";

    DateFormat df = new SimpleDateFormat( DATE_FORMAT );
    long aWeekAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis( 7 );
    startDate = df.format( new Date( aWeekAgo ) );
    endDate = df.format( new Date() );

    metrics = "ga:sessions,ga:bounces";

    // dimensions, filters, sorters, segment are optional
  }

  @Override
  public Ga3InputStep getStep( StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
                               TransMeta transMeta, Trans trans ) {
    return new Ga3InputStep( stepMeta, stepDataInterface, copyNr, transMeta, trans );
  }

  @Override
  public Ga3InputStepData getStepData() {
    return new Ga3InputStepData();
  }


  @Override
  public String getXML() throws KettleException {
    return new StringBuilder( 1024 )
      .append( addTagValue( F_APP_NAME, applicationName ) )
      .append( addTagValue( F_EMAIL, accountEmail ) )
      .append( addTagValue( F_KEY, encryptPassword( key ) ) )
      .append( addTagValue( F_PROFILE_ID, profileId ) )
      .append( addTagValue( F_START_DATE, startDate ) )
      .append( addTagValue( F_END_DATE, endDate ) )
      .append( addTagValue( F_DIMENSIONS, dimensions ) )
      .append( addTagValue( F_METRICS, metrics ) )
      .append( addTagValue( F_FILTERS, filters ) )
      .append( addTagValue( F_SORTERS, sorters ) )
      .append( addTagValue( F_SEGMENT, segment ) )
      .append( addTagValue( F_MAX_RESULTS, maxResults ) )
      .toString();
  }

  @Override
  public void loadXML( Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore )
    throws KettleXMLException {
    try {
      applicationName = getTagValue( stepnode, F_APP_NAME );
      accountEmail = getTagValue( stepnode, F_EMAIL );
      key = decryptPassword( getTagValue( stepnode, F_KEY ) );
      profileId = getTagValue( stepnode, F_PROFILE_ID );
      startDate = getTagValue( stepnode, F_START_DATE );
      endDate = getTagValue( stepnode, F_END_DATE );
      dimensions = getTagValue( stepnode, F_DIMENSIONS );
      metrics = getTagValue( stepnode, F_METRICS );
      filters = getTagValue( stepnode, F_FILTERS );
      sorters = getTagValue( stepnode, F_SORTERS );
      segment = getTagValue( stepnode, F_SEGMENT );
      maxResults = getTagValue( stepnode, F_MAX_RESULTS );
    } catch ( Exception e ) {
      throw new KettleXMLException( BaseMessages.getString( PKG, "Ga3.Error.UnableToReadFromXML" ), e );
    }
  }

  @Override
  public void saveRep( Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step )
    throws KettleException {
    try {
      rep.saveStepAttribute( id_transformation, id_step, F_APP_NAME, applicationName );
      rep.saveStepAttribute( id_transformation, id_step, F_EMAIL, accountEmail );
      rep.saveStepAttribute( id_transformation, id_step, F_KEY, encryptPassword( key ) );
      rep.saveStepAttribute( id_transformation, id_step, F_PROFILE_ID, profileId );
      rep.saveStepAttribute( id_transformation, id_step, F_START_DATE, startDate );
      rep.saveStepAttribute( id_transformation, id_step, F_END_DATE, endDate );
      rep.saveStepAttribute( id_transformation, id_step, F_DIMENSIONS, dimensions );
      rep.saveStepAttribute( id_transformation, id_step, F_METRICS, metrics );
      rep.saveStepAttribute( id_transformation, id_step, F_FILTERS, filters );
      rep.saveStepAttribute( id_transformation, id_step, F_SORTERS, sorters );
      rep.saveStepAttribute( id_transformation, id_step, F_SEGMENT, segment );
      rep.saveStepAttribute( id_transformation, id_step, F_MAX_RESULTS, maxResults );
    } catch ( Exception e ) {
      throw new KettleException( BaseMessages.getString( PKG, "Ga3.Error.UnableToSaveToRep" ) + id_step, e );
    }
  }

  @Override
  public void readRep( Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases )
    throws KettleException {
    try {
      applicationName = rep.getStepAttributeString( id_step, F_APP_NAME );
      accountEmail = rep.getStepAttributeString( id_step, F_EMAIL );
      key = decryptPassword( rep.getStepAttributeString( id_step, F_KEY ) );
      profileId = rep.getStepAttributeString( id_step, F_PROFILE_ID );
      startDate = rep.getStepAttributeString( id_step, F_START_DATE );
      endDate = rep.getStepAttributeString( id_step, F_END_DATE );
      dimensions = rep.getStepAttributeString( id_step, F_DIMENSIONS );
      metrics = rep.getStepAttributeString( id_step, F_METRICS );
      filters = rep.getStepAttributeString( id_step, F_FILTERS );
      sorters = rep.getStepAttributeString( id_step, F_SORTERS );
      segment = rep.getStepAttributeString( id_step, F_SEGMENT );
      maxResults = rep.getStepAttributeString( id_step, F_MAX_RESULTS );
    } catch ( Exception e ) {
      throw new KettleException( BaseMessages.getString( PKG, "Ga3.Error.UnableToReadFromRep" ), e );
    }
  }


  public String getApplicationName() {
    return applicationName;
  }

  public void setApplicationName( String applicationName ) {
    this.applicationName = applicationName;
  }

  public String getAccountEmail() {
    return accountEmail;
  }

  public void setAccountEmail( String accountEmail ) {
    this.accountEmail = accountEmail;
  }

  public String getProfileId() {
    return profileId;
  }

  public void setProfileId( String profileId ) {
    this.profileId = profileId;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate( String startDate ) {
    this.startDate = startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate( String endDate ) {
    this.endDate = endDate;
  }

  public String getDimensions() {
    return dimensions;
  }

  public void setDimensions( String dimensions ) {
    this.dimensions = dimensions;
  }

  public String getMetrics() {
    return metrics;
  }

  public void setMetrics( String metrics ) {
    this.metrics = metrics;
  }

  public String getFilters() {
    return filters;
  }

  public void setFilters( String filters ) {
    this.filters = filters;
  }

  public String getSorters() {
    return sorters;
  }

  public void setSorters( String sorters ) {
    this.sorters = sorters;
  }

  public String getSegment() {
    return segment;
  }

  public void setSegment( String segment ) {
    this.segment = segment;
  }

  public String getMaxResults() {
    return maxResults;
  }

  public void setMaxResults( String maxResults ) {
    this.maxResults = maxResults;
  }


  String getKey() {
    return key;
  }

  void setKey( String key ) {
    this.key = key;
  }

  public void loadKeyFrom( InputStream is ) throws IOException {
    byte[] data;
    try {
      data = IOUtils.toByteArray( is );
    } finally {
      is.close();
    }

    key = Base64.encodeBase64String( data );
  }

  public void saveKeyTo( OutputStream os ) throws IOException {
    IOUtils.write( Base64.decodeBase64( key ), os );
  }

  public boolean isKeyLoaded() {
    return !Const.isEmpty( key );
  }


  public GaApi3Facade getOrCreateGaFacade() throws Exception {
    if ( facade == null ) {
      facade = new GaApi3Facade( applicationName, accountEmail, Base64.decodeBase64( key ) );
    }
    return facade;
  }
}

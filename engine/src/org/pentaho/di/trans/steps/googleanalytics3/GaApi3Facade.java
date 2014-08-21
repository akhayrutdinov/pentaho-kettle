package org.pentaho.di.trans.steps.googleanalytics3;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analytics.model.Account;
import com.google.api.services.analytics.model.Profile;
import com.google.api.services.analytics.model.Profiles;
import com.google.api.services.analytics.model.Segment;
import com.google.api.services.analytics.model.Webproperties;
import com.google.api.services.analytics.model.Webproperty;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author Andrey Khayrutdinov
 */
public class GaApi3Facade {
  private final Analytics analytics;

  public GaApi3Facade( String applicationName, String accountEmail, byte[] serviceKey ) throws Exception {
    this( GoogleNetHttpTransport.newTrustedTransport(), applicationName, accountEmail, serviceKey );
  }

  public GaApi3Facade( HttpTransport httpTransport, String applicationName, String accountEmail, byte[] serviceKey )
    throws Exception {
    File key = createKeyFile( serviceKey );

    Credential credential = new GoogleCredential.Builder()
      .setTransport( httpTransport )
      .setJsonFactory( JacksonFactory.getDefaultInstance() )
      .setServiceAccountScopes( Collections.singleton( AnalyticsScopes.ANALYTICS_READONLY ) )
      .setServiceAccountId( accountEmail )
      .setServiceAccountPrivateKeyFromP12File( key )
      .build();

    analytics = new Analytics.Builder( httpTransport, JacksonFactory.getDefaultInstance(), credential )
      .setApplicationName( applicationName )
      .build();

    key.delete();
  }

  private static File createKeyFile( byte[] serviceKey ) throws IOException {
    Random random = new Random();
    // create a file with tricky name
    File tmp = File.createTempFile( Integer.toHexString( random.nextInt() ),
      Integer.toHexString( random.nextInt( Short.MAX_VALUE ) ) );

    FileOutputStream os = new FileOutputStream( tmp );
    try {
      IOUtils.write( serviceKey, os );
      os.flush();
    } finally {
      os.close();
    }

    return tmp;
  }


  public Account getAccount() throws IOException {
    List<Account> accounts = analytics.management().accounts().list().execute().getItems();
    return accounts.isEmpty() ? null : accounts.get( 0 );
  }

  public List<Profile> getProfilesOf( String accountId ) throws IOException {
    Webproperties webproperties = analytics.management().webproperties().list( accountId ).execute();

    List<Profile> result = new ArrayList<Profile>();
    for ( Webproperty property : webproperties.getItems() ) {
      String propertyId = property.getId();
      Profiles profiles = analytics.management().profiles().list( accountId, propertyId ).execute();
      result.addAll( profiles.getItems() );
    }
    return result;
  }

  public List<Segment> getSegments() throws IOException {
    return analytics.management().segments().list().execute().getItems();
  }
}

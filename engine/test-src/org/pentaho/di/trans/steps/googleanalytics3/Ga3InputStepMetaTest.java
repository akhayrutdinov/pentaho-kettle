package org.pentaho.di.trans.steps.googleanalytics3;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.encryption.Encr;
import org.pentaho.di.core.encryption.TwoWayPasswordEncoderPluginType;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.MemoryRepository;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;
import org.pentaho.metastore.api.IMetaStore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Andrey Khayrutdinov
 */
public class Ga3InputStepMetaTest {

  private static final String TEST_KEY = "A Secret Key";

  @BeforeClass
  public static void beforeClass() throws KettleException {
    PluginRegistry.addPluginType( TwoWayPasswordEncoderPluginType.getInstance() );
    PluginRegistry.init();
    String passwordEncoderPluginID =
      Const.NVL( EnvUtil.getSystemProperty( Const.KETTLE_PASSWORD_ENCODER_PLUGIN ), "Kettle" );
    Encr.init( passwordEncoderPluginID );
  }

  public LoadSaveTester createTester() throws Exception {
    List<String> attributes = pickupFieldNames();

    return new LoadSaveTester(
      Ga3InputStepMeta.class,
      attributes,
      new HashMap<String, String>(  ),
      new HashMap<String, String>(  ),
      Collections.<String, FieldLoadSaveValidator<?>>emptyMap(),
      Collections.<String, FieldLoadSaveValidator<?>>emptyMap()
    );
  }

  private static List<String> pickupFieldNames() throws Exception {
    List<String> fields = new ArrayList<String>(  );
    for ( Field field : Ga3InputStepMeta.class.getDeclaredFields() ) {
      if (field.getName().startsWith( "F_" ) ) {
        String value = field.get( null ).toString();
        // ignore "key" field due to visibility restriction
        if (!Ga3InputStepMeta.F_KEY.equals( value )) {
          fields.add( value );
        }
      }
    }
    return fields;
  }


  @Test
  public void loadSaveToXml() throws Exception {
    createTester().testXmlRoundTrip();
  }

  @Test
  public void loadSaveToRepository() throws Exception {
    createTester().testRepoRoundTrip();
  }

  @Test
  public void loadSaveKeyToXml() throws Exception {
    Ga3InputStepMeta meta = new Ga3InputStepMeta();
    meta.setKey( TEST_KEY );

    final String xml = "<step>" + meta.getXML() + "</step>";
    InputStream is = new ByteArrayInputStream( xml.getBytes() );

    meta = new Ga3InputStepMeta();
    meta.loadXML(
      XMLHandler.getSubNode( XMLHandler.loadXMLFile( is, null, false, false ), "step" ),
      Collections.<DatabaseMeta>emptyList(),
      (IMetaStore) null
    );

    assertEquals( TEST_KEY, meta.getKey() );
  }

  @Test
  public void loadSaveKeyToRepository() throws Exception {
    Ga3InputStepMeta meta = new Ga3InputStepMeta();
    meta.setKey( TEST_KEY );
    Repository rep = new MemoryRepository();
    meta.saveRep( rep, null, null, null );

    meta = new Ga3InputStepMeta();
    meta.readRep( rep, (IMetaStore) null, null, null );

    assertEquals( TEST_KEY, meta.getKey() );
  }

  @Test
  public void loadSaveKeyToStream() throws Exception {
    Ga3InputStepMeta meta = new Ga3InputStepMeta();

    meta.loadKeyFrom( new ByteArrayInputStream( TEST_KEY.getBytes() ) );
    assertNotNull( meta.getKey() );

    ByteArrayOutputStream os = new ByteArrayOutputStream(  );
    meta.saveKeyTo( os );
    assertEquals( TEST_KEY, new String(os.toByteArray()) );
  }
}

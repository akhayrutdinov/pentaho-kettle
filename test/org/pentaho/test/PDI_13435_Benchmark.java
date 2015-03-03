package org.pentaho.test;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;

import java.net.URL;

/**
 * @author Andrey Khayrutdinov
 */
public class PDI_13435_Benchmark {

  public static void main( String[] args ) throws Exception {
    KettleEnvironment.init();

    URL resource = PDI_13435_Benchmark.class.getResource( "PDI-13435-main-wa.ktr" );
    String path = resource.getPath();

    TransMeta transMeta = new TransMeta( path );
    transMeta.setTransformationType( TransMeta.TransformationType.Normal );

    final int CYCLES = 100;
    long cnt = 0;
    for (int i = 0; i < CYCLES; i++) {
      cnt += runExecution( transMeta );
    }
    System.out.println("Average time: " + (cnt / CYCLES) + " ms.");
  }

  private static long runExecution( TransMeta transMeta ) throws KettleException {
    long start = System.currentTimeMillis();
    Trans trans = new Trans( transMeta );
    trans.setLogLevel( LogLevel.NOTHING );

    trans.prepareExecution( null );
    trans.startThreads();
    trans.waitUntilFinished();
    long end = System.currentTimeMillis();

    if (trans.getErrors() != 0) {
      System.err.println( "!!! errors cnt = " + trans.getErrors() );
    }

    return (end - start);
  }
}

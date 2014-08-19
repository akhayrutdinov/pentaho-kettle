package org.pentaho.di.trans.steps.googleanalytics3;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

/**
 * @author Andrey Khayrutdinov
 */
public class Ga3InputStep extends BaseStep {

  public Ga3InputStep( StepMeta stepMeta,
                       StepDataInterface stepDataInterface, int copyNr,
                       TransMeta transMeta, Trans trans ) {
    super( stepMeta, stepDataInterface, copyNr, transMeta, trans );
  }

  @Override
  public boolean processRow( StepMetaInterface smi, StepDataInterface sdi ) throws KettleException {
    Ga3InputStepMeta meta = (Ga3InputStepMeta) smi;
    Ga3InputStepData data = (Ga3InputStepData) sdi;

    if ( first ) {
      first = false;
      init( meta, data );
    }

    Object[] outputRow = RowDataUtil.allocateRowData( data.getOutputRowMeta().size() );

    return super.processRow( smi, sdi );
  }

  private void init( Ga3InputStepMeta meta, Ga3InputStepData data ) throws KettleException {
    RowMeta outputRowMeta = new RowMeta();
    data.setOutputRowMeta( outputRowMeta );
  }
}

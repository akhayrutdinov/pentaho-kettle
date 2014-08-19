package org.pentaho.di.trans.steps.googleanalytics3;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;

/**
 * @author Andrey Khayrutdinov
 */
public class Ga3InputStepData extends BaseStepData {

  private RowMetaInterface outputRowMeta;


  public RowMetaInterface getOutputRowMeta() {
    return outputRowMeta;
  }

  public void setOutputRowMeta( RowMetaInterface outputRowMeta ) {
    this.outputRowMeta = outputRowMeta;
  }
}

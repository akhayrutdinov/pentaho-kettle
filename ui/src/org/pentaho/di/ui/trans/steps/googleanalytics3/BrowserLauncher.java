package org.pentaho.di.ui.trans.steps.googleanalytics3;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.pentaho.ui.util.Launch;

/**
 * @author Andrey Khayrutdinov
 */
class BrowserLauncher implements Listener {

  private final String url;

  BrowserLauncher( String url ) {
    this.url = url;
  }

  @Override
  public void handleEvent( Event event ) {
    Launch.openURL( url );
  }
}

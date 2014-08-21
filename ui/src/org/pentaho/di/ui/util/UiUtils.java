package org.pentaho.di.ui.util;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.ui.core.widget.TextVar;

public class UiUtils {
  public static void addModifyListenerForTexts( ModifyListener listener, Text... texts ) {
    for ( Text text : texts ) {
      text.addModifyListener( listener );
    }
  }

  public static void addModifyListenerForTextVars( ModifyListener listener, TextVar... textVars ) {
    for ( TextVar textVar : textVars ) {
      textVar.addModifyListener( listener );
    }
  }

  public static void addModifyListenerForComboBoxes( ModifyListener listener, CCombo... boxes ) {
    for ( CCombo combo : boxes ) {
      combo.addModifyListener( listener );
    }
  }


  public static void addSelectionListenerForTextVars( SelectionAdapter listener, TextVar... textVars ) {
    for ( TextVar textVar : textVars ) {
      textVar.addSelectionListener( listener );
    }
  }

  public static void addSelectionListenerForTexts( SelectionAdapter listener, Text... texts ) {
    for ( Text text : texts ) {
      text.addSelectionListener( listener );
    }
  }


  public static void setTextTo( TextVar control, String text ) {
    if ( text != null ) {
      control.setText( text );
    }
  }


  public static void setEnabled( boolean enabled, Control... controls ) {
    for ( Control control : controls ) {
      control.setEnabled( enabled );
    }
  }
}

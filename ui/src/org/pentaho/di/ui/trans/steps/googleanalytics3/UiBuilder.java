package org.pentaho.di.ui.trans.steps.googleanalytics3;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.widget.TextVar;

import static org.pentaho.di.ui.trans.steps.googleanalytics3.Ga3InputStepDialog.getString;

/**
 * @author Andrey Khayrutdinov
 */
class UiBuilder {
  private final Shell shell;
  private final PropsUI props;

  final int middle;
  final int margin;

  UiBuilder( Shell shell, PropsUI props, int middle, int margin ) {
    this.shell = shell;
    this.props = props;
    this.middle = middle;
    this.margin = margin;
  }

  Group createSettingsGroup( String i18n, Control upperControl ) {
    Group group = new Group( shell, SWT.SHADOW_ETCHED_IN );
    group.setText( getString( i18n ) );
    setDefaultWidgetStyle( group );

    FormLayout layout = new FormLayout();
    layout.marginWidth = 3;
    layout.marginHeight = 3;
    group.setLayout( layout );

    FormData formData = new FormData();
    formData.top = new FormAttachment( upperControl, margin );
    formData.left = new FormAttachment( 0, 0 );
    formData.right = new FormAttachment( 100, 0 );
    group.setLayoutData( formData );

    return group;
  }

  TextVar createLabelWithTextVarRow( VariableSpace space,
                                     Composite composite, Control upperControl,
                                     String labelI18n, String tooltipI18n ) {
    Label label = new Label( composite, SWT.RIGHT );
    label.setText( getString( labelI18n ) );

    FormData labelFormData = new FormData();
    if ( upperControl == null ) {
      labelFormData.top = new FormAttachment( 0, margin );
    } else {
      labelFormData.top = new FormAttachment( upperControl, margin );
    }
    labelFormData.left = new FormAttachment( 0, 0 );
    labelFormData.right = new FormAttachment( middle, -margin );
    label.setLayoutData( labelFormData );

    TextVar textVar = new TextVar( space, composite, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    textVar.setToolTipText( getString( tooltipI18n ) );

    FormData textForData = new FormData();
    if ( upperControl == null ) {
      textForData.top = new FormAttachment( 0, margin );
    } else {
      textForData.top = new FormAttachment( upperControl, margin );
    }
    textForData.left = new FormAttachment( middle, 0 );
    textForData.right = new FormAttachment( 100, 0 );
    textVar.setLayoutData( textForData );

    setDefaultWidgetStyle( label, textVar );

    return textVar;
  }

  Pair<TextVar, Link> createLabelWithTextAndLinkRow( VariableSpace space,
                                                     Composite composite,
                                                     Control upperControl,
                                                     String labelI18n,
                                                     String tooltipI18n,
                                                     String linkI18n ) {
    Label label = new Label( composite, SWT.RIGHT );
    label.setText( getString( labelI18n ) );

    FormData labelFormData = new FormData();
    labelFormData.top = new FormAttachment( upperControl, margin );
    labelFormData.left = new FormAttachment( 0, 0 );
    labelFormData.right = new FormAttachment( middle, -margin );
    label.setLayoutData( labelFormData );

    TextVar text = new TextVar( space, composite, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    text.setToolTipText( getString( tooltipI18n ) );

    Link link = new Link( composite, SWT.SINGLE );
    link.setText( getString( linkI18n ) );
    link.pack( true );

    FormData textFormData = new FormData();
    textFormData.top = new FormAttachment( upperControl, margin );
    textFormData.left = new FormAttachment( middle, 0 );
    textFormData.right = new FormAttachment( 100, -link.getBounds().width - margin );
    text.setLayoutData( textFormData );

    FormData linkFormData = new FormData();
    linkFormData.top = new FormAttachment( upperControl, margin );
    linkFormData.left = new FormAttachment( text, 0 );
    linkFormData.right = new FormAttachment( 100, 0 );
    link.setLayoutData( linkFormData );

    setDefaultWidgetStyle( label, text, link );

    return new Pair<TextVar, Link>( text, link );
  }

  Triple<TextVar, Link, Button> createLabelWithTextAndLinkAndCheckboxRow( VariableSpace space,
                                                                          Composite composite,
                                                                          Control upperControl,
                                                                          String labelI18n,
                                                                          String tooltipI18n,
                                                                          String linkI18n ) {
    Label label = new Label( composite, SWT.RIGHT );
    label.setText( getString( labelI18n ) );

    FormData labelFormData = new FormData();
    labelFormData.top = new FormAttachment( upperControl, margin );
    labelFormData.left = new FormAttachment( 0, 0 );
    labelFormData.right = new FormAttachment( middle, -margin );
    label.setLayoutData( labelFormData );


    Button checkbox = new Button( composite, SWT.CHECK );
    checkbox.pack( true );

    FormData checkboxFormData = new FormData();
    checkboxFormData.top = new FormAttachment( upperControl, margin );
    checkboxFormData.left = new FormAttachment( middle, 0 );
    checkbox.setLayoutData( checkboxFormData );


    TextVar text = new TextVar( space, composite, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    text.setToolTipText( getString( tooltipI18n ) );

    Link link = new Link( composite, SWT.SINGLE );
    link.setText( getString( linkI18n ) );
    link.pack( true );

    FormData textFormData = new FormData();
    textFormData.top = new FormAttachment( upperControl, margin );
    textFormData.left = new FormAttachment( checkbox, margin );
    textFormData.right = new FormAttachment( 100, -link.getBounds().width - margin );
    text.setLayoutData( textFormData );

    FormData linkFormData = new FormData();
    linkFormData.top = new FormAttachment( upperControl, margin );
    linkFormData.left = new FormAttachment( text, 0 );
    linkFormData.right = new FormAttachment( 100, 0 );
    link.setLayoutData( linkFormData );

    setDefaultWidgetStyle( label, checkbox, text, link );

    return new Triple<TextVar, Link, Button>( text, link, checkbox );
  }

  Pair<CCombo, Button> createLabelWithComboAndButtonRow( Composite composite,
                                                         Control upperControl,
                                                         String labelI18n,
                                                         String tooltipI18n,
                                                         String buttonI18n,
                                                         String buttonTooltipI18n ) {
    Label label = new Label( composite, SWT.RIGHT );
    label.setText( getString( labelI18n ) );

    FormData labelFormData = new FormData();
    labelFormData.top = new FormAttachment( upperControl, margin );
    labelFormData.left = new FormAttachment( 0, 0 );
    labelFormData.right = new FormAttachment( middle, -margin );
    label.setLayoutData( labelFormData );


    CCombo combo = new CCombo( composite, SWT.LEFT | SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY );
    combo.setToolTipText( getString( tooltipI18n ) );

    Button button = new Button( composite, SWT.PUSH );
    button.setText( getString( buttonI18n ) );
    button.setToolTipText( getString( buttonTooltipI18n ) );
    button.pack( true );

    FormData comboFormData = new FormData();
    comboFormData.top = new FormAttachment( upperControl, margin );
    comboFormData.left = new FormAttachment( middle, 0 );
    comboFormData.right = new FormAttachment( 100, -button.getBounds().width - margin );
    combo.setLayoutData( comboFormData );

    FormData buttonFormData = new FormData();
    buttonFormData.top = new FormAttachment( upperControl, margin );
    buttonFormData.left = new FormAttachment( combo, 0 );
    buttonFormData.right = new FormAttachment( 100, 0 );
    button.setLayoutData( buttonFormData );

    setDefaultWidgetStyle( label, combo, button );

    return new Pair<CCombo, Button>( combo, button );
  }

  Button createButton( String i18n ) {
    Button button = new Button( shell, SWT.PUSH );
    button.setText( getString( i18n ) );
    return button;
  }


  private void setDefaultWidgetStyle( Control... controls ) {
    Ga3InputStepDialog.setDefaultWidgetStyle( props, controls );
  }


  static void addModifyListenerForTexts( ModifyListener listener, Text... texts ) {
    for ( Text text : texts ) {
      text.addModifyListener( listener );
    }
  }

  static void addModifyListenerForTextVars( ModifyListener listener, TextVar... textVars ) {
    for ( TextVar textVar : textVars ) {
      textVar.addModifyListener( listener );
    }
  }

  static void addModifyListenerForComboBoxes( ModifyListener listener, CCombo... boxes ) {
    for ( CCombo combo : boxes ) {
      combo.addModifyListener( listener );
    }
  }

  static void addSelectionListenerForTextVars( SelectionAdapter listener, TextVar... textVars ) {
    for ( TextVar textVar : textVars ) {
      textVar.addSelectionListener( listener );
    }
  }

  static void addSelectionListenerForTexts( SelectionAdapter listener, Text... texts ) {
    for ( Text text : texts ) {
      text.addSelectionListener( listener );
    }
  }

  static void setTextTo( TextVar control, String text ) {
    if ( text != null ) {
      control.setText( text );
    }
  }


  static class Pair<T1, T2> {
    final T1 first;
    final T2 second;

    Pair( T1 first, T2 second ) {
      this.first = first;
      this.second = second;
    }
  }

  static class Triple<T1, T2, T3> {
    final T1 first;
    final T2 second;
    final T3 third;

    Triple( T1 first, T2 second, T3 third ) {
      this.first = first;
      this.second = second;
      this.third = third;
    }
  }
}

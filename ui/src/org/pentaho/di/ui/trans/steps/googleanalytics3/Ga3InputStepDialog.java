package org.pentaho.di.ui.trans.steps.googleanalytics3;

import com.google.api.services.analytics.model.Account;
import com.google.api.services.analytics.model.Profile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.steps.googleanalytics3.Ga3InputStepMeta;
import org.pentaho.di.trans.steps.googleanalytics3.GaApi3Facade;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.pentaho.di.ui.trans.steps.googleanalytics3.UiBuilder.*;

/**
 * @author Andrey Khayrutdinov
 */
public class Ga3InputStepDialog extends BaseStepDialog implements StepDialogInterface, ModifyListener {

  public static final String REFERENCE_IDS_URL =
    "https://developers.google.com/analytics/devguides/reporting/core/v3/reference#ids";
  public static final String REFERENCE_DIMENSIONS_URL =
    "https://developers.google.com/analytics/devguides/reporting/core/v3/reference#dimensions";
  public static final String REFERENCE_METRICS_URL =
    "https://developers.google.com/analytics/devguides/reporting/core/v3/reference#metrics";
  public static final String REFERENCE_FILTERS_URL =
    "https://developers.google.com/analytics/devguides/reporting/core/v3/reference#filters";
  public static final String REFERENCE_SORTERS_URL =
    "https://developers.google.com/analytics/devguides/reporting/core/v3/reference#sort";


  private static final String LOADED_PROFILES_ITEM_TEMPLATE = "%s - profile: %s";

  private final Ga3InputStepMeta input;

  private final Map<String, String> profilesMapping;

  private Group connectionSettings;
  private TextVar applicationName;
  private TextVar accountEmail;
  private TextVar keyFilename;
  private Button fileChooser;
  private Label keyStatus;
  private Button useCustomProfile;
  private TextVar customProfile;
  private Link customProfileLink;
  private CCombo loadedProfiles;
  private Button loadProfilesButton;

  private Group querySettings;
  private TextVar startDate;
  private TextVar endDate;
  private TextVar dimensions;
  private Link dimensionsLink;
  private TextVar metrics;
  private Link metricsLink;
  private TextVar filters;
  private Link filtersLink;
  private TextVar sorters;
  private Link sortersLink;

  private Text maxResults;

  public Ga3InputStepDialog( Shell parent, Object in, TransMeta transMeta, String sname ) {
    super( parent, (BaseStepMeta) in, transMeta, sname );
    input = (Ga3InputStepMeta) in;
    profilesMapping = new HashMap<String, String>();
  }

  @Override
  public String open() {
    backupChanged = input.hasChanged();

    createShell( getParent() );
    createContent();
    setSize();

    installListeners();

    pickupSettingsFromMeta();

    input.setChanged( backupChanged );

    wStepname.setFocus();
    shell.setTabList( new Control[] { wStepname, connectionSettings, querySettings } );

    shell.open();

    Display display = getParent().getDisplay();
    while ( !shell.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
    return stepname;
  }

  private void createShell( Shell parent ) {
    shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX );
    setDefaultWidgetStyle( shell );
    setShellImage( shell, input );

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;

    shell.setLayout( formLayout );
    shell.setText( getString( "Ga3Dialog.Shell.Title" ) );
  }

  private void createContent() {
    int middle = props.getMiddlePct();
    int margin = Const.MARGIN;

    UiBuilder uiBuilder = new UiBuilder( shell, props, middle, margin );

    createStepNameRow( middle, margin );
    createConnectionPanel( uiBuilder );
    createQueryPanel( uiBuilder );
    createMaxResultsRow( middle, margin );
    createButtons( uiBuilder );
  }

  private void createStepNameRow( int middle, int margin ) {
    wlStepname = new Label( shell, SWT.RIGHT );
    wlStepname.setText( getString( "System.Label.StepName" ) );

    fdlStepname = new FormData();
    fdlStepname.left = new FormAttachment( 0, 0 );
    fdlStepname.right = new FormAttachment( middle, -margin );
    fdlStepname.top = new FormAttachment( 0, margin );
    wlStepname.setLayoutData( fdlStepname );

    wStepname = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    wStepname.setText( stepname );

    fdStepname = new FormData();
    fdStepname.top = new FormAttachment( 0, margin );
    fdStepname.left = new FormAttachment( middle, 0 );
    fdStepname.right = new FormAttachment( 100, 0 );
    wStepname.setLayoutData( fdStepname );

    setDefaultWidgetStyle( wlStepname, wStepname );
  }

  private void createConnectionPanel( UiBuilder uiBuilder ) {
    createConnectionSettingsGroup( uiBuilder );
    createApplicationNameRow( uiBuilder );
    createAccountEmailRow( uiBuilder );
    createAccountKeyRow( uiBuilder.middle, uiBuilder.margin );
    createAccountKeyStatusRow( uiBuilder.middle, uiBuilder.margin );
    createCustomProfileRow( uiBuilder );
    createLoadProfileRow( uiBuilder );

    connectionSettings.setTabList( new Control[] {
      applicationName, accountEmail, keyFilename, useCustomProfile, customProfile, loadedProfiles,
      loadProfilesButton } );
  }

  private void createConnectionSettingsGroup( UiBuilder uiBuilder ) {
    connectionSettings = uiBuilder.createSettingsGroup( "Ga3Dialog.ConnectGroup.Label", wStepname );
  }

  private void createApplicationNameRow( UiBuilder uiBuilder ) {
    applicationName = uiBuilder
      .createLabelWithTextVarRow( transMeta, connectionSettings, null, "Ga3Dialog.AppName.Label",
        "Ga3Dialog.AppName.Tooltip" );
  }

  private void createAccountEmailRow( UiBuilder uiBuilder ) {
    accountEmail = uiBuilder
      .createLabelWithTextVarRow( transMeta, connectionSettings, applicationName, "Ga3Dialog.Email.Label",
        "Ga3Dialog.Email.Tooltip" );
  }

  private void createAccountKeyRow( int middle, int margin ) {
    fileChooser = new Button( connectionSettings, SWT.PUSH | SWT.CENTER );
    fileChooser.setText( getString( "System.Button.Browse" ) );
    fileChooser.setToolTipText( getString( "System.Tooltip.BrowseForFileOrDirAndAdd" ) );

    FormData fdbFilename = new FormData();
    fdbFilename.top = new FormAttachment( accountEmail, margin );
    fdbFilename.right = new FormAttachment( 100, 0 );
    fileChooser.setLayoutData( fdbFilename );

    Label wlFilename = new Label( connectionSettings, SWT.RIGHT );
    wlFilename.setText( getString( "Ga3Dialog.KeyFilename.Label" ) );

    FormData fdlFilename = new FormData();
    fdlFilename.top = new FormAttachment( accountEmail, margin );
    fdlFilename.left = new FormAttachment( 0, 0 );
    fdlFilename.right = new FormAttachment( middle, -margin );
    wlFilename.setLayoutData( fdlFilename );

    keyFilename = new TextVar( transMeta, connectionSettings, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    keyFilename.setToolTipText( getString( "Ga3Dialog.KeyFilename.Tooltip" ) );

    FormData fdFilename = new FormData();
    fdFilename.top = new FormAttachment( accountEmail, margin );
    fdFilename.left = new FormAttachment( middle, 0 );
    fdFilename.right = new FormAttachment( fileChooser, -margin );
    keyFilename.setLayoutData( fdFilename );

    setDefaultWidgetStyle( fileChooser, wlFilename, keyFilename );
  }

  private void createAccountKeyStatusRow( int middle, int margin ) {
    Label label = new Label( connectionSettings, SWT.RIGHT );

    FormData labelFormData = new FormData();
    labelFormData.top = new FormAttachment( keyFilename, margin );
    labelFormData.left = new FormAttachment( 0, 0 );
    labelFormData.right = new FormAttachment( middle, -margin );
    label.setLayoutData( labelFormData );

    keyStatus = new Label( connectionSettings, SWT.SINGLE | SWT.LEFT );

    FormData textFormData = new FormData();
    textFormData.top = new FormAttachment( keyFilename, margin );
    textFormData.left = new FormAttachment( middle, 0 );
    textFormData.right = new FormAttachment( 100, 0 );
    keyStatus.setLayoutData( textFormData );

    setDefaultWidgetStyle( label, keyStatus );

    FontData fontData = keyStatus.getFont().getFontData()[ 0 ];
    Font bold = new Font( shell.getDisplay(), new FontData( fontData.getName(), fontData.getHeight(), SWT.BOLD ) );
    keyStatus.setFont( bold );
  }

  private void createCustomProfileRow( UiBuilder uiBuilder ) {
    UiBuilder.Triple<TextVar, Link, Button> triple =
      uiBuilder.createLabelWithTextAndLinkAndCheckboxRow( transMeta, connectionSettings, keyStatus,
        "Ga3Dialog.CustomProfile.Label", "Ga3Dialog.CustomProfile.Tooltip", "Ga3Dialog.Reference.Label" );

    customProfile = triple.first;
    customProfileLink = triple.second;
    useCustomProfile = triple.third;
  }

  private void createLoadProfileRow( UiBuilder uiBuilder ) {
    UiBuilder.Pair<CCombo, Button> pair =
      uiBuilder.createLabelWithComboAndButtonRow( connectionSettings, customProfile, "Ga3Dialog.Profile.Label",
        "Ga3Dialog.Profile.Tooltip", "Ga3Dialog.Profile.GetProfilesButton.Label",
        "Ga3Dialog.Profile.GetProfilesButton.Tooltip" );

    loadedProfiles = pair.first;
    loadProfilesButton = pair.second;
  }


  private void createQueryPanel( UiBuilder uiBuilder ) {
    createQuerySettingsGroup( uiBuilder );
    createStartDateRow( uiBuilder );
    createEndDateRow( uiBuilder );
    createDimensionsRow( uiBuilder );
    createMetricsRow( uiBuilder );
    createFiltersRow( uiBuilder );
    createSortersRow( uiBuilder );
    // todo

    querySettings.setTabList( new Control[] {
      startDate, endDate, dimensions, metrics, filters, sorters } );
  }

  private void createQuerySettingsGroup( UiBuilder uiBuilder ) {
    querySettings = uiBuilder.createSettingsGroup( "Ga3Dialog.QueryGroup.Label", connectionSettings );
  }

  private void createStartDateRow( UiBuilder uiBuilder ) {
    startDate = uiBuilder.createLabelWithTextVarRow( transMeta, querySettings, null, "Ga3Dialog.StartDate.Label",
      "Ga3Dialog.StartDate.Tooltip" );
  }

  private void createEndDateRow( UiBuilder uiBuilder ) {
    endDate = uiBuilder.createLabelWithTextVarRow( transMeta, querySettings, startDate, "Ga3Dialog.EndDate.Label",
      "Ga3Dialog.EndDate.Tooltip"
    );
  }

  private void createDimensionsRow( UiBuilder uiBuilder ) {
    Pair<TextVar, Link> pair = uiBuilder
      .createLabelWithTextAndLinkRow( transMeta, querySettings, endDate, "Ga3Dialog.Dimensions.Label",
        "Ga3Dialog.Dimensions.Tooltip", "Ga3Dialog.Reference.Label" );

    dimensions = pair.first;
    dimensionsLink = pair.second;
  }

  private void createMetricsRow( UiBuilder uiBuilder ) {
    Pair<TextVar, Link> pair = uiBuilder
      .createLabelWithTextAndLinkRow( transMeta, querySettings, dimensions, "Ga3Dialog.Metrics.Label",
        "Ga3Dialog.Metrics.Tooltip", "Ga3Dialog.Reference.Label" );

    metrics = pair.first;
    metricsLink = pair.second;
  }

  private void createFiltersRow( UiBuilder uiBuilder ) {
    Pair<TextVar, Link> pair = uiBuilder
      .createLabelWithTextAndLinkRow( transMeta, querySettings, metrics, "Ga3Dialog.Filters.Label",
        "Ga3Dialog.Filters.Tooltip", "Ga3Dialog.Reference.Label" );

    filters = pair.first;
    filtersLink = pair.second;
  }

  private void createSortersRow( UiBuilder uiBuilder ) {
    Pair<TextVar, Link> pair = uiBuilder
      .createLabelWithTextAndLinkRow( transMeta, querySettings, filters, "Ga3Dialog.Sorters.Label",
        "Ga3Dialog.Sorters.Tooltip", "Ga3Dialog.Reference.Label" );

    sorters = pair.first;
    sortersLink = pair.second;
  }

  private void createMaxResultsRow( int middle, int margin ) {
    Label wlMaxResults = new Label( shell, SWT.RIGHT );
    wlMaxResults.setText( getString( "Ga3Dialog.MaxResults.Label" ) );

    FormData fdLabel = new FormData();
    fdLabel.left = new FormAttachment( 0, 0 );
    fdLabel.right = new FormAttachment( middle, -margin );
    fdLabel.bottom = new FormAttachment( 100, -50 );
    wlMaxResults.setLayoutData( fdLabel );

    maxResults = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    maxResults.setToolTipText( getString( "Ga3Dialog.MaxResults.Tooltip" ) );

    FormData fdText = new FormData();
    fdText.left = new FormAttachment( middle, 0 );
    fdText.right = new FormAttachment( 100, 0 );
    fdText.bottom = new FormAttachment( 100, -50 );
    maxResults.setLayoutData( fdText );

    setDefaultWidgetStyle( wlMaxResults, maxResults );
  }

  private void createButtons( UiBuilder uiBuilder ) {
    wOK = uiBuilder.createButton( "System.Button.OK" );
    wCancel = uiBuilder.createButton( "System.Button.Cancel" );
    wGet = uiBuilder.createButton( "System.Button.GetFields" );
    wPreview = uiBuilder.createButton( "System.Button.Preview" );

    BaseStepDialog
      .positionBottomButtons( shell, new Button[] { wOK, wGet, wPreview, wCancel }, uiBuilder.margin, maxResults );
  }


  private void installListeners() {
    installModifyListeners();
    installFileChooserListener();
    installCheckboxesListeners();
    installQueryingListeners();
    installLinksListeners();
    installButtonsListeners();
    installSelectionListeners();

    shell.addShellListener( new ShellAdapter() {
      public void shellClosed( ShellEvent e ) {
        cancel();
      }
    } );
  }

  private void installModifyListeners() {
    addModifyListenerForTexts( this, wStepname, maxResults );
    addModifyListenerForTextVars( this, applicationName, accountEmail, keyFilename, customProfile, startDate, endDate,
      dimensions, metrics, sorters );
    addModifyListenerForComboBoxes( this, loadedProfiles );

    keyFilename.addModifyListener( new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent modifyEvent ) {
        updateKeyStatus();
      }
    } );
  }

  private void installFileChooserListener() {
    fileChooser.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        FileDialog dialog = new FileDialog( shell, SWT.OPEN );
        if ( keyFilename.getText() != null ) {
          String existingPath = transMeta.environmentSubstitute( keyFilename.getText() );
          dialog.setFileName( existingPath );
        }

        dialog.setFilterExtensions( new String[] { "*.p12", "*" } );
        dialog.setFilterNames( new String[] {
          getString( "Ga3Dialog.FileType.P12Files" ),
          getString( "System.FileType.AllFiles" )
        } );

        if ( dialog.open() != null ) {
          if ( getKeyStatus() == KeyStatus.LOADED ) {
            MessageBox box = new MessageBox( shell, SWT.YES | SWT.NO | SWT.ICON_WARNING );
            box.setText( getString( "Ga3Dialog.KeyFilename.Overwrite.Caption" ) );
            box.setMessage( getString( "Ga3Dialog.KeyFilename.Overwrite.Text" ) );
            if ( box.open() != SWT.YES ) {
              return;
            }
          }
          String keyPath = dialog.getFilterPath() + System.getProperty( "file.separator" ) + dialog.getFileName();
          keyFilename.setText( keyPath );
          updateKeyStatus();
        }
      }
    } );
  }

  private void installCheckboxesListeners() {
    useCustomProfile.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        input.setChanged();
        boolean selected = useCustomProfile.getSelection();
        setCustomProfileEnabled( selected );
        if ( selected ) {
          customProfile.setFocus();
        } else {
          loadedProfiles.setFocus();
        }
      }
    } );
  }

  private void installQueryingListeners() {
    loadProfilesButton.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event ev ) {
        shell.getDisplay().asyncExec( new Runnable() {
          @Override
          public void run() {
            try {
              loadProfiles();
            } catch ( Exception e ) {
              logError( "Loading profiles", e );
              throw new RuntimeException( e );
            }
          }
        } );
      }
    } );
  }

  private void installSelectionListeners() {
    lsDef = new SelectionAdapter() {
      public void widgetDefaultSelected( SelectionEvent e ) {
        ok();
      }
    };
    addSelectionListenerForTexts( lsDef, wStepname, maxResults );
    addSelectionListenerForTextVars( lsDef, applicationName, accountEmail, keyFilename, customProfile, startDate,
      endDate, dimensions, metrics, sorters );
  }

  private void installButtonsListeners() {
    lsCancel = new Listener() {
      public void handleEvent( Event e ) {
        cancel();
      }
    };
    wCancel.addListener( SWT.Selection, lsCancel );

    lsOK = new Listener() {
      public void handleEvent( Event e ) {
        ok();
      }
    };
    wOK.addListener( SWT.Selection, lsOK );
  }

  private void installLinksListeners() {
    customProfileLink.addListener( SWT.Selection, new BrowserLauncher( REFERENCE_IDS_URL ) );
    dimensionsLink.addListener( SWT.Selection, new BrowserLauncher( REFERENCE_DIMENSIONS_URL ) );
    metricsLink.addListener( SWT.Selection, new BrowserLauncher( REFERENCE_METRICS_URL ) );
    filtersLink.addListener( SWT.Selection, new BrowserLauncher( REFERENCE_FILTERS_URL ) );
    sortersLink.addListener( SWT.Selection, new BrowserLauncher( REFERENCE_SORTERS_URL ) );
  }


  public void modifyText( ModifyEvent e ) {
    input.setChanged();
  }


  private void pickupSettingsFromMeta() {
    pickupCredentialSettings();
    pickupProfileSettings();

    // todo
  }

  private void pickupCredentialSettings() {
    setTextTo( applicationName, input.getApplicationName() );
    setTextTo( accountEmail, input.getAccountEmail() );
    updateKeyStatus();
  }

  private void pickupProfileSettings() {
    if ( !Const.isEmpty( input.getProfileId() ) ) {
      customProfile.setText( input.getProfileId() );
      useCustomProfile.setSelection( true );
    } else {
      setCustomProfileEnabled( false );
      useCustomProfile.setSelection( false );
    }
  }

  private void copySettingsToMeta() {
    copyCredentialSettings();
    copyProfileSettings();
    // todo
  }

  private void copyCredentialSettings() {
    input.setApplicationName( applicationName.getText() );
    input.setAccountEmail( accountEmail.getText() );
    if ( getKeyStatus() == KeyStatus.SPECIFIED ) {
      loadNewKey( transMeta.environmentSubstitute( keyFilename.getText() ) );
    }
  }

  private void loadNewKey( String path ) {
    try {
      FileInputStream fis = new FileInputStream( path );
      try {
        input.loadKeyFrom( fis );
      } finally {
        fis.close();
      }
    } catch ( IOException e ) {
      logError( "Trying to load a secret key", e );
      throw new RuntimeException( e );
    }
  }

  private void copyProfileSettings() {
    if ( useCustomProfile.getSelection() ) {
      input.setProfileId( customProfile.getText() );
    } else {
      String item = loadedProfiles.getText();
      input.setProfileId( profilesMapping.get( item ) );
    }
  }


  private void cancel() {
    stepname = null;
    input.setChanged( backupChanged );
    dispose();
  }

  private void ok() {
    copySettingsToMeta();
    dispose();
  }

  private void setCustomProfileEnabled( boolean enabled ) {
    customProfile.setEnabled( enabled );
    customProfileLink.setEnabled( enabled );
    loadedProfiles.setEnabled( !enabled );
    loadProfilesButton.setEnabled( !enabled );
  }

  private void updateKeyStatus() {
    String text;
    Color color;
    switch( getKeyStatus() ) {
      case LOADED:
        text = getString( "Ga3Dialog.KeyStatus.Loaded" );
        color = new Color( shell.getDisplay(), 0, 255, 0 );
        break;
      case SPECIFIED:
        text = getString( "Ga3Dialog.KeyStatus.Specified" );
        color = new Color( shell.getDisplay(), 0, 0, 0 );
        break;
      case ABSENT:
        text = getString( "Ga3Dialog.KeyStatus.Absent" );
        color = new Color( shell.getDisplay(), 255, 0, 0 );
        break;
      default:
        throw new IllegalStateException();
    }
    keyStatus.setText( text );
    keyStatus.setForeground( color );
  }

  private KeyStatus getKeyStatus() {
    if ( Const.isEmpty( keyFilename.getText() ) ) {
      return input.isKeyLoaded() ? KeyStatus.LOADED : KeyStatus.ABSENT;
    } else {
      return KeyStatus.SPECIFIED;
    }
  }

  private void loadProfiles() throws Exception {
    List<Profile> profiles = loadProfilesFromFacade();
    String[] items = prepareComboBoxItems( profiles );

    loadedProfiles.setItems( items );
    if ( items.length > 0 ) {
      loadedProfiles.select( 0 );
    }
  }

  private List<Profile> loadProfilesFromFacade() throws Exception {
    copyCredentialSettings();

    GaApi3Facade facade = input.getOrCreateGaFacade();
    Account account = facade.getAccount();
    if ( account == null ) {
      throw new IllegalStateException( getString( "Ga3Dialog.Profile.GetProfilesButton.NoAccountFound" ) );
    }
    return facade.getProfilesOf( account.getId() );
  }

  private String[] prepareComboBoxItems( List<Profile> profiles ) {
    profilesMapping.clear();
    String[] items = new String[ profiles.size() ];
    int index = 0;
    for ( Profile profile : profiles ) {
      String profileId = profile.getId();
      String itemStr = String.format( LOADED_PROFILES_ITEM_TEMPLATE, profileId, profile.getName() );
      items[ index++ ] = itemStr;
      profilesMapping.put( itemStr, profileId );
    }
    return items;
  }


  static String getString( String key ) {
    return BaseMessages.getString( Ga3InputStepMeta.class, key );
  }

  static String getString( String key, Object... params ) {
    return BaseMessages.getString( Ga3InputStepMeta.class, key, params );
  }

  private void setDefaultWidgetStyle( Control... controls ) {
    setDefaultWidgetStyle( props, controls );
  }

  static void setDefaultWidgetStyle( PropsUI props, Control... controls ) {
    for ( Control control : controls ) {
      props.setLook( control );
    }
  }

  private static enum KeyStatus {
    LOADED, SPECIFIED, ABSENT
  }
}

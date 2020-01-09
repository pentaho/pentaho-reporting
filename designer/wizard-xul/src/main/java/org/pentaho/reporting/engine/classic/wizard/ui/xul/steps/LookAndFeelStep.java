/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.wizard.ui.xul.steps;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeUtil;
import org.pentaho.reporting.engine.classic.wizard.WizardProcessorUtil;
import org.pentaho.reporting.engine.classic.wizard.model.DefaultWizardSpecification;
import org.pentaho.reporting.engine.classic.wizard.model.WizardSpecification;
import org.pentaho.reporting.engine.classic.wizard.ui.xul.WizardEditorModel;
import org.pentaho.reporting.engine.classic.wizard.ui.xul.components.AbstractWizardStep;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.DocumentBundle;
import org.pentaho.reporting.libraries.docbundle.MemoryDocumentBundle;
import org.pentaho.reporting.libraries.docbundle.ODFMetaAttributeNames;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.binding.Binding;
import org.pentaho.ui.xul.binding.BindingConvertor;
import org.pentaho.ui.xul.components.XulFileDialog;
import org.pentaho.ui.xul.components.XulRadio;
import org.pentaho.ui.xul.components.XulTextbox;
import org.pentaho.ui.xul.containers.XulListbox;
import org.pentaho.ui.xul.impl.AbstractXulEventHandler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LookAndFeelStep extends AbstractWizardStep {
  private static class BooleanToIntegerBindingConverter extends BindingConvertor<Boolean, Integer> {

    @Override
    public Integer sourceToTarget( final Boolean value ) {
      return value ? 1 : 0;
    }

    @Override
    public Boolean targetToSource( final Integer value ) {
      if ( value == null ) {
        return null;
      }
      if ( 1 == value ) {
        return Boolean.TRUE;
      }
      return Boolean.FALSE;
    }
  }


  /**
   * @author wseyler
   */
  private class SelectedTemplateToImageConverter extends BindingConvertor<Integer, String> {

    private SelectedTemplateToImageConverter() {
    }/* (non-Javadoc)
     * @see org.pentaho.ui.xul.binding.BindingConvertor#sourceToTarget(java.lang.Object)
     */

    @Override
    public String sourceToTarget( final Integer value ) {
      if ( value != -1 ) {
        final File srcPath = templateHash.get( value );
        final File homeDir = computeInstallationDirectory();
        if ( homeDir == null ) {
          return null;
        }

        final File templatesDir = new File( homeDir, "templates" );
        if ( templatesDir.exists() ) {
          final String simpleName = IOUtils.getInstance().stripFileExtension( srcPath.getName() );
          final File file = new File( templatesDir, simpleName + PAINTING_EXTENSION );
          try {
            return file.getCanonicalPath();
          } catch ( IOException e ) {
            return file.getAbsolutePath();
          }
        }
      }
      return null;
    }

    /* (non-Javadoc)
     * @see org.pentaho.ui.xul.binding.BindingConvertor#targetToSource(java.lang.Object)
     */
    @Override
    public Integer targetToSource( final String value ) {
      return null;
    }

  }

  protected class SelectTemplateStepHandler extends AbstractXulEventHandler {
    private File file;

    public String getName() {
      return HANDLER_NAME;
    }

    public void selectFile() {
      try {
        final XulFileDialog fd = (XulFileDialog) document.createElement( "filedialog" ); //$NON-NLS-1$
        fd.setModalParent( LookAndFeelStep.this.getDesignTimeContext().getParentWindow() );
        fd.showOpenDialog();
        file = (File) fd.getFile();
        if ( file != null ) { // If the file is null then the user hit cancel
          final String filePath = file.getAbsolutePath();
          final XulTextbox fileTextBox = (XulTextbox) document.getElementById( WIZARD_FILENAME_TB_ID );
          fileTextBox.setValue( filePath );
          LookAndFeelStep.this.setFileName( filePath );
        }
      } catch ( XulException e ) {
        getDesignTimeContext().error( e );
      }
    }

    public File getFile() {
      return file;
    }
  }

  private static final String LOOK_AND_FEEL_STEP_OVERLAY =
    "org/pentaho/reporting/engine/classic/wizard/ui/xul/res/look_and_feel_step_Overlay.xul"; //$NON-NLS-1$

  private static final String HANDLER_NAME = "look_and_feel_handler"; //$NON-NLS-1$

  private static final String SELECTED_PROPERTY_NAME = "selected"; //$NON-NLS-1$

  private static final String SELECTED_INDEX_PROPERTY_NAME = "selectedIndex"; //$NON-NLS-1$

  private static final String START_FROM_FILE_PROPERTY_NAME = "startFromFile"; //$NON-NLS-1$

  private static final String NOT_SELECTED_PROPERTY_NAME = "!selected"; //$NON-NLS-1$

  private static final String FILENAME_PROPERTY_NAME = "fileName"; //$NON-NLS-1$

  private static final String VALUE_PROPERTY_NAME = "value"; //$NON-NLS-1$

  private static final String ELEMENTS_PROPERTY_NAME = "elements"; //$NON-NLS-1$

  private static final String SELECTED_TEMPLATE_PROPERTY_NAME = "selectedTemplate"; //$NON-NLS-1$

  private static final String TEMPLATES_PROPERTY_NAME = "templates"; //$NON-NLS-1$

  private static final String SOURCE_PROPERTY_NAME = "src"; //$NON-NLS-1$

  private static final String NEW_WIZARD_FILE_RB_ID = "new_wizard_file_rb"; //$NON-NLS-1$

  private static final String SELECT_LOOK_AND_FEEL_DECK_ID = "select_lf_deck"; //$NON-NLS-1$

  private static final String WIZARD_FILENAME_TB_ID = "wizard_filename_tb"; //$NON-NLS-1$

  private static final String AVAILABLE_TEMPLATES_LB_ID = "avail_template_lb"; //$NON-NLS-1$

  private static final String TEMPLATE_IMAGE_ID = "template_image"; //$NON-NLS-1$

  private static final String REPORT_EXTENSION = ".prpt"; //$NON-NLS-1$

  private static final String PAINTING_EXTENSION = ".png"; //$NON-NLS-1$

  private String fileName;

  private boolean startFromFile;

  private List<String> templates = new ArrayList<String>();

  private Map<Integer, File> templateHash = new HashMap<Integer, File>();

  private Integer selectedTemplate = -1;

  private boolean selectedTemplateChanged = false;

  private ResourceManager resourceManager;

  public LookAndFeelStep() {
    super();
    this.resourceManager = new ResourceManager();
    loadTemplates();
  }

  /**
   *
   */
  private void loadTemplates() {

    final File homeDir = computeInstallationDirectory();
    if ( homeDir == null ) {
      return;
    }

    final File templatesDir = new File( homeDir, "templates" );
    if ( templatesDir.exists() ) {
      final File[] reportFiles =
        templatesDir.listFiles( new FilesystemFilter( REPORT_EXTENSION, REPORT_EXTENSION, false ) );
      final List<String> templateNameList = new ArrayList<String>();
      for ( final File file : reportFiles ) {
        final String reportName = computeReportName( file );
        if ( reportName == null ) {
          continue;
        }

        templateHash.put( templateNameList.size(), file );
        templateNameList.add( reportName );
      }

      setTemplates( templateNameList );
      if ( templateNameList.size() > 0 ) {
        final boolean oldSelectedTemplateChanged = this.selectedTemplateChanged;
        if ( selectedTemplate < 0 ) {
          setSelectedTemplate( 0 ); //Select the first template by default.
        }
        this.selectedTemplateChanged = oldSelectedTemplateChanged;
      }
    }
  }

  private String computeReportName( final File reportFile ) {
    try {
      final ResourceKey resourceKey = resourceManager.createKey( reportFile );
      final String reportName = computeNameFromMetadata( resourceManager, resourceKey );
      if ( StringUtils.isEmpty( reportName ) ) {
        return computeNameFromReport( resourceManager, resourceKey );
      }
      return reportName;
    } catch ( ResourceException re ) {
      return null;
    }
  }


  private String computeNameFromMetadata( final ResourceManager resourceManager, final ResourceKey key ) {
    try {
      final Resource res = resourceManager.create( key, null, new Class[] { DocumentBundle.class } );
      final DocumentBundle rawResource = (DocumentBundle) res.getResource();
      final Object possibleTitle = rawResource.getMetaData().getBundleAttribute
        ( ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.TITLE );
      if ( possibleTitle != null ) {
        return possibleTitle.toString();
      }
      return null;
    } catch ( ResourceException re ) {
      return null;
    }
  }

  private String computeNameFromReport( final ResourceManager resourceManager, final ResourceKey key ) {
    try {
      final Resource res = resourceManager.create( key, null, new Class[] { MasterReport.class } );
      final MasterReport rawResource = (MasterReport) res.getResource();
      final Object possibleTitle = rawResource.getName();
      if ( possibleTitle != null ) {
        return possibleTitle.toString();
      }
      return null;
    } catch ( ResourceException re ) {
      return null;
    }
  }

  public static File computeInstallationDirectory() {
    final URL location = LookAndFeelStep.class.getProtectionDomain().getCodeSource().getLocation();
    DebugLog.log( "InstallationDirectory: Protection-Domain: " + location );
    if ( location == null ) {
      return null;
    }
    if ( "file".equals( location.getProtocol() ) == false ) {
      DebugLog.log( "InstallationDirectory: Protection-Domain: Protocol failure." );
      return null;
    }
    try {
      File jarPositon = new File( location.getFile() );
      if ( jarPositon.isFile() == false ) {
        final String file = URLDecoder.decode( location.getFile(),
          ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty( "file.encoding", "ISO-8859-1" ) );
        jarPositon = new File( file );
      }
      DebugLog.log( "InstallationDirectory: JAR file: " + jarPositon );
      if ( jarPositon.isFile() ) {
        // secret knowledge here: We know all jars are in the lib-directory.
        final File libDirectory = jarPositon.getCanonicalFile().getParentFile();
        if ( libDirectory == null ) {
          DebugLog.log( "InstallationDirectory: No lib directory." );
          return null;
        }
        DebugLog.log( "InstallationDirectory: Work directory: " + libDirectory.getParentFile() );
        return libDirectory.getParentFile();
      }
    } catch ( IOException ioe ) {
      // ignore, but log.
      DebugLog.log( "InstallationDirectory: Failed to decode URL: ", ioe );
    }

    // a directory, so we are running in an IDE.
    // hope for the best by using the current working directory.
    DebugLog.log( "InstallationDirectory: Work directory: Defaulting to current work directory." );
    return new File( "." );
  }

  /* (non-Javadoc)
   * @see org.pentaho.reporting.engine.classic.wizard.ui.xul.components.WizardStep#initialize()
   */
  public void setBindings() {
    // update the gui based on the selection
    getBindingFactory().setBindingType( Binding.Type.ONE_WAY );
    final Binding typeBinding = getBindingFactory()
      .createBinding( NEW_WIZARD_FILE_RB_ID, SELECTED_PROPERTY_NAME, SELECT_LOOK_AND_FEEL_DECK_ID,
        SELECTED_INDEX_PROPERTY_NAME, new BooleanToIntegerBindingConverter() );
    final Binding templatesBinding = getBindingFactory()
      .createBinding( this, TEMPLATES_PROPERTY_NAME, AVAILABLE_TEMPLATES_LB_ID, ELEMENTS_PROPERTY_NAME );
    final Binding imageBinding = getBindingFactory().createBinding( this, SELECTED_TEMPLATE_PROPERTY_NAME, TEMPLATE_IMAGE_ID, SOURCE_PROPERTY_NAME,
      new SelectedTemplateToImageConverter() );

    // update both the gui and the model
    getBindingFactory().setBindingType( Binding.Type.BI_DIRECTIONAL );
    final Binding selectedTemplateBinding = getBindingFactory()
      .createBinding( this, SELECTED_TEMPLATE_PROPERTY_NAME, AVAILABLE_TEMPLATES_LB_ID, SELECTED_INDEX_PROPERTY_NAME );
    getBindingFactory()
      .createBinding( this, START_FROM_FILE_PROPERTY_NAME, NEW_WIZARD_FILE_RB_ID, NOT_SELECTED_PROPERTY_NAME );
    getBindingFactory().createBinding( this, FILENAME_PROPERTY_NAME, WIZARD_FILENAME_TB_ID, VALUE_PROPERTY_NAME );
    try {
      typeBinding.fireSourceChanged();
      templatesBinding.fireSourceChanged();
      selectedTemplateBinding.fireSourceChanged();
      imageBinding.fireSourceChanged();
    } catch ( Exception e ) {
      if ( getDesignTimeContext() != null ) {
        getDesignTimeContext().error( e );
      }
    }
  }

  public boolean stepDeactivating() {
    super.stepDeactivating();
    final WizardEditorModel editorModel = getEditorModel();
    try {
      if ( startFromFile && fileName != null ) {
        final AbstractReportDefinition substituteReportDefinition = loadDefinitionFromFile( new File( fileName ) );
        if ( substituteReportDefinition == null ) {
          return false;
        }
        substituteReportDefinition.setName( editorModel.getReportDefinition().getName() );
        substituteReportDefinition
          .setAttribute( "http://reporting.pentaho.org/namespaces/engine/attributes/wizard", "template", fileName );
        editorModel.setReportDefinition( substituteReportDefinition, false );
        return true;
      }

      if ( editorModel.isEditing() ) {
        if ( selectedTemplateChanged ) {
          final AbstractReportDefinition report;
          if ( selectedTemplate == -1 ) {
            report = editorModel.getEmptyTemplate();
          } else {
            final File file = templateHash.get( selectedTemplate );
            if ( file != null ) {
              fileName = file.getAbsolutePath();
              report = loadDefinitionFromFile( file );
              if ( report == null ) {
                return false;
              }
            } else {
              return false;
            }
          }
          final WizardSpecification specification = editorModel.getReportSpec();
          final DataFactory dataFactory = editorModel.getReportDefinition().getDataFactory().derive();
          final String oldName = editorModel.getReportDefinition().getName();

          report
            .setAttribute( "http://reporting.pentaho.org/namespaces/engine/attributes/wizard", "template", fileName );
          report.setDataFactory( dataFactory );
          report.setName( oldName );
          WizardProcessorUtil.applyWizardSpec( report, specification );
          editorModel.setReportDefinition( report, true );
          return true;
        }
        return true;
      }

      if ( !startFromFile && selectedTemplate == -1 ) {
        final AbstractReportDefinition report = editorModel.getEmptyTemplate();
        final DefaultWizardSpecification specification = new DefaultWizardSpecification();
        WizardProcessorUtil.applyWizardSpec( report, specification );
        editorModel.setReportDefinition( report, false );
        return true;
      }
      if ( selectedTemplate != -1 ) {
        final File reportFile = templateHash.get( selectedTemplate );
        if ( reportFile != null ) {
          this.fileName = reportFile.getAbsolutePath();
        } else {
          this.fileName = null;
        }
      }

      if ( fileName == null || fileName.length() < 1 ) {
        return false;
      }

      final AbstractReportDefinition fileReportDefinition = loadDefinitionFromFile( new File( fileName ) );
      if ( fileReportDefinition == null ) {
        return false;
      }
      if ( fileReportDefinition instanceof MasterReport ) {
        DesignTimeUtil.resetTemplate( (MasterReport) fileReportDefinition );
      }
      fileReportDefinition
        .setAttribute( "http://reporting.pentaho.org/namespaces/engine/attributes/wizard", "template", fileName );
      editorModel.setReportDefinition( fileReportDefinition );
      return true;
    } catch ( Exception e ) {
      getDesignTimeContext().error( e );
      return false;
    }
  }

  private AbstractReportDefinition loadDefinitionFromFile( final File filename ) {
    try {
      final ResourceKey selectedFile = resourceManager.createKey( filename );
      final Resource directly = resourceManager.create( selectedFile, null, new Class[] { MasterReport.class } );
      final MasterReport resource = (MasterReport) directly.getResource();
      final DocumentBundle bundle = resource.getBundle();
      if ( bundle == null ) {
        // Ok, that should not happen if we work with the engine's parsers, but better safe than sorry.
        final MemoryDocumentBundle documentBundle = new MemoryDocumentBundle( resource.getContentBase() );
        documentBundle.getWriteableDocumentMetaData().setBundleType( ClassicEngineBoot.BUNDLE_TYPE );
        resource.setBundle( documentBundle );
        resource.setContentBase( documentBundle.getBundleMainKey() );
      } else {
        final MemoryDocumentBundle mem = new MemoryDocumentBundle( resource.getContentBase() );
        BundleUtilities.copyStickyInto( mem, bundle );
        BundleUtilities.copyMetaData( mem, bundle );
        resource.setBundle( mem );
        resource.setContentBase( mem.getBundleMainKey() );
      }

      return (AbstractReportDefinition) resource.derive();
    } catch ( Exception ex ) {
      getDesignTimeContext().error( ex );
      return null;
    }
  }

  public void stepActivating() {
    super.stepActivating();

    selectedTemplateChanged = false;

    if ( getEditorModel().isEditing() == false ) {
      if ( ( (XulRadio) getDocument().getElementById( NEW_WIZARD_FILE_RB_ID ) ).isSelected() &&
        ( (XulListbox) getDocument().getElementById( AVAILABLE_TEMPLATES_LB_ID ) ).getSelectedIndex() == -1 ) {
        ( (XulListbox) getDocument().getElementById( AVAILABLE_TEMPLATES_LB_ID ) ).setSelectedIndex( 0 );
      }
    }
    setValid( validateStep() );
  }

  private boolean validateStep() {
    if ( getEditorModel().isEditing() ) {
      return true;
    }

    if ( startFromFile ) {
      if ( fileName == null || fileName.length() < 1 ) {
        return false;
      }
    } else { // This is where we check for a selected template
      return true;
    }

    return true;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName( final String fileName ) {
    final String oldFileName = this.fileName;
    this.fileName = fileName;
    if ( ObjectUtilities.equal( oldFileName, fileName ) == false ) {
      setValid( validateStep() );
    }
  }

  public boolean isStartFromFile() {
    return startFromFile;
  }

  public void setStartFromFile( final boolean startFromFile ) {
    final boolean oldStartFromFile = this.startFromFile;
    this.startFromFile = startFromFile;
    if ( oldStartFromFile != startFromFile ) {
      setValid( validateStep() );
    }
  }

  /* (non-Javadoc)
   * @see org.pentaho.reporting.engine.classic.wizard.ui.xul.components.WizardStep#createPresentationComponent(org
   * .pentaho.ui.xul.XulDomContainer)
   */

  public void createPresentationComponent( final XulDomContainer mainWizardContainer ) throws XulException {
    super.createPresentationComponent( mainWizardContainer );

    // Add the overlay
    mainWizardContainer.loadOverlay( LOOK_AND_FEEL_STEP_OVERLAY );

    // Add event handlers
    mainWizardContainer.addEventHandler( new SelectTemplateStepHandler() );
  }

  /* (non-Javadoc)
   * @see org.pentaho.reporting.engine.classic.wizard.ui.xul.components.WizardStep#getStepName()
   */

  public String getStepName() {
    return messages.getString( "LOOK_AND_FEEL_STEP.Step_Name" ); //$NON-NLS-1$
  }

  public List<String> getTemplates() {
    return templates;
  }

  public void setTemplates( final List<String> templates ) {
    final List<String> oldTemplates = this.templates;
    this.templates = templates;

    this.firePropertyChange( TEMPLATES_PROPERTY_NAME, oldTemplates, this.templates );
  }

  private Integer getIndexForTemplatePath( String absolutePath ) {
    if ( absolutePath == null || absolutePath.length() < 1 ) {
      return -1;
    }
    for ( int i = 0; i < templateHash.size(); i++ ) {
      File file = templateHash.get( i );
      if ( file.getAbsolutePath().equals( absolutePath ) ) {
        return i;
      }
    }
    return -1;
  }

  public void setSelectedTemplateByPath( String path ) {
    setSelectedTemplate( getIndexForTemplatePath( path ) );
  }

  public Integer getSelectedTemplate() {
    return selectedTemplate;
  }

  public void setSelectedTemplate( final Integer selectedTemplate ) {
    final Integer oldSelectedTemplate = this.selectedTemplate;
    this.selectedTemplate = selectedTemplate;
    this.selectedTemplateChanged = true;
    this.firePropertyChange( SELECTED_TEMPLATE_PROPERTY_NAME, oldSelectedTemplate, this.selectedTemplate );
  }

}

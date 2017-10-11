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

package org.pentaho.reporting.ui.datasources.kettle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.openformula.ui.DefaultFieldDefinition;
import org.pentaho.openformula.ui.FieldDefinition;
import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeUtil;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DataFactoryEditorSupport;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaModel;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.FormulaArgument;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.FormulaParameter;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransformationProducer;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.background.CancelEvent;
import org.pentaho.reporting.libraries.designtime.swing.background.DataPreviewDialog;
import org.pentaho.reporting.libraries.designtime.swing.background.PreviewWorker;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.CommonFileChooser;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.FileChooserService;
import org.pentaho.reporting.libraries.designtime.swing.icons.IconLoader;
import org.pentaho.reporting.ui.datasources.kettle.embedded.KettleParameterInfo;
import org.pentaho.reporting.ui.datasources.kettle.parameter.FormulaParameterDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashSet;
import java.util.List;

/**
 * @author Ezequiel Cuellar
 */
public class KettleDataSourceDialog extends CommonDialog {

  private class BrowseAction extends AbstractAction implements ListSelectionListener {
    private BrowseAction() {
      putValue( Action.NAME, Messages.getString( "KettleDataSourceDialog.Browse.Name" ) );
      setEnabled( false );
    }

    public void actionPerformed( final ActionEvent e ) {
      final FileFilter[] fileFilters = new FileFilter[] { new FilesystemFilter( new String[] { ".ktr" },
        Messages.getString( "KettleDataSourceDialog.KtrFileDescription" ) + " (*.ktr)", true ) };

      final File reportContextFile = DesignTimeUtil.getContextAsFile( designTimeContext.getReport() );

      final CommonFileChooser fileChooser = FileChooserService.getInstance().getFileChooser( "kettle" );
      final String fileText = fileTextField.getText();
      if ( StringUtils.isEmpty( fileText ) == false ) {
        if ( reportContextFile != null ) {
          fileChooser.setSelectedFile( new File( reportContextFile.getParentFile(), fileTextField.getText() ) );
        } else {
          fileChooser.setSelectedFile( new File( fileTextField.getText() ) );
        }
      }
      fileChooser.setFilters( fileFilters );
      if ( fileChooser.showDialog( KettleDataSourceDialog.this, JFileChooser.OPEN_DIALOG ) == false ) {
        return;
      }

      final File file = fileChooser.getSelectedFile();
      if ( file == null ) {
        return;
      }

      final String path;
      if ( reportContextFile != null ) {
        path = IOUtils.getInstance().createRelativePath( file.getPath(), reportContextFile.getAbsolutePath() );
      } else {
        path = file.getPath();
      }
      final KettleQueryEntry queryEntry = (KettleQueryEntry) queryNameList.getSelectedValue();
      if ( queryEntry instanceof FileKettleQueryEntry ) {
        FileKettleQueryEntry fe = (FileKettleQueryEntry) queryEntry;
        fe.setFile( path );
      }
      fileTextField.setText( path );
    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged( final ListSelectionEvent e ) {
      setEnabled( queryNameList.getSelectedValue() != null );
    }
  }

  private class NameSyncHandler implements DocumentListener {
    private NameSyncHandler() {
    }

    /**
     * Gives notification that there was an insert into the document.  The range given by the DocumentEvent bounds the
     * freshly inserted region.
     *
     * @param e the document event
     */
    public void insertUpdate( final DocumentEvent e ) {
      update();
    }

    /**
     * Gives notification that a portion of the document has been removed.  The range is given in terms of what the view
     * last saw (that is, before updating sticky positions).
     *
     * @param e the document event
     */
    public void removeUpdate( final DocumentEvent e ) {
      update();
    }

    /**
     * Gives notification that an attribute or set of attributes changed.
     *
     * @param e the document event
     */
    public void changedUpdate( final DocumentEvent e ) {
      update();
    }

    private void update() {
      if ( inUpdateFromList ) {
        return;
      }

      final String queryName = nameTextField.getText();
      final KettleQueryEntry selectedQuery = (KettleQueryEntry) queryNameList.getSelectedValue();
      selectedQuery.setName( queryName );
      queryNameList.repaint();
    }
  }

  private class FileSyncHandler implements DocumentListener, Runnable {
    private boolean armed;

    private FileSyncHandler() {
    }

    /**
     * Gives notification that there was an insert into the document.  The range given by the DocumentEvent bounds the
     * freshly inserted region.
     *
     * @param e the document event
     */
    public void insertUpdate( final DocumentEvent e ) {
      update();
    }

    /**
     * Gives notification that a portion of the document has been removed.  The range is given in terms of what the view
     * last saw (that is, before updating sticky positions).
     *
     * @param e the document event
     */
    public void removeUpdate( final DocumentEvent e ) {
      update();
    }

    /**
     * Gives notification that an attribute or set of attributes changed.
     *
     * @param e the document event
     */
    public void changedUpdate( final DocumentEvent e ) {
      update();
    }

    private void update() {
      if ( armed ) {
        return;
      }
      armed = true;
      SwingUtilities.invokeLater( this );
    }

    public void run() {
      final String fileName = fileTextField.getText();
      final KettleQueryEntry selectedQuery = (KettleQueryEntry) queryNameList.getSelectedValue();
      if ( selectedQuery instanceof FileKettleQueryEntry == false ) {
        return;
      }
      FileKettleQueryEntry fe = (FileKettleQueryEntry) selectedQuery;
      fe.setFile( fileName );

      try {
        inUpdateFromList = true;
        refreshStepList( fe );
        stepsList.setEnabled( true );
        editParameterAction.setEnabled( true );
        stopOnErrorsCheckBox.setEnabled( true );
      } catch ( final ReportDataFactoryException rdfe ) {
        logger.warn( "Non-critical failure while executing the query", rdfe );
        stepsList.setEnabled( false );
        editParameterAction.setEnabled( false );
        stopOnErrorsCheckBox.setEnabled( false );
      } catch ( final Exception e1 ) {
        designTimeContext.error( e1 );
        stepsList.setEnabled( false );
        editParameterAction.setEnabled( false );
        stopOnErrorsCheckBox.setEnabled( false );
      } catch ( final Throwable t1 ) {
        designTimeContext.error( new RuntimeException( "Fatal error", t1 ) );
        stepsList.setEnabled( false );
        editParameterAction.setEnabled( false );
        stopOnErrorsCheckBox.setEnabled( false );
      } finally {
        inUpdateFromList = false;
        armed = false;
      }
    }
  }

  private void refreshStepList( final FileKettleQueryEntry fe ) throws KettleException, ReportDataFactoryException {
    DataFactoryContext dataFactoryContext = getDesignTimeContext().getDataFactoryContext();
    final List<StepMeta> data = fe.getSteps( dataFactoryContext );
    stepsList.setListData( data.toArray( new StepMeta[ data.size() ] ) );
    final String selectedStepName = fe.getSelectedStep();
    if ( selectedStepName != null ) {
      for ( final StepMeta stepMeta : data ) {
        if ( selectedStepName.equals( stepMeta.getName() ) ) {
          stepsList.setSelectedValue( stepMeta, true );
          break;
        }
      }
    }
  }

  private class StepsListListener implements ListSelectionListener {
    private StepsListListener() {
    }

    public void valueChanged( final ListSelectionEvent aEvt ) {
      final KettleQueryEntry queryEntry = (KettleQueryEntry) queryNameList.getSelectedValue();
      if ( queryEntry instanceof FileKettleQueryEntry ) {
        FileKettleQueryEntry fe = (FileKettleQueryEntry) queryEntry;
        final StepMeta selectedValue = (StepMeta) stepsList.getSelectedValue();
        if ( selectedValue != null ) {
          fe.setSelectedStep( selectedValue.getName() );
        }
      }
    }
  }

  protected class QueryNameListSelectionListener implements ListSelectionListener {
    protected QueryNameListSelectionListener() {
    }

    public void valueChanged( final ListSelectionEvent e ) {
      final KettleQueryEntry value = getSelectedQuery();
      if ( value == null ) {
        nameTextField.setEnabled( false );
        fileTextField.setEnabled( false );
        stepsList.setEnabled( false );
        editParameterAction.setEnabled( false );
        stopOnErrorsCheckBox.setEnabled( false );
        stopOnErrorsCheckBox.setSelected( false );
        handleSelection( value );
        return;
      }

      inUpdateFromList = true;
      nameTextField.setEnabled( true );
      fileTextField.setEnabled( true );

      try {
        nameTextField.setText( value.getName() );
        editParameterAction.setEnabled( true );
        stopOnErrorsCheckBox.setSelected( value.isStopOnErrors() );
        stopOnErrorsCheckBox.setEnabled( true );
        handleSelection( value );
      } finally {
        inUpdateFromList = false;
      }
    }

    protected void handleSelection( final KettleQueryEntry value ) {
      if ( value instanceof FileKettleQueryEntry ) {
        handleSelection( (FileKettleQueryEntry) value );
      }
    }

    protected void handleSelection( final FileKettleQueryEntry selectedQuery ) {
      try {
        fileTextField.setText( selectedQuery.getFile() );
        refreshStepList( selectedQuery );
        stepsList.setEnabled( true );
        editParameterAction.setEnabled( true );
        stopOnErrorsCheckBox.setEnabled( true );
      } catch ( final ReportDataFactoryException rdfe ) {
        logger.warn( "Non-critical failure while executing the query", rdfe );
        stepsList.setEnabled( false );
        editParameterAction.setEnabled( false );
        stopOnErrorsCheckBox.setEnabled( false );
      } catch ( final Exception e1 ) {
        designTimeContext.error( e1 );
        stepsList.setEnabled( false );
        editParameterAction.setEnabled( false );
        stopOnErrorsCheckBox.setEnabled( false );
      } catch ( final Throwable t1 ) {
        designTimeContext.error( new RuntimeException( "Fatal error", t1 ) );
        stepsList.setEnabled( false );
        editParameterAction.setEnabled( false );
        stopOnErrorsCheckBox.setEnabled( false );
      }
    }
  }

  private class AddQueryAction extends AbstractAction {
    public AddQueryAction() {
      putValue( Action.SMALL_ICON, IconLoader.getInstance().getAddIcon() );
      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "KettleDataSourceDialog.AddQuery.Description" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      String queryName = findNextName();

      try {
        final KettleQueryEntry newQuery = createNewQueryEntry( queryName );
        queryListModel.addElement( newQuery );
        queryNameList.setSelectedValue( newQuery, true );
      } catch ( final KettleException e1 ) {
        getDesignTimeContext().error( e1 );
      }

    }
  }

  private class RemoveQueryAction extends AbstractAction implements ListSelectionListener {
    public RemoveQueryAction() {
      putValue( Action.SMALL_ICON, IconLoader.getInstance().getDeleteIcon() );
      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "KettleDataSourceDialog.RemoveQuery.Description" ) );
      setEnabled( false );
    }

    public void actionPerformed( final ActionEvent e ) {
      final Object selectedValue = queryNameList.getSelectedValue();
      if ( selectedValue == null ) {
        return;
      }
      inUpdateFromList = true;
      try {
        clearComponents();
        queryListModel.removeElement( selectedValue );
      } finally {
        inUpdateFromList = false;
      }
    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged( final ListSelectionEvent e ) {
      setEnabled( queryNameList.getSelectedValue() != null );
    }
  }

  private class PreviewAction extends AbstractAction implements ListSelectionListener {
    private PreviewAction() {
      putValue( Action.NAME, Messages.getString( "KettleDataSourceDialog.Preview.Name" ) );
      setEnabled( false );
    }

    public void actionPerformed( final ActionEvent e ) {
      try {
        final KettleQueryEntry kettleQueryEntry = (KettleQueryEntry) queryNameList.getSelectedValue();
        final KettleTransformationProducer fileProducer = kettleQueryEntry.createProducer();
        final KettleDataFactory dataFactory = new KettleDataFactory();
        dataFactory.setQuery( kettleQueryEntry.getName(), fileProducer );

        DataFactoryEditorSupport.configureDataFactoryForPreview( dataFactory, designTimeContext );

        final DataPreviewDialog previewDialog = new DataPreviewDialog( KettleDataSourceDialog.this );

        final KettlePreviewWorker worker = new KettlePreviewWorker( dataFactory, kettleQueryEntry.getName() );
        previewDialog.showData( worker );

        final ReportDataFactoryException factoryException = worker.getException();
        if ( factoryException != null ) {
          ExceptionDialog.showExceptionDialog( KettleDataSourceDialog.this,
            Messages.getString( "KettleDataSourceDialog.PreviewError.Title" ),
            Messages.getString( "KettleDataSourceDialog.PreviewError.Message" ), factoryException );
        }
      } catch ( final Exception ex ) {
        ExceptionDialog.showExceptionDialog( KettleDataSourceDialog.this,
          Messages.getString( "KettleDataSourceDialog.PreviewError.Title" ),
          Messages.getString( "KettleDataSourceDialog.PreviewError.Message" ), ex );
      }
    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged( final ListSelectionEvent e ) {
      setEnabled( stepsList.getSelectedValue() != null );
    }
  }

  private static class KettlePreviewWorker implements PreviewWorker {
    private KettleDataFactory dataFactory;
    private TableModel resultTableModel;
    private ReportDataFactoryException exception;
    private String query;

    private KettlePreviewWorker( final KettleDataFactory dataFactory,
                                 final String query ) {
      if ( dataFactory == null ) {
        throw new NullPointerException();
      }
      this.query = query;
      this.dataFactory = dataFactory;
    }

    public ReportDataFactoryException getException() {
      return exception;
    }

    public TableModel getResultTableModel() {
      return resultTableModel;
    }

    public void close() {
    }

    /**
     * Requests that the thread stop processing as soon as possible.
     */
    public void cancelProcessing( final CancelEvent event ) {
      dataFactory.cancelRunningQuery();
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread
     * causes the object's <code>run</code> method to be called in that separately executing thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {
      try {
        resultTableModel = dataFactory.queryData( query, new ReportParameterValues() );
      } catch ( final ReportDataFactoryException e ) {
        exception = e;
      } finally {
        dataFactory.close();
      }
    }
  }

  private class EditParameterAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private EditParameterAction() {
      putValue( Action.NAME, Messages.getString( "KettleDataSourceDialog.EditParameter.Name" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final KettleQueryEntry queryEntry = (KettleQueryEntry) queryNameList.getSelectedValue();
      if ( queryEntry == null ) {
        return;
      }

      try {
        final FormulaParameterDialog dialog =
          new FormulaParameterDialog( KettleDataSourceDialog.this, designTimeContext );
        FieldDefinition[] fields = createFieldDefinitions();
        FormulaParameter[] parameters = queryEntry.getParameters();
        FormulaArgument[] arguments = queryEntry.getArguments();
        KettleParameterInfo[] declaredParameters =
          queryEntry.getDeclaredParameters( getDesignTimeContext().getDataFactoryContext() );
        final FormulaParameterDialog.EditResult editResult = dialog.performEdit
          ( arguments, parameters, fields, declaredParameters );
        if ( editResult == null ) {
          return;
        }

        queryEntry.setArguments( editResult.getArgumentNames() );
        queryEntry.setParameters( editResult.getParameterMappings() );
      } catch ( final Exception e1 ) {
        designTimeContext.error( e1 );
      } catch ( final Throwable t1 ) {
        designTimeContext.error( new RuntimeException( "Fatal error", t1 ) );
      }
    }

    private FieldDefinition[] createFieldDefinitions() {
      DataSchemaModel dataSchemaModel = designTimeContext.getDataSchemaModel();
      String[] reportFields = dataSchemaModel.getColumnNames();
      FieldDefinition[] fields = new FieldDefinition[ reportFields.length ];
      for ( int i = 0; i < reportFields.length; i++ ) {
        String reportField = reportFields[ i ];
        fields[ i ] = new DefaultFieldDefinition( reportField );
      }
      return fields;
    }
  }

  private class StopOnErrorSync implements ActionListener {
    public void actionPerformed( final ActionEvent e ) {
      KettleQueryEntry selectedQuery = getSelectedQuery();
      if ( selectedQuery != null ) {
        selectedQuery.setStopOnErrors( stopOnErrorsCheckBox.isSelected() );
      }
    }
  }

  private static final Log logger = LogFactory.getLog( KettleDataSourceDialog.class );

  private DesignTimeContext designTimeContext;
  private JTextField fileTextField;
  private JTextField nameTextField;
  private JList stepsList;
  private JList queryNameList;
  private DefaultListModel queryListModel;
  private boolean inUpdateFromList;
  private Action editParameterAction;
  private PreviewAction previewAction;
  private JCheckBox stopOnErrorsCheckBox;

  public KettleDataSourceDialog( final DesignTimeContext designTimeContext, final JDialog parent ) {
    super( parent );
    initDialog( designTimeContext );
  }

  public KettleDataSourceDialog( final DesignTimeContext designTimeContext, final JFrame parent ) {
    super( parent );
    initDialog( designTimeContext );

  }

  public KettleDataSourceDialog( final DesignTimeContext designTimeContext ) {
    initDialog( designTimeContext );
  }

  private void initDialog( final DesignTimeContext designTimeContext ) {
    if ( designTimeContext == null ) {
      throw new NullPointerException();
    }

    this.designTimeContext = designTimeContext;

    stopOnErrorsCheckBox = new JCheckBox( Messages.getString( "KettleDataSourceDialog.StopOnErrors" ) );
    stopOnErrorsCheckBox.setEnabled( false );
    stopOnErrorsCheckBox.addActionListener( new StopOnErrorSync() );

    editParameterAction = new EditParameterAction();
    editParameterAction.setEnabled( false );

    queryListModel = new DefaultListModel();

    queryNameList = new JList( queryListModel );
    queryNameList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    queryNameList.setVisibleRowCount( 5 );
    queryNameList.addListSelectionListener( getQueryNameListener() );

    previewAction = new PreviewAction();

    fileTextField = new JTextField( 30 );
    fileTextField.setEnabled( false );
    fileTextField.getDocument().addDocumentListener( new FileSyncHandler() );

    stepsList = new JList();
    stepsList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    stepsList.addListSelectionListener( new StepsListListener() );
    stepsList.addListSelectionListener( previewAction );

    nameTextField = new JTextField( 30 );
    nameTextField.setEnabled( false );
    nameTextField.getDocument().addDocumentListener( new NameSyncHandler() );

    setTitle( getDialogTitle() );
    setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
    setModal( true );

    super.init();
  }

  protected JCheckBox getStopOnErrorsCheckBox() {
    return stopOnErrorsCheckBox;
  }

  protected Action getPreviewAction() {
    return previewAction;
  }

  protected Action getEditParameterAction() {
    return editParameterAction;
  }

  protected DesignTimeContext getDesignTimeContext() {
    return designTimeContext;
  }

  protected KettleQueryEntry getSelectedQuery() {
    return (KettleQueryEntry) queryNameList.getSelectedValue();
  }

  protected void updateQueryName( final String name ) {
    nameTextField.setText( name );
  }

  protected String getDialogTitle() {
    return Messages.getString( "KettleDataSourceDialog.Title" );
  }

  protected String getDialogId() {
    return "KettleDataSourceDialog";
  }

  protected Component createContentPane() {
    final JPanel previewAndParameterPanel = createTransformParameterPanel();
    final JPanel queryListPanel = createQueryListPanel();

    final JPanel mainPanel = new JPanel( new GridBagLayout() );
    mainPanel.setBorder( new EmptyBorder( 5, 5, 0, 5 ) );
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    mainPanel.add( new JLabel( Messages.getString( "KettleDataSourceDialog.QueryName" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 1;
    mainPanel.add( nameTextField, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 3;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.anchor = GridBagConstraints.WEST;
    mainPanel.add( createDatasourcePanel(), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.gridwidth = 3;
    gbc.anchor = GridBagConstraints.WEST;
    mainPanel.add( previewAndParameterPanel, gbc );

    final JSplitPane panel = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
    panel.setLeftComponent( queryListPanel );
    panel.setRightComponent( mainPanel );
    panel.setDividerLocation( 250 );
    return panel;
  }

  private JPanel createQueryListPanel() {
    final RemoveQueryAction removeQueryAction = new RemoveQueryAction();
    queryNameList.addListSelectionListener( removeQueryAction );

    final JPanel queryListButtonsPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
    queryListButtonsPanel.add( new BorderlessButton( new AddQueryAction() ) );
    queryListButtonsPanel.add( new BorderlessButton( removeQueryAction ) );

    final JPanel queryListPanel = new JPanel( new BorderLayout() );
    queryListPanel.setBorder( BorderFactory.createEmptyBorder( 0, 5, 5, 0 ) );
    queryListPanel.add( new JScrollPane( queryNameList ), BorderLayout.CENTER );
    queryListPanel.add( queryListButtonsPanel, BorderLayout.NORTH );
    return queryListPanel;
  }

  private JPanel createTransformParameterPanel() {
    final JPanel stopOnErrorsPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 5 ) );
    stopOnErrorsPanel.add( stopOnErrorsCheckBox );

    final JPanel previewAndParameterPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 5 ) );
    previewAndParameterPanel.add( new JButton( editParameterAction ) );
    previewAndParameterPanel.add( new JButton( previewAction ) );

    final JPanel transParameterPanel = new JPanel( new BorderLayout() );
    transParameterPanel.add( stopOnErrorsPanel, BorderLayout.NORTH );
    transParameterPanel.add( previewAndParameterPanel, BorderLayout.CENTER );
    return transParameterPanel;
  }

  protected JPanel createDatasourcePanel() {
    JPanel panel = new JPanel( new GridBagLayout() );

    final BrowseAction browseAction = new BrowseAction();
    queryNameList.addListSelectionListener( browseAction );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 0, 0, 0, 13 );
    panel.add( new JLabel( Messages.getString( "KettleDataSourceDialog.FileName" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    panel.add( fileTextField, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    panel.add( new JButton( browseAction ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 0, 0, 5, 0 );
    panel.add( new JLabel( Messages.getString( "KettleDataSourceDialog.Steps" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 3;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.anchor = GridBagConstraints.WEST;
    panel.add( new JScrollPane( stepsList ), gbc );

    return panel;

  }

  public KettleDataFactory performConfiguration( final DesignTimeContext context,
                                                 final KettleDataFactory dataFactory,
                                                 final String queryName ) throws KettleException {
    configureFromDataFactory( dataFactory, queryName );
    if ( performEdit() == false ) {
      return null;
    }

    final KettleDataFactory kettleDataFactory = new KettleDataFactory();
    for ( final KettleQueryEntry queryEntry : getQueryEntries() ) {
      final KettleTransformationProducer producer = queryEntry.createProducer();

      kettleDataFactory.setQuery( queryEntry.getName(), producer );
    }

    return kettleDataFactory;
  }

  protected KettleQueryEntry[] getQueryEntries() {
    final KettleQueryEntry[] data = new KettleQueryEntry[ queryListModel.size() ];
    queryListModel.copyInto( data );
    return data;
  }

  protected void configureFromDataFactory( final KettleDataFactory dataFactory, final String selectedQueryName )
    throws KettleException {
    queryListModel.clear();
    if ( dataFactory == null ) {
      return;
    }

    KettleQueryEntry selectedDataSet = null;

    final String[] queryNames = dataFactory.getQueryNames();
    for ( int i = 0; i < queryNames.length; i++ ) {
      final String queryName = queryNames[ i ];
      final KettleTransformationProducer producer = dataFactory.getQuery( queryName );

      final KettleQueryEntry dataSet = createQueryEntry( queryName, producer );
      queryListModel.addElement( dataSet );
      if ( ObjectUtilities.equal( selectedQueryName, queryName ) ) {
        selectedDataSet = dataSet;
      }
    }

    queryNameList.setSelectedValue( selectedDataSet, true );
  }

  protected boolean validateInputs( final boolean onConfirm ) {
    getConfirmAction().setEnabled( queryNameList.getModel().getSize() > 0 );
    if ( queryNameList.getModel().getSize() == 0 ) {
      return false;
    }

    return true;
  }

  protected KettleQueryEntry createNewQueryEntry( final String queryName ) throws KettleException {
    return new FileKettleQueryEntry( queryName );
  }

  protected KettleQueryEntry createQueryEntry( final String queryName,
                                               final KettleTransformationProducer producer )
    throws KettleException {
    return new FileKettleQueryEntry( queryName, producer );
  }

  protected ListSelectionListener getQueryNameListener() {
    return new QueryNameListSelectionListener();
  }

  protected void clearComponents() {
    nameTextField.setText( "" );
    fileTextField.setText( "" );
    stepsList.setListData( new StepMeta[ 0 ] );
  }

  protected String findNextName() {
    final HashSet<String> names = new HashSet<String>();
    for ( int i = 0; i < queryListModel.getSize(); i++ ) {
      final KettleQueryEntry o = (KettleQueryEntry) queryListModel.getElementAt( i );
      names.add( o.getName() );
    }

    String queryName = Messages.getString( "KettleDataSourceDialog.Query" );
    for ( int i = 1; i < 1000; ++i ) {
      final String newQuery = Messages.getString( "KettleDataSourceDialog.Query" ) + " " + i;
      if ( names.contains( newQuery ) == false ) {
        queryName = newQuery;
        break;
      }
    }
    return queryName;
  }

}

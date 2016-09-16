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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.designer.core.editor.parameters;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.settings.ui.ValidationMessage;
import org.pentaho.reporting.designer.core.settings.ui.ValidationResult;
import org.pentaho.reporting.designer.core.util.FormulaEditorDataModel;
import org.pentaho.reporting.designer.core.util.FormulaEditorPanel;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.DefaultReportEnvironment;
import org.pentaho.reporting.engine.classic.core.DefaultResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportEnvironment;
import org.pentaho.reporting.engine.classic.core.ReportEnvironmentDataRow;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.cache.CachingDataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DesignTimeDataFactoryContext;
import org.pentaho.reporting.engine.classic.core.parameters.AbstractParameter;
import org.pentaho.reporting.engine.classic.core.parameters.CompoundDataRow;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultListParameter;
import org.pentaho.reporting.engine.classic.core.parameters.ListParameter;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterAttributeNames;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.PlainParameter;
import org.pentaho.reporting.engine.classic.core.parameters.StaticListParameter;
import org.pentaho.reporting.engine.classic.core.states.NoOpPerformanceMonitorContext;
import org.pentaho.reporting.engine.classic.core.states.PerformanceMonitorContext;
import org.pentaho.reporting.engine.classic.core.states.QueryDataRowWrapper;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.KeyedComboBoxModel;
import org.pentaho.reporting.libraries.designtime.swing.event.DocumentChangeHandler;
import org.pentaho.reporting.libraries.docbundle.DocumentMetaData;
import org.pentaho.reporting.libraries.docbundle.MemoryDocumentMetaData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.TimeZone;

/*
 * @author Ezequiel Cuellar
 */

public class ParameterDialog extends CommonDialog implements FormulaEditorDataModel {
  private static final ValidationMessage.Severity[] ALL_SEVERITIES = new ValidationMessage.Severity[] {
    ValidationMessage.Severity.WARN, ValidationMessage.Severity.ERROR };
  private static final Class[] DEFAULT_CLASSES = { String.class,
    Boolean.class,
    Number.class,
    Date.class,
    java.sql.Date.class,
    Time.class,
    Timestamp.class,
    Double.class,
    Float.class,
    Integer.class,
    Long.class,
    Short.class,
    Byte.class,
    BigInteger.class,
    BigDecimal.class,
    TableModel.class,
    Object.class
  };
  private ReportDesignerContext reportDesignerContext;
  private ProvisionDataSourcePanel provisionDataSourcePanel;
  JTextField nameTextField;
  private JTextField labelTextField;
  JTextField resourceTextField;
  JTextField errorMesageTextField;
  private DefaultValueEditorPanel defaultValueTextField;
  private JTextField dataFormatField;
  private DataFactoryTreeModel availableDataSourcesModel;
  JComboBox idComboBox;
  private JComboBox displayValueComboBox;
  private JComboBox valueTypeComboBox;
  private JCheckBox mandatoryCheckBox;
  private JCheckBox hiddenCheckBox;
  JCheckBox strictValuesCheckBox;
  private JCheckBox autofillSelectionCheckBox;
  private JCheckBox reevaluateOnInvalidStrictParamCheckBox;
  JCheckBox translateLabel;
  JCheckBox translateDataFormat;
  JCheckBox translateValues;
  JCheckBox translateErrorMesage;
  private JLabel displayFormulaLabel;
  private FormulaEditorPanel postProcessingFormulaField;
  private FormulaEditorPanel displayFormulaField;
  private FormulaEditorPanel defaultValueFormulaField;
  private JSpinner visibleItemsTextField;
  private JLabel visibleItemsLabel;
  private JComboBox queryBox;
  private ComboBoxModel parameterTypeModel;
  private JTree availableDataSources;
  // def visibility for testing purposes
  StaticTextComboBoxModel queryComboBoxModel;
  private ParameterContext parameterContext;
  private JLabel displayValueLabel;
  private JComboBox timeZoneBox;
  private KeyedComboBoxModel<String, String> timeZoneModel;
  private JLabel timeZoneLabel;
  private String parameter;

  public ParameterDialog( final ReportDesignerContext context ) {
    this.reportDesignerContext = context;
    init();
  }

  public ParameterDialog( final Dialog aParent, final ReportDesignerContext context ) {
    super( aParent );
    this.reportDesignerContext = context;
    init();
  }

  public ParameterDialog( final Frame aParent, final ReportDesignerContext context ) {
    super( aParent );
    this.reportDesignerContext = context;
    init();
  }

  protected void init() {
    provisionDataSourcePanel = new ProvisionDataSourcePanel();
    provisionDataSourcePanel.setReportDesignerContext( reportDesignerContext );

    parameterContext = new EditorParameterContext();
    setModal( true );

    availableDataSourcesModel = provisionDataSourcePanel.getDataFactoryTreeModel();
    availableDataSourcesModel.addTreeModelListener( new DataSetQueryUpdateHandler() );
    provisionDataSourcePanel.getDataSourcesTree()
      .addTreeSelectionListener( new QuerySelectionHandler() );

    availableDataSources = provisionDataSourcePanel.getDataSourcesTree();

    defaultValueTextField = new DefaultValueEditorPanel();

    dataFormatField = new JTextField();
    final TypeSelectionHandler typeSelectionHandler = new TypeSelectionHandler( defaultValueTextField );
    dataFormatField.getDocument().addDocumentListener( typeSelectionHandler );
    nameTextField = new JTextField();
    labelTextField = new JTextField();
    resourceTextField = new JTextField();
    errorMesageTextField = new JTextField();

    mandatoryCheckBox = new JCheckBox( Messages.getString( "ParameterDialog.Mandatory" ) );
    mandatoryCheckBox.setBorder( BorderFactory.createEmptyBorder( 3, 0, 0, 0 ) );

    hiddenCheckBox = new JCheckBox( Messages.getString( "ParameterDialog.Hidden" ) );
    hiddenCheckBox.setBorder( BorderFactory.createEmptyBorder( 3, 0, 0, 0 ) );

    translateLabel = new JCheckBox( Messages.getString( "ParameterDialog.TranslateLabel" ) );
    translateLabel.setBorder( BorderFactory.createEmptyBorder( 3, 0, 0, 0 ) );

    translateDataFormat = new JCheckBox( Messages.getString( "ParameterDialog.TranslateDataFormat" ) );
    translateDataFormat.setBorder( BorderFactory.createEmptyBorder( 3, 0, 0, 0 ) );

    translateValues = new JCheckBox( Messages.getString( "ParameterDialog.TranslateValues" ) );
    translateValues.setBorder( BorderFactory.createEmptyBorder( 3, 0, 0, 0 ) );

    translateErrorMesage = new JCheckBox( Messages.getString( "ParameterDialog.TranslateErrorMessage" ) );
    translateErrorMesage.setBorder( BorderFactory.createEmptyBorder( 3, 0, 0, 0 ) );

    strictValuesCheckBox = new JCheckBox( Messages.getString( "ParameterDialog.StrictValues" ) );
    strictValuesCheckBox.setBorder( BorderFactory.createEmptyBorder( 3, 0, 0, 0 ) );

    reevaluateOnInvalidStrictParamCheckBox =
      new JCheckBox( Messages.getString( "ParameterDialog.ReevaluateOnInvalidStrictParam" ) );
    reevaluateOnInvalidStrictParamCheckBox.setBorder( BorderFactory.createEmptyBorder( 3, 0, 0, 0 ) );

    autofillSelectionCheckBox = new JCheckBox( Messages.getString( "ParameterDialog.AutofillSelection" ) );
    autofillSelectionCheckBox.setBorder( BorderFactory.createEmptyBorder( 3, 0, 0, 0 ) );

    parameterTypeModel = ParameterType.createParameterTypesModel();
    parameterTypeModel.addListDataListener( new TypeListener() );
    parameterTypeModel.addListDataListener( typeSelectionHandler );

    timeZoneModel = new KeyedComboBoxModel<>();
    timeZoneModel.setAllowOtherValue( true );
    timeZoneModel.add( "utc", Messages.getString( "ParameterDialog.UseUniversalTime" ) );
    timeZoneModel.add( "server", Messages.getString( "ParameterDialog.UseServerTimezone" ) );
    timeZoneModel.add( "client", Messages.getString( "ParameterDialog.UseClientTimezone" ) );
    final String[] timeZoneId = TimeZone.getAvailableIDs();
    Arrays.sort( timeZoneId );
    for ( int i = 0; i < timeZoneId.length; i++ ) {
      final String string = timeZoneId[ i ];
      final TimeZone timeZone = TimeZone.getTimeZone( string );
      timeZoneModel.add( timeZone.getID(), Messages.getString( "ParameterDialog.TimeZoneLabel",
        timeZone.getID(), timeZone.getDisplayName() ) );
    }
    timeZoneModel.addListDataListener( typeSelectionHandler );

    timeZoneBox = new JComboBox( timeZoneModel );
    timeZoneLabel = new JLabel( Messages.getString( "ParameterDialog.TimeZone" ) );

    queryComboBoxModel = new StaticTextComboBoxModel();
    queryComboBoxModel.addListDataListener( new QuerySelectionHandler() );

    queryBox = new JComboBox( queryComboBoxModel );

    idComboBox = new JComboBox();
    idComboBox.setEditable( true );

    displayValueLabel = new JLabel( Messages.getString( "ParameterDialog.DisplayName" ) );

    displayValueComboBox = new JComboBox();
    displayValueComboBox.setEditable( true );

    valueTypeComboBox = new JComboBox( new DefaultComboBoxModel( DEFAULT_CLASSES ) );
    valueTypeComboBox.setEditable( true );
    valueTypeComboBox.setEditor( new ClassComboBoxEditor( true, DEFAULT_CLASSES ) );
    valueTypeComboBox.setRenderer( new ClassListCellRenderer() );
    valueTypeComboBox.getModel().addListDataListener( typeSelectionHandler );

    visibleItemsLabel = new JLabel( Messages.getString( "ParameterDialog.VisibleItems" ) );

    visibleItemsTextField = new JSpinner( new SpinnerNumberModel( 0, 0, 50000, 1 ) );

    displayFormulaLabel = new JLabel( Messages.getString( "ParameterDialog.DisplayFormula" ) );

    displayFormulaField = new FormulaEditorPanel();
    displayFormulaField.setEditorDataModel( this );
    displayFormulaField.setLimitFields( true );

    postProcessingFormulaField = new FormulaEditorPanel();
    postProcessingFormulaField.setEditorDataModel( this );
    postProcessingFormulaField.setLimitFields( true );

    defaultValueFormulaField = new FormulaEditorPanel();
    defaultValueFormulaField.setEditorDataModel( this );
    defaultValueFormulaField.setLimitFields( true );


    super.init();

    timeZoneBox.setVisible( false );
    timeZoneLabel.setVisible( false );
    displayFormulaField.setVisible( false );
    visibleItemsLabel.setVisible( false );
    visibleItemsTextField.setVisible( false );
    displayFormulaLabel.setVisible( false );
    displayValueLabel.setVisible( false );
    displayValueComboBox.setVisible( false );
    strictValuesCheckBox.setVisible( false );
    reevaluateOnInvalidStrictParamCheckBox.setVisible( false );
    autofillSelectionCheckBox.setVisible( false );

    translateLabel.setEnabled( false );
    translateDataFormat.setEnabled( false );
    translateValues.setEnabled( false );
    translateErrorMesage.setEnabled( false );
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.Parameter"; // NON-NLS
  }

  @SuppressWarnings( "HardCodedStringLiteral" )
  private TimeZone getSelectedTimeZone() {
    final Object selectedItem = timeZoneModel.getSelectedKey();
    if ( selectedItem == null ) {
      return TimeZone.getDefault();
    }
    if ( "server".equals( selectedItem ) || "client".equals( selectedItem ) ) {
      return TimeZone.getDefault();
    }

    final String id = String.valueOf( selectedItem );
    if ( "utc".equals( id ) ) {
      return TimeZone.getTimeZone( "UTC" );
    }
    final TimeZone timeZone = TimeZone.getTimeZone( id );
    if ( "GMT".equals( timeZone.getID() ) && "GMT".equals( id ) == false ) {
      // Handle timezones that are not understood by the current JVM.
      return TimeZone.getDefault();
    }
    return timeZone;
  }

  protected Component createContentPane() {
    final JSplitPane mainPanel = new JSplitPane();
    mainPanel.setOrientation( JSplitPane.HORIZONTAL_SPLIT );
    mainPanel.setLeftComponent( provisionDataSourcePanel );
    mainPanel.setRightComponent( createDetailsPanel() );
    mainPanel.setDividerLocation( 300 );
    return mainPanel;
  }

  private JPanel createDetailsPanel() {
    queryBox.setEditable( true );

    final JPanel detailsPanel = new JPanel( new GridBagLayout() );
    detailsPanel.setBorder( BorderFactory.createEmptyBorder( 30, 5, 10, 10 ) );

    final GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets( 5, 5, 0, 0 );
    gbc.anchor = GridBagConstraints.LINE_START;
    gbc.gridy = 0;
    gbc.gridx = 0;
    detailsPanel.add( new JLabel( Messages.getString( "ParameterDialog.Name" ) ), gbc );

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    gbc.gridx = 1;
    detailsPanel.add( nameTextField, gbc );

    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0.0;
    gbc.gridy = 1;
    gbc.gridx = 0;
    detailsPanel.add( new JLabel( Messages.getString( "ParameterDialog.Label" ) ), gbc );

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    detailsPanel.add( labelTextField, gbc );
    // translate label check box enabled in case of label is not empty.
    labelTextField.getDocument().addDocumentListener( new DocumentListener() {
      @Override public void insertUpdate( DocumentEvent e ) {
        translateLabel.setEnabled( e.getDocument().getLength() > 0 );
      }
      @Override public void removeUpdate( DocumentEvent e ) {
        translateLabel.setEnabled( e.getDocument().getLength() > 0 );
      }
      @Override public void changedUpdate( DocumentEvent e ) {
        translateLabel.setEnabled( e.getDocument().getLength() > 0 );
      }
    } );

    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridy = 2;
    gbc.gridx = 0;
    detailsPanel.add( new JLabel( Messages.getString( "ParameterDialog.ValueType" ) ), gbc );

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    detailsPanel.add( valueTypeComboBox, gbc );

    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridy = 3;
    gbc.gridx = 0;
    detailsPanel.add( new JLabel( Messages.getString( "ParameterDialog.DataFormat" ) ), gbc );

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    detailsPanel.add( dataFormatField, gbc );
    // translate data format enabled in case of data format is set
    dataFormatField.getDocument().addDocumentListener( new DocumentListener() {
      @Override public void insertUpdate( DocumentEvent e ) {
        translateDataFormat.setEnabled( e.getDocument().getLength() > 0 );
      }
      @Override public void removeUpdate( DocumentEvent e ) {
        translateDataFormat.setEnabled( e.getDocument().getLength() > 0 );
      }
      @Override public void changedUpdate( DocumentEvent e ) {
        translateDataFormat.setEnabled( e.getDocument().getLength() > 0 );
      }
    } );

    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridy = 4;
    gbc.gridx = 0;
    detailsPanel.add( timeZoneLabel, gbc );

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    detailsPanel.add( timeZoneBox, gbc );

    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridy = 5;
    gbc.gridx = 0;
    detailsPanel.add( new JLabel( Messages.getString( "ParameterDialog.DefaultValue" ) ), gbc );

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    detailsPanel.add( defaultValueTextField, gbc );

    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridy = 6;
    gbc.gridx = 0;
    detailsPanel.add( new JLabel( Messages.getString( "ParameterDialog.DefaultValueFormula" ) ), gbc );

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    detailsPanel.add( defaultValueFormulaField, gbc );

    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridy = 7;
    gbc.gridx = 0;
    detailsPanel.add( new JLabel( Messages.getString( "ParameterDialog.PostProcessingFormula" ) ), gbc );

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    detailsPanel.add( postProcessingFormulaField, gbc );

    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridy = 8;
    gbc.gridx = 1;
    detailsPanel.add( mandatoryCheckBox, gbc );

    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridy = 9;
    gbc.gridx = 1;
    detailsPanel.add( hiddenCheckBox, gbc );

    // BACKLOG-10392
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridy = 10;
    gbc.gridx = 1;
    detailsPanel.add( translateLabel, gbc );

    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridy = 11;
    gbc.gridx = 1;
    detailsPanel.add( translateDataFormat, gbc );

    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridy = 12;
    gbc.gridx = 1;
    detailsPanel.add( translateValues, gbc );

    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridy = 13;
    gbc.gridx = 1;
    detailsPanel.add( translateErrorMesage, gbc );

    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridy = 14;
    gbc.gridx = 0;
    detailsPanel.add( new JLabel( Messages.getString( "ParameterDialog.ResourceId" ) ), gbc );

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    detailsPanel.add( resourceTextField, gbc );

    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridy = 15;
    gbc.gridx = 0;
    detailsPanel.add( new JLabel( Messages.getString( "ParameterDialog.ErrorMessage" ) ), gbc );

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    detailsPanel.add( errorMesageTextField, gbc );
    // checkbox disabled if error message is empty
    errorMesageTextField.getDocument().addDocumentListener( new DocumentListener() {
      @Override public void insertUpdate( DocumentEvent e ) {
        translateErrorMesage.setEnabled( e.getDocument().getLength() > 0 );
      }
      @Override public void removeUpdate( DocumentEvent e ) {
        translateErrorMesage.setEnabled( e.getDocument().getLength() > 0 );
      }
      @Override public void changedUpdate( DocumentEvent e ) {
        translateErrorMesage.setEnabled( e.getDocument().getLength() > 0 );
      }
    } );

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridy = 16;
    gbc.gridx = 0;
    detailsPanel.add( createPromptPanel(), gbc );

    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridy = 17;
    gbc.gridx = 0;
    detailsPanel.add( new JLabel( Messages.getString( "ParameterDialog.Type" ) ), gbc );

    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    detailsPanel.add( new JComboBox( parameterTypeModel ), gbc );

    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridy = 18;
    gbc.gridx = 0;
    detailsPanel.add( new JLabel( Messages.getString( "ParameterDialog.Query" ) ), gbc );

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    detailsPanel.add( queryBox, gbc );


    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridy = 19;
    gbc.gridx = 0;
    detailsPanel.add( new JLabel( Messages.getString( "ParameterDialog.Id" ) ), gbc );

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    detailsPanel.add( idComboBox, gbc );

    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridy = 20;
    gbc.gridx = 0;
    detailsPanel.add( displayValueLabel, gbc );

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    detailsPanel.add( displayValueComboBox, gbc );
    // when items changed
    displayValueComboBox.addItemListener( l -> {
      boolean empty = StringUtils.isEmpty( String.valueOf( l.getItem() ), true );
      translateValues.setEnabled( !empty );
    } );
    // when re-init combo box model
    displayValueComboBox.addPropertyChangeListener( "model", l -> {
      boolean enable = false;
      enable = l.getNewValue() != null;
      if ( enable ) {
        // we have to have some items in list model to enable checkbox
        ListModel model = ListModel.class.cast( l.getNewValue() );
        translateValues.setEnabled( model.getSize() > 0 );
      } else {
        translateValues.setEnabled( false );
      }
    } );

    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridy = 21;
    gbc.gridx = 0;
    detailsPanel.add( displayFormulaLabel, gbc );

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    detailsPanel.add( displayFormulaField, gbc );

    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridy = 22;
    gbc.gridx = 0;
    detailsPanel.add( visibleItemsLabel, gbc );

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    detailsPanel.add( visibleItemsTextField, gbc );

    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridy = 23;
    gbc.gridx = 1;
    detailsPanel.add( strictValuesCheckBox, gbc );

    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridy = 24;
    gbc.gridx = 1;
    detailsPanel.add( reevaluateOnInvalidStrictParamCheckBox, gbc );

    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridy = 25;
    gbc.gridx = 1;
    detailsPanel.add( autofillSelectionCheckBox, gbc );


    final JPanel theDetailsLayoutPanel = new JPanel( new BorderLayout() );
    theDetailsLayoutPanel.add( detailsPanel, BorderLayout.NORTH );
    return theDetailsLayoutPanel;
  }

  private Box createPromptPanel() {
    final Box pane = Box.createHorizontalBox();
    pane.setBorder( BorderFactory.createEmptyBorder( 15, 0, 10, 0 ) );

    final JLabel promptLabel = new JLabel( Messages.getString( "ParameterDialog.Prompt" ) );
    pane.add( promptLabel );

    pane.add( Box.createRigidArea( new Dimension( 10, 0 ) ) );

    final JSeparator horizontalSeparator = new JSeparator();
    horizontalSeparator.setAlignmentY( TOP_ALIGNMENT );
    pane.add( horizontalSeparator );

    return pane;
  }

  void updateFromParameter( final ParameterDefinitionEntry p ) {
    if ( p == null ) {
      dataFormatField.setText( null );
      labelTextField.setText( null );
      nameTextField.setText( null );
      resourceTextField.setText( null );
      defaultValueTextField.setValue( null, String.class );
      visibleItemsTextField.setValue( 0 );
      parameterTypeModel.setSelectedItem( null );
      availableDataSources.clearSelection();
      displayValueComboBox.setSelectedItem( null );
      valueTypeComboBox.setSelectedItem( String.class );
      mandatoryCheckBox.setSelected( false );
      hiddenCheckBox.setSelected( false );
      postProcessingFormulaField.setFormula( null );
      displayFormulaField.setFormula( null );
      strictValuesCheckBox.setSelected( true );
      timeZoneBox.setSelectedItem( null );
      autofillSelectionCheckBox.setSelected( false );
      reevaluateOnInvalidStrictParamCheckBox.setSelected( false );
      translateLabel.setSelected( false );
      translateDataFormat.setSelected( false );
      translateValues.setSelected( false );
      translateErrorMesage.setSelected( false );
      errorMesageTextField.setText( null );
      resourceTextField.setText( null );
      setSelectedQuery( null );
      return;
    }


    final boolean multiSelection;
    if ( p instanceof DefaultListParameter ) {
      final DefaultListParameter parameter = (DefaultListParameter) p;
      final String queryName = parameter.getQueryName();
      final DataFactory factoryForQuery = findDataFactoryForQuery( queryName );
      if ( factoryForQuery != null ) {
        final int idx = availableDataSourcesModel.indexOf( factoryForQuery );
        availableDataSources.setSelectionPath( new TreePath(
          new Object[] { availableDataSourcesModel.getRoot(), availableDataSourcesModel.get( idx ), queryName } ) );
      } else {
        setSelectedQuery( queryName );
      }

      autofillSelectionCheckBox.setSelected( "true".equals(
        parameter.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
          ParameterAttributeNames.Core.AUTOFILL_SELECTION, parameterContext ) ) );
      reevaluateOnInvalidStrictParamCheckBox.setSelected( "true".equals(
        parameter.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
          ParameterAttributeNames.Core.RE_EVALUATE_ON_FAILED_VALUES, parameterContext ) ) );
      strictValuesCheckBox.setSelected( parameter.isStrictValueCheck() );
      displayValueComboBox.setSelectedItem( parameter.getTextColumn() );
      idComboBox.setSelectedItem( parameter.getKeyColumn() );
      final int visibleItems =
        ParserUtil.parseInt( parameter.getParameterAttribute(
          ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.VISIBLE_ITEMS ), 0 );
      visibleItemsTextField.setValue( visibleItems );
    } else {
      autofillSelectionCheckBox.setSelected( false );
      reevaluateOnInvalidStrictParamCheckBox.setSelected( false );
      strictValuesCheckBox.setSelected( true );
    }

    if ( p instanceof ListParameter ) {
      multiSelection = ( (ListParameter) p ).isAllowMultiSelection();
    } else {
      multiSelection = false;
    }

    final Class theType = p.getValueType();
    dataFormatField.setText( p.getParameterAttribute(
      ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.DATA_FORMAT, parameterContext ) );
    valueTypeComboBox.setSelectedItem( multiSelection ? theType.getComponentType() : theType );
    nameTextField.setText( p.getName() );
    labelTextField.setText( p.getParameterAttribute(
      ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.LABEL, parameterContext ) );
    mandatoryCheckBox.setSelected( p.isMandatory() );
    postProcessingFormulaField.setFormula( p.getParameterAttribute(
      ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.POST_PROCESSOR_FORMULA,
        parameterContext ) );
    displayFormulaField.setFormula( p.getParameterAttribute(
      ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.DISPLAY_VALUE_FORMULA,
        parameterContext ) );

    final String hiddenValue =
      p.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.HIDDEN,
        parameterContext );
    if ( hiddenValue != null ) {
      hiddenCheckBox.setSelected( hiddenValue.equals( "true" ) );
    }

    this.loadParameterTranslations( p );

    if ( p instanceof AbstractParameter ) {
      final AbstractParameter parameter = (AbstractParameter) p;
      defaultValueTextField.setValue( parameter.getDefaultValue(), parameter.getValueType() );
    } else {
      defaultValueTextField.setValue( null, p.getValueType() );
    }

    defaultValueFormulaField.setFormula( p.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.DEFAULT_VALUE_FORMULA, parameterContext ) );
    timeZoneModel.setSelectedKey( p.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.TIMEZONE, parameterContext ) );

    final String type = p.getParameterAttribute(
      ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.TYPE, parameterContext );
    if ( type != null ) {
      final int size = parameterTypeModel.getSize();
      for ( int i = 0; i < size; i++ ) {
        final ParameterType typeEntry = (ParameterType) parameterTypeModel.getElementAt( i );
        if ( typeEntry == null ) {
          continue;
        }
        if ( typeEntry.isMultiSelection() == multiSelection ) {
          if ( type.equals( typeEntry.getInternalName() ) ) {
            parameterTypeModel.setSelectedItem( typeEntry );
            break;
          }
        }
      }
    } else {
      parameterTypeModel.setSelectedItem( null );
    }
  }

  private String getSelectedQuery() {
    return (String) queryComboBoxModel.getSelectedItem();
  }

  private void setSelectedQuery( final String query ) {
    queryComboBoxModel.setSelectedItem( query );
  }

  ParameterDefinitionEntry createParameterResult() {
    final String query = getSelectedQuery();
    final String name = nameTextField.getText();
    final String label = labelTextField.getText();
    final Object rawDefaultValue = defaultValueTextField.getValue();
    final String dataFormat = dataFormatField.getText();
    final boolean isMandatory = mandatoryCheckBox.isSelected();

    final boolean translateDateFormat = this.translateDataFormat.isSelected();

    final ParameterType type = (ParameterType) parameterTypeModel.getSelectedItem();

    if ( query == null ) {
      return createQuerylessParameter( name, label, rawDefaultValue, dataFormat, isMandatory, type );
    }

    final String keyColumn = (String) idComboBox.getSelectedItem();

    final boolean isMultiSelect;
    final boolean hasVisibleItems;
    final boolean queryIsOptional;
    final String layout;
    final String typeName;
    if ( type != null ) {
      isMultiSelect = type.isMultiSelection();
      layout = type.getLayout();
      typeName = type.getInternalName();
      queryIsOptional = type.isQueryOptional();
      hasVisibleItems = type.isHasVisibleItems();
    } else {
      isMultiSelect = false;
      layout = null;
      typeName = null;
      queryIsOptional = true;
      hasVisibleItems = false;
    }

    final String textColumn;
    if ( queryIsOptional ) {
      textColumn = keyColumn;
    } else {
      textColumn = (String) displayValueComboBox.getSelectedItem();
    }

    final Class selectedType = (Class) valueTypeComboBox.getSelectedItem();
    final Class valueType = ( isMultiSelect ? Array.newInstance( selectedType, 0 ).getClass() : selectedType );

    final DefaultListParameter parameter =
      new DefaultListParameter( query, keyColumn, textColumn, name, isMultiSelect,
        strictValuesCheckBox.isSelected(), valueType );
    parameter
      .setParameterAttribute( ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.LAYOUT, layout );
    parameter
      .setParameterAttribute( ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.TYPE, typeName );
    parameter.setParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.RE_EVALUATE_ON_FAILED_VALUES,
      String.valueOf( reevaluateOnInvalidStrictParamCheckBox.isSelected() ) );
    parameter
      .setParameterAttribute( ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.AUTOFILL_SELECTION,
        String.valueOf( autofillSelectionCheckBox.isSelected() ) );
    if ( StringUtils.isEmpty( label ) == false ) {
      parameter
        .setParameterAttribute( ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.LABEL, label );
    }

    parameter.setMandatory( isMandatory );
    parameter.setParameterAttribute(
      ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.HIDDEN,
        String.valueOf( hiddenCheckBox.isSelected() ) );

    parameter.setDefaultValue( rawDefaultValue );

    if ( hasVisibleItems ) {
      final Number visibleItemsInput = (Number) visibleItemsTextField.getValue();
      if ( visibleItemsInput != null && visibleItemsInput.intValue() > 0 ) {
        parameter.setParameterAttribute(
          ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.VISIBLE_ITEMS,
            String.valueOf( visibleItemsInput ) );
      }
    }

    if ( StringUtils.isEmpty( dataFormat ) == false ) {
      parameter.setParameterAttribute(
        ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.DATA_FORMAT, dataFormat );
    }
    parameter.setParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.TRANSLATE_DATA_FORMAT, String.valueOf( translateDateFormat ) );

    if ( queryIsOptional == false ) {
      parameter.setParameterAttribute(
        ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.DISPLAY_VALUE_FORMULA,
          displayFormulaField.getFormula() );
    }
    parameter.setParameterAttribute(
      ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.DEFAULT_VALUE_FORMULA,
        defaultValueFormulaField.getFormula() );
    parameter.setParameterAttribute(
      ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.POST_PROCESSOR_FORMULA,
        postProcessingFormulaField.getFormula() );
    parameter.setParameterAttribute(
      ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.TIMEZONE,
        timeZoneModel.getSelectedKey() );

    updateParameterTranslation( parameter );

    return parameter;
  }

  // default visibility for testing properties
  ParameterDefinitionEntry createQuerylessParameter( final String name,
                                                             final String label,
                                                             final Object rawDefaultValue,
                                                             final String dataFormat,
                                                             final boolean mandatory,
                                                             final ParameterType type ) {
    final Class selectedType = (Class) valueTypeComboBox.getSelectedItem();
    final AbstractParameter parameter;
    if ( type == null || !type.isMultiSelection() ) {
      // single value parameter
      parameter = new PlainParameter( name );
      parameter.setValueType( selectedType );
    } else {
      // multi-value parameter
      final Class valueType = Array.newInstance( selectedType, 0 ).getClass();
      parameter = new StaticListParameter( name, true, false, valueType );
    }
    if ( type != null ) {
      parameter.setParameterAttribute(
        ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.TYPE, type.getInternalName() );
    }
    if ( StringUtils.isEmpty( label ) == false ) {
      parameter.setParameterAttribute(
        ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.LABEL, label );
    }
    parameter.setDefaultValue( rawDefaultValue );
    parameter.setMandatory( mandatory );
    parameter.setParameterAttribute(
      ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.HIDDEN,
        String.valueOf( hiddenCheckBox.isSelected() ) );
    if ( StringUtils.isEmpty( dataFormat ) == false ) {
      parameter.setParameterAttribute(
        ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.DATA_FORMAT, dataFormat );
    }
    parameter.setParameterAttribute(
      ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.DEFAULT_VALUE_FORMULA,
        defaultValueFormulaField.getFormula() );
    parameter.setParameterAttribute(
      ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.POST_PROCESSOR_FORMULA,
        postProcessingFormulaField.getFormula() );
    parameter.setParameterAttribute(
      ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.TIMEZONE,
        timeZoneModel.getSelectedKey() );

    updateParameterTranslation( parameter );

    return parameter;
  }

  // default visibility for testing purposes
  void updateParameterTranslation( final AbstractParameter parameter ) {
    if ( translateLabel.isSelected() ) {
      parameter.setParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.TRANSLATE_LABEL, Boolean.TRUE.toString() );
    }
    if ( translateDataFormat.isSelected() ) {
      parameter.setParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.TRANSLATE_DATA_FORMAT, Boolean.TRUE.toString() );
    }
    if ( translateValues.isSelected() ) {
      parameter.setParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.TRANSLATE_DISPLAY_NAMES, Boolean.TRUE.toString() );
    }
    if ( translateErrorMesage.isSelected() ) {
      parameter.setParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.TRANSLATE_ERROR_MESSAGE, Boolean.TRUE.toString() );
    }

    final String translateResourceId = resourceTextField.getText();
    if ( !StringUtils.isEmpty( translateResourceId ) ) {
      parameter.setParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.TRANSLATE_RESOURCE_ID, translateResourceId );
    }
    final String errorMessage = errorMesageTextField.getText();
    if ( !StringUtils.isEmpty( errorMessage ) ) {
      parameter.setParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.ERROR_MESSAGE, errorMessage );
    }
  }

  void loadParameterTranslations( final  ParameterDefinitionEntry p ) {
    final String strTranslateLabel = p.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.TRANSLATE_LABEL, parameterContext );
    final String strTranslateDataFormat = p.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.TRANSLATE_DATA_FORMAT, parameterContext );
    final String strTranslateValues = p.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.TRANSLATE_DISPLAY_NAMES, parameterContext );
    final String srtTranslateErrorMessage = p.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.TRANSLATE_ERROR_MESSAGE, parameterContext );

    final String resourceId = p.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.TRANSLATE_RESOURCE_ID, parameterContext );
    final String strErrorMessage = p.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.ERROR_MESSAGE, parameterContext );

    translateLabel.setSelected( Boolean.valueOf( strTranslateLabel ) );
    translateDataFormat.setSelected( Boolean.valueOf( strTranslateDataFormat ) );
    translateValues.setSelected( Boolean.valueOf( strTranslateValues ) );
    translateErrorMesage.setSelected( Boolean.valueOf( srtTranslateErrorMessage ) );

    resourceTextField.setText( resourceId );
    errorMesageTextField.setText( strErrorMessage );
  }

  private ParameterType getSelectedParameterType() {
    return (ParameterType) parameterTypeModel.getSelectedItem();
  }

  private DataFactory findDataFactoryForQuery( final String query ) {
    if ( query == null ) {
      return null;
    }

    final QueryDataRowWrapper dataRow = new QueryDataRowWrapper( new ReportParameterValues(), 1, 0 );
    final int size = availableDataSourcesModel.size();
    for ( int i = 0; i < size; i++ ) {
      final DataFactoryWrapper factoryWrapper = availableDataSourcesModel.get( i );
      if ( factoryWrapper.isRemoved() ) {
        continue;
      }
      final DataFactory factory = factoryWrapper.getEditedDataFactory();
      if ( factory.isQueryExecutable( query, dataRow ) ) {
        return factory;
      }
    }
    return null;
  }

  public ParameterEditResult performEditParameter( final ReportDesignerContext context,
                                                   final MasterReport masterReport,
                                                   final ParameterDefinitionEntry parameterDefinitionEntry ) {
    if ( context == null ) {
      throw new NullPointerException();
    }

    if ( parameterDefinitionEntry == null ) {
      setTitle( Messages.getString( "ParameterDialog.Title.Add" ) );
      parameter = null;
    } else {
      setTitle( Messages.getString( "ParameterDialog.Title.Edit" ) );
      parameter = parameterDefinitionEntry.getName();
    }

    try {
      reportDesignerContext = context;
      displayFormulaField.setReportDesignerContext( context );
      defaultValueFormulaField.setReportDesignerContext( context );
      postProcessingFormulaField.setReportDesignerContext( context );

      availableDataSourcesModel.importFromReport( (CompoundDataFactory) masterReport.getDataFactory() );

      // Expand all the nodes
      provisionDataSourcePanel.expandAllNodes();

      updateFromParameter( parameterDefinitionEntry );

      if ( performEdit() ) {
        return new ParameterEditResult( availableDataSourcesModel.toArray(), createParameterResult() );
      }
      return null;
    } finally {
      reportDesignerContext = null;
      displayFormulaField.setReportDesignerContext( null );
      postProcessingFormulaField.setReportDesignerContext( null );
      defaultValueFormulaField.setReportDesignerContext( null );
    }
  }

  public String[] getDataFields() {
    final String queryName = getSelectedQuery();
    if ( queryName == null ) {
      return new String[ 0 ];
    }
    final DataFactory rawDataFactory = findDataFactoryForQuery( queryName );
    if ( rawDataFactory == null ) {
      return new String[ 0 ];
    }

    final DataFactory dataFactory = new CachingDataFactory( rawDataFactory, true );

    try {
      final ReportDocumentContext activeContext = reportDesignerContext.getActiveContext();
      if ( activeContext != null ) {
        final MasterReport reportDefinition = activeContext.getContextRoot();
        dataFactory.initialize( new DesignTimeDataFactoryContext( reportDefinition ) );
      }

      final TableModel tableModel = dataFactory.queryData(
        queryName, new QueryDataRowWrapper( new ReportParameterValues(), 1, 0 ) );

      final int colCount = tableModel.getColumnCount();
      final String[] cols = new String[ colCount ];
      for ( int i = 0; i < colCount; i++ ) {
        cols[ i ] = tableModel.getColumnName( i );
      }
      return cols;
    } catch ( ReportProcessingException aExc ) {
      UncaughtExceptionsModel.getInstance().addException( aExc );
      return new String[ 0 ];
    } finally {
      dataFactory.close();
    }
  }

  public String getParameter() {
    return parameter;
  }

  @Override
  protected boolean validateInputs( boolean onConfirm ) {
    final ValidationResult validationResult = this.validate( new ValidationResult() );
    final ValidationMessage[] validationMessages = validationResult.getValidationMessages( ALL_SEVERITIES );

    if ( validationMessages.length == 0 ) {
      return true;
    }

    final StringBuilder messages = new StringBuilder( 100 );
    for ( final ValidationMessage validationMessage : validationMessages ) {
      messages.append( validationMessage.getMessage() );
      messages.append( '\n' );
    }

    JOptionPane.showMessageDialog( ParameterDialog.this, messages, Messages.getString( "ParameterDialog.ErrorTitle" ),
      JOptionPane.WARNING_MESSAGE );

    return false;
  }

  private ValidationResult validate( ValidationResult validationResult ) {
    if ( StringUtils.isEmpty( this.nameTextField.getText() ) ) {
      final String nameCaption = Messages.getString( "ParameterDialog.Name" );
      validationResult.addValidationMessage( new ValidationMessage( ValidationMessage.Severity.ERROR, Messages
        .getString( "ParameterDialog.ValueMustBeSetMessage", nameCaption ) ) );
    }

    int errors = validationResult.getValidationMessages(
      new ValidationMessage.Severity[] { ValidationMessage.Severity.ERROR } ).length;
    if ( errors == 0 ) {
      validateLocalizations( validationResult );
    }
    return validationResult;
  }

  private ValidationResult validateLocalizations( ValidationResult validationResult ) {
    if ( translateLabel.isSelected() || translateDataFormat.isSelected()
      || translateValues.isSelected() || translateErrorMesage.isSelected() ) {
      if ( StringUtils.isEmpty( resourceTextField.getText(), true ) ) {
        final String nameCaption = Messages.getString( "ParameterDialog.ResourceId" );
        validationResult.addValidationMessage(
          new ValidationMessage( ValidationMessage.Severity.ERROR,
            Messages.getString( "ParameterDialog.ResourceIdMustBeSet", nameCaption ) ) );
      }
    }
    return validationResult;
  }

  public static class ParameterEditResult {
    private DataFactoryWrapper[] wrappers;
    private ParameterDefinitionEntry parameter;

    public ParameterEditResult( final DataFactoryWrapper[] wrappers, final ParameterDefinitionEntry entries ) {
      this.wrappers = wrappers;
      this.parameter = entries;
    }

    public DataFactoryWrapper[] getWrappers() {
      return wrappers;
    }

    public ParameterDefinitionEntry getParameter() {
      return parameter;
    }
  }

  private class TypeListener implements ListDataListener {
    public void intervalAdded( final ListDataEvent e ) {

    }

    public void intervalRemoved( final ListDataEvent e ) {

    }

    public void contentsChanged( final ListDataEvent e ) {
      final ParameterType type = (ParameterType) parameterTypeModel.getSelectedItem();
      final boolean visible = ( type != null ) && type.isHasVisibleItems();

      visibleItemsTextField.setVisible( visible );
      visibleItemsLabel.setVisible( visible );

      final boolean displayFormulaVisible = ( type != null ) && type.isQueryOptional() == false;
      displayFormulaField.setVisible( displayFormulaVisible );
      displayFormulaLabel.setVisible( displayFormulaVisible );
      displayValueLabel.setVisible( displayFormulaVisible );
      displayValueComboBox.setVisible( displayFormulaVisible );

      final String selectedQuery = (String) queryComboBoxModel.getSelectedItem();
      final boolean querySelected = StringUtils.isEmpty( selectedQuery, false ) == false;
      strictValuesCheckBox.setVisible( querySelected );
      reevaluateOnInvalidStrictParamCheckBox.setVisible( querySelected );
      autofillSelectionCheckBox.setVisible( querySelected );
    }
  }

  private class DataSetQueryUpdateHandler implements TreeModelListener {

    private DataSetQueryUpdateHandler() {
    }

    /**
     * <p>Invoked after a node (or a set of siblings) has changed in some way. The node(s) have not changed locations in
     * the tree or altered their children arrays, but other attributes have changed and may affect presentation.
     * Example: the name of a file has changed, but it is in the same location in the file system.</p> <p>To indicate
     * the root has changed, childIndices and children will be null. </p> <p/> <p>Use <code>e.getPath()</code> to get
     * the parent of the changed node(s). <code>e.getChildIndices()</code> returns the index(es) of the changed
     * node(s).</p>
     */
    public void treeNodesChanged( final TreeModelEvent e ) {
      valueChanged();
    }

    /**
     * <p>Invoked after nodes have been inserted into the tree.</p> <p/> <p>Use <code>e.getPath()</code> to get the
     * parent of the new node(s). <code>e.getChildIndices()</code> returns the index(es) of the new node(s) in ascending
     * order.</p>
     */
    public void treeNodesInserted( final TreeModelEvent e ) {
      valueChanged();
    }

    /**
     * <p>Invoked after nodes have been removed from the tree.  Note that if a subtree is removed from the tree, this
     * method may only be invoked once for the root of the removed subtree, not once for each individual set of siblings
     * removed.</p> <p/> <p>Use <code>e.getPath()</code> to get the former parent of the deleted node(s).
     * <code>e.getChildIndices()</code> returns, in ascending order, the index(es) the node(s) had before being
     * deleted.</p>
     */
    public void treeNodesRemoved( final TreeModelEvent e ) {
      valueChanged();
    }

    /**
     * <p>Invoked after the tree has drastically changed structure from a given node down.  If the path returned by
     * e.getPath() is of length one and the first element does not identify the current root node the first element
     * should become the new root of the tree.<p> <p/> <p>Use <code>e.getPath()</code> to get the path to the node.
     * <code>e.getChildIndices()</code> returns null.</p>
     */
    public void treeStructureChanged( final TreeModelEvent e ) {
      valueChanged();
    }

    private void valueChanged() {
      final int count = availableDataSourcesModel.size();
      final LinkedHashSet<String> set = new LinkedHashSet<String>();
      for ( int i = 0; i < count; i++ ) {
        final DataFactoryWrapper factoryWrapper = availableDataSourcesModel.get( i );
        if ( factoryWrapper.isRemoved() ) {
          continue;
        }
        final String[] strings = factoryWrapper.getEditedDataFactory().getQueryNames();
        set.addAll( Arrays.asList( strings ) );
      }

      queryComboBoxModel.setValues( set.toArray( new String[ set.size() ] ) );
      final TreePath selectionPath = availableDataSources.getSelectionPath();
      if ( selectionPath == null ) {
        setSelectedQuery( null );
        idComboBox.setModel( new DefaultComboBoxModel() );
        displayValueComboBox.setModel( new DefaultComboBoxModel() );
        return;
      }

      final Object maybeQuery = selectionPath.getLastPathComponent();
      if ( maybeQuery instanceof String ) {
        setSelectedQuery( (String) maybeQuery );
      }
    }
  }

  private class QuerySelectionHandler implements ListDataListener, TreeSelectionListener {
    private QuerySelectionHandler() {
    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged( final TreeSelectionEvent e ) {
      if ( e.getPath() == null ) {
        return;
      }
      final Object o = e.getPath().getLastPathComponent();
      if ( o instanceof String ) {
        queryComboBoxModel.setSelectedItem( o );
      }
    }

    public void intervalAdded( final ListDataEvent e ) {

    }

    public void intervalRemoved( final ListDataEvent e ) {

    }

    public void contentsChanged( final ListDataEvent e ) {
      final String[] cols = getDataFields();
      idComboBox.setModel( new DefaultComboBoxModel( cols ) );
      displayValueComboBox.setModel( new DefaultComboBoxModel( cols ) );

      final String selectedQuery = (String) queryComboBoxModel.getSelectedItem();
      final boolean querySelected = StringUtils.isEmpty( selectedQuery, false ) == false;
      strictValuesCheckBox.setVisible( querySelected );
      reevaluateOnInvalidStrictParamCheckBox.setVisible( querySelected );
      autofillSelectionCheckBox.setVisible( querySelected );
    }
  }

  private class EditorParameterContext implements ParameterContext {
    private ReportEnvironment defaultEnvironment;
    private DocumentMetaData defaultDocumentMetaData;
    private DataRow dataRow;
    private ResourceBundleFactory resourceBundleFactory;
    private ResourceManager resourceManager;

    private EditorParameterContext() {
      resourceManager = new ResourceManager();
      resourceBundleFactory = new DefaultResourceBundleFactory();
      defaultEnvironment = new DefaultReportEnvironment( ClassicEngineBoot.getInstance().getGlobalConfig() );
      defaultDocumentMetaData = new MemoryDocumentMetaData();

      final ReportEnvironmentDataRow envDataRow = new ReportEnvironmentDataRow( defaultEnvironment );
      dataRow = new CompoundDataRow( envDataRow, new StaticDataRow() );
    }

    public PerformanceMonitorContext getPerformanceMonitorContext() {
      return new NoOpPerformanceMonitorContext();
    }

    public DataRow getParameterData() {
      return dataRow;
    }

    public DataFactory getDataFactory() {
      return provisionDataSourcePanel.getSelectedDataSource();
    }

    public ResourceManager getResourceManager() {
      if ( reportDesignerContext == null ) {
        return resourceManager;
      }
      final ReportDocumentContext activeContext = reportDesignerContext.getActiveContext();
      if ( activeContext == null ) {
        return resourceManager;
      }
      return activeContext.getContextRoot().getResourceManager();
    }

    public ResourceBundleFactory getResourceBundleFactory() {
      if ( reportDesignerContext == null ) {
        return resourceBundleFactory;
      }
      final ReportDocumentContext activeContext = reportDesignerContext.getActiveContext();
      if ( activeContext == null ) {
        return resourceBundleFactory;
      }
      final MasterReport report = activeContext.getContextRoot();
      return MasterReport.computeAndInitResourceBundleFactory( report.getResourceBundleFactory(),
        report.getReportEnvironment() );
    }

    public Configuration getConfiguration() {
      if ( reportDesignerContext == null ) {
        return ClassicEngineBoot.getInstance().getGlobalConfig();
      }
      final ReportDocumentContext activeContext = reportDesignerContext.getActiveContext();
      if ( activeContext == null ) {
        return ClassicEngineBoot.getInstance().getGlobalConfig();
      }
      return activeContext.getContextRoot().getConfiguration();
    }

    public ResourceKey getContentBase() {
      if ( reportDesignerContext == null ) {
        return null;
      }
      final ReportDocumentContext activeContext = reportDesignerContext.getActiveContext();
      if ( activeContext == null ) {
        return null;
      }
      return activeContext.getContextRoot().getContentBase();
    }

    /**
     * the document metadata of the report. Can be null, if the report does not have a bundle associated or if this
     * context is not part of a report-processing.
     *
     * @return the document metadata.
     */
    public DocumentMetaData getDocumentMetaData() {
      if ( reportDesignerContext == null ) {
        return defaultDocumentMetaData;
      }
      final ReportDocumentContext activeContext = reportDesignerContext.getActiveContext();
      if ( activeContext == null ) {
        return defaultDocumentMetaData;
      }
      return activeContext.getContextRoot().getBundle().getMetaData();
    }

    public ReportEnvironment getReportEnvironment() {
      if ( reportDesignerContext == null ) {
        return defaultEnvironment;
      }
      final ReportDocumentContext activeContext = reportDesignerContext.getActiveContext();
      if ( activeContext == null ) {
        return defaultEnvironment;
      }
      return activeContext.getContextRoot().getReportEnvironment();
    }

    public void close() throws ReportDataFactoryException {
    }
  }

  private class TypeSelectionHandler extends DocumentChangeHandler implements ListDataListener {
    private DefaultValueEditorPanel valueEditorPanel;

    private TypeSelectionHandler( final DefaultValueEditorPanel valueEditorPanel ) {
      this.valueEditorPanel = valueEditorPanel;
    }

    /**
     * Sent after the indices in the index0,index1 interval have been inserted in the data model. The new interval
     * includes both index0 and index1.
     *
     * @param e a <code>ListDataEvent</code> encapsulating the event information
     */
    public void intervalAdded( final ListDataEvent e ) {
      // ignorable, model is static
    }

    /**
     * Sent after the indices in the index0,index1 interval have been removed from the data model.  The interval
     * includes both index0 and index1.
     *
     * @param e a <code>ListDataEvent</code> encapsulating the event information
     */
    public void intervalRemoved( final ListDataEvent e ) {
      // ignorable, model is static
    }

    /**
     * Sent when the contents of the list has changed in a way that's too complex to characterize with the previous
     * methods. For example, this is sent when an item has been replaced. Index0 and index1 bracket the change.
     *
     * @param event a <code>ListDataEvent</code> encapsulating the event information
     */
    public void contentsChanged( final ListDataEvent event ) {
      final Object o = valueTypeComboBox.getSelectedItem();
      if ( o instanceof Class == false ) {
        timeZoneLabel.setVisible( false );
        timeZoneBox.setVisible( false );
        valueEditorPanel.setValueType( String.class, null, TimeZone.getDefault() );
        return;
      }

      final Class selectedClass = (Class) o;
      final ParameterType parameterType = getSelectedParameterType();
      final Class type;
      if ( parameterType == null || parameterType.isMultiSelection() == false ) {
        type = selectedClass;
      } else {
        type = Array.newInstance( selectedClass, 0 ).getClass();
      }
      timeZoneLabel.setVisible( Date.class.isAssignableFrom( selectedClass ) );
      timeZoneBox.setVisible( Date.class.isAssignableFrom( selectedClass ) );
      valueEditorPanel.setValueType( type, dataFormatField.getText(), getSelectedTimeZone() );
    }

    protected void handleChange( final DocumentEvent e ) {
      contentsChanged( null );
    }
  }

}

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
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.designtime.DataFactoryChange;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.designtime.DefaultDataFactoryChangeRecorder;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaModel;
import org.pentaho.reporting.engine.classic.wizard.ui.xul.WizardEditorModel;
import org.pentaho.reporting.engine.classic.wizard.ui.xul.components.AbstractWizardStep;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.binding.Binding.Type;
import org.pentaho.ui.xul.binding.BindingConvertor;
import org.pentaho.ui.xul.containers.XulDialog;
import org.pentaho.ui.xul.containers.XulListbox;
import org.pentaho.ui.xul.containers.XulTree;
import org.pentaho.ui.xul.impl.AbstractXulEventHandler;
import org.pentaho.ui.xul.util.AbstractModelNode;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class DataSourceAndQueryStep extends AbstractWizardStep {
  private DesignTimeContext designTimeContext;

  private enum DATASOURCE_TYPE {
    ROOT, DATAFACTORY, CONNECTION, QUERY
  }

  private static class DataFactoryMetaDataComparator implements Comparator<DataFactoryMetaData> {
    private DataFactoryMetaDataComparator() {
    }

    public int compare( final DataFactoryMetaData o1, final DataFactoryMetaData o2 ) {
      return o1.getDisplayName( Locale.getDefault() ).compareTo( o2.getDisplayName( Locale.getDefault() ) );
    }
  }

  private class SelectedIndexUpdateHandler implements PropertyChangeListener {
    private SelectedIndexUpdateHandler() {
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      if ( SELECTED_INDEX_PROPERTY_NAME.equals( evt.getPropertyName() ) ) {
        final XulDialog datasourceType = (XulDialog) getDocument().getElementById( DATASOURCE_TYPE_DIALOG_ID );
        datasourceType.setVisible( false );
      }
    }

  }

  protected static class XulEditorDataFactoryMetaData {
    private DataFactoryMetaData metadata;

    public XulEditorDataFactoryMetaData( final DataFactoryMetaData metadata ) {
      if ( metadata == null ) {
        throw new NullPointerException();
      }
      this.metadata = metadata;
    }

    public String getName() {
      return metadata.getDisplayName( Locale.getDefault() );
    }

    public DataFactoryMetaData getMetadata() {
      return metadata;
    }

    public String toString() {
      return getName();
    }
  }


  /**
   * @author wseyler
   */
  private class CurrentQueryBindingConverter extends BindingConvertor<DatasourceModelNode, String> {

    /* (non-Javadoc)
     * @see org.pentaho.ui.xul.binding.BindingConvertor#sourceToTarget(java.lang.Object)
     */
    @Override
    public String sourceToTarget( final DatasourceModelNode value ) {
      if ( value != null && value.getType() == DATASOURCE_TYPE.QUERY ) {
        return value.getValue();
      }
      return DataSourceAndQueryStep.this.getCurrentQuery();
    }

    /* (non-Javadoc)
     * @see org.pentaho.ui.xul.binding.BindingConvertor#targetToSource(java.lang.Object)
     */
    @Override
    public DatasourceModelNode targetToSource( final String value ) {
      // not used for one way binding
      return null;
    }
  }

  protected class DatasourceAndQueryStepHandler extends AbstractXulEventHandler {
    public DatasourceAndQueryStepHandler() {
    }

    public String getName() {
      return HANDLER_NAME;
    }

    public void doCreateDataFactory() {
      DataSourceAndQueryStep.this.createDataFactory();
    }

    public void doEditDatasource() {
      final XulTree tree = (XulTree) document.getElementById( DATASOURCES_TREE_ID );
      final DatasourceModelNode node = (DatasourceModelNode) tree.getSelectedItem();
      switch( node.getType() ) {
        case CONNECTION:
          final DataFactory df = (DataFactory) node.getUserObject();
          final DataFactoryMetaData o = getMetaForDataFactory( df, dataFactoryMetas );
          editOrCreateDataFactory( o );
          break;
        case QUERY:
          editQuery( node.getValue() );
          break;
        default:
          break;
      }
    }

    public void doDeleteDatasourceItem() {
      final XulTree tree = (XulTree) document.getElementById( DATASOURCES_TREE_ID );
      final DatasourceModelNode node = (DatasourceModelNode) tree.getSelectedItem();
      switch( node.getType() ) {
        case DATAFACTORY:
          deleteDataFactory( (DataFactoryMetaData) node.getUserObject() );
          break;
        case CONNECTION:
          deleteConnection( (DataFactory) node.getUserObject() );
        default:
          break;
      }
      updateDatasourceTree();
    }
  }


  protected class DatasourceModelNode extends AbstractModelNode<DatasourceModelNode> {
    private DATASOURCE_TYPE type;

    private String value;
    private Object userObject;

    public DatasourceModelNode( final String value, final Object userObject, final DATASOURCE_TYPE type ) {
      this.value = value;
      this.userObject = userObject;
      this.type = type;
    }

    public String getValue() {
      return value;
    }

    public void setValue( final String value ) {
      final String oldValue = this.value;
      this.value = value;

      this.firePropertyChange( VALUE_PROPERTY_NAME, oldValue, value );
    }

    public DATASOURCE_TYPE getType() {
      return type;
    }

    public void setType( final DATASOURCE_TYPE type ) {
      this.type = type;
    }

    public Object getUserObject() {
      return userObject;
    }

    public void setUserObject( final Object userObject ) {
      this.userObject = userObject;
    }
  }

  private class IndiciesToBooleanBindingConverter extends BindingConvertor<int[], Boolean> {

    /* (non-Javadoc)
     * @see org.pentaho.ui.xul.binding.BindingConvertor#sourceToTarget(java.lang.Object)
     */
    @Override
    public Boolean sourceToTarget( final int[] value ) {
      return value.length > 0;
    }

    /* (non-Javadoc)
     * @see org.pentaho.ui.xul.binding.BindingConvertor#targetToSource(java.lang.Object)
     */
    @Override
    public int[] targetToSource( final Boolean value ) {
      // Not needed for one way binding
      return null;
    }

  }

  private static final String DATASOURCES_ROOT_NODE_NAME = "Datasources Root"; //$NON-NLS-1$

  private static final String DATASOURCE_AND_QUERY_STEP_OVERLAY =
    "org/pentaho/reporting/engine/classic/wizard/ui/xul/res/datasource_and_query_step_Overlay.xul"; //$NON-NLS-1$
  private static final String HANDLER_NAME = "datasource_and_query_step_handler"; //$NON-NLS-1$

  private static final String DATASOURCES_TREE_ID = "datasources_tree"; //$NON-NLS-1$
  private static final String DATASOURCE_TYPE_DIALOG_ID = "datasource_type_dialog"; //$NON-NLS-1$
  private static final String DATASOURCE_SELECTIONS_BOX_ID = "datasource_selections_box"; //$NON-NLS-1$
  private static final String EDIT_DATASOURCES_BTN_ID = "edit_datasource_btn"; //$NON-NLS-1$
  private static final String REMOVE_DATASOURCES_BTN_ID = "remove_datasource_btn"; //$NON-NLS-1$

  private static final String ELEMENTS_PROPERTY_NAME = "elements"; //$NON-NLS-1$
  private static final String SELECTED_INDEX_PROPERTY_NAME = "selectedIndex"; //$NON-NLS-1$
  private static final String SELECTED_ROWS_PROPERTY_NAME = "selectedRows"; //$NON-NLS-1$
  private static final String SELECTED_ITEM_PROPERTY_NAME = "selectedItem"; //$NON-NLS-1$
  private static final String CURRENT_QUERY_PROPERTY_NAME = "currentQuery"; //$NON-NLS-1$
  private static final String DATASOURCES_ROOT_PROPERTY_NAME = "dataSourcesRoot"; //$NON-NLS-1$
  private static final String VALUE_PROPERTY_NAME = "value"; //$NON-NLS-1$
  private static final String ENABLED_PROPERTY_NAME = "!disabled"; //$NON-NLS-1$

  private IndiciesToBooleanBindingConverter indiciesToBooleanBindingConverter;
  private DatasourceModelNode dataSourcesRoot;
  private List<XulEditorDataFactoryMetaData> dataFactoryMetas;
  private CompoundDataFactory cdf;

  public DataSourceAndQueryStep() {
    super();
    indiciesToBooleanBindingConverter = new IndiciesToBooleanBindingConverter();
    dataFactoryMetas = new ArrayList<XulEditorDataFactoryMetaData>();

    refreshMetadata();
  }

  public DesignTimeContext getDesignTimeContext() {
    return designTimeContext;
  }

  public void setDesignTimeContext( final DesignTimeContext designTimeContext ) {
    this.designTimeContext = designTimeContext;
    refreshMetadata();
  }

  private void refreshMetadata() {
    final DataFactoryMetaData[] dfmdArray = DataFactoryRegistry.getInstance().getAll();
    Arrays.sort( dfmdArray, new DataFactoryMetaDataComparator() );
    for ( final DataFactoryMetaData dfmd : dfmdArray ) {
      if ( dfmd.isEditable() == false ) {
        continue;
      }
      if ( dfmd.isEditorAvailable() == false ) {
        continue;
      }

      if ( dfmd.isHidden() ) {
        continue;
      }
      dataFactoryMetas.add( new XulEditorDataFactoryMetaData( dfmd ) );
    }
  }

  public void setBindings() {
    getBindingFactory().setBindingType( Type.ONE_WAY );
    getBindingFactory()
      .createBinding( this, DATASOURCES_ROOT_PROPERTY_NAME, DATASOURCES_TREE_ID, ELEMENTS_PROPERTY_NAME );
    getBindingFactory()
      .createBinding( DATASOURCES_TREE_ID, SELECTED_ITEM_PROPERTY_NAME, this, CURRENT_QUERY_PROPERTY_NAME,
        new CurrentQueryBindingConverter() );
    getBindingFactory()
      .createBinding( DATASOURCES_TREE_ID, SELECTED_ROWS_PROPERTY_NAME, EDIT_DATASOURCES_BTN_ID, ENABLED_PROPERTY_NAME,
        indiciesToBooleanBindingConverter );
    getBindingFactory().createBinding( DATASOURCES_TREE_ID, SELECTED_ROWS_PROPERTY_NAME, REMOVE_DATASOURCES_BTN_ID,
      ENABLED_PROPERTY_NAME, indiciesToBooleanBindingConverter );
  }

  public void editQuery( final String queryName ) {
    final DataFactory dataFactory = getOwnerDataFactory( queryName );
    final DataFactoryMetaData o = getMetaForDataFactory( dataFactory, dataFactoryMetas );
    editOrCreateDataFactory( o );
  }

  private DataFactoryMetaData getMetaForDataFactory( final DataFactory dataFactory,
                                                     final List<XulEditorDataFactoryMetaData> metaDatas ) {
    final String dfClassName = dataFactory.getClass().getName();
    for ( final XulEditorDataFactoryMetaData mdfmd : metaDatas ) {
      final DataFactoryMetaData data = mdfmd.getMetadata();
      final String mdFactoryName = data.getName();
      if ( dfClassName.equals( mdFactoryName ) ) {
        return mdfmd.getMetadata();
      }
    }
    return null;
  }

  private int getDataFactoryForMeta( final DataFactoryMetaData dfMetaData ) {
    for ( int i = 0; i < cdf.size(); i++ ) {
      final DataFactory df = cdf.getReference( i );
      if ( dfMetaData.getName().equals( df.getClass().getName() ) ) {
        return i;
      }
    }

    return -1;
  }

  private DataFactory getOwnerDataFactory( final String queryName ) {
    return cdf.getDataFactoryForQuery( queryName );
  }

  public void createDataFactory() {
    final XulDialog datasourceType = (XulDialog) getDocument().getElementById( DATASOURCE_TYPE_DIALOG_ID );
    datasourceType.setVisible( true );
    final XulListbox box = (XulListbox) getDocument().getElementById( DATASOURCE_SELECTIONS_BOX_ID );
    final XulEditorDataFactoryMetaData myEditData = (XulEditorDataFactoryMetaData) box.getSelectedItem();
    if ( myEditData != null ) {
      editOrCreateDataFactory( myEditData.getMetadata() );
    }
    box.setSelectedIndices( new int[ 0 ] );  // clear the selection for next time.
  }

  public void editOrCreateDataFactory( final DataFactoryMetaData o ) {

    if ( o == null ) {
      return;
    }

    if ( o.isHidden() ) {
      return;
    }

    final DefaultDataFactoryChangeRecorder changeRecorder = new DefaultDataFactoryChangeRecorder();
    final DataFactory editDataFactory = grabAndRemoveEditDataFactory( o );
    final DataSourcePlugin dataSourcePlugin = o.createEditor();
    final DataFactory generatedDataFactory =
      dataSourcePlugin.performEdit( getDesignTimeContext(), editDataFactory, null, changeRecorder );
    if ( generatedDataFactory != null ) {
      final DataFactoryChange[] changes = changeRecorder.getChanges();
      DefaultDataFactoryChangeRecorder.applyChanges( cdf, changes );

      cdf.add( generatedDataFactory );
      cdf = CompoundDataFactory.normalize( cdf );
      updateDatasourceTree();
    } else {  // user must have cancelled
      if ( editDataFactory != null ) {
        cdf.add( editDataFactory );
      }
    }

    setValid( validateStep() );
  }

  /**
   * @param o
   * @return a DataFactory that matches the type of the DataFactoryMetaData (o) if it exists in the CompoundDataFactory
   * (cdf), null if it doesn't exist.
   */
  private DataFactory grabAndRemoveEditDataFactory( final DataFactoryMetaData o ) {
    final String mdfactoryName = o.getName();
    for ( int i = 0; i < cdf.size(); i++ ) {
      final DataFactory df = cdf.getReference( i );
      final String dfClassName = df.getClass().getName();
      if ( mdfactoryName.equals( dfClassName ) ) {
        cdf.remove( i );
        return df;
      }
    }
    return null;
  }

  public void stepActivating() {
    super.stepActivating();
    cdf = (CompoundDataFactory) getEditorModel().getReportDefinition().getDataFactory();
    updateDatasourceTree();
    setValid( validateStep() );
  }

  public boolean stepDeactivating() {
    getEditorModel().getReportDefinition().setDataFactory( cdf );
    return super.stepDeactivating();
  }

  public void deleteDataFactory( final DataFactoryMetaData userObject ) {
    final int datasourceIndex = getDataFactoryForMeta( userObject );
    if ( datasourceIndex >= 0 ) {
      cdf.remove( getDataFactoryForMeta( userObject ) );
    }
  }

  public void deleteConnection( final DataFactory datafactory ) {
    cdf.remove( datafactory );
  }

  /**
   *
   */
  private void updateDatasourceTree() {
    final DatasourceModelNode newRoot =
      new DatasourceModelNode( DATASOURCES_ROOT_NODE_NAME, null, DATASOURCE_TYPE.ROOT );

    for ( int i = 0; i < cdf.size(); i++ ) {
      final DataFactory df = cdf.getReference( i );
      final DataFactoryMetaData dfmd = getMetaForDataFactory( df, dataFactoryMetas );
      if ( dfmd == null ) {
        continue;
      }

      DatasourceModelNode dfmdNode = findUserObjectInTree( dfmd, newRoot );
      if ( dfmdNode == null ) {
        dfmdNode =
          new DatasourceModelNode( dfmd.getDisplayName( Locale.getDefault() ), dfmd, DATASOURCE_TYPE.DATAFACTORY );
        newRoot.add( dfmdNode );
      }
      DatasourceModelNode dataSourceNode = null;
      final String connectionName = dfmd.getDisplayConnectionName( df );
      if ( connectionName != null ) {
        dataSourceNode = new DatasourceModelNode( connectionName, df, DATASOURCE_TYPE.CONNECTION );
      }
      if ( dataSourceNode != null ) {
        dfmdNode.add( dataSourceNode );
      }
      for ( final String queryName : df.getQueryNames() ) {
        final DatasourceModelNode queryNode = new DatasourceModelNode( queryName, null, DATASOURCE_TYPE.QUERY );
        if ( dataSourceNode != null ) {
          dataSourceNode.add( queryNode );
        } else {
          dfmdNode.add( queryNode );
        }
      }
    }
    this.setDataSourcesRoot( newRoot );
    final XulTree tree = (XulTree) getDocument().getElementById( DATASOURCES_TREE_ID );

    final String currentQuery = getCurrentQuery();
    final int selectedQueryRow = findRowForObject( getDataSourcesRoot(), currentQuery, new int[] { 0 } );
    if ( selectedQueryRow == -1 ) {
      final int[] selectedRows = new int[ 1 ];
      selectedRows[ 0 ] = selectedQueryRow - 1;  // have to subtract one for the (unshown) root
      tree.setSelectedRows( selectedRows );
    }
  }

  private int findRowForObject( final DatasourceModelNode startNode, final Object searchObj, final int[] index ) {
    if ( index == null || index.length != 1 ) {
      throw new IllegalArgumentException();
    }

    if ( searchObj == null ) {
      return -1;
    }
    // First try to match a user object if we have one
    if ( ObjectUtilities.equal( searchObj, startNode.getUserObject() ) ) {
      return index[ 0 ];
    }

    // Otherwise check the children

    for ( final DatasourceModelNode node : startNode ) {
      index[ 0 ] += 1;
      final int result = findRowForObject( node, searchObj, index );
      if ( result != -1 ) {
        return result;
      }
    }

    return -1;
  }

  private DatasourceModelNode findUserObjectInTree( final Object userObj, final DatasourceModelNode startNode ) {
    if ( userObj == null ) {
      throw new NullPointerException( "UserObject must not be null" );
    }
    if ( startNode == null ) {
      throw new NullPointerException( "StartNode must not be null" );
    }

    if ( userObj.equals( startNode.getUserObject() ) ) {
      return startNode;
    }
    for ( final DatasourceModelNode childNode : startNode ) {
      final DatasourceModelNode found = findUserObjectInTree( userObj, childNode );
      if ( found != null ) {
        return found;
      }
    }

    return null;
  }

  protected boolean validateStep() {
    // If we have no createdDataFactory and we don't have anything in the model then we can't continue
    final AbstractReportDefinition reportDefinition = getEditorModel().getReportDefinition();
    if ( reportDefinition.getDataFactory() == null ||
      StringUtils.isEmpty( reportDefinition.getQuery() ) ) {
      DebugLog.log( "Have no query or no datafactory " +
        reportDefinition.getDataFactory() + " " + reportDefinition.getQuery() );
      return false;
    }

    // if we have a DataFactory and a query make sure that they are contained in cdf.
    final String queryName = reportDefinition.getQuery();
    if ( cdf.isQueryExecutable( queryName, new StaticDataRow() ) == false ) {
      return false;
    }

    try {
      final AbstractReportDefinition abstractReportDefinition =
        (AbstractReportDefinition) reportDefinition.derive();
      abstractReportDefinition.setDataFactory( cdf.derive() );
      final DataSchemaModel schemaModel = WizardEditorModel.compileDataSchemaModel( abstractReportDefinition );
      return schemaModel.isValid();
    } catch ( Exception ee ) {
      getDesignTimeContext().userError( ee );
      return false;
    }
  }

  public void createPresentationComponent( final XulDomContainer mainWizardContainer ) throws XulException {
    super.createPresentationComponent( mainWizardContainer );

    mainWizardContainer.loadOverlay( DATASOURCE_AND_QUERY_STEP_OVERLAY );
    mainWizardContainer.addEventHandler( new DatasourceAndQueryStepHandler() );
    final XulListbox box = (XulListbox) getDocument().getElementById( DATASOURCE_SELECTIONS_BOX_ID );
    box.removeItems();
    for ( final XulEditorDataFactoryMetaData dfMeta : dataFactoryMetas ) {
      box.addItem( dfMeta );
    }
    box.addPropertyChangeListener( new SelectedIndexUpdateHandler() );
  }

  public List<XulEditorDataFactoryMetaData> getDataFactoryMetas() {
    return dataFactoryMetas;
  }

  public void setDataFactoryMetas( final ArrayList<XulEditorDataFactoryMetaData> datas ) {
    this.dataFactoryMetas = datas;
  }

  public String getCurrentQuery() {
    return getEditorModel().getReportDefinition().getQuery();
  }

  public void setCurrentQuery( final String currentQuery ) {
    final String oldQuery = getCurrentQuery();
    if ( !( currentQuery != null && currentQuery.equals( oldQuery ) ) ) {
      getEditorModel().getReportDefinition().setQuery( currentQuery );
      this.firePropertyChange( CURRENT_QUERY_PROPERTY_NAME, oldQuery, currentQuery );
      this.setValid( validateStep() );
    }
  }

  public DatasourceModelNode getDataSourcesRoot() {
    return dataSourcesRoot;
  }

  public void setDataSourcesRoot( final DatasourceModelNode dataSourcesRoot ) {
    final DatasourceModelNode oldDataSourcesRoot = this.dataSourcesRoot;
    this.dataSourcesRoot = dataSourcesRoot;

    this.firePropertyChange( DATASOURCES_ROOT_PROPERTY_NAME, oldDataSourcesRoot, dataSourcesRoot );
  }

  /* (non-Javadoc)
   * @see org.pentaho.reporting.engine.classic.wizard.ui.xul.components.WizardStep#getStepName()
   */
  public String getStepName() {
    return messages.getString( "DATASOURCE_AND_QUERY_STEP.Step_Name" ); //$NON-NLS-1$
  }


}

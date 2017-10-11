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

import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaModel;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributeContext;
import org.pentaho.reporting.engine.classic.wizard.model.DefaultDetailFieldDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.DefaultGroupDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.DetailFieldDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.GroupDefinition;
import org.pentaho.reporting.engine.classic.wizard.ui.xul.components.AbstractWizardStep;
import org.pentaho.reporting.engine.classic.wizard.ui.xul.util.FieldWrapper;
import org.pentaho.reporting.engine.classic.wizard.ui.xul.util.SourceFieldDefinition;
import org.pentaho.reporting.engine.classic.wizard.ui.xul.util.XulGroupDefinition;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.binding.Binding;
import org.pentaho.ui.xul.binding.Binding.Type;
import org.pentaho.ui.xul.binding.BindingConvertor;
import org.pentaho.ui.xul.containers.XulListbox;
import org.pentaho.ui.xul.impl.AbstractXulEventHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LayoutStep extends AbstractWizardStep {

  protected class SelectFieldsAndGroupsEventHandler extends AbstractXulEventHandler {
    protected SelectFieldsAndGroupsEventHandler() {
    }

    @Override
    public String getName() {
      return "layout_controller"; //$NON-NLS-1$
    }

    private int[] getNewSelections( final int[] oldSelectedRows, final DIRECTION direction ) {
      final int offset;
      offset = direction == DIRECTION.DOWN ? 1 : -1;

      // update the selection to move with the items
      final int[] newSelectedRows = new int[ oldSelectedRows.length ];
      for ( int i = 0; i < oldSelectedRows.length; i++ ) {
        newSelectedRows[ i ] = oldSelectedRows[ i ] + offset;
      }
      return newSelectedRows;
    }

    public void doMoveToGroups() {
      final XulListbox availableList = (XulListbox) getDocument().getElementById( AVAILABLE_COLUMNS_LIST_ID );
      final int[] selectedIndices = availableList.getSelectedIndices();
      final List<FieldWrapper> groups = getGroupFields();
      final DataSchema schema = getEditorModel().getDataSchema().getDataSchema();
      for ( final int i : selectedIndices ) {
        final SourceFieldDefinition group = getSelectableFields().get( i );
        final GroupDefinition xulGroup = new DefaultGroupDefinition();
        xulGroup.setField( group.getFieldName() );
        FieldWrapper wrapper = new FieldWrapper( xulGroup, schema );
        groups.add( wrapper );
        final DefaultGroupDefinition definition = (DefaultGroupDefinition) wrapper.getFieldDefinition();
        new XulGroupDefinition( definition, schema );

      }
      setGroupFields( groups );
    }

    public void doMoveToDetails() {
      final XulListbox availableList = (XulListbox) getDocument().getElementById( AVAILABLE_COLUMNS_LIST_ID );
      final int[] selectedIndices = availableList.getSelectedIndices();
      final List<FieldWrapper> details = getDetailFields();
      final DataSchema schema = getEditorModel().getDataSchema().getDataSchema();
      for ( final int i : selectedIndices ) {
        final SourceFieldDefinition field = getSelectableFields().get( i );
        final DetailFieldDefinition xulField = new DefaultDetailFieldDefinition();
        xulField.setField( field.getFieldName() );
        details.add( new FieldWrapper( xulField, schema ) );
      }
      setDetailFields( details );
    }

    public void doMoveUpSelectedGroupItems() {
      final List<FieldWrapper> groups = getGroupFields();
      final XulListbox groupList = (XulListbox) getDocument().getElementById( GROUP_FIELDS_LIST_ID );
      final int[] selectedIndices = groupList.getSelectedIndices();
      Arrays.sort( selectedIndices );
      for ( final int selectedRow : selectedIndices ) {
        final FieldWrapper group = groups.remove( selectedRow );
        groups.add( selectedRow - 1, group );
      }
      setGroupFields( groups );

      // update the selection to move with the items
      groupList.setSelectedIndices( getNewSelections( selectedIndices, DIRECTION.UP ) );
    }

    public void doMoveDownSelectedGroupItems() {
      final List<FieldWrapper> groups = getGroupFields();
      final XulListbox groupList = (XulListbox) getDocument().getElementById( GROUP_FIELDS_LIST_ID );
      final int[] selectedIndices = groupList.getSelectedIndices();
      Arrays.sort( selectedIndices );
      reverseArray( selectedIndices );
      for ( final int selectedRow : selectedIndices ) {
        final FieldWrapper group = groups.remove( selectedRow );
        groups.add( selectedRow + 1, group );
      }
      setGroupFields( groups );

      // update the selection to move with the items
      groupList.setSelectedIndices( getNewSelections( selectedIndices, DIRECTION.DOWN ) );
    }

    public void doRemoveSelectedGroupItems() {
      final List<FieldWrapper> groups = getGroupFields();
      final XulListbox groupList = (XulListbox) getDocument().getElementById( GROUP_FIELDS_LIST_ID );
      for ( int i = groupList.getSelectedIndices().length - 1; i >= 0; i-- ) { // Count from the end back
        groups.remove( groupList.getSelectedIndices()[ i ] );
      }
      setGroupFields( groups );
      groupList.setSelectedIndices( EMPTY_SELECTION ); // Clear any selections
    }

    public void doMoveUpSelectedDetailItems() {
      final List<FieldWrapper> details = getDetailFields();
      final XulListbox detailList = (XulListbox) getDocument().getElementById( DETAIL_FIELDS_LIST_ID );
      final int[] selectedIndices = detailList.getSelectedIndices();
      Arrays.sort( selectedIndices );
      for ( final int selectedRow : selectedIndices ) {
        final FieldWrapper detail = details.remove( selectedRow );
        details.add( selectedRow - 1, detail );
      }
      setDetailFields( details );

      // update the selection to move with the items
      detailList.setSelectedIndices( getNewSelections( selectedIndices, DIRECTION.UP ) );
    }

    public void doMoveDownSelectedDetailItems() {
      final List<FieldWrapper> details = getDetailFields();
      final XulListbox detailList = (XulListbox) getDocument().getElementById( DETAIL_FIELDS_LIST_ID );
      final int[] selectedIndices = detailList.getSelectedIndices();
      Arrays.sort( selectedIndices );
      reverseArray( selectedIndices );
      for ( final int selectedRow : selectedIndices ) {
        final FieldWrapper detail = details.remove( selectedRow );
        details.add( selectedRow + 1, detail );
      }
      setDetailFields( details );

      // update the selection to move with the items
      detailList.setSelectedIndices( getNewSelections( selectedIndices, DIRECTION.DOWN ) );
    }

    public void doRemoveSelectedDetailItems() {
      final List<FieldWrapper> details = getDetailFields();
      final XulListbox detailList = (XulListbox) getDocument().getElementById( DETAIL_FIELDS_LIST_ID );
      for ( int i = detailList.getSelectedIndices().length - 1; i >= 0; i-- ) { // Count from the end back
        details.remove( detailList.getSelectedIndices()[ i ] );
      }
      setDetailFields( details );
      detailList.setSelectedIndices( EMPTY_SELECTION ); // Clear any selections
    }

    private void reverseArray( final int[] target ) {
      for ( int i = 0; i < target.length / 2; i++ ) {
        final int temp = target[ i ];
        target[ i ] = target[ target.length - i - 1 ];
        target[ target.length - i - 1 ] = temp;
      }
    }
  }

  /**
   * @author wseyler
   */
  private static class ListSelectionToBooleanConverter extends BindingConvertor<int[], Boolean> {
    private ListSelectionToBooleanConverter() {
    }
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
      return null;
    }

  }

  /**
   * @author wseyler
   */
  private static class MoveUpBindingConverter extends BindingConvertor<int[], Boolean> {
    private MoveUpBindingConverter() {
    }

/* (non-Javadoc)
     * @see org.pentaho.ui.xul.binding.BindingConvertor#sourceToTarget(java.lang.Object)
     */

    @Override
    public Boolean sourceToTarget( final int[] value ) {
      Arrays.sort( value );
      return value.length > 0 && value[ 0 ] > 0;
    }

    /* (non-Javadoc)
     * @see org.pentaho.ui.xul.binding.BindingConvertor#targetToSource(java.lang.Object)
     */

    @Override
    public int[] targetToSource( final Boolean value ) {
      return null;
    }

  }

  /**
   * @author wseyler
   */
  private static class MoveDownBindingConverter extends BindingConvertor<int[], Boolean> {

    private XulListbox list;

    private MoveDownBindingConverter( final XulListbox list ) {
      this.list = list;
    }

    /* (non-Javadoc)
     * @see org.pentaho.ui.xul.binding.BindingConvertor#sourceToTarget(java.lang.Object)
     */

    @Override
    public Boolean sourceToTarget( final int[] value ) {
      Arrays.sort( value );
      return value.length > 0 && value[ value.length - 1 ] < list.getElements().size() - 1;
    }

    /* (non-Javadoc)
     * @see org.pentaho.ui.xul.binding.BindingConvertor#targetToSource(java.lang.Object)
     */

    @Override
    public int[] targetToSource( final Boolean value ) {
      return null;
    }

  }

  private enum DIRECTION {
    UP, DOWN
  }

  private static final String LAYOUT_STEP_OVERLAY =
    "org/pentaho/reporting/engine/classic/wizard/ui/xul/res/layout_step_Overlay.xul"; //$NON-NLS-1$

  private static final String ELEMENTS_PROPERTY_NAME = "elements"; //$NON-NLS-1$
  private static final String NOT_DISABLED_PROPERTY_NAME = "!disabled"; //$NON-NLS-1$
  private static final String SELECTABLE_FIELDS_PROPERTY_NAME = "selectableFields"; //$NON-NLS-1$
  private static final String GROUP_FIELDS_PROPERTY_NAME = "groupFields"; //$NON-NLS-1$
  private static final String DETAIL_FIELDS_PROPERTY_NAME = "detailFields"; //$NON-NLS-1$
  private static final String SELECTED_INDICES_PROPERTY_NAME = "selectedIndices"; //$NON-NLS-1$

  protected static final String AVAILABLE_COLUMNS_LIST_ID = "available_columns_list"; //$NON-NLS-1$
  protected static final String GROUP_FIELDS_LIST_ID = "group_fields_list"; //$NON-NLS-1$
  protected static final String DETAIL_FIELDS_LIST_ID = "detail_fields_list"; //$NON-NLS-1$
  private static final String MOVE_TO_GROUPS_BTN_ID = "move_to_groups_btn"; //$NON-NLS-1$
  private static final String MOVE_TO_DETAILS_BTN_ID = "move_to_details_btn"; //$NON-NLS-1$
  private static final String MOVE_GROUP_UP_BTN_ID = "move_group_up_btn"; //$NON-NLS-1$
  private static final String MOVE_GROUP_DOWN_BTN_ID = "move_group_down_btn"; //$NON-NLS-1$
  private static final String REMOVE_GROUP_ITEM_BTN_ID = "remove_group_item_btn"; //$NON-NLS-1$ 
  private static final String MOVE_DETAIL_UP_BTN_ID = "move_detail_up_btn"; //$NON-NLS-1$
  private static final String MOVE_DETAIL_DOWN_BTN_ID = "move_detail_down_btn"; //$NON-NLS-1$
  private static final String REMOVE_DETAIL_ITEM_BTN_ID = "remove_detail_item_btn"; //$NON-NLS-1$
  private static final String PREVIEW_BUTTON_ID = "layout_preview_btn"; //$NON-NLS-1$

  private Binding previewBinding;
  private Binding selectableFieldsBinding;
  private Binding groupsBinding;
  private Binding detailsBinding;

  private ArrayList<SourceFieldDefinition> selectableFields;
  protected static final int[] EMPTY_SELECTION = new int[ 0 ];

  public LayoutStep() {
    selectableFields = new ArrayList<SourceFieldDefinition>();
  }

  /* (non-Javadoc)
   * @see org.pentaho.reporting.engine.classic.wizard.ui.xul.components.WizardStep#initialize()
   */

  public void setBindings() {
    // Create the binding converters
    final ListSelectionToBooleanConverter listToBooleanConverter = new ListSelectionToBooleanConverter();
    final MoveUpBindingConverter moveUpBindingConverter = new MoveUpBindingConverter();

    // Bindings for the lists
    getBindingFactory().setBindingType( Type.BI_DIRECTIONAL );
    selectableFieldsBinding = getBindingFactory()
      .createBinding( this, SELECTABLE_FIELDS_PROPERTY_NAME, AVAILABLE_COLUMNS_LIST_ID, ELEMENTS_PROPERTY_NAME );
    groupsBinding = getBindingFactory()
      .createBinding( this, GROUP_FIELDS_PROPERTY_NAME, GROUP_FIELDS_LIST_ID, ELEMENTS_PROPERTY_NAME );
    detailsBinding = getBindingFactory()
      .createBinding( this, DETAIL_FIELDS_PROPERTY_NAME, DETAIL_FIELDS_LIST_ID, ELEMENTS_PROPERTY_NAME );

    // Bindings for the move to fields and groups buttons
    getBindingFactory().setBindingType( Type.ONE_WAY );
    getBindingFactory().createBinding( AVAILABLE_COLUMNS_LIST_ID, SELECTED_INDICES_PROPERTY_NAME, MOVE_TO_GROUPS_BTN_ID,
      NOT_DISABLED_PROPERTY_NAME, listToBooleanConverter );
    getBindingFactory()
      .createBinding( AVAILABLE_COLUMNS_LIST_ID, SELECTED_INDICES_PROPERTY_NAME, MOVE_TO_DETAILS_BTN_ID,
        NOT_DISABLED_PROPERTY_NAME, listToBooleanConverter );

    // Bindings for the buttons on the groups panel
    getBindingFactory().setBindingType( Type.ONE_WAY );
    getBindingFactory().createBinding( GROUP_FIELDS_LIST_ID, SELECTED_INDICES_PROPERTY_NAME, MOVE_GROUP_UP_BTN_ID,
      NOT_DISABLED_PROPERTY_NAME, moveUpBindingConverter );
    getBindingFactory().createBinding( GROUP_FIELDS_LIST_ID, SELECTED_INDICES_PROPERTY_NAME, MOVE_GROUP_DOWN_BTN_ID,
      NOT_DISABLED_PROPERTY_NAME,
      new MoveDownBindingConverter( (XulListbox) getDocument().getElementById( GROUP_FIELDS_LIST_ID ) ) );
    getBindingFactory().createBinding( GROUP_FIELDS_LIST_ID, SELECTED_INDICES_PROPERTY_NAME, REMOVE_GROUP_ITEM_BTN_ID,
      NOT_DISABLED_PROPERTY_NAME, listToBooleanConverter );

    // Bindings for the buttons on the detail panel
    getBindingFactory().setBindingType( Type.ONE_WAY );
    getBindingFactory().createBinding( DETAIL_FIELDS_LIST_ID, SELECTED_INDICES_PROPERTY_NAME, MOVE_DETAIL_UP_BTN_ID,
      NOT_DISABLED_PROPERTY_NAME, moveUpBindingConverter );
    getBindingFactory().createBinding( DETAIL_FIELDS_LIST_ID, SELECTED_INDICES_PROPERTY_NAME, MOVE_DETAIL_DOWN_BTN_ID,
      NOT_DISABLED_PROPERTY_NAME,
      new MoveDownBindingConverter( (XulListbox) getDocument().getElementById( DETAIL_FIELDS_LIST_ID ) ) );
    getBindingFactory().createBinding( DETAIL_FIELDS_LIST_ID, SELECTED_INDICES_PROPERTY_NAME, REMOVE_DETAIL_ITEM_BTN_ID,
      NOT_DISABLED_PROPERTY_NAME, listToBooleanConverter );

    // Binding for the preview button
    getBindingFactory().setBindingType( Type.ONE_WAY );
    previewBinding = getBindingFactory()
      .createBinding( this, PREVIEWABLE_PROPERTY_NAME, PREVIEW_BUTTON_ID, NOT_DISABLED_PROPERTY_NAME );
  }

  public void stepActivating() {
    super.stepActivating();
    try {
      previewBinding.fireSourceChanged();
      selectableFieldsBinding.fireSourceChanged();
      groupsBinding.fireSourceChanged();
      detailsBinding.fireSourceChanged();
    } catch ( Exception e ) {
      if ( getDesignTimeContext() != null ) {
        getDesignTimeContext().error( e );
      } else {
        DebugLog.log( e );
      }
    }

    if ( getEditorModel().isRelationalModel() ) {
      populateSourceList();
      setDetailFields( getDetailFields() );
      setGroupFields( getGroupFields() );
    } else {
      setValid( false );
    }
  }

  private void populateSourceList() {
    final DataSchemaModel dataSchemaModel = getEditorModel().getDataSchema();
    final DataSchema dataSchema = dataSchemaModel.getDataSchema();
    final String[] names = dataSchema.getNames();
    Arrays.sort( names );
    final ArrayList<SourceFieldDefinition> fields = new ArrayList<SourceFieldDefinition>();
    for ( int i = 0; i < names.length; i++ ) {
      final String fieldName = names[ i ];
      if ( fieldName == null ) {
        continue;
      }
      if ( isFilteredField( dataSchema, fieldName ) ) {
        continue;
      }

      final SourceFieldDefinition fieldDefinition = new SourceFieldDefinition( fieldName, dataSchema );
      fields.add( fieldDefinition );
    }
    this.setSelectableFields( fields );
  }

  private boolean isFilteredField( final DataSchema dataSchema, final String fieldName ) {
    final DefaultDataAttributeContext dac = new DefaultDataAttributeContext();
    final DataAttributes attributes = dataSchema.getAttributes( fieldName );
    final Object source = attributes.getMetaAttribute
      ( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.SOURCE, String.class, dac );
    if ( MetaAttributeNames.Core.SOURCE_VALUE_ENVIRONMENT.equals( source ) ) {
      return true;
    }
    if ( MetaAttributeNames.Core.SOURCE_VALUE_PARAMETER.equals( source ) ) {
      return true;
    }

    final Object indexColumn = attributes.getMetaAttribute
      ( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.INDEXED_COLUMN, Boolean.class, dac );
    if ( Boolean.TRUE.equals( indexColumn ) ) {
      return true;
    }
    return false;
  }

  public List<FieldWrapper> getDetailFields() {
    final ArrayList<FieldWrapper> fields = new ArrayList<FieldWrapper>();
    final DataSchema schema = getEditorModel().getDataSchema().getDataSchema();
    for ( final DetailFieldDefinition field : getEditorModel().getReportSpec().getDetailFieldDefinitions() ) {
      fields.add( new FieldWrapper( field, schema ) );
    }
    return fields;
  }

  public void setDetailFields( final List<FieldWrapper> detailFields ) {
    final List<FieldWrapper> oldFields = getDetailFields();
    final DetailFieldDefinition[] fields = new DetailFieldDefinition[ detailFields.size() ];
    for ( int i = 0; i < detailFields.size(); i++ ) {
      final FieldWrapper fieldWrapper = detailFields.get( i );
      fields[ i ] = (DetailFieldDefinition) fieldWrapper.getFieldDefinition();
    }

    getEditorModel().getReportSpec().setDetailFieldDefinitions( fields );

    // If we change the detail fields check and see if the list is populated
    // if it is we can enable preview
    // this should be refactored to a binding
    this.setPreviewable( !detailFields.isEmpty() );
    this.setFinishable( !detailFields.isEmpty() );
    this.setValid( !detailFields.isEmpty() );

    this.firePropertyChange( DETAIL_FIELDS_PROPERTY_NAME, oldFields, detailFields );
  }

  public List<FieldWrapper> getGroupFields() {
    final ArrayList<FieldWrapper> groups = new ArrayList<FieldWrapper>();
    final DataSchema schema = getEditorModel().getDataSchema().getDataSchema();
    for ( final GroupDefinition group : getEditorModel().getReportSpec().getGroupDefinitions() ) {
      groups.add( new FieldWrapper( group, schema ) );
    }
    return groups;
  }

  public void setGroupFields( final List<FieldWrapper> groupFields ) {
    final List<FieldWrapper> oldGroups = getGroupFields();
    final GroupDefinition[] fields = new GroupDefinition[ groupFields.size() ];
    for ( int i = 0; i < groupFields.size(); i++ ) {
      final FieldWrapper fieldWrapper = groupFields.get( i );
      fields[ i ] = (GroupDefinition) fieldWrapper.getFieldDefinition();
    }
    getEditorModel().getReportSpec().setGroupDefinitions( fields );

    this.firePropertyChange( GROUP_FIELDS_PROPERTY_NAME, oldGroups, groupFields );
  }

  /* (non-Javadoc)
   * @see org.pentaho.reporting.engine.classic.wizard.ui.xul.components.WizardStep#createPresentationComponent(org
   * .pentaho.ui.xul.XulDomContainer)
   */

  public void createPresentationComponent( final XulDomContainer mainWizardContainer ) throws XulException {
    super.createPresentationComponent( mainWizardContainer );

    mainWizardContainer.loadOverlay( LAYOUT_STEP_OVERLAY );
    mainWizardContainer.addEventHandler( new SelectFieldsAndGroupsEventHandler() );
  }

  public ArrayList<SourceFieldDefinition> getSelectableFields() {
    return selectableFields;
  }

  public void setSelectableFields( final ArrayList<SourceFieldDefinition> selectableFields ) {
    if ( selectableFields == null ) {
      throw new NullPointerException();
    }

    final ArrayList<SourceFieldDefinition> oldSelectableFields = this.selectableFields;

    //noinspection AssignmentToCollectionOrArrayFieldFromParameter
    this.selectableFields = selectableFields;

    this.firePropertyChange( SELECTABLE_FIELDS_PROPERTY_NAME, oldSelectableFields, this.selectableFields );
  }

  /* (non-Javadoc)
   * @see org.pentaho.reporting.engine.classic.wizard.ui.xul.components.WizardStep#getStepName()
   */

  public String getStepName() {
    return messages.getString( "LAYOUT_STEP.Step_Name" ); //$NON-NLS-1$
  }

}

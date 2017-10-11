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

import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.function.AggregationFunction;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.engine.classic.wizard.model.DefaultDetailFieldDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.DefaultGroupDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.DetailFieldDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.FieldDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.GroupDefinition;
import org.pentaho.reporting.engine.classic.wizard.ui.xul.components.AbstractWizardStep;
import org.pentaho.reporting.engine.classic.wizard.ui.xul.util.FieldWrapper;
import org.pentaho.reporting.engine.classic.wizard.ui.xul.util.XulDetailFieldDefinition;
import org.pentaho.reporting.engine.classic.wizard.ui.xul.util.XulGroupDefinition;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.binding.Binding;
import org.pentaho.ui.xul.binding.Binding.Type;
import org.pentaho.ui.xul.binding.BindingConvertor;
import org.pentaho.ui.xul.components.XulButton;
import org.pentaho.ui.xul.containers.XulDeck;
import org.pentaho.ui.xul.containers.XulListbox;
import org.pentaho.ui.xul.impl.AbstractXulEventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Todo: Document Me
 *
 * @author William Seyler
 */
public class FormatStep extends AbstractWizardStep {

  /**
   * @author wseyler
   *         <p/>
   *         Class that provides conversion between selections made on the Details or Group lists and handles updating
   *         of the bindings
   */
  private class FormatTypeBinding extends BindingConvertor<int[], Integer> {
    private XulListbox activeTree;
    private XulListbox inactiveTree;

    public FormatTypeBinding( final XulListbox activeTree, final XulListbox inactiveTree ) {
      if ( activeTree == null ) {
        throw new NullPointerException();
      }
      if ( inactiveTree == null ) {
        throw new NullPointerException();
      }
      this.activeTree = activeTree;
      this.inactiveTree = inactiveTree;
    }

    /* (non-Javadoc)
     * @see org.pentaho.ui.xul.binding.BindingConvertor#sourceToTarget(java.lang.Object)
     */
    @Override
    public Integer sourceToTarget( final int[] value ) {
      // Handles the case where there is no selection in either groups or details
      if ( ( activeTree.getSelectedIndices() == null || activeTree.getSelectedIndices().length < 1 ) &&
        ( inactiveTree.getSelectedIndices() == null || inactiveTree.getSelectedIndices().length < 1 ) ) {
        return 0;
      }
      // Handles case where nothing is selected in the target
      if ( value == null || value.length < 1 ) { // nothing changes
        final XulDeck deck = (XulDeck) getDocument().getElementById( FORMAT_DECK_ID );
        return deck.getSelectedIndex();
      }
      // Remove the selection from the opposite list
      inactiveTree.setSelectedIndices( new int[ 0 ] );
      // Update the bindings to the new selections
      updateBindings( activeTree );
      // Return index mapping of the current selected item.
      if ( activeTree.getId().equals( FORMAT_DETAILS_LIST_ID ) ) {
        return 1;
      }
      return 2;
    }

    /* (non-Javadoc)
     * @see org.pentaho.ui.xul.binding.BindingConvertor#targetToSource(java.lang.Object)
     */
    @Override
    public int[] targetToSource( final Integer value ) {
      // TODO Auto-generated method stub
      return null;
    }
  }


  /**
   * @author wseyler
   *         <p/>
   *         Provides binding converion between Class and Integer that represents that class in the GUI.
   */
  private class AggregationBindingConverter extends BindingConvertor<Class, Integer> {

    /* (non-Javadoc)
     * @see org.pentaho.ui.xul.binding.BindingConvertor#sourceToTarget(java.lang.Object)
     */
    @Override
    public Integer sourceToTarget( final Class value ) {
      if ( value == null ) {
        return 0;
      }

      for ( int i = 1; i < allExpressionMetaDatas.size(); i++ ) {
        final ExpressionMetaData data = allExpressionMetaDatas.get( i ).getWrappedObject();
        if ( data == null ) {
          return 0;
        }
        final Class testValue = data.getExpressionType();
        if ( value.equals( testValue ) ) {
          return i;
        }
      }
      return 0;
    }

    /* (non-Javadoc)
     * @see org.pentaho.ui.xul.binding.BindingConvertor#targetToSource(java.lang.Object)
     */
    @Override
    public Class targetToSource( final Integer value ) {
      if ( value == null ) {
        return null;
      }
      final ExpressionMetaData emd = allExpressionMetaDatas.get( value ).getWrappedObject();
      if ( emd == null ) {
        return null;
      }
      return emd.getExpressionType();
    }
  }

  /**
   * @author wseyler
   *         <p/>
   *         Wraps ExpressionMetaData so that it may be easily mainipulated by the gui.
   */
  private static class MetaDataWrapper {
    private ExpressionMetaData emd;

    public MetaDataWrapper( final ExpressionMetaData emd ) {
      this.emd = emd;
    }

    public ExpressionMetaData getWrappedObject() {
      return emd;
    }

    public String toString() {
      if ( emd == null ) {
        return messages.getString( "FORMAT_STEP.None" ); //$NON-NLS-1$
      }
      return emd.getDisplayName( Locale.getDefault() );
    }
  }

  protected class FormatStepHandler extends AbstractXulEventHandler {
    private static final String HANDLER_NAME = "format_step_controller"; //$NON-NLS-1$

    public String getName() {
      return HANDLER_NAME;
    }

    public void setAlignmentLeft() {
      if ( activeField == null ) {
        return;
      }
      final FieldDefinition fieldDefinition = activeField.getFieldDefinition();
      if ( fieldDefinition instanceof DetailFieldDefinition ) {
        final DetailFieldDefinition definition = (DetailFieldDefinition) fieldDefinition;
        definition.setHorizontalAlignment( ElementAlignment.LEFT );
        updateAlignmentButtons( ElementAlignment.LEFT );
      } else if ( fieldDefinition instanceof GroupDefinition ) {
        final GroupDefinition groupDefinition = (GroupDefinition) fieldDefinition;
        groupDefinition.setTotalsHorizontalAlignment( ElementAlignment.LEFT );
        updateAlignmentButtons( ElementAlignment.LEFT );
      }
    }

    public void setAlignmentCenter() {
      if ( activeField == null ) {
        return;
      }
      final FieldDefinition fieldDefinition = activeField.getFieldDefinition();

      if ( fieldDefinition instanceof DetailFieldDefinition ) {
        final DetailFieldDefinition definition = (DetailFieldDefinition) fieldDefinition;
        definition.setHorizontalAlignment( ElementAlignment.CENTER );
        updateAlignmentButtons( ElementAlignment.CENTER );
      } else if ( fieldDefinition instanceof GroupDefinition ) {
        final GroupDefinition groupDefinition = (GroupDefinition) fieldDefinition;
        groupDefinition.setTotalsHorizontalAlignment( ElementAlignment.CENTER );
        updateAlignmentButtons( ElementAlignment.CENTER );
      }
    }

    public void setAlignmentRight() {
      if ( activeField == null ) {
        return;
      }
      final FieldDefinition fieldDefinition = activeField.getFieldDefinition();

      if ( fieldDefinition instanceof DetailFieldDefinition ) {
        final DetailFieldDefinition definition = (DetailFieldDefinition) fieldDefinition;
        definition.setHorizontalAlignment( ElementAlignment.RIGHT );
        updateAlignmentButtons( ElementAlignment.RIGHT );
      } else if ( fieldDefinition instanceof GroupDefinition ) {
        final GroupDefinition groupDefinition = (GroupDefinition) fieldDefinition;
        groupDefinition.setTotalsHorizontalAlignment( ElementAlignment.RIGHT );
        updateAlignmentButtons( ElementAlignment.RIGHT );
      }
    }

    public void setAlignmentJustify() {
      if ( activeField == null ) {
        return;
      }
      final FieldDefinition fieldDefinition = activeField.getFieldDefinition();

      if ( fieldDefinition instanceof DetailFieldDefinition ) {
        final DetailFieldDefinition definition = (DetailFieldDefinition) fieldDefinition;
        definition.setHorizontalAlignment( ElementAlignment.JUSTIFY );
        updateAlignmentButtons( ElementAlignment.JUSTIFY );
      } else if ( fieldDefinition instanceof GroupDefinition ) {
        final GroupDefinition groupDefinition = (GroupDefinition) fieldDefinition;
        groupDefinition.setTotalsHorizontalAlignment( ElementAlignment.JUSTIFY );
        updateAlignmentButtons( ElementAlignment.JUSTIFY );
      }
    }
  }

  private static final String FORMAT_STEP_OVERLAY =
    "org/pentaho/reporting/engine/classic/wizard/ui/xul/res/format_step_Overlay.xul"; //$NON-NLS-1$

  // Property Names
  private static final String GROUP_FIELDS_PROPERTY_NAME = "groupFields"; //$NON-NLS-1$
  private static final String DETAIL_FIELDS_PROPERTY_NAME = "detailFields"; //$NON-NLS-1$
  private static final String ELEMENTS_PROPERTY_NAME = "elements"; //$NON-NLS-1$
  private static final String SELECTED_INDICES_PROPERTY_NAME = "selectedIndices"; //$NON-NLS-1$
  private static final String SELECTED_INDEX_PROPERTY_NAME = "selectedIndex"; //$NON-NLS-1$
  private static final String CHECKED_PROPERTY_NAME = "checked"; //$NON-NLS-1$
  private static final String ONLY_SHOW_CHANGING_VALUES_PROPERTY_NAME = "onlyShowChangingValues"; //$NON-NLS-1$
  private static final String WIDTH_PROPERTY_NAME = "width"; //$NON-NLS-1$
  private static final String VALUE_PROPERTY_NAME = "value"; //$NON-NLS-1$
  private static final String DISPLAY_NAME_PROPERTY_NAME = "displayName"; //$NON-NLS-1$
  private static final String DATA_FORMAT_PROPERTY_NAME = "dataFormat"; //$NON-NLS-1$
  private static final String ALL_EXPRESSIONS_META_DATAS = "allExpressionMetaDatas"; //$NON-NLS-1$
  private static final String AGGREGATION_FUNCTION_PROPERTY_NAME = "aggregationFunction"; //$NON-NLS-1$
  //  private static final String FIELD_PROPERTY_NAME = "field"; //$NON-NLS-1$
  private static final String GROUP_TOTALS_LABEL_PROPERTY_NAME = "groupTotalsLabel"; //$NON-NLS-1$
  private static final String SELECTED_ITEM_PROPERTY_NAME = "selectedItem"; //$NON-NLS-1$
  private static final String DISABLED_PROPERTY_ID = "disabled"; //$NON-NLS-1$

  // XUL GUI IDs
  private static final String FORMAT_DETAILS_LIST_ID = "format_details_list"; //$NON-NLS-1$
  private static final String FORMAT_GROUPS_LIST_ID = "format_groups_list"; //$NON-NLS-1$
  private static final String FORMAT_DECK_ID = "format_options_deck"; //$NON-NLS-1$
  private static final String FORMAT_DETAIL_DISTINCT_CB_ID = "format_detail_distinct_only_cb"; //$NON-NLS-1$
  private static final String FORMAT_DETAIL_WIDTH_SCALE_ID = "format_detail_width_scale"; //$NON-NLS-1$
  private static final String FORMAT_DETAIL_DISPLAY_NAME_TB_ID = "format_detail_display_name_tb"; //$NON-NLS-1$
  private static final String FORMAT_DETAIL_DATA_ML_ID = "format_detail_data_ml"; //$NON-NLS-1$
  private static final String FORMAT_DETAIL_AGGREGATION_ML_ID = "format_detail_aggregation_ml"; //$NON-NLS-1$
  private static final String FORMAT_GROUP_TOTALS_LABEL_TB_ID = "format_group_totals_tb"; //$NON-NLS-1$
  private static final String FORMAT_GROUP_DISPLAY_NAME_TB_ID = "format_group_display_name_tb"; //$NON-NLS-1$
  private static final String FORMAT_DETAIL_AUTO_WIDTH_CB_ID = "auto_width_cb"; //$NON-NLS-1$
  private static final String FORMAT_DETAIL_WIDTH_LABEL_ID = "format_detail_width_label"; //$NON-NLS-1$
  private static final String ALIGN_DETAIL_LEFT_BTN_ID = "align_detail_left"; //$NON-NLS-1$
  private static final String ALIGN_DETAIL_CENTER_BTN_ID = "align_detail_center"; //$NON-NLS-1$
  private static final String ALIGN_DETAIL_RIGHT_BTN_ID = "align_detail_right"; //$NON-NLS-1$
  private static final String ALIGN_DETAIL_JUSTIFY_BTN_ID = "align_detail_justify";

  /*
   * This element was commented to turn off the Group Summary Alignment.  Uncomment this to turn this
   * feature back on when the engine supports it.
   */
  //  private static final String ALIGN_GROUP_LEFT_BTN_ID = "align_group_left"; //$NON-NLS-1$
  //  private static final String ALIGN_GROUP_CENTER_BTN_ID = "align_group_center"; //$NON-NLS-1$
  //  private static final String ALIGN_GROUP_RIGHT_BTN_ID = "align_group_right"; //$NON-NLS-1$

  private FormatStepHandler formatStepHandler;

  private Binding groupBinding;
  private Binding detailBinding;
  private Binding detailExpressionsBinding; // , groupExpressionsBinding;

  private List<Binding> fieldAndGroupBindings;

  private List<MetaDataWrapper> allExpressionMetaDatas;

  private FieldWrapper activeField; // current field being worked on
  /**
   * @noinspection FieldCanBeLocal
   */
  private Object activeXulWrapper;

  public FormatStep() {
    super();

    allExpressionMetaDatas = new ArrayList<MetaDataWrapper>();
    this.allExpressionMetaDatas.add( new MetaDataWrapper( null ) ); // None
    for ( final ExpressionMetaData emd : ExpressionRegistry.getInstance().getAllExpressionMetaDatas() ) {
      if ( AggregationFunction.class.isAssignableFrom( emd.getExpressionType() ) ) {
        allExpressionMetaDatas.add( new MetaDataWrapper( emd ) );
      }
    }

    formatStepHandler = new FormatStepHandler();
    fieldAndGroupBindings = new ArrayList<Binding>();
  }

  protected FieldWrapper getActiveField() {
    return activeField;
  }

  protected Object getActiveXulWrapper() {
    return activeXulWrapper;
  }

  /* (non-Javadoc)
   * @see org.pentaho.reporting.engine.classic.wizard.ui.xul.components.WizardStep#setBindings()
   */
  public void setBindings() {
    getBindingFactory().setBindingType( Type.ONE_WAY );
    getBindingFactory().createBinding( FORMAT_DETAILS_LIST_ID, SELECTED_INDICES_PROPERTY_NAME, FORMAT_DECK_ID,
      SELECTED_INDEX_PROPERTY_NAME,
      new FormatTypeBinding( (XulListbox) getDocument().getElementById( FORMAT_DETAILS_LIST_ID ),
        (XulListbox) getDocument().getElementById( FORMAT_GROUPS_LIST_ID ) ) );
    getBindingFactory().createBinding( FORMAT_GROUPS_LIST_ID, SELECTED_INDICES_PROPERTY_NAME, FORMAT_DECK_ID,
      SELECTED_INDEX_PROPERTY_NAME,
      new FormatTypeBinding( (XulListbox) getDocument().getElementById( FORMAT_GROUPS_LIST_ID ),
        (XulListbox) getDocument().getElementById( FORMAT_DETAILS_LIST_ID ) ) );
    groupBinding = getBindingFactory()
      .createBinding( this, GROUP_FIELDS_PROPERTY_NAME, FORMAT_GROUPS_LIST_ID, ELEMENTS_PROPERTY_NAME );
    detailBinding = getBindingFactory()
      .createBinding( this, DETAIL_FIELDS_PROPERTY_NAME, FORMAT_DETAILS_LIST_ID, ELEMENTS_PROPERTY_NAME );
    detailExpressionsBinding = getBindingFactory()
      .createBinding( this, ALL_EXPRESSIONS_META_DATAS, FORMAT_DETAIL_AGGREGATION_ML_ID, ELEMENTS_PROPERTY_NAME );
  }

  /* (non-Javadoc)
   * @see org.pentaho.reporting.engine.classic.wizard.ui.xul.components.WizardStep#stepActivating()
   */
  public void stepActivating() {
    super.stepActivating();
    this.setFinishable( true ); // there is nothing we can do in this step to keep
    this.setPreviewable( true ); // from previewing or finishing.

    final XulListbox groupList = (XulListbox) getDocument().getElementById( FORMAT_GROUPS_LIST_ID );
    final XulListbox detailList = (XulListbox) getDocument().getElementById( FORMAT_DETAILS_LIST_ID );
    try {
      groupBinding.fireSourceChanged();
      detailBinding.fireSourceChanged();
      detailExpressionsBinding.fireSourceChanged();
      //      groupExpressionsBinding.fireSourceChanged();

      if ( groupList.getRowCount() > 0 ) {
        groupList.setSelectedIndex( 0 );
      } else {
        detailList.setSelectedIndex( 0 );
      }
    } catch ( Exception e ) {
      getDesignTimeContext().error( e );
    }
  }

  public boolean stepDeactivating() {
    super.stepDeactivating();
    resetFieldAndGroupBindings();

    XulListbox list = (XulListbox) getDocument().getElementById( FORMAT_DETAILS_LIST_ID );
    list.setSelectedIndices( new int[ 0 ] );
    list = (XulListbox) getDocument().getElementById( FORMAT_GROUPS_LIST_ID );
    list.setSelectedIndices( new int[ 0 ] );

    return true;
  }

  /**
   *
   */
  private void resetFieldAndGroupBindings() {
    // Get rid of all the old bindings
    for ( final Binding binding : fieldAndGroupBindings ) {
      binding.destroyBindings();
    }
    fieldAndGroupBindings.clear();
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

    this.firePropertyChange( DETAIL_FIELDS_PROPERTY_NAME, oldFields, detailFields );
  }

  /* (non-Javadoc)
   * @see org.pentaho.reporting.engine.classic.wizard.ui.xul.components.WizardStep#createPresentationComponent(org
   * .pentaho.ui.xul.XulDomContainer)
   */
  public void createPresentationComponent( final XulDomContainer mainWizardContainer ) throws XulException {
    super.createPresentationComponent( mainWizardContainer );

    mainWizardContainer.loadOverlay( FORMAT_STEP_OVERLAY );

    // Add event handlers
    mainWizardContainer.addEventHandler( formatStepHandler );
  }

  /* (non-Javadoc)
   * @see org.pentaho.reporting.engine.classic.wizard.ui.xul.components.WizardStep#getStepName()
   */
  public String getStepName() {
    return messages.getString( "FORMAT_STEP.Step_Name" ); //$NON-NLS-1$
  }

  /**
   * @param activeTree Updates the bindings based on which list contains the currently selected item (either Detail or
   *                   Group) The selected items fields are bound to their associated gui editor.
   */
  protected void updateBindings( final XulListbox activeTree ) {
    // Get rid of all the old bindings
    resetFieldAndGroupBindings();

    // Set the new bindings based on the item type (group or detail) selected
    getBindingFactory().setBindingType( Type.BI_DIRECTIONAL );
    final int[] selectedIndices = activeTree.getSelectedIndices();
    if ( selectedIndices == null || selectedIndices.length == 0 ) {
      activeField = null;
      activeXulWrapper = null;
      return;
    }

    if ( activeTree.getId().equals( FORMAT_DETAILS_LIST_ID ) ) { // We need to update the bindings for the detail list
      activeField = getDetailFields().get( selectedIndices[ 0 ] );
      final XulDetailFieldDefinition fieldDef = new XulDetailFieldDefinition
        ( (DefaultDetailFieldDefinition) activeField.getFieldDefinition(),
          getEditorModel().getDataSchema().getDataSchema() );
      activeXulWrapper = fieldDef;

      // Set the bindings on this field definition
      getBindingFactory().setBindingType( Type.ONE_WAY );
      fieldAndGroupBindings.add( getBindingFactory()
        .createBinding( FORMAT_DETAIL_WIDTH_SCALE_ID, VALUE_PROPERTY_NAME, FORMAT_DETAIL_WIDTH_LABEL_ID,
          VALUE_PROPERTY_NAME, new IntegerToStringBindingConvertor() ) );
      fieldAndGroupBindings.add( getBindingFactory()
        .createBinding( FORMAT_DETAIL_AUTO_WIDTH_CB_ID, CHECKED_PROPERTY_NAME, FORMAT_DETAIL_WIDTH_SCALE_ID,
          DISABLED_PROPERTY_ID ) );

      getBindingFactory().setBindingType( Type.BI_DIRECTIONAL );
      fieldAndGroupBindings.add( getBindingFactory()
        .createBinding( fieldDef, ONLY_SHOW_CHANGING_VALUES_PROPERTY_NAME, FORMAT_DETAIL_DISTINCT_CB_ID,
          CHECKED_PROPERTY_NAME, new BooleanBindingConvertor() ) );
      fieldAndGroupBindings.add( getBindingFactory()
        .createBinding( fieldDef, WIDTH_PROPERTY_NAME, FORMAT_DETAIL_WIDTH_SCALE_ID, VALUE_PROPERTY_NAME,
          new LengthToIntegerBindingConverter() ) );
      fieldAndGroupBindings.add( getBindingFactory()
        .createBinding( fieldDef, DISPLAY_NAME_PROPERTY_NAME, FORMAT_DETAIL_DISPLAY_NAME_TB_ID, VALUE_PROPERTY_NAME ) );
      fieldAndGroupBindings.add( getBindingFactory()
        .createBinding( fieldDef, DATA_FORMAT_PROPERTY_NAME, FORMAT_DETAIL_DATA_ML_ID, SELECTED_ITEM_PROPERTY_NAME,
          new DataFormatBindingConvertor() ) );
      fieldAndGroupBindings.add( getBindingFactory()
        .createBinding( fieldDef, AGGREGATION_FUNCTION_PROPERTY_NAME, FORMAT_DETAIL_AGGREGATION_ML_ID,
          SELECTED_INDEX_PROPERTY_NAME, new AggregationBindingConverter() ) );
      fieldAndGroupBindings.add( getBindingFactory()
        .createBinding( fieldDef, WIDTH_PROPERTY_NAME, FORMAT_DETAIL_AUTO_WIDTH_CB_ID, CHECKED_PROPERTY_NAME,
          new AutoWidthBindingConverter( fieldDef.getWidth() ) ) );
      updateAlignmentButtons( fieldDef.getHorizontalAlignment() );
    } else if ( activeTree.getId()
      .equals( FORMAT_GROUPS_LIST_ID ) ) { // We need to update the bindings for the groups list
      if ( getGroupFields().size() > 0 ) {
        activeField = getGroupFields().get( selectedIndices[ 0 ] );
        final DefaultGroupDefinition definition = (DefaultGroupDefinition) activeField.getFieldDefinition();
        final XulGroupDefinition groupDef =
          new XulGroupDefinition( definition, getEditorModel().getDataSchema().getDataSchema() );
        activeXulWrapper = groupDef;

        // Set the bindings on this group definition
        fieldAndGroupBindings.add( getBindingFactory()
          .createBinding( groupDef, GROUP_TOTALS_LABEL_PROPERTY_NAME, FORMAT_GROUP_TOTALS_LABEL_TB_ID,
            VALUE_PROPERTY_NAME ) );
        fieldAndGroupBindings.add( getBindingFactory()
          .createBinding( groupDef, DISPLAY_NAME_PROPERTY_NAME, FORMAT_GROUP_DISPLAY_NAME_TB_ID,
            VALUE_PROPERTY_NAME ) );
        final ElementAlignment alignment = groupDef.getTotalsHorizontalAlignment();
      
      /*
       * This element was commented to turn off the Group Summary Alignment.  Uncomment this to turn this
       * feature back on when the engine supports it.
       */
        //      if (alignment == null || alignment.equals(ElementAlignment.LEFT))
        //      {
        //        XulButton button = (XulButton) getDocument().getElementById(ALIGN_GROUP_LEFT_BTN_ID);
        //        button.setSelected(true);
        //      }
        //      else if (alignment.equals(ElementAlignment.CENTER))
        //      {
        //        XulButton button = (XulButton) getDocument().getElementById(ALIGN_GROUP_CENTER_BTN_ID);
        //        button.setSelected(true);
        //      }
        //      else if (alignment.equals(ElementAlignment.RIGHT))
        //      {
        //        XulButton button = (XulButton) getDocument().getElementById(ALIGN_GROUP_RIGHT_BTN_ID);
        //        button.setSelected(true);
        //      }
      }
    }

    // Fire the bindings set up above to synch the GUI
    // TODO Better error handling
    for ( final Binding binding : fieldAndGroupBindings ) {
      try {
        binding.fireSourceChanged();
      } catch ( Exception e ) {
        getDesignTimeContext().error( e );
      }
    }
  }

  private void updateAlignmentButtons( final ElementAlignment align ) {
    final XulButton alignLeftBtn = (XulButton) getDocument().getElementById( ALIGN_DETAIL_LEFT_BTN_ID );
    final XulButton alignCenterBtn = (XulButton) getDocument().getElementById( ALIGN_DETAIL_CENTER_BTN_ID );
    final XulButton alignRightBtn = (XulButton) getDocument().getElementById( ALIGN_DETAIL_RIGHT_BTN_ID );
    final XulButton alignJustifyBtn = (XulButton) getDocument().getElementById( ALIGN_DETAIL_JUSTIFY_BTN_ID );

    alignLeftBtn.setSelected( align == ElementAlignment.LEFT );
    alignCenterBtn.setSelected( align == ElementAlignment.CENTER );
    alignRightBtn.setSelected( align == ElementAlignment.RIGHT );
    alignJustifyBtn.setSelected( align == ElementAlignment.JUSTIFY );
  }

  /**
   * @return a list of MetaDataWrappers
   */
  public List<MetaDataWrapper> getAllExpressionMetaDatas() {
    return allExpressionMetaDatas;
  }


}

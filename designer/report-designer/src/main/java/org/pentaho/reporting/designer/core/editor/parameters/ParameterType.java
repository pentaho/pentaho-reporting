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

package org.pentaho.reporting.designer.core.editor.parameters;

import org.pentaho.reporting.engine.classic.core.parameters.ParameterAttributeNames;

import javax.swing.*;

/**
 * Todo: Document me!
 * <p/>
 * Date: 10.05.2010 Time: 16:16:27
 *
 * @author Thomas Morgner.
 */
public class ParameterType {
  private String displayName;
  private boolean multiSelection;
  private boolean queryOptional;
  private String internalName;

  public ParameterType( final String internalName, final String displayName,
                        final boolean multiSelection, final boolean queryOptional ) {
    this.internalName = internalName;
    this.displayName = displayName;
    this.multiSelection = multiSelection;
    this.queryOptional = queryOptional;
  }

  public boolean isHasVisibleItems() {
    return "list".equals( internalName );
  }

  public boolean isQueryOptional() {
    return queryOptional;
  }

  public String getDisplayName() {
    return displayName;
  }

  public boolean isMultiSelection() {
    return multiSelection;
  }

  public String getInternalName() {
    return internalName;
  }

  public String toString() {
    return displayName;
  }

  public String getLayout() {
    if ( ParameterAttributeNames.Core.TYPE_TOGGLEBUTTON.equals( internalName ) ) {
      return ParameterAttributeNames.Core.LAYOUT_HORIZONTAL;
    }
    if ( ParameterAttributeNames.Core.TYPE_RADIO.equals( internalName ) ||
      ParameterAttributeNames.Core.TYPE_CHECKBOX.equals( internalName ) ) {
      return ParameterAttributeNames.Core.LAYOUT_VERTICAL;
    }
    return null;
  }

  public static DefaultComboBoxModel createParameterTypesModel() {
    final DefaultComboBoxModel model = new DefaultComboBoxModel();
    model.addElement( null );
    model.addElement( new ParameterType( ParameterAttributeNames.Core.TYPE_DROPDOWN,
      Messages.getString( "ParameterDialog.ParameterType.DropDown" ), false, false ) );
    model.addElement( new ParameterType( ParameterAttributeNames.Core.TYPE_LIST,
      Messages.getString( "ParameterDialog.ParameterType.SingleValueList" ), false, false ) );
    model.addElement( new ParameterType( ParameterAttributeNames.Core.TYPE_LIST,
      Messages.getString( "ParameterDialog.ParameterType.MultiValueList" ), true, false ) );
    model.addElement( new ParameterType( ParameterAttributeNames.Core.TYPE_RADIO,
      Messages.getString( "ParameterDialog.ParameterType.RadioButton" ), false, false ) );
    model.addElement( new ParameterType( ParameterAttributeNames.Core.TYPE_CHECKBOX,
      Messages.getString( "ParameterDialog.ParameterType.CheckBox" ), true, false ) );
    model.addElement( new ParameterType( ParameterAttributeNames.Core.TYPE_TOGGLEBUTTON,
      Messages.getString( "ParameterDialog.ParameterType.SingleSelectionButton" ), false, false ) );
    model.addElement( new ParameterType( ParameterAttributeNames.Core.TYPE_TOGGLEBUTTON,
      Messages.getString( "ParameterDialog.ParameterType.MultiSelectionButton" ), true, false ) );
    model.addElement( new ParameterType( ParameterAttributeNames.Core.TYPE_TEXTBOX,
      Messages.getString( "ParameterDialog.ParameterType.TextBox" ), false, true ) );
    model.addElement( new ParameterType( ParameterAttributeNames.Core.TYPE_MULTILINE,
      Messages.getString( "ParameterDialog.ParameterType.TextArea" ), false, true ) );
    model.addElement( new ParameterType( ParameterAttributeNames.Core.TYPE_DATEPICKER,
      Messages.getString( "ParameterDialog.ParameterType.DatePicker" ), false, true ) );
    return model;
  }
}

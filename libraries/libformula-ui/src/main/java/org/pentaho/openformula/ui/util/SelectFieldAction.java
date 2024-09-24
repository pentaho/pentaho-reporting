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

package org.pentaho.openformula.ui.util;

import org.pentaho.openformula.ui.DefaultFunctionParameterEditor;
import org.pentaho.openformula.ui.FieldDefinition;
import org.pentaho.openformula.ui.FieldDefinitionSource;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;

public class SelectFieldAction extends AbstractAction {
  private PropertyChangeListener selectorUpdateHandler;
  private FieldDefinitionSource fieldDefinitionSource;
  private FieldSelectorDialog fieldSelectorDialog;
  private Component parent;
  private Component focusReturn;

  public SelectFieldAction( final Component parent,
                            final PropertyChangeListener selectorUpdateHandler,
                            final FieldDefinitionSource fieldDefinitionSource ) {
    if ( fieldDefinitionSource == null ) {
      throw new NullPointerException();
    }
    if ( selectorUpdateHandler == null ) {
      throw new NullPointerException();
    }

    this.parent = parent;
    this.selectorUpdateHandler = selectorUpdateHandler;
    this.fieldDefinitionSource = fieldDefinitionSource;
    final URL resource = DefaultFunctionParameterEditor.class.getResource
      ( "/org/pentaho/openformula/ui/images/field.gif" );  //NON-NLS
    if ( resource != null ) {
      final Icon icon = new ImageIcon( resource );
      putValue( Action.SMALL_ICON, icon );
    } else {
      putValue( Action.NAME, ".." );
    }
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    if ( fieldSelectorDialog == null ) {
      final Window w = LibSwingUtil.getWindowAncestor( parent );
      if ( w instanceof Frame ) {
        this.fieldSelectorDialog = new FieldSelectorDialog( (Frame) w );
        LibSwingUtil.positionDialogRelativeToParent( this.fieldSelectorDialog, 0.5, 0.5 );
      } else if ( w instanceof Dialog ) {
        this.fieldSelectorDialog = new FieldSelectorDialog( (Dialog) w );
        LibSwingUtil.positionDialogRelativeToParent( this.fieldSelectorDialog, 0.5, 0.5 );
      } else {
        this.fieldSelectorDialog = new FieldSelectorDialog();
        LibSwingUtil.positionDialogRelativeToParent( this.fieldSelectorDialog, 0.5, 0.5 );
      }
    }

    final FieldDefinition[] fields = fieldDefinitionSource.getFields();
    if ( fields != null ) {
      this.fieldSelectorDialog.setFields( fields );
    }

    fieldSelectorDialog.setFocusReturn( focusReturn );
    fieldSelectorDialog.removePropertyChangeListener
      ( FieldSelectorDialog.SELECTED_DEFINITION_PROPERTY, selectorUpdateHandler );
    fieldSelectorDialog.setSelectedDefinition( null );
    fieldSelectorDialog.addPropertyChangeListener( FieldSelectorDialog.SELECTED_DEFINITION_PROPERTY,
      selectorUpdateHandler );
    fieldSelectorDialog.setVisible( true );
  }

  public void setFocusReturn( Component component ) {
    focusReturn = component;
  }

  public void dispose() {
    if ( fieldSelectorDialog != null ) {
      fieldSelectorDialog.dispose();
    }
  }
}

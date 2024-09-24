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

package org.pentaho.reporting.designer.core.util;

import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Locale;

public class ExpressionComboBoxEditor implements ComboBoxEditor {
  private static class BorderlessTextField extends JTextField {
    private BorderlessTextField() {
    }

    public void setText( final String s ) {
      if ( getText().equals( s ) ) {
        return;
      }
      super.setText( s );
    }

    public void setBorder( final Border b ) {
      // ignore any request to change the border.
    }
  }

  private EventListenerList listenerList;
  private JTextField textField;
  private Object value;
  private String orginalText;

  public ExpressionComboBoxEditor() {
    this( false );
  }

  public ExpressionComboBoxEditor( final boolean withBorder ) {
    this.listenerList = new EventListenerList();
    if ( withBorder ) {
      this.textField = new JTextField();
    } else {
      this.textField = new BorderlessTextField();
    }
    this.textField.setDisabledTextColor( textField.getForeground() );
  }

  /**
   * Return the component that should be added to the tree hierarchy for this editor
   */
  public Component getEditorComponent() {
    return textField;
  }

  /**
   * Set the item that should be edited. Cancel any editing if necessary *
   */
  public void setItem( final Object anObject ) {
    if ( anObject instanceof FormulaExpression ) {
      final FormulaExpression fe = (FormulaExpression) anObject;
      this.value = fe;
      this.textField.setEnabled( true );
      this.textField.setText( fe.getFormula() );
    } else if ( anObject instanceof Expression ) {
      this.value = anObject;
      this.textField.setEnabled( false );
      if ( ExpressionRegistry.getInstance().isExpressionRegistered( value.getClass().getName() ) ) {
        final ExpressionMetaData data =
          ExpressionRegistry.getInstance().getExpressionMetaData( value.getClass().getName() );
        this.textField.setText( data.getDisplayName( Locale.getDefault() ) );
      } else {
        this.textField.setText( value.getClass().getName() );
      }
    } else if ( anObject instanceof ExpressionMetaData ) {
      final ExpressionMetaData emd = (ExpressionMetaData) anObject;
      this.value = anObject;
      this.textField.setEnabled( false );
      this.textField.setText( emd.getDisplayName( Locale.getDefault() ) );
    } else if ( anObject != null ) {
      DebugLog.log( "ExpressionComboBoxEditor: Invalid object encountered: " + anObject ); // NON-NLS
      this.value = null;
      this.textField.setEnabled( false );
      this.textField.setText( "" );
    } else {
      this.value = new FormulaExpression();
      this.textField.setEnabled( true );
      this.textField.setText( "" );
    }

    this.orginalText = this.textField.getText();
  }

  /**
   * Return the edited item *
   */
  public Object getItem() {
    if ( value instanceof FormulaExpression ) {
      final String editorText = textField.getText();
      if ( editorText.trim().length() == 0 ) {
        return null;
      }
      if ( ObjectUtilities.equal( orginalText, editorText ) ) {
        return value;
      }
      final FormulaExpression ofe = (FormulaExpression) value;
      final FormulaExpression fe = (FormulaExpression) ofe.getInstance();
      fe.setFormula( editorText );
      return fe;
    }
    return value;
  }

  /**
   * Ask the editor to start editing and to select everything *
   */
  public void selectAll() {
    this.textField.requestFocus();
    this.textField.select( 0, this.textField.getText().length() );
  }

  /**
   * Add an ActionListener. An action event is generated when the edited item changes *
   */
  public void addActionListener( final ActionListener l ) {
    listenerList.add( ActionListener.class, l );
  }

  /**
   * Remove an ActionListener *
   */
  public void removeActionListener( final ActionListener l ) {
    listenerList.remove( ActionListener.class, l );
  }
}

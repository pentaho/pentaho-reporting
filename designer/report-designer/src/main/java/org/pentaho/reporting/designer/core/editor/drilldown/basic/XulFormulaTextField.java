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

package org.pentaho.reporting.designer.core.editor.drilldown.basic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.designer.core.util.FormulaEditorPanel;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.dom.Element;
import org.pentaho.ui.xul.swing.AbstractSwingContainer;
import org.pentaho.ui.xul.swing.tags.SwingListitem;
import org.pentaho.ui.xul.util.TextType;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * A xulified version of the formula-input.
 *
 * @author Thomas Morgner.
 */
public class XulFormulaTextField extends AbstractSwingContainer implements XulComponent {
  private class KeyInputHandler implements KeyListener {
    public void keyPressed( final KeyEvent e ) {
      oldValue = textField.getFormula();
    }

    public void keyReleased( final KeyEvent e ) {
      if ( oldValue != null && !oldValue.equals( textField.getFormula() ) ) {
        changeSupport.firePropertyChange( "value", oldValue, getValue() );
        oldValue = textField.getFormula();
      } else if ( oldValue == null ) {
        //AWT error where sometimes the keyReleased is fired before keyPressed.
        oldValue = textField.getFormula();
      } else {
        logger.debug( "Special key pressed, ignoring" );
      }
    }

    public void keyTyped( final KeyEvent e ) {
    }

  }

  private class MethodInvokeHandler extends KeyAdapter {
    private final String method;

    public MethodInvokeHandler( final String method ) {
      this.method = method;
    }

    public void keyReleased( final KeyEvent e ) {
      invoke( method );
    }
  }

  private class ChangeEventForwarder implements PropertyChangeListener {
    private ChangeEventForwarder() {
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public void propertyChange( final PropertyChangeEvent evt ) {
      XulFormulaTextField.this.value = textField.getFormula();

      changeSupport.firePropertyChange( "value", evt.getOldValue(), evt.getNewValue() );
    }
  }

  private static final Log logger = LogFactory.getLog( XulFormulaTextField.class );

  private FormulaEditorPanel textField;
  boolean disabled;
  private String value;
  private boolean readonly;
  private TextType type;
  private String onInput;
  private int maxlength;
  private String oldValue;

  public XulFormulaTextField( final Element self,
                              final XulComponent parent,
                              final XulDomContainer domContainer,
                              final String tagName ) {
    super( tagName );
    setManagedObject( null );
    disabled = false;
    value = "";
    type = TextType.NORMAL;
    readonly = false;
    maxlength = -1;
    oldValue = null;
  }

  public String getValue() {
    return value;
  }

  public void setValue( final String text ) {
    final String oldVal = this.value;
    if ( textField != null && ObjectUtilities.equal( text, textField.getFormula() ) ) {
      return;
    }
    if ( textField != null ) {
      textField.setFormula( text );
    }
    this.value = text;
    if ( text != null || oldVal != null ) {
      changeSupport.firePropertyChange( "value", oldVal, text );
    }
  }

  public void layout() {
    final ArrayList<String> list = new ArrayList<String>();
    for ( final Element comp : getChildNodes() ) {
      if ( comp instanceof SwingListitem ) {
        final SwingListitem swingListitem = (SwingListitem) comp;
        list.add( (String) swingListitem.getValue() );
        logger.info( "added tag to formula editor" );
      }
    }

    final FormulaEditorPanel panel = (FormulaEditorPanel) getManagedObject();
    panel.setTags( list.toArray( new String[ list.size() ] ) );
  }

  public int getMaxlength() {
    return maxlength;
  }

  public boolean isDisabled() {
    return this.disabled;
  }

  public void setDisabled( final boolean dis ) {
    final boolean oldValue = this.disabled;
    this.disabled = dis;
    if ( textField != null ) {
      textField.setEnabled( !dis );
    }
    changeSupport.firePropertyChange( "disabled", oldValue, dis );
  }

  public void setMaxlength( final int length ) {
    maxlength = length;
  }

  public boolean isReadonly() {
    return readonly;
  }

  public void setReadonly( final boolean readOnly ) {
    this.readonly = readOnly;
  }

  public String getType() {
    if ( type == null ) {
      return null;
    }

    return type.toString();
  }

  public void selectAll() {
    textField.selectAll();
  }

  public void setFocus() {

  }

  public Object getTextControl() {
    return getManagedObject();
  }

  @Override
  public Object getManagedObject() {
    if ( super.getManagedObject() == null ) {
      textField = new FormulaEditorPanel();
      textField.setFormula( value );
      textField.setPreferredSize( new Dimension( 150, textField.getPreferredSize().height ) );
      textField.setMinimumSize( new Dimension( textField.getPreferredSize().width,
        textField.getPreferredSize().height ) );
      textField.setEditable( !readonly );
      textField.setEnabled( !disabled );
      textField.addFormulaKeyListener( new KeyInputHandler() );
      textField.addPropertyChangeListener( "formula", new ChangeEventForwarder() );
      setManagedObject( textField );
    }

    textField.setToolTipText( this.getTooltiptext() );
    return super.getManagedObject();

  }

  public void setOninput( final String method ) {
    if ( textField != null ) {
      onInput = method;
      textField.addFormulaKeyListener( new MethodInvokeHandler( method ) );
    } else { //Not instantiated, save for later
      onInput = method;
    }
  }

  public String getOninput() {
    return onInput;
  }
}

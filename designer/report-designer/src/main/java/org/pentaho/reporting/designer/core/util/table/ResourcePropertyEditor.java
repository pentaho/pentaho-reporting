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

package org.pentaho.reporting.designer.core.util.table;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ClassicEngineFactoryParameters;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.propertyeditors.AdvancedPropertyEditor;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Property editor for resource properties.
 *
 * @author Thomas Morgner
 * @author David Kincade
 */
public class ResourcePropertyEditor implements AdvancedPropertyEditor {
  private PropertyChangeSupport propertyChangeSupport;
  private ResourcePropertyEditorComponent editorComponent;
  private Object currentValue;

  /**
   * Creates a ReportPropertyEditor while specifying a ReportRenderContext.
   *
   * @param reportRenderContext the current render context.
   */
  public ResourcePropertyEditor( final ReportDocumentContext reportRenderContext ) {
    this.propertyChangeSupport = new PropertyChangeSupport( this );
    this.editorComponent = new ResourcePropertyEditorComponent( reportRenderContext );
    this.editorComponent.addPropertyChangeListener( new ComponentChangeListener() );
  }

  /**
   * Sets the value as text
   */
  public void setAsText( final String text ) throws IllegalArgumentException {
    setValue( text );
  }

  /**
   * Sets the value - we need to handle all the possible datatypes that may be given to us.
   */
  public void setValue( final Object newValue ) {
    // Don't do any processing if nothing has changed
    if ( ObjectUtilities.equal( newValue, currentValue ) ) {
      return;
    }

    final Object oldValue = currentValue;
    currentValue = newValue;
    editorComponent.setValue( currentValue );
    propertyChangeSupport.firePropertyChange( null, oldValue, newValue );
  }

  /**
   * Returns the value as a ResourceKey
   */
  public Object getValue() {
    return editorComponent.getValue();
  }

  /**
   * This property editor does not support painting
   */
  public boolean isPaintable() {
    return false;
  }

  /**
   * This property editor does not support painting
   */
  public void paintValue( final Graphics gfx, final Rectangle box ) {
  }

  /**
   * This property editor does not support java initialization string
   */
  public String getJavaInitializationString() {
    return null;
  }

  /**
   * Returns the current value as text. If a value exists, check if it is embedded or linked. If it is linked, the
   * identifier is fine. Otherwise we should specify the location where the embedded resource came from.
   */
  public String getAsText() {
    final Object value = getValue();
    if ( value == null ) {
      return null;
    }

    if ( value instanceof ResourceKey ) {
      // Get the source from the resource key (checking for embedded first)
      final ResourceKey resourceKey = (ResourceKey) value;
      final Object source = resourceKey.getFactoryParameters().get( ClassicEngineFactoryParameters.ORIGINAL_VALUE );
      if ( source != null ) {
        return String.valueOf( source );
      }
      return resourceKey.getIdentifierAsString();
    }

    return value.toString();
  }

  /**
   * Returns the valid set of tagged values for this property. This is not supported.
   */
  public String[] getTags() {
    return null;
  }

  /**
   * Returns the custom editor component for this property.
   */
  public Component getCustomEditor() {
    return editorComponent;
  }

  /**
   * Indicates if this property editor uses a customer editor (which it does)
   */
  public boolean supportsCustomEditor() {
    return true;
  }

  public void addPropertyChangeListener( final PropertyChangeListener listener ) {
    propertyChangeSupport.addPropertyChangeListener( listener );
  }

  public void removePropertyChangeListener( final PropertyChangeListener listener ) {
    propertyChangeSupport.removePropertyChangeListener( listener );
  }

  /**
   * Indicates that this property editor can not be set as text since it it more complicated (it has to support linking
   * vs. embedding.
   */
  public boolean supportsText() {
    return false;
  }

  /**
   * Class which will listen to the component for information about the changing properties
   */
  private class ComponentChangeListener implements PropertyChangeListener {
    public void propertyChange( final PropertyChangeEvent evt ) {
      final Object oldValue = currentValue;
      currentValue = editorComponent.getValue();
      if ( ObjectUtilities.equal( oldValue, currentValue ) == false ) {
        propertyChangeSupport.firePropertyChange( null, oldValue, currentValue );
      }
    }
  }
}

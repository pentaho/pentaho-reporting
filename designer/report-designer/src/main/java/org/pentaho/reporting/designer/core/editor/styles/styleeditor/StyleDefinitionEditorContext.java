/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.designer.core.editor.styles.styleeditor;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.engine.classic.core.style.css.ElementStyleDefinition;
import org.pentaho.reporting.engine.classic.core.style.css.ElementStyleRule;

import javax.swing.event.EventListenerList;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;

public class StyleDefinitionEditorContext {
  private PropertyChangeSupport propertyChangeSupport;
  private EventListenerList listeners;
  private Component parent;
  private ReportDesignerContext designerContext;
  private ElementStyleDefinition styleDefinition;
  private File source;

  public StyleDefinitionEditorContext( final ReportDesignerContext designerContext,
                                       final Component parent,
                                       final ElementStyleDefinition styleDefinition ) {
    this.propertyChangeSupport = new PropertyChangeSupport( this );
    this.listeners = new EventListenerList();
    this.designerContext = designerContext;
    this.parent = parent;
    this.styleDefinition = styleDefinition;
  }

  public void addElementStyleDefinitionChangeListener( final ElementStyleDefinitionChangeListener listener ) {
    listeners.add( ElementStyleDefinitionChangeListener.class, listener );
  }

  public void removeElementStyleDefinitionChangeListener( final ElementStyleDefinitionChangeListener listener ) {
    listeners.remove( ElementStyleDefinitionChangeListener.class, listener );
  }

  public void addPropertyChangeListener( final PropertyChangeListener listener ) {
    propertyChangeSupport.addPropertyChangeListener( listener );
  }

  public void removePropertyChangeListener( final PropertyChangeListener listener ) {
    propertyChangeSupport.removePropertyChangeListener( listener );
  }

  public void addPropertyChangeListener( final String propertyName, final PropertyChangeListener listener ) {
    propertyChangeSupport.addPropertyChangeListener( propertyName, listener );
  }

  public void removePropertyChangeListener( final String propertyName, final PropertyChangeListener listener ) {
    propertyChangeSupport.removePropertyChangeListener( propertyName, listener );
  }

  public void fireChange() {
    final ElementStyleDefinitionChangeEvent event =
      new ElementStyleDefinitionChangeEvent( this, styleDefinition, null );
    final ElementStyleDefinitionChangeListener[] changeListeners =
      listeners.getListeners( ElementStyleDefinitionChangeListener.class );
    for ( int i = changeListeners.length - 1; i >= 0; i -= 1 ) {
      final ElementStyleDefinitionChangeListener changeListener = changeListeners[ i ];
      changeListener.styleRulesChanged( event );
    }
  }

  public void fireRuleAdded( final ElementStyleRule rule ) {
    final ElementStyleDefinitionChangeEvent event =
      new ElementStyleDefinitionChangeEvent( this, styleDefinition, rule );
    final ElementStyleDefinitionChangeListener[] changeListeners =
      listeners.getListeners( ElementStyleDefinitionChangeListener.class );
    for ( int i = changeListeners.length - 1; i >= 0; i -= 1 ) {
      final ElementStyleDefinitionChangeListener changeListener = changeListeners[ i ];
      changeListener.styleRuleAdded( event );
    }
  }

  public void fireRuleRemoved( final ElementStyleRule rule ) {
    final ElementStyleDefinitionChangeEvent event =
      new ElementStyleDefinitionChangeEvent( this, styleDefinition, rule );
    final ElementStyleDefinitionChangeListener[] changeListeners =
      listeners.getListeners( ElementStyleDefinitionChangeListener.class );
    for ( int i = changeListeners.length - 1; i >= 0; i -= 1 ) {
      final ElementStyleDefinitionChangeListener changeListener = changeListeners[ i ];
      changeListener.styleRuleRemoved( event );
    }
  }

  public void addStyleRule( final ElementStyleRule rule ) {
    for ( int i = 0; i < this.styleDefinition.getRuleCount(); i += 1 ) {
      if ( this.styleDefinition.getRule( i ) == rule ) {
        return;
      }
    }

    this.styleDefinition.addRule( rule );
    fireRuleAdded( rule );
  }

  public void removeStyleRule( final ElementStyleRule rule ) {
    for ( int i = 0; i < this.styleDefinition.getRuleCount(); i += 1 ) {
      if ( this.styleDefinition.getRule( i ).equals( rule ) ) {
        this.styleDefinition.removeRule( rule );
        fireRuleRemoved( rule );
        return;
      }
    }
  }

  public File getSource() {
    return source;
  }

  public void setSource( final File source ) {
    final File oldFile = this.source;
    this.source = source;
    propertyChangeSupport.firePropertyChange( "source", oldFile, source );
  }

  public Component getParent() {
    return parent;
  }

  public ReportDesignerContext getDesignerContext() {
    return designerContext;
  }

  public ElementStyleDefinition getStyleDefinition() {
    return styleDefinition;
  }

  public void setStyleDefinition( final ElementStyleDefinition styleDefinition ) {
    this.styleDefinition.clearRules();
    if ( styleDefinition != null ) {
      for ( int i = 0; i < styleDefinition.getRuleCount(); i += 1 ) {
        this.styleDefinition.addRule( styleDefinition.getRule( i ) );
      }
    }
    fireChange();
  }
}

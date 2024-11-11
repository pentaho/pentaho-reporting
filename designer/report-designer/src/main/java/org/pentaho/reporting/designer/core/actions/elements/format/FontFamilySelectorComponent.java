/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.designer.core.actions.elements.format;

import org.pentaho.reporting.designer.core.DesignerContextComponent;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.ElementSelectionComponentSupport;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.FontFamilyComboBox;

import java.awt.*;
import java.util.List;

/**
 * @author Thomas Morgner
 */
public final class FontFamilySelectorComponent extends FontFamilyComboBox implements DesignerContextComponent {
  private class SelectionUpdateHelper extends ElementSelectionComponentSupport {
    private Element lastSelection;

    private SelectionUpdateHelper() {
    }

    protected void updateSelection() {
      if ( getSelectionModel() == null ) {
        setEnabled( false );
        setValueFromModel( null );
        lastSelection = null;
      } else {

        final List<Element> visualElements = getSelectionModel().getSelectedElementsOfType( Element.class );
        if ( visualElements.isEmpty() ) {
          setValueFromModel( null );
          setEnabled( false );
          lastSelection = null;
        } else {
          lastSelection = visualElements.get( 0 );
          final Object color = lastSelection.getStyle().getStyleProperty( TextStyleKeys.FONT );
          for ( int i = 1; i < visualElements.size(); i++ ) {
            final Element element = visualElements.get( i );
            final Object otherColor = element.getStyle().getStyleProperty( TextStyleKeys.FONT );
            if ( ObjectUtilities.equal( color, otherColor ) == false ) {
              setEnabled( true );
              setValueFromModel( null );
              return;
            }
          }

          setEnabled( true );
          setValueFromModel( color );
        }
      }
    }

    protected void nodeChanged( final ReportModelEvent event ) {
      if ( event.getElement() == lastSelection ) {
        final Object color = lastSelection.getStyle().getStyleProperty( TextStyleKeys.FONT );
        setValueFromModel( color );
      }
    }
  }

  private ApplyFontFamilyAction applyFontAction;
  private SelectionUpdateHelper updateHelper;

  public FontFamilySelectorComponent() {
    applyFontAction = new ApplyFontFamilyAction( this );
    updateHelper = new SelectionUpdateHelper();

    setFocusable( false );
    setMaximumSize( new Dimension( 250, getPreferredSize().height ) );
    setAction( applyFontAction );
  }

  public void setReportDesignerContext( final ReportDesignerContext context ) {
    applyFontAction.setReportDesignerContext( context );
    updateHelper.setReportDesignerContext( context );
  }

  public ReportDesignerContext getReportDesignerContext() {
    return applyFontAction.getReportDesignerContext();
  }
}

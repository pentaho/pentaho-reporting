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

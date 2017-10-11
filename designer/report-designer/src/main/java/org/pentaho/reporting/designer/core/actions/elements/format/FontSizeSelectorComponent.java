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
import org.pentaho.reporting.libraries.designtime.swing.SmartComboBox;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public final class FontSizeSelectorComponent extends SmartComboBox implements DesignerContextComponent {

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
          final Object color = lastSelection.getStyle().getStyleProperty( TextStyleKeys.FONTSIZE );
          for ( int i = 1; i < visualElements.size(); i++ ) {
            final Element element = visualElements.get( i );
            final Object otherColor = element.getStyle().getStyleProperty( TextStyleKeys.FONTSIZE );
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
        final Object color = lastSelection.getStyle().getStyleProperty( TextStyleKeys.FONTSIZE );
        setValueFromModel( color );
      }
    }
  }

  private ApplyFontSizeAction applyFontSizeAction;
  private SelectionUpdateHelper updateHelper;

  public FontSizeSelectorComponent() {
    applyFontSizeAction = new ApplyFontSizeAction( this );
    updateHelper = new SelectionUpdateHelper();

    final Integer[] fontSizes = new Integer[] { 6, 8, 10, 12, 14, 16, 18, 20, 24, 28, 32, 36, 48, 72 };
    setModel( new DefaultComboBoxModel( fontSizes ) );
    setFocusable( false );
    final int height1 = getPreferredSize().height;
    setMaximumSize( new Dimension( height1 * 3, height1 ) );
    setAction( applyFontSizeAction );
  }

  public void setReportDesignerContext( final ReportDesignerContext context ) {
    applyFontSizeAction.setReportDesignerContext( context );
    updateHelper.setReportDesignerContext( context );
  }

  public ReportDesignerContext getReportDesignerContext() {
    return applyFontSizeAction.getReportDesignerContext();
  }

  protected void setValueFromModel( final Object o ) {
    setAction( null );
    setSelectedItem( o );
    setAction( applyFontSizeAction );
  }

}

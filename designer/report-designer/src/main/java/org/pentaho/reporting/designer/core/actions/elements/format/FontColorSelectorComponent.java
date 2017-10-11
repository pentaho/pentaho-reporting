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
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.ColorComboBox;

import java.awt.*;
import java.util.List;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public final class FontColorSelectorComponent extends ColorComboBox implements DesignerContextComponent {
  private class SelectionUpdateHelper extends ElementSelectionComponentSupport {
    private Element lastSelection;

    protected void updateSelection() {
      if ( getSelectionModel() == null ) {
        setEnabled( false );
        setValueFromModel( null );
        lastSelection = null;
      } else {

        final List<Element> visualElements = getSelectionModel().getSelectedElementsOfType( Element.class );
        if ( visualElements.isEmpty() ) {
          setEnabled( false );
          setValueFromModel( null );
          lastSelection = null;
        } else {
          lastSelection = visualElements.get( 0 );
          setEnabled( true );
          final Color color = (Color) lastSelection.getStyle().getStyleProperty( ElementStyleKeys.PAINT );
          for ( int i = 1; i < visualElements.size(); i++ ) {
            final Element element = visualElements.get( i );
            final Object otherColor = element.getStyle().getStyleProperty( ElementStyleKeys.PAINT );
            if ( ObjectUtilities.equal( color, otherColor ) == false ) {
              setValueFromModel( null );
              return;
            }
          }

          setValueFromModel( color );
        }
      }
    }

    protected void nodeChanged( final ReportModelEvent event ) {
      if ( event.getElement() == lastSelection ) {
        final Color color = (Color) lastSelection.getStyle().getStyleProperty( ElementStyleKeys.PAINT );
        setValueFromModel( color );
      }
    }
  }

  private ApplyFontColorAction applyFontColorAction;
  private SelectionUpdateHelper updateHelper;

  public FontColorSelectorComponent() {
    applyFontColorAction = new ApplyFontColorAction( this );
    updateHelper = new SelectionUpdateHelper();

    final int height1 = getPreferredSize().height;
    setMaximumSize( new Dimension( height1 * 4, height1 ) );
    setFocusable( false );
    setAction( applyFontColorAction );
  }

  public void setReportDesignerContext( final ReportDesignerContext context ) {
    applyFontColorAction.setReportDesignerContext( context );
    updateHelper.setReportDesignerContext( context );
  }

  public ReportDesignerContext getReportDesignerContext() {
    return applyFontColorAction.getReportDesignerContext();
  }
}

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

package org.pentaho.reporting.designer.core.editor.format;

import org.pentaho.reporting.engine.classic.core.layout.model.Border;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinitionFactory;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.internal.BorderRenderer;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class BorderRenderPanel extends JPanel {
  private class MouseSelectionHandler extends MouseAdapter {
    private MouseSelectionHandler() {
    }

    public void mouseClicked( final MouseEvent e ) {
      final int x1 = e.getX();
      final int y1 = e.getY();
      if ( x1 <= 20 ) {
        // left
        if ( y1 <= 20 ) {
          invertSelection( BorderSelection.TOP_LEFT );
        } else if ( y1 >= ( getHeight() - 20 ) ) {
          invertSelection( BorderSelection.BOTTOM_LEFT );
        } else {
          invertSelection( BorderSelection.LEFT );
        }
      } else if ( x1 >= ( getWidth() - 20 ) ) {
        // right
        // left
        if ( y1 <= 20 ) {
          invertSelection( BorderSelection.TOP_RIGHT );
        } else if ( y1 >= ( getHeight() - 20 ) ) {
          invertSelection( BorderSelection.BOTTOM_RIGHT );
        } else {
          invertSelection( BorderSelection.RIGHT );
        }
      } else {
        // left
        if ( y1 <= 20 ) {
          invertSelection( BorderSelection.TOP );
        } else if ( y1 >= ( getHeight() - 20 ) ) {
          invertSelection( BorderSelection.BOTTOM );
        }
      }
    }

    private void invertSelection( final BorderSelection selection ) {
      final BorderSelectionModel model = getSelectionModel();
      if ( model.isSelected( selection ) ) {
        model.removeSelection( selection );
      } else {
        model.addSelection( selection );
      }
    }
  }

  private class SelectionUpdateHandler implements BorderSelectionListener {
    private SelectionUpdateHandler() {
    }

    public void selectionAdded( final BorderSelectionEvent event ) {
      repaint();
    }

    public void selectionRemoved( final BorderSelectionEvent event ) {
      repaint();
    }
  }

  private static final StyleKey[] BORDER_KEYS = new StyleKey[] {
    ElementStyleKeys.BORDER_BOTTOM_COLOR,
    ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_HEIGHT,
    ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_WIDTH,
    ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_HEIGHT,
    ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_WIDTH,
    ElementStyleKeys.BORDER_BOTTOM_STYLE,
    ElementStyleKeys.BORDER_BOTTOM_WIDTH,

    ElementStyleKeys.BORDER_TOP_COLOR,
    ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_HEIGHT,
    ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_WIDTH,
    ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_HEIGHT,
    ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_WIDTH,
    ElementStyleKeys.BORDER_TOP_STYLE,
    ElementStyleKeys.BORDER_TOP_WIDTH,

    ElementStyleKeys.BORDER_LEFT_COLOR,
    ElementStyleKeys.BORDER_LEFT_STYLE,
    ElementStyleKeys.BORDER_LEFT_WIDTH,

    ElementStyleKeys.BORDER_RIGHT_COLOR,
    ElementStyleKeys.BORDER_RIGHT_STYLE,
    ElementStyleKeys.BORDER_RIGHT_WIDTH,
  };

  private BorderRenderer borderRenderer;
  private ElementStyleSheet elementStyleSheet;
  private BoxDefinitionFactory boxDefinitionFactory;
  private BorderSelectionModel selectionModel;

  public BorderRenderPanel() {
    setBackground( Color.WHITE );
    addMouseListener( new MouseSelectionHandler() );

    borderRenderer = new BorderRenderer();
    boxDefinitionFactory = new BoxDefinitionFactory();
    elementStyleSheet = new EditableStyleSheet();
    selectionModel = new BorderSelectionModel();
    selectionModel.addBorderSelectionListener( new SelectionUpdateHandler() );
  }

  public ElementStyleSheet getElementStyleSheet() {
    return elementStyleSheet;
  }

  public void updateElementStyleSheet( final ElementStyleSheet elementStyleSheet ) {
    final StyleKey[] localKeys = this.elementStyleSheet.getDefinedPropertyNamesArray();
    for ( int i = 0; i < localKeys.length; i++ ) {
      final StyleKey styleKey = localKeys[ i ];
      if ( styleKey == null ) {
        continue;
      }
      elementStyleSheet.setStyleProperty( styleKey, null );
    }

    for ( int i = 0; i < BORDER_KEYS.length; i++ ) {
      final StyleKey styleKey = BORDER_KEYS[ i ];
      this.elementStyleSheet.setStyleProperty( styleKey, elementStyleSheet.getStyleProperty( styleKey ) );
    }

    repaint();
  }

  public BorderSelectionModel getSelectionModel() {
    return selectionModel;
  }

  protected void paintComponent( final Graphics g ) {
    final Graphics2D g2 = (Graphics2D) g.create();
    g2.clipRect( 0, 0, getWidth(), getHeight() );
    g2.setColor( getBackground() );
    g2.fillRect( 0, 0, getWidth(), getHeight() );

    if ( elementStyleSheet == null ) {
      g2.dispose();
      return;
    }

    final int rightSelectorEdge = Math.max( 80, getWidth() - 20 );
    final int bottomSelectorEdge = Math.max( 80, getHeight() - 20 );

    g2.setColor( Color.GREEN );
    if ( getSelectionModel().isSelected( BorderSelection.TOP_LEFT ) ) {
      g2.fillRect( 0, 0, 20, 20 );
    }
    if ( getSelectionModel().isSelected( BorderSelection.TOP ) ) {
      g2.fillRect( 20, 0, rightSelectorEdge - 20, 20 );
    }
    if ( getSelectionModel().isSelected( BorderSelection.TOP_RIGHT ) ) {
      g2.fillRect( rightSelectorEdge, 0, 20, 20 );
    }
    if ( getSelectionModel().isSelected( BorderSelection.LEFT ) ) {
      g2.fillRect( 0, 20, 20, bottomSelectorEdge - 20 );
    }
    if ( getSelectionModel().isSelected( BorderSelection.RIGHT ) ) {
      g2.fillRect( rightSelectorEdge, 20, 20, bottomSelectorEdge - 20 );
    }
    if ( getSelectionModel().isSelected( BorderSelection.BOTTOM_LEFT ) ) {
      g2.fillRect( 0, bottomSelectorEdge, 20, 20 );
    }
    if ( getSelectionModel().isSelected( BorderSelection.BOTTOM ) ) {
      g2.fillRect( 20, bottomSelectorEdge, rightSelectorEdge - 20, 20 );
    }
    if ( getSelectionModel().isSelected( BorderSelection.BOTTOM_RIGHT ) ) {
      g2.fillRect( rightSelectorEdge, bottomSelectorEdge, 20, 20 );
    }

    final StaticBoxLayoutProperties sblp = new StaticBoxLayoutProperties();

    final BoxDefinition definition = boxDefinitionFactory.getBoxDefinition( elementStyleSheet );
    final Border border = definition.getBorder();
    sblp.setBorderTop( border.getTop().getWidth() );
    sblp.setBorderLeft( border.getLeft().getWidth() );
    sblp.setBorderBottom( border.getBottom().getWidth() );
    sblp.setBorderRight( border.getRight().getWidth() );

    borderRenderer.paintBackgroundAndBorder( sblp, definition, elementStyleSheet,
      StrictGeomUtility.toInternalValue( 10 ),
      StrictGeomUtility.toInternalValue( 10 ),
      StrictGeomUtility.toInternalValue( Math.max( 80, getWidth() - 20 ) ),
      StrictGeomUtility.toInternalValue( Math.max( 80, getHeight() - 20 ) ), g2 );

    g2.setColor( Color.LIGHT_GRAY );
    g2.fillRect( 25, 25, Math.max( 50, getWidth() - 50 ), Math.max( 50, getHeight() - 50 ) );

    g2.drawLine( 20, 0, 20, getHeight() );
    g2.drawLine( 0, 20, getWidth(), 20 );
    g2.drawLine( rightSelectorEdge, 0, rightSelectorEdge, getHeight() );
    g2.drawLine( 0, bottomSelectorEdge, getWidth(), bottomSelectorEdge );
    g2.dispose();
  }

  public void commitValues( final ElementStyleSheet styleSheet ) {
    for ( int i = 0; i < BORDER_KEYS.length; i++ ) {
      final StyleKey styleKey = BORDER_KEYS[ i ];
      styleSheet.setStyleProperty( styleKey, elementStyleSheet.getStyleProperty( styleKey ) );
    }
  }
}

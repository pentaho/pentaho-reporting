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

package org.pentaho.reporting.libraries.designtime.swing.colorchooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SwatchColorChooser extends AbstractColorChooserPanel {
  private class SwatchPanel extends JPanel implements Scrollable {
    private Color[] colors;
    private Dimension swatchSize;
    private Dimension gap;
    private int swatchCountPerRow;

    private SwatchPanel() {
      this( new Color[ 0 ] );
    }

    public SwatchPanel( final Color[] colors ) {
      this.colors = colors;
      swatchSize = new Dimension( 20, 20 );
      gap = new Dimension( 2, 2 );
      setOpaque( false );
    }

    public void setColors( final Color[] colors ) {
      if ( colors == null ) {
        throw new NullPointerException();
      }
      this.colors = colors;
      invalidate();
      revalidate();
      repaint();
    }

    public Color getColorForPosition( final Point p ) {

      final int width = getWidth();
      if ( width == 0 ) {
        return null;
      }
      final int cx = ( width - 1 ) / ( gap.width + swatchSize.width );
      final int x = p.x / ( swatchSize.width + gap.width );
      final int y = p.y / ( swatchSize.height + gap.height );
      if ( y * cx + x < colors.length ) {
        return ( colors[ y * cx + x ] );
      }
      return null;
    }

    protected void paintComponent( final Graphics g ) {
      final int width = getWidth();
      if ( width == 0 ) {
        return;
      }

      final int cx = ( width - 1 ) / ( gap.width + swatchSize.width );
      final int cy = (int) ( Math.ceil( Math.max( 1.0f, colors.length ) / cx ) );
      final Color selectedColor = getColorSelectionModel().getSelectedColor();

      for ( int y = 0; y < cy; y++ ) {
        for ( int x = 0; x < cx; x++ ) {
          final int px = x * ( swatchSize.width + gap.width );
          final int py = y * ( swatchSize.height + gap.height );
          if ( y * cx + x < colors.length ) {
            final Color color = colors[ y * cx + x ];
            g.setColor( color );
            g.fillRect( px, py, swatchSize.width, swatchSize.height );
            if ( selectedColor != null && selectedColor.equals( color ) ) {
              g.setColor( Color.white );
            } else {
              g.setColor( Color.black );
            }
            g.drawRect( px, py, swatchSize.width, swatchSize.height );
          }
        }
      }
    }

    public Dimension getPreferredScrollableViewportSize() {
      return getPreferredSize();
    }

    public int getScrollableUnitIncrement( final Rectangle visibleRect, final int orientation, final int direction ) {
      if ( orientation == SwingConstants.VERTICAL ) {
        return swatchSize.height;
      }
      return swatchSize.width;
    }

    public int getScrollableBlockIncrement( final Rectangle visibleRect, final int orientation, final int direction ) {
      return 5 * getScrollableUnitIncrement( visibleRect, orientation, direction );
    }

    public boolean getScrollableTracksViewportWidth() {
      return true;
    }

    public boolean getScrollableTracksViewportHeight() {
      return false;
    }

    public int getSwatchCountPerRow() {
      return swatchCountPerRow;
    }

    public void setSwatchCountPerRow( final int swatchCountPerRow ) {
      this.swatchCountPerRow = swatchCountPerRow;
    }

    public Dimension getPreferredSize() {
      final int preferredSize;
      if ( swatchCountPerRow <= 0 ) {
        preferredSize = (int) Math.ceil( Math.sqrt( colors.length ) );
      } else {
        preferredSize = swatchCountPerRow;
      }
      if ( preferredSize == 0 ) {
        return new Dimension( 0, 0 );
      }

      final int width = ( gap.width + swatchSize.width ) * preferredSize + gap.width;
      final int height = (int) ( gap.height + ( swatchSize.height + gap.height ) * Math
        .ceil( preferredSize / Math.min( 1, colors.length ) ) );
      return new Dimension( width, height );
      //      return new Dimension(360, 360);
    }


  }

  private class SchemaSelectionListener implements ItemListener {
    private SchemaSelectionListener() {
    }

    public void itemStateChanged( final ItemEvent e ) {
      final ColorSchema selectedKey = (ColorSchema) swatches.getSelectedItem();
      if ( selectedKey != null ) {
        colorList.setColors( selectedKey.getColors() );
      }
    }
  }

  private class SwatchPanelSelectionListener extends MouseAdapter {
    private SwatchPanelSelectionListener() {
    }

    public void mouseClicked( final MouseEvent e ) {
      if ( e.getButton() != MouseEvent.BUTTON1 ) {
        return;
      }

      final Color color = colorList.getColorForPosition( e.getPoint() );
      if ( color != null ) {
        getColorSelectionModel().setSelectedColor( color );
      }
    }
  }

  private class ColorChooserRenderer extends DefaultListCellRenderer {
    private ColorChooserRenderer() {
    }

    public Component getListCellRendererComponent( final JList list,
                                                   final Object value,
                                                   final int index,
                                                   final boolean isSelected,
                                                   final boolean cellHasFocus ) {
      if ( value instanceof ColorSchema ) {
        final ColorSchema cs = (ColorSchema) value;
        return super.getListCellRendererComponent( list, cs.getName(), index, isSelected, cellHasFocus );
      }
      return super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
    }
  }

  private DefaultComboBoxModel swatches;
  private SwatchPanel colorList;

  public SwatchColorChooser() {
    swatches = new DefaultComboBoxModel();
    colorList = new SwatchPanel();
    colorList.addMouseListener( new SwatchPanelSelectionListener() );

    final JComboBox comboBox = new JComboBox( swatches );
    comboBox.addItemListener( new SchemaSelectionListener() );
    comboBox.setRenderer( new ColorChooserRenderer() );

    final JPanel selectorPanel = new JPanel();
    selectorPanel.setLayout( new BorderLayout() );
    selectorPanel.add( comboBox, BorderLayout.CENTER );
    selectorPanel.add( new JLabel( ColorChooserMessages.getInstance().getString( "ColorSchema" ) ), BorderLayout.WEST );

    final JScrollPane scrollPane = new JScrollPane( colorList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
      JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
    scrollPane.getViewport().setOpaque( false );
    scrollPane.setOpaque( false );

    setLayout( new BorderLayout() );
    add( selectorPanel, BorderLayout.NORTH );
    add( scrollPane, BorderLayout.CENTER );

    addSwatches( new SwingColorSchema() );
    addSwatches( new ExcelColorSchema() );
  }

  public void addSwatches( final ColorSchema colorSchema ) {
    swatches.addElement( colorSchema );
    if ( swatches.getSize() == 1 ) {
      swatches.setSelectedItem( colorSchema );
    }
  }

  public void removeSwatches( final ColorSchema colorSchema ) {
    swatches.removeElement( colorSchema );
  }

  public void clearSwatches() {
    swatches.removeAllElements();
  }

  public String getDisplayName() {
    return ColorChooserMessages.getInstance().getString( "SwatchesTitle" );
  }

  public Icon getSmallDisplayIcon() {
    return null;
  }

  protected void colorUpdated() {
    colorList.repaint();
  }
}

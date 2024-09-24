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

package org.pentaho.reporting.libraries.designtime.swing;

import java.awt.*;

public class VerticalLayout implements LayoutManager {

  public static final int CENTER = 0;
  public static final int RIGHT = 1;
  public static final int LEFT = 2;
  public static final int BOTH = 3;
  public static final int TOP = 1;
  public static final int BOTTOM = 2;

  private int vgap; //the vertical vgap between components...defaults to 5
  private int alignment; //LEFT, RIGHT, CENTER or BOTH...how the components are justified
  private int anchor; //TOP, BOTTOM or CENTER ...where are the components positioned in an overlarge space

  public VerticalLayout() {
    this( 5, CENTER, TOP );
  }

  public VerticalLayout( final int vgap ) {
    this( vgap, CENTER, TOP );
  }

  public VerticalLayout( final int vgap, final int alignment ) {
    this( vgap, alignment, TOP );
  }

  public VerticalLayout( final int vgap, final int alignment, final int anchor ) {
    this.vgap = vgap;
    this.alignment = alignment;
    this.anchor = anchor;
  }

  private Dimension layoutSize( final Container parent, final boolean minimum ) {
    final Dimension dim = new Dimension( 0, 0 );
    synchronized( parent.getTreeLock() ) {
      final int n = parent.getComponentCount();
      for ( int i = 0; i < n; i++ ) {
        final Component c = parent.getComponent( i );
        if ( c.isVisible() ) {
          final Dimension d = minimum ? c.getMinimumSize() : c.getPreferredSize();
          dim.width = Math.max( dim.width, d.width );
          dim.height += d.height;
          if ( i > 0 ) {
            dim.height += vgap;
          }
        }
      }
    }
    final Insets insets = parent.getInsets();
    dim.width += insets.left + insets.right;
    dim.height += insets.top + insets.bottom + vgap + vgap;
    return dim;
  }

  public void layoutContainer( final Container parent ) {
    final Insets insets = parent.getInsets();
    synchronized( parent.getTreeLock() ) {
      final int n = parent.getComponentCount();
      final Dimension pd = parent.getSize();
      int y = 0;
      //work out the total size
      for ( int i = 0; i < n; i++ ) {
        final Component c = parent.getComponent( i );
        final Dimension d = c.getPreferredSize();
        y += d.height + vgap;
      }
      y -= vgap; //otherwise there's a vgap too many
      //Work out the anchor paint
      if ( anchor == TOP ) {
        y = insets.top;
      } else if ( anchor == CENTER ) {
        y = ( pd.height - y ) / 2;
      } else {
        y = pd.height - y - insets.bottom;
      }
      //do layout
      for ( int i = 0; i < n; i++ ) {
        final Component c = parent.getComponent( i );
        final Dimension d = c.getPreferredSize();
        int x = insets.left;
        int wid = d.width;
        if ( alignment == CENTER ) {
          x = ( pd.width - d.width ) / 2;
        } else if ( alignment == RIGHT ) {
          x = pd.width - d.width - insets.right;
        } else if ( alignment == BOTH ) {
          wid = pd.width - insets.left - insets.right;
        }
        c.setBounds( x, y, wid, d.height );
        y += d.height + vgap;
      }
    }
  }

  public Dimension minimumLayoutSize( final Container parent ) {
    return layoutSize( parent, true );
  }

  public Dimension preferredLayoutSize( final Container parent ) {
    return layoutSize( parent, false );
  }

  public void addLayoutComponent( final String name, final Component comp ) {
  }

  public void removeLayoutComponent( final Component comp ) {
  }

  public String toString() {
    return getClass().getName() + "[vgap=" + vgap + " align=" + alignment + " anchor=" + anchor + ']';
  }
}

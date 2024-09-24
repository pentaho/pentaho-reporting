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

package org.pentaho.reporting.designer.core.util.docking;

import javax.swing.*;
import javax.swing.plaf.ButtonUI;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * User: Martin Date: 24.03.2005 Time: 13:50:20
 */
public class ImageToggleButton extends JToggleButton {
  private enum FirstIconAlignment {
    TOP,
    BOTTOM,
    LEFT,
    RIGHT
  }

  private static class RotateTextIcon {
    public static final int NONE = 0;
    public static final int CW = 1;
    public static final int CCW = 2;

    private RotateTextIcon() {
    }
  }

  static {
    UIManager.put( "ImageToggleButton", "javax.swing.plaf.basic.BasicToggleButtonUI" );//NON-NLS
  }

  private ImageIcon imageIcon;
  private String text;
  private GlobalPane.Alignment alignment;

  public ImageToggleButton( final ImageIcon icon,
                            final String text ) {
    this( icon, text, GlobalPane.Alignment.LEFT );
  }

  public ImageToggleButton( final ImageIcon icon,
                            final String text,
                            final GlobalPane.Alignment alignment ) {
    super();
    this.imageIcon = icon;
    this.text = text;

    setMargin( new Insets( 0, 0, 0, 0 ) );
    setContentAreaFilled( false );
    setBorderPainted( false );
    setBorder( BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) );
    setFont( StyleContext.getDefaultStyleContext().getFont( getFont().getName(), Font.PLAIN, 10 ) );
    setAlignmentX( alignment );

    setFocusable( false );
  }

  public String getUIClassID() {
    return "ImageToggleButton";//NON-NLS
  }

  public void setAlignmentX( final GlobalPane.Alignment alignment ) {
    this.alignment = alignment;

    switch( alignment ) {
      case TOP:
      case BOTTOM: {
        ImageIcon icon = createRotatedTextIcon( getForeground(), RotateTextIcon.NONE, getFont(), text );
        icon = createComposedImageIcon( this.imageIcon, icon, FirstIconAlignment.LEFT );

        final ImageIcon normalIcon = createButtonImageIcon( icon, new Insets( 3, 3, 3, 3 ), false, false );
        final ImageIcon selectedIcon = createButtonImageIcon( icon, new Insets( 3, 3, 3, 3 ), true, false );
        final ImageIcon selectedRolloverIcon = createButtonImageIcon( icon, new Insets( 3, 3, 3, 3 ), true, true );
        final ImageIcon rolloverIcon = createButtonImageIcon( icon, new Insets( 3, 3, 3, 3 ), false, true );

        setIcon( normalIcon );
        setSelectedIcon( selectedIcon );
        setRolloverSelectedIcon( selectedRolloverIcon );
        setRolloverIcon( rolloverIcon );

        break;
      }
      case LEFT: {
        ImageIcon icon = createRotatedTextIcon( getForeground(), RotateTextIcon.CCW, getFont(), text );
        icon = createComposedImageIcon( this.imageIcon, icon, FirstIconAlignment.BOTTOM );

        final ImageIcon normalIcon = createButtonImageIcon( icon, new Insets( 3, 3, 3, 3 ), false, false );
        final ImageIcon selectedIcon = createButtonImageIcon( icon, new Insets( 3, 3, 3, 3 ), true, false );
        final ImageIcon selectedRolloverIcon = createButtonImageIcon( icon, new Insets( 3, 3, 3, 3 ), true, true );
        final ImageIcon rolloverIcon = createButtonImageIcon( icon, new Insets( 3, 3, 3, 3 ), false, true );

        setIcon( normalIcon );
        setSelectedIcon( selectedIcon );
        setRolloverSelectedIcon( selectedRolloverIcon );
        setRolloverIcon( rolloverIcon );

        break;
      }
      case RIGHT: {
        ImageIcon icon = createRotatedTextIcon( getForeground(), RotateTextIcon.CW, getFont(), text );
        icon = createComposedImageIcon( this.imageIcon, icon, FirstIconAlignment.TOP );

        final ImageIcon normalIcon = createButtonImageIcon( icon, new Insets( 3, 3, 3, 3 ), false, false );
        final ImageIcon selectedIcon = createButtonImageIcon( icon, new Insets( 3, 3, 3, 3 ), true, false );
        final ImageIcon selectedRolloverIcon = createButtonImageIcon( icon, new Insets( 3, 3, 3, 3 ), true, true );
        final ImageIcon rolloverIcon = createButtonImageIcon( icon, new Insets( 3, 3, 3, 3 ), false, true );

        setIcon( normalIcon );
        setSelectedIcon( selectedIcon );
        setRolloverSelectedIcon( selectedRolloverIcon );
        setRolloverIcon( rolloverIcon );

        break;
      }
    }
  }

  private static ImageIcon createComposedImageIcon( final ImageIcon firstIcon,
                                                    final ImageIcon secondIcon,
                                                    final FirstIconAlignment firstIconAlignment ) {
    if ( firstIcon == null && secondIcon == null ) {
      return null;
    }
    if ( firstIcon == null ) {
      return secondIcon;
    }
    if ( secondIcon == null ) {
      return firstIcon;
    }

    switch( firstIconAlignment ) {
      case TOP: {
        final int width = Math.max( firstIcon.getIconWidth(), secondIcon.getIconWidth() );
        final int height = firstIcon.getIconHeight() + secondIcon.getIconHeight();

        final BufferedImage bi = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
        final Graphics graphics = bi.getGraphics();

        graphics.drawImage( firstIcon.getImage(), ( width - firstIcon.getIconWidth() ) / 2, 0, null );
        graphics.drawImage( secondIcon.getImage(), ( width - secondIcon.getIconWidth() ) / 2, firstIcon.getIconHeight(),
          null );

        return new ImageIcon( bi );
      }
      case BOTTOM: {
        final int width = Math.max( firstIcon.getIconWidth(), secondIcon.getIconWidth() );
        final int height = firstIcon.getIconHeight() + secondIcon.getIconHeight();

        final BufferedImage bi = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
        final Graphics graphics = bi.getGraphics();

        graphics.drawImage( firstIcon.getImage(), ( width - firstIcon.getIconWidth() ) / 2,
          height - firstIcon.getIconHeight(), null );
        graphics.drawImage( secondIcon.getImage(), ( width - secondIcon.getIconWidth() ) / 2, 0, null );

        return new ImageIcon( bi );
      }
      case LEFT: {
        final int width = firstIcon.getIconWidth() + secondIcon.getIconWidth();
        final int height = Math.max( firstIcon.getIconHeight(), secondIcon.getIconHeight() );

        final BufferedImage bi = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
        final Graphics graphics = bi.getGraphics();

        graphics.drawImage( firstIcon.getImage(), 0, ( height - firstIcon.getIconHeight() ) / 2, null );
        graphics
          .drawImage( secondIcon.getImage(), firstIcon.getIconWidth(), ( height - secondIcon.getIconHeight() ) / 2,
            null );

        return new ImageIcon( bi );
      }
      case RIGHT: {
        final int width = firstIcon.getIconWidth() + secondIcon.getIconWidth();
        final int height = Math.max( firstIcon.getIconHeight(), secondIcon.getIconHeight() );

        final BufferedImage bi = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
        final Graphics graphics = bi.getGraphics();

        graphics.drawImage( firstIcon.getImage(), secondIcon.getIconWidth(), ( height - firstIcon.getIconHeight() ) / 2,
          null );
        graphics.drawImage( secondIcon.getImage(), 0, ( height - secondIcon.getIconHeight() ) / 2, null );

        return new ImageIcon( bi );
      }
    }

    return new ImageIcon();
  }

  private static ImageIcon createButtonImageIcon( final ImageIcon icon,
                                                  final Insets insets,
                                                  final boolean selected,
                                                  final boolean rollover ) {
    final int w = icon.getIconWidth() + insets.left + insets.right;
    final int h = icon.getIconHeight() + insets.top + insets.bottom;

    final BufferedImage bi = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
    final Graphics2D g = bi.createGraphics();
    g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

    if ( selected ) {
      final int o = -5;
      int b = 40;
      if ( rollover ) {
        b = 60;
      }
      final GradientPaint gp = new GradientPaint( 0, o,
        new Color( Math.min( 164 + b, 255 ), Math.min( 166 + b, 255 ), Math.min( 172 + b, 255 ) ), 0, h + o,
        new Color( Math.min( 189 + b, 255 ), Math.min( 192 + b, 255 ), Math.min( 198 + b, 255 ) ), true );
      g.setPaint( gp );
    } else {
      final int o = -5;
      int b = 0;
      if ( rollover ) {
        b = 60;
      }
      final GradientPaint gp = new GradientPaint( 0, o,
        new Color( Math.min( 251 + b, 255 ), Math.min( 251 + b, 255 ), Math.min( 252 + b, 255 ) ), 0, h + o,
        new Color( Math.min( 215 + b, 255 ), Math.min( 218 + b, 255 ), Math.min( 224 + b, 255 ) ), true );
      g.setPaint( gp );
    }

    final RoundRectangle2D roundRectangle2D = new RoundRectangle2D.Double( 0, 0, w - 1, h - 1, 6, 6 );

    //if (!selected || rollover)
    {
      g.fill( roundRectangle2D );
    }

    if ( selected ) {
      final GradientPaint gp = new GradientPaint( 0, 0,
        new Color( Math.min( 125, 255 ), Math.min( 127, 255 ), Math.min( 131, 255 ) ), 0, h,
        new Color( Math.min( 162, 255 ), Math.min( 164, 255 ), Math.min( 169, 255 ) ), true );
      g.setPaint( gp );
    } else {
      final GradientPaint gp = new GradientPaint( 0, 0,
        new Color( Math.min( 149, 255 ), Math.min( 155, 255 ), Math.min( 158, 255 ) ), 0, h,
        new Color( Math.min( 85, 255 ), Math.min( 88, 255 ), Math.min( 94, 255 ) ), true );
      g.setPaint( gp );
    }

    //if (!selected || rollover)
    {
      g.draw( roundRectangle2D );
    }

    g.drawImage( icon.getImage(), insets.left, insets.top, null );

    return new ImageIcon( bi );
  }

  private static ImageIcon createRotatedTextIcon( final Color foreground,
                                                  final int rotate,
                                                  final Font font,
                                                  final String text ) {
    final FontRenderContext fontRenderContext = new FontRenderContext( null, false, false );
    final GlyphVector glyphs = font.createGlyphVector( fontRenderContext, text );
    final int width = (int) glyphs.getLogicalBounds().getWidth() + 4;
    //height = (int)glyphs.getLogicalBounds().getHeight();

    final LineMetrics lineMetrics = font.getLineMetrics( text, fontRenderContext );
    final float ascent = lineMetrics.getAscent();
    final int height = (int) Math.ceil( lineMetrics.getHeight() );

    final int w = rotate == RotateTextIcon.CW || rotate == RotateTextIcon.CCW ? height : width;
    final int h = rotate == RotateTextIcon.CW || rotate == RotateTextIcon.CCW ? width : height;

    final BufferedImage bufferedImage = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
    final Graphics2D g2d = (Graphics2D) bufferedImage.getGraphics();

    g2d.setFont( font );
    final AffineTransform oldTransform = g2d.getTransform();

    g2d.setColor( foreground );
    g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
    g2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );

    if ( rotate == RotateTextIcon.NONE ) {
      g2d.drawString( text, 2, ascent );
    } else if ( rotate == RotateTextIcon.CW ) {
      final AffineTransform trans = new AffineTransform();
      trans.concatenate( oldTransform );
      trans.translate( 0, 2 );
      trans.rotate( Math.PI / 2, height / 2, width / 2 );
      g2d.setTransform( trans );
      g2d.drawString( text, ( height - width ) / 2, ( width - height ) / 2 + ascent );
    } else if ( rotate == RotateTextIcon.CCW ) {
      final AffineTransform trans = new AffineTransform();
      trans.concatenate( oldTransform );
      trans.translate( 0, -2 );
      trans.rotate( Math.PI * 3 / 2, height / 2, width / 2 );
      g2d.setTransform( trans );
      g2d.drawString( text, ( height - width ) / 2, ( width - height ) / 2 + ascent );
    }

    return new ImageIcon( bufferedImage );
  }

  public void setUI( final ButtonUI ui ) {
    super.setUI( ui );

    if ( alignment != null ) {
      setAlignmentX( alignment );
    }
  }
}

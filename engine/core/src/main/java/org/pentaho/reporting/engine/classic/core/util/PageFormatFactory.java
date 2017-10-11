/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.util;

import java.awt.Insets;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The PageFormatFactory is used to create PageFormats on a higher level. The Factory contains templates for all
 * PageSizes defined by Adobe:
 * <p/>
 * <a href="http://partners.adobe.com/asn/developer/pdfs/tn/5003.PPD_Spec_v4.3.pdf" >Postscript Specifications</a>
 * <p/>
 * Usage for creating an printjob on A4 paper with 2.5 cm border:
 * 
 * <pre>
 * Paper paper = PageFormatFactory.createPaper (PageSize.A4);
 * PageFormatFactory.setBordersMm (paper, 25, 25, 25, 25);
 * PageFormat format = PageFormatFactory.createPageFormat (paper, PageFormat.PORTRAIT);
 * </code>
 * 
 * Defining a pageformat can be an ugly task and full of dependencies. The call to
 * PageFormatFactory.setBorders(...) will setup the paper's border and always assumes
 * that the paper is laid out in Portrait.
 * 
 * Changing the PageFormat's orientation does not change the PageFormat's paper object,
 * but it changes the way, how the paper object is interpreted.
 *
 * @author Thomas Morgner
 */
public final class PageFormatFactory {
  private static final Log logger = LogFactory.getLog( PageFormatFactory.class );

  /**
   * A single instance of the factory.
   */
  private static PageFormatFactory singleton;
  private static final String[] EMPTY_PAGEFORMATS = new String[0];

  /**
   * Default constructor.
   */
  private PageFormatFactory() {
  }

  /**
   * Returns a single instance of the factory.
   *
   * @return an instance of a PageFormatFactory.
   */
  public static synchronized PageFormatFactory getInstance() {
    if ( singleton == null ) {
      singleton = new PageFormatFactory();
    }
    return singleton;
  }

  /**
   * Creates a paper by using the paper size in points found in the int-array. The array must have a length of 2 and the
   * first value of this array has to contain the width and the second the height parameter. The created Paper has no
   * ImagableArea defined.
   *
   * @param papersize
   *          the definition of the papersize in a 2-element int-array
   * @return the created paper
   */
  public Paper createPaper( final int[] papersize ) {
    if ( papersize.length != 2 ) {
      throw new IllegalArgumentException( "Paper must have a width and a height" );
    }

    return createPaper( (double) papersize[0], (double) papersize[1] );
  }

  /**
   * Creates a paper by using the paper size in points found in the int-array. The array must have a length of 2 and the
   * first value of this array has to contain the width and the second the height parameter. The created Paper has no
   * ImagableArea defined.
   *
   * @param papersize
   *          the definition of the papersize in a 2-element int-array
   * @return the created paper
   */
  public Paper createPaper( final PageSize papersize ) {
    return createPaper( papersize.getWidth(), papersize.getHeight() );
  }

  /**
   * Creates a paper by using the paper size in points. The created Paper has no ImagableArea defined.
   *
   * @param width
   *          the width of the paper in points
   * @param height
   *          the height of the paper in points
   * @return the created paper
   */
  public Paper createPaper( final double width, final double height ) {
    final Paper p = new Paper();
    p.setSize( width, height );
    setBorders( p, 0, 0, 0, 0 );
    return p;
  }

  /**
   * Defines the imageable area of the given paper by adjusting the border around the imagable area. The bordersizes are
   * given in points.
   *
   * @param paper
   *          the paper that should be modified
   * @param top
   *          the bordersize of the top-border
   * @param left
   *          the border in points in the left
   * @param bottom
   *          the border in points in the bottom
   * @param right
   *          the border in points in the right
   */
  public void setBorders( final Paper paper, final double top, final double left, final double bottom,
      final double right ) {
    final double w = paper.getWidth() - ( right + left );
    final double h = paper.getHeight() - ( bottom + top );
    paper.setImageableArea( left, top, w, h );
  }

  /**
   * Defines the imageable area of the given paper by adjusting the border around the imagable area. The bordersizes are
   * given in inches.
   *
   * @param paper
   *          the paper that should be modified
   * @param top
   *          the bordersize of the top-border
   * @param left
   *          the border in points in the left
   * @param bottom
   *          the border in points in the bottom
   * @param right
   *          the border in points in the right
   */
  public void setBordersInch( final Paper paper, final double top, final double left, final double bottom,
      final double right ) {
    setBorders( paper, convertInchToPoints( top ), convertInchToPoints( left ), convertInchToPoints( bottom ),
        convertInchToPoints( right ) );
  }

  /**
   * Defines the imageable area of the given paper by adjusting the border around the imagable area. The bordersizes are
   * given in millimeters.
   *
   * @param paper
   *          the paper that should be modified
   * @param top
   *          the bordersize of the top-border
   * @param left
   *          the border in points in the left
   * @param bottom
   *          the border in points in the bottom
   * @param right
   *          the border in points in the right
   */
  public void setBordersMm( final Paper paper, final double top, final double left, final double bottom,
      final double right ) {
    setBorders( paper, convertMmToPoints( top ), convertMmToPoints( left ), convertMmToPoints( bottom ),
        convertMmToPoints( right ) );
  }

  /**
   * Converts the given inch value to a valid point-value.
   *
   * @param inches
   *          the size in inch
   * @return the size in points
   */
  public double convertInchToPoints( final double inches ) {
    return inches * 72.0f;
  }

  /**
   * Converts the given millimeter value to a valid point-value.
   *
   * @param mm
   *          the size in inch
   * @return the size in points
   */
  public double convertMmToPoints( final double mm ) {
    return mm * ( 72.0d / 254.0d ) * 10;
  }

  /**
   * Creates a new pageformat using the given paper and the given orientation.
   *
   * @param paper
   *          the paper to use in the new pageformat
   * @param orientation
   *          one of PageFormat.PORTRAIT, PageFormat.LANDSCAPE or PageFormat.REVERSE_LANDSCAPE
   * @return the created Pageformat
   * @throws NullPointerException
   *           if the paper given was null
   */
  public PageFormat createPageFormat( final Paper paper, final int orientation ) {
    if ( paper == null ) {
      throw new NullPointerException( "Paper given must not be null" );
    }
    final PageFormat pf = new PageFormat();
    pf.setPaper( paper );
    pf.setOrientation( orientation );
    return pf;
  }

  /**
   * Creates a paper by looking up the given Uppercase name in this classes defined constants. The value if looked up by
   * introspection, if the value is not defined in this class, null is returned.
   *
   * @param name
   *          the name of the constant defining the papersize
   * @return the defined paper or null, if the name was invalid.
   */
  public Paper createPaper( final String name ) {
    try {
      final Field f = PageSize.class.getDeclaredField( name );
      final Object o = f.get( null );
      if ( o instanceof PageSize == false ) {
        // Log.debug ("Is no valid pageformat definition");
        return null;
      }
      final PageSize pageformat = (PageSize) o;
      return createPaper( pageformat );
    } catch ( NoSuchFieldException nfe ) {
      // Log.debug ("There is no pageformat " + name + " defined.");
      return null;
    } catch ( IllegalAccessException aie ) {
      // Log.debug ("There is no pageformat " + name + " accessible.");
      return null;
    }
  }

  public static PageFormat create( final PageSize papersize, final int orientation, final Insets margins ) {
    final PageFormatFactory instance = PageFormatFactory.getInstance();
    final PageFormat pageFormat = instance.createPageFormat( instance.createPaper( papersize ), orientation );
    instance.setPageMargins( pageFormat, margins );
    return pageFormat;
  }

  /**
   * Logs the page format.
   *
   * @param pf
   *          the page format.
   */
  public static void logPageFormat( final PageFormat pf ) {
    logger.debug( printPageFormat( pf ) );
  }

  public static String printPageFormat( final PageFormat pf ) {
    StringBuffer b = new StringBuffer();
    b.append( "PageFormat={width=" );
    b.append( pf.getWidth() );
    b.append( ", height=" );
    b.append( pf.getHeight() );
    b.append( ", imageableX=" );
    b.append( pf.getImageableX() );
    b.append( ", imageableY=" );
    b.append( pf.getImageableY() );
    b.append( ", imageableWidth=" );
    b.append( pf.getImageableWidth() );
    b.append( ", imageableHeight=" );
    b.append( pf.getImageableHeight() );
    b.append( ", orientation=" ).append( pf.getOrientation() );
    b.append( ", paper=" );
    b.append( printPaper( pf.getPaper() ) );
    b.append( "}" );
    return b.toString();
  }

  private static String printPaper( final Paper paper ) {
    StringBuffer b = new StringBuffer();
    b.append( "Paper={width=" );
    b.append( paper.getWidth() );
    b.append( ", height=" );
    b.append( paper.getHeight() );
    b.append( ", imageableX=" );
    b.append( paper.getImageableX() );
    b.append( ", imageableY=" );
    b.append( paper.getImageableY() );
    b.append( ", imageableWidth=" );
    b.append( paper.getImageableWidth() );
    b.append( ", imageableHeight=" );
    b.append( paper.getImageableHeight() );
    b.append( "}" );
    return b.toString();
  }

  /**
   * Logs the paper size.
   *
   * @param pf
   *          the paper size.
   */
  public static void logPaper( final Paper pf ) {
    logger.debug( printPaper( pf ) );
  }

  /**
   * Tests, whether the given two page format objects are equal.
   *
   * @param pf1
   *          the first page format that should be compared.
   * @param pf2
   *          the second page format that should be compared.
   * @return true, if both page formats are equal, false otherwise.
   */
  public static boolean isEqual( final PageFormat pf1, final PageFormat pf2 ) {
    if ( pf1 == pf2 ) {
      return true;
    }
    if ( pf1 == null || pf2 == null ) {
      return false;
    }

    if ( pf1.getOrientation() != pf2.getOrientation() ) {
      return false;
    }
    final Paper p1 = pf1.getPaper();
    final Paper p2 = pf2.getPaper();

    if ( p1.getWidth() != p2.getWidth() ) {
      return false;
    }
    if ( p1.getHeight() != p2.getHeight() ) {
      return false;
    }
    if ( p1.getImageableX() != p2.getImageableX() ) {
      return false;
    }
    if ( p1.getImageableY() != p2.getImageableY() ) {
      return false;
    }
    if ( p1.getImageableWidth() != p2.getImageableWidth() ) {
      return false;
    }
    if ( p1.getImageableHeight() != p2.getImageableHeight() ) {
      return false;
    }
    return true;
  }

  /**
   * Returns the left border of the given paper.
   *
   * @param p
   *          the paper that defines the borders.
   * @return the left border.
   */
  public double getLeftBorder( final Paper p ) {
    return p.getImageableX();
  }

  /**
   * Returns the right border of the given paper.
   *
   * @param p
   *          the paper that defines the borders.
   * @return the right border.
   */
  public double getRightBorder( final Paper p ) {
    return p.getWidth() - ( p.getImageableX() + p.getImageableWidth() );
  }

  /**
   * Returns the top border of the given paper.
   *
   * @param p
   *          the paper that defines the borders.
   * @return the top border.
   */
  public double getTopBorder( final Paper p ) {
    return p.getImageableY();
  }

  /**
   * Returns the bottom border of the given paper.
   *
   * @param p
   *          the paper that defines the borders.
   * @return the bottom border.
   */
  public double getBottomBorder( final Paper p ) {
    return p.getHeight() - ( p.getImageableY() + p.getImageableHeight() );
  }

  public Insets getPageMargins( final PageFormat format ) {

    final int marginLeft = (int) format.getImageableX();
    final int marginRight = (int) ( format.getWidth() - format.getImageableWidth() - format.getImageableX() );
    final int marginTop = (int) ( format.getImageableY() );
    final int marginBottom = (int) ( format.getHeight() - format.getImageableHeight() - format.getImageableY() );
    return new Insets( marginTop, marginLeft, marginBottom, marginRight );
  }

  public String getPageFormatName( final double width, final double height ) {
    try {
      final Field[] fields = PageSize.class.getFields();
      for ( int i = 0; i < fields.length; i++ ) {
        final Field f = fields[i];
        if ( Modifier.isPublic( f.getModifiers() ) && Modifier.isStatic( f.getModifiers() ) ) {
          final Object o = f.get( PageFormatFactory.getInstance() );
          if ( o instanceof PageSize ) {
            final PageSize pageDef = (PageSize) o;
            if ( pageDef.getWidth() == width && pageDef.getHeight() == height ) {
              return f.getName();
            }
          }
        }
      }
    } catch ( Exception e ) {
      PageFormatFactory.logger.warn( "Unable to lookup the page name", e );
    }
    return null;
  }

  public String[] getPageFormats() {
    try {
      final ArrayList a = new ArrayList();
      final Field[] fields = PageSize.class.getFields();
      for ( int i = 0; i < fields.length; i++ ) {
        final Field f = fields[i];
        if ( Modifier.isPublic( f.getModifiers() ) && Modifier.isStatic( f.getModifiers() ) ) {
          final Object o = f.get( PageFormatFactory.getInstance() );
          if ( o instanceof PageSize ) {
            a.add( f.getName() );
          }
        }
      }

      return (String[]) a.toArray( new String[a.size()] );
    } catch ( Exception e ) {
      PageFormatFactory.logger.warn( "Unable to lookup the page name", e );
    }
    return PageFormatFactory.EMPTY_PAGEFORMATS;
  }

  public void setPageMargins( final PageFormat pageFormat, final Insets pageMargins ) {
    final Paper paper = pageFormat.getPaper();
    setBorders( paper, pageMargins.top, pageMargins.left, pageMargins.bottom, pageMargins.right );
    pageFormat.setPaper( paper );
  }
}

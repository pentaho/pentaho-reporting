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

/**
 * A class defining a page-dimension.
 *
 * @author Thomas Morgner
 */
public final class PageSize {
  /**
   * A standard paper size.
   */
  public static final PageSize PAPER11X17 = new PageSize( 792, 1224 );

  /**
   * A standard paper size.
   */
  public static final PageSize PAPER10X11 = new PageSize( 720, 792 );

  /**
   * A standard paper size.
   */
  public static final PageSize PAPER10X13 = new PageSize( 720, 936 );

  /**
   * A standard paper size.
   */
  public static final PageSize PAPER10X14 = new PageSize( 720, 1008 );

  /**
   * A standard paper size.
   */
  public static final PageSize PAPER12X11 = new PageSize( 864, 792 );

  /**
   * A standard paper size.
   */
  public static final PageSize PAPER15X11 = new PageSize( 1080, 792 );

  /**
   * A standard paper size.
   */
  public static final PageSize PAPER7X9 = new PageSize( 504, 648 );

  /**
   * A standard paper size.
   */
  public static final PageSize PAPER8X10 = new PageSize( 576, 720 );

  /**
   * A standard paper size.
   */
  public static final PageSize PAPER9X11 = new PageSize( 648, 792 );

  /**
   * A standard paper size.
   */
  public static final PageSize PAPER9X12 = new PageSize( 648, 864 );

  /**
   * A standard paper size.
   */
  public static final PageSize A0 = new PageSize( 2384, 3370 );

  /**
   * A standard paper size.
   */
  public static final PageSize A1 = new PageSize( 1684, 2384 );

  /**
   * A standard paper size.
   */
  public static final PageSize A2 = new PageSize( 1191, 1684 );

  /**
   * A standard paper size.
   */
  public static final PageSize A3 = new PageSize( 842, 1191 );

  /**
   * A standard paper size.
   */
  public static final PageSize A3_TRANSVERSE = new PageSize( 842, 1191 );

  /**
   * A standard paper size.
   */
  public static final PageSize A3_EXTRA = new PageSize( 913, 1262 );

  /**
   * A standard paper size.
   */
  public static final PageSize A3_EXTRATRANSVERSE = new PageSize( 913, 1262 );

  /**
   * A standard paper size.
   */
  public static final PageSize A3_ROTATED = new PageSize( 1191, 842 );

  /**
   * A standard paper size.
   */
  public static final PageSize A4 = new PageSize( 595, 842 );

  /**
   * A standard paper size.
   */
  public static final PageSize A4_TRANSVERSE = new PageSize( 595, 842 );

  /**
   * A standard paper size.
   */
  public static final PageSize A4_EXTRA = new PageSize( 667, 914 );

  /**
   * A standard paper size.
   */
  public static final PageSize A4_PLUS = new PageSize( 595, 936 );

  /**
   * A standard paper size.
   */
  public static final PageSize A4_ROTATED = new PageSize( 842, 595 );

  /**
   * A standard paper size.
   */
  public static final PageSize A4_SMALL = new PageSize( 595, 842 );

  /**
   * A standard paper size.
   */
  public static final PageSize A5 = new PageSize( 420, 595 );

  /**
   * A standard paper size.
   */
  public static final PageSize A5_TRANSVERSE = new PageSize( 420, 595 );

  /**
   * A standard paper size.
   */
  public static final PageSize A5_EXTRA = new PageSize( 492, 668 );

  /**
   * A standard paper size.
   */
  public static final PageSize A5_ROTATED = new PageSize( 595, 420 );

  /**
   * A standard paper size.
   */
  public static final PageSize A6 = new PageSize( 297, 420 );

  /**
   * A standard paper size.
   */
  public static final PageSize A6_ROTATED = new PageSize( 420, 297 );

  /**
   * A standard paper size.
   */
  public static final PageSize A7 = new PageSize( 210, 297 );

  /**
   * A standard paper size.
   */
  public static final PageSize A8 = new PageSize( 148, 210 );

  /**
   * A standard paper size.
   */
  public static final PageSize A9 = new PageSize( 105, 148 );

  /**
   * A standard paper size.
   */
  public static final PageSize A10 = new PageSize( 73, 105 );

  /**
   * A standard paper size.
   */
  public static final PageSize ANSIC = new PageSize( 1224, 1584 );

  /**
   * A standard paper size.
   */
  public static final PageSize ANSID = new PageSize( 1584, 2448 );

  /**
   * A standard paper size.
   */
  public static final PageSize ANSIE = new PageSize( 2448, 3168 );

  /**
   * A standard paper size.
   */
  public static final PageSize ARCHA = new PageSize( 648, 864 );

  /**
   * A standard paper size.
   */
  public static final PageSize ARCHB = new PageSize( 864, 1296 );

  /**
   * A standard paper size.
   */
  public static final PageSize ARCHC = new PageSize( 1296, 1728 );

  /**
   * A standard paper size.
   */
  public static final PageSize ARCHD = new PageSize( 1728, 2592 );

  /**
   * A standard paper size.
   */
  public static final PageSize ARCHE = new PageSize( 2592, 3456 );

  /**
   * A standard paper size.
   */
  public static final PageSize B0 = new PageSize( 2920, 4127 );

  /**
   * A standard paper size.
   */
  public static final PageSize B1 = new PageSize( 2064, 2920 );

  /**
   * A standard paper size.
   */
  public static final PageSize B2 = new PageSize( 1460, 2064 );

  /**
   * A standard paper size.
   */
  public static final PageSize B3 = new PageSize( 1032, 1460 );

  /**
   * A standard paper size.
   */
  public static final PageSize B4 = new PageSize( 729, 1032 );

  /**
   * A standard paper size.
   */
  public static final PageSize B4_ROTATED = new PageSize( 1032, 729 );

  /**
   * A standard paper size.
   */
  public static final PageSize B5 = new PageSize( 516, 729 );

  /**
   * A standard paper size.
   */
  public static final PageSize B5_TRANSVERSE = new PageSize( 516, 729 );

  /**
   * A standard paper size.
   */
  public static final PageSize B5_ROTATED = new PageSize( 729, 516 );

  /**
   * A standard paper size.
   */
  public static final PageSize B6 = new PageSize( 363, 516 );

  /**
   * A standard paper size.
   */
  public static final PageSize B6_ROTATED = new PageSize( 516, 363 );

  /**
   * A standard paper size.
   */
  public static final PageSize B7 = new PageSize( 258, 363 );

  /**
   * A standard paper size.
   */
  public static final PageSize B8 = new PageSize( 181, 258 );

  /**
   * A standard paper size.
   */
  public static final PageSize B9 = new PageSize( 127, 181 );

  /**
   * A standard paper size.
   */
  public static final PageSize B10 = new PageSize( 91, 127 );

  /**
   * A standard paper size.
   */
  public static final PageSize C4 = new PageSize( 649, 918 );

  /**
   * A standard paper size.
   */
  public static final PageSize C5 = new PageSize( 459, 649 );

  /**
   * A standard paper size.
   */
  public static final PageSize C6 = new PageSize( 323, 459 );

  /**
   * A standard paper size.
   */
  public static final PageSize COMM10 = new PageSize( 297, 684 );

  /**
   * A standard paper size.
   */
  public static final PageSize DL = new PageSize( 312, 624 );

  /**
   * A standard paper size.
   */
  public static final PageSize DOUBLEPOSTCARD = new PageSize( 567, 419 ); // should be 419.5, but I ignore that..

  /**
   * A standard paper size.
   */
  public static final PageSize DOUBLEPOSTCARD_ROTATED = new PageSize( 419, 567 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENV9 = new PageSize( 279, 639 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENV10 = new PageSize( 297, 684 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENV11 = new PageSize( 324, 747 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENV12 = new PageSize( 342, 792 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENV14 = new PageSize( 360, 828 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVC0 = new PageSize( 2599, 3676 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVC1 = new PageSize( 1837, 2599 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVC2 = new PageSize( 1298, 1837 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVC3 = new PageSize( 918, 1296 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVC4 = new PageSize( 649, 918 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVC5 = new PageSize( 459, 649 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVC6 = new PageSize( 323, 459 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVC65 = new PageSize( 324, 648 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVC7 = new PageSize( 230, 323 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVCHOU3 = new PageSize( 340, 666 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVCHOU3_ROTATED = new PageSize( 666, 340 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVCHOU4 = new PageSize( 255, 581 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVCHOU4_ROTATED = new PageSize( 581, 255 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVDL = new PageSize( 312, 624 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVINVITE = new PageSize( 624, 624 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVISOB4 = new PageSize( 708, 1001 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVISOB5 = new PageSize( 499, 709 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVISOB6 = new PageSize( 499, 354 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVITALIAN = new PageSize( 312, 652 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVELOPE = new PageSize( 312, 652 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVKAKU2 = new PageSize( 680, 941 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVKAKU2_ROTATED = new PageSize( 941, 680 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVKAKU3 = new PageSize( 612, 785 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVKAKU3_ROTATED = new PageSize( 785, 612 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVMONARCH = new PageSize( 279, 540 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVPERSONAL = new PageSize( 261, 468 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVPRC1 = new PageSize( 289, 468 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVPRC1_ROTATED = new PageSize( 468, 289 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVPRC2 = new PageSize( 289, 499 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVPRC2_ROTATED = new PageSize( 499, 289 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVPRC3 = new PageSize( 354, 499 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVPRC3_ROTATED = new PageSize( 499, 354 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVPRC4 = new PageSize( 312, 590 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVPRC4_ROTATED = new PageSize( 590, 312 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVPRC5 = new PageSize( 312, 624 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVPRC5_ROTATED = new PageSize( 624, 312 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVPRC6 = new PageSize( 340, 652 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVPRC6_ROTATED = new PageSize( 652, 340 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVPRC7 = new PageSize( 454, 652 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVPRC7_ROTATED = new PageSize( 652, 454 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVPRC8 = new PageSize( 340, 876 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVPRC8_ROTATED = new PageSize( 876, 340 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVPRC9 = new PageSize( 649, 918 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVPRC9_ROTATED = new PageSize( 918, 649 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVPRC10 = new PageSize( 918, 1298 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVPRC10_ROTATED = new PageSize( 1298, 918 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVYOU4 = new PageSize( 298, 666 );

  /**
   * A standard paper size.
   */
  public static final PageSize ENVYOU4_ROTATED = new PageSize( 666, 298 );

  /**
   * A standard paper size.
   */
  public static final PageSize EXECUTIVE = new PageSize( 522, 756 );

  /**
   * A standard paper size.
   */
  public static final PageSize FANFOLDUS = new PageSize( 1071, 792 );

  /**
   * A standard paper size.
   */
  public static final PageSize FANFOLDGERMAN = new PageSize( 612, 864 );

  /**
   * A standard paper size.
   */
  public static final PageSize FANFOLDGERMANLEGAL = new PageSize( 612, 936 );

  /**
   * A standard paper size.
   */
  public static final PageSize FOLIO = new PageSize( 595, 935 );

  /**
   * A standard paper size.
   */
  public static final PageSize ISOB0 = new PageSize( 2835, 4008 );

  /**
   * A standard paper size.
   */
  public static final PageSize ISOB1 = new PageSize( 2004, 2835 );

  /**
   * A standard paper size.
   */
  public static final PageSize ISOB2 = new PageSize( 1417, 2004 );

  /**
   * A standard paper size.
   */
  public static final PageSize ISOB3 = new PageSize( 1001, 1417 );

  /**
   * A standard paper size.
   */
  public static final PageSize ISOB4 = new PageSize( 709, 1001 );

  /**
   * A standard paper size.
   */
  public static final PageSize ISOB5 = new PageSize( 499, 709 );

  /**
   * A standard paper size.
   */
  public static final PageSize ISOB5_EXTRA = new PageSize( 570, 782 );

  /**
   * A standard paper size.
   */
  public static final PageSize ISOB6 = new PageSize( 354, 499 );

  /**
   * A standard paper size.
   */
  public static final PageSize ISOB7 = new PageSize( 249, 354 );

  /**
   * A standard paper size.
   */
  public static final PageSize ISOB8 = new PageSize( 176, 249 );

  /**
   * A standard paper size.
   */
  public static final PageSize ISOB9 = new PageSize( 125, 176 );

  /**
   * A standard paper size.
   */
  public static final PageSize ISOB10 = new PageSize( 88, 125 );

  /**
   * A standard paper size.
   */
  public static final PageSize LEDGER = new PageSize( 1224, 792 );

  /**
   * A standard paper size.
   */
  public static final PageSize LEGAL = new PageSize( 612, 1008 );

  /**
   * A standard paper size.
   */
  public static final PageSize LEGAL_EXTRA = new PageSize( 684, 1080 );

  /**
   * A standard paper size.
   */
  public static final PageSize LETTER = new PageSize( 612, 792 );

  /**
   * A standard paper size.
   */
  public static final PageSize LETTER_TRANSVERSE = new PageSize( 612, 792 );

  /**
   * A standard paper size.
   */
  public static final PageSize LETTER_EXTRA = new PageSize( 684, 864 );

  /**
   * A standard paper size.
   */
  public static final PageSize LETTER_EXTRATRANSVERSE = new PageSize( 684, 864 );

  /**
   * A standard paper size.
   */
  public static final PageSize LETTER_PLUS = new PageSize( 612, 914 );

  /**
   * A standard paper size.
   */
  public static final PageSize LETTER_ROTATED = new PageSize( 792, 612 );

  /**
   * A standard paper size.
   */
  public static final PageSize LETTER_SMALL = new PageSize( 612, 792 );

  /**
   * A standard paper size.
   */
  public static final PageSize MONARCH = ENVMONARCH;

  /**
   * A standard paper size.
   */
  public static final PageSize NOTE = new PageSize( 612, 792 );

  /**
   * A standard paper size.
   */
  public static final PageSize POSTCARD = new PageSize( 284, 419 );

  /**
   * A standard paper size.
   */
  public static final PageSize POSTCARD_ROTATED = new PageSize( 419, 284 );

  /**
   * A standard paper size.
   */
  public static final PageSize PRC16K = new PageSize( 414, 610 );

  /**
   * A standard paper size.
   */
  public static final PageSize PRC16K_ROTATED = new PageSize( 610, 414 );

  /**
   * A standard paper size.
   */
  public static final PageSize PRC32K = new PageSize( 275, 428 );

  /**
   * A standard paper size.
   */
  public static final PageSize PRC32K_ROTATED = new PageSize( 428, 275 );

  /**
   * A standard paper size.
   */
  public static final PageSize PRC32K_BIG = new PageSize( 275, 428 );

  /**
   * A standard paper size.
   */
  public static final PageSize PRC32K_BIGROTATED = new PageSize( 428, 275 );

  /**
   * A standard paper size.
   */
  public static final PageSize QUARTO = new PageSize( 610, 780 );

  /**
   * A standard paper size.
   */
  public static final PageSize STATEMENT = new PageSize( 396, 612 );

  /**
   * A standard paper size.
   */
  public static final PageSize SUPERA = new PageSize( 643, 1009 );

  /**
   * A standard paper size.
   */
  public static final PageSize SUPERB = new PageSize( 864, 1380 );

  /**
   * A standard paper size.
   */
  public static final PageSize TABLOID = new PageSize( 792, 1224 );

  /**
   * A standard paper size.
   */
  public static final PageSize TABLOIDEXTRA = new PageSize( 864, 1296 );

  /**
   * The width of the page in point.
   */
  private double width;
  /**
   * The height of the page in point.
   */
  private double height;

  /**
   * Creates a new page-size object with the given width and height.
   *
   * @param width
   *          the width in point.
   * @param height
   *          the height in point.
   */
  public PageSize( final double width, final double height ) {
    this.width = width;
    this.height = height;
  }

  /**
   * Returns the page's width.
   *
   * @return the width in point.
   */
  public double getWidth() {
    return width;
  }

  /**
   * Returns the page's height.
   *
   * @return the height in point.
   */
  public double getHeight() {
    return height;
  }

  /**
   * Compares this page size with the given object.
   *
   * @param o
   *          the other object.
   * @return true, if the given object is also a PageSize object and has the same width and height, false otherwise.
   */
  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final PageSize pageSize = (PageSize) o;

    if ( equal( pageSize.height, height ) == false ) {
      return false;
    }
    if ( equal( pageSize.width, width ) == false ) {
      return false;
    }

    return true;
  }

  /**
   * An internal helper method that compares two doubles for equality.
   *
   * @param d1
   *          the one double.
   * @param d2
   *          the other double.
   * @return true, if both doubles are binary equal, false otherwise.
   */
  private boolean equal( final double d1, final double d2 ) {
    return Double.doubleToLongBits( d1 ) == Double.doubleToLongBits( d2 );
  }

  /**
   * Computes a hashcode for this page-size.
   *
   * @return the hashcode.
   */
  public int hashCode() {
    long temp = width != +0.0d ? Double.doubleToLongBits( width ) : 0L;
    int result = (int) ( temp ^ ( temp >>> 32 ) );
    temp = height != +0.0d ? Double.doubleToLongBits( height ) : 0L;
    result = 29 * result + (int) ( temp ^ ( temp >>> 32 ) );
    return result;
  }
}

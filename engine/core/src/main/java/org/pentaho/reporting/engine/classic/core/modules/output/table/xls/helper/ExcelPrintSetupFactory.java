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

package org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.apache.poi.ss.usermodel.PrintSetup;
import org.pentaho.reporting.engine.classic.core.layout.model.PhysicalPageBox;
import org.pentaho.reporting.engine.classic.core.util.PageSize;

/**
 * Performs all steps to setup the printer page of an excel sheet.
 * <p/>
 * This list is based on the specifications found in the OpenOffice documentation. <a
 * href="http://sc.openoffice.org/excelfileformat.pdf"> http://sc.openoffice.org/excelfileformat.pdf</a>.
 *
 * @author user
 */
public final class ExcelPrintSetupFactory {
  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition LETTER = new ExcelPageDefinition( (short) 1, PageSize.LETTER );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition LETTER_SMALL = new ExcelPageDefinition( (short) 2, PageSize.LETTER_SMALL );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition TABLOID = new ExcelPageDefinition( (short) 3, PageSize.TABLOID );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition LEDGER = new ExcelPageDefinition( (short) 4, PageSize.LEDGER );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition LEGAL = new ExcelPageDefinition( (short) 5, PageSize.LEGAL );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition STATEMENT = new ExcelPageDefinition( (short) 6, PageSize.STATEMENT );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition EXECUTIVE = new ExcelPageDefinition( (short) 7, PageSize.EXECUTIVE );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition A3 = new ExcelPageDefinition( (short) 8, PageSize.A3 );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition A4 = new ExcelPageDefinition( (short) 9, PageSize.A4 );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition A4_SMALL = new ExcelPageDefinition( (short) 10, PageSize.A4_SMALL );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition A5 = new ExcelPageDefinition( (short) 11, PageSize.A5 );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition B4 = new ExcelPageDefinition( (short) 12, PageSize.B4 );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition B5 = new ExcelPageDefinition( (short) 13, PageSize.B5 );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition FOLIO = new ExcelPageDefinition( (short) 14, PageSize.FOLIO );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition QUARTO = new ExcelPageDefinition( (short) 15, PageSize.QUARTO );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition PAPER10X14 = new ExcelPageDefinition( (short) 16, PageSize.PAPER10X14 );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition PAPER11X17 = new ExcelPageDefinition( (short) 17, PageSize.PAPER11X17 );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition NOTE = new ExcelPageDefinition( (short) 18, PageSize.NOTE );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition ENV9 = new ExcelPageDefinition( (short) 19, PageSize.ENV9 );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition ENV10 = new ExcelPageDefinition( (short) 20, PageSize.ENV10 );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition ENV11 = new ExcelPageDefinition( (short) 21, PageSize.ENV11 );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition ENV12 = new ExcelPageDefinition( (short) 22, PageSize.ENV12 );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition ENV14 = new ExcelPageDefinition( (short) 23, PageSize.ENV14 );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition ENVDL = new ExcelPageDefinition( (short) 27, PageSize.ENVDL );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition ENVC5 = new ExcelPageDefinition( (short) 28, PageSize.ENVC5 );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition ENVC3 = new ExcelPageDefinition( (short) 29, PageSize.ENVC3 );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition ENVC4 = new ExcelPageDefinition( (short) 30, PageSize.ENVC4 );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition ENVC6 = new ExcelPageDefinition( (short) 31, PageSize.ENVC6 );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition ENVC65 = new ExcelPageDefinition( (short) 32, PageSize.ENVC65 );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition ENVISOB4 = new ExcelPageDefinition( (short) 33, PageSize.ENVISOB4 );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition ENVB5 = new ExcelPageDefinition( (short) 34, PageSize.ENVISOB5 );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition ENVB6 = new ExcelPageDefinition( (short) 35, PageSize.ENVISOB6 );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition ENVELOPE = new ExcelPageDefinition( (short) 36, PageSize.ENVELOPE );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition ENVMONARCH = new ExcelPageDefinition( (short) 37, PageSize.ENVMONARCH );

  /**
   * A standard page format mapping for excel.
   */
  // envelope 6 3/4
  public static final ExcelPageDefinition ENVPERSONAL = new ExcelPageDefinition( (short) 38, PageSize.ENVPERSONAL );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition FANFOLDUS = new ExcelPageDefinition( (short) 39, PageSize.FANFOLDUS );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition FANFOLDGERMAN = new ExcelPageDefinition( (short) 40, PageSize.FANFOLDGERMAN );

  /**
   * A standard page format mapping for excel.
   */
  public static final ExcelPageDefinition FANFOLDGERMANLEGAL = new ExcelPageDefinition( (short) 41,
      PageSize.FANFOLDGERMANLEGAL );

  /**
   * Default Constructor.
   */
  private ExcelPrintSetupFactory() {
  }

  /**
   * Performs the page setup and searches a matching page format for the report.
   *
   * @param printSetup
   *          the print setup object of the current sheet.
   * @param pageformat
   *          the pageformat defined for the report.
   * @param paperdef
   *          the excel paper size property (may be null).
   * @param paperOrientation
   *          the paper orientation, either "Landscape" or "Portrait"
   */
  public static void performPageSetup( final PrintSetup printSetup, final PhysicalPageBox pageformat,
      final String paperdef, final String paperOrientation ) {
    short pageCode = ExcelPrintSetupFactory.parsePaperSizeProperty( paperdef );
    if ( pageCode == -1 ) {
      pageCode = ExcelPrintSetupFactory.computePaperSize( pageformat );
    }
    if ( pageCode != -1 ) {
      printSetup.setPaperSize( pageCode );
    }
    if ( paperOrientation != null && paperOrientation.equals( "auto" ) == false ) {
      printSetup.setLandscape( "Landscape".equalsIgnoreCase( paperOrientation ) );
    } else {
      final boolean landscape = pageformat.getWidth() > pageformat.getHeight();
      printSetup.setLandscape( landscape );
    }
  }

  /**
   * Searches all defined excel page formats to find a page format that matches the given pageformat. If no matching
   * format was found, the next greater page format is used.
   * <p/>
   * If no page format fits the definition, -1 is returned.
   *
   * @param format
   *          the page format
   * @return the computed paper size or -1 if no paper size matches the requirements
   */
  private static short computePaperSize( final PhysicalPageBox format ) {
    ExcelPageDefinition pageDef = null;
    final int width = (int) format.getWidth();
    final int height = (int) format.getHeight();
    int delta = -1;

    final Field[] fields = ExcelPrintSetupFactory.class.getDeclaredFields();
    for ( int i = 0; i < fields.length; i++ ) {
      final Field field = fields[i];
      if ( ExcelPageDefinition.class.isAssignableFrom( field.getType() ) == false ) {
        // Log.debug ("Is no valid pageformat definition");
        continue;
      }
      if ( Modifier.isStatic( field.getModifiers() ) == false ) {
        // is no static field, who defined it here?
        continue;
      }
      try {
        final ExcelPageDefinition pageformat = (ExcelPageDefinition) field.get( null );
        if ( ( pageformat.getWidth() < width ) || ( pageformat.getHeight() < height ) ) {
          // paper is too small, ignore it
          continue;
        }
        final int newDelta = ( pageformat.getWidth() - width ) + ( pageformat.getHeight() - height );
        if ( ( delta == -1 ) || ( newDelta < delta ) ) {
          pageDef = pageformat;
          delta = newDelta;
        }
      } catch ( IllegalAccessException iae ) {
        // ignore ..
      }
    }
    if ( pageDef == null ) {
      return -1;
    } else {
      return pageDef.getPageFormatCode();
    }
  }

  /**
   * Parses the defined paper size for the excel sheets. The paper size can be defined using the report configuration
   * properties.
   *
   * @param paper
   *          the paper constant for the excel page size.
   * @return the parsed HSSF paper size constant or -1 if undefined.
   */
  private static short parsePaperSizeProperty( final String paper ) {
    if ( paper == null ) {
      return -1;
    }
    try {
      final Field field = ExcelPrintSetupFactory.class.getDeclaredField( paper );
      if ( ExcelPageDefinition.class.isAssignableFrom( field.getType() ) == false ) {
        // Log.debug ("Is no valid pageformat definition");
        return -1;
      }
      final Object o = field.get( null );
      final ExcelPageDefinition pageformat = (ExcelPageDefinition) o;
      return pageformat.getPageFormatCode();
    } catch ( NoSuchFieldException nfe ) {
      // Log.debug ("There is no pageformat " + name + " defined.");
      return -1;
    } catch ( IllegalAccessException aie ) {
      // Log.debug ("There is no pageformat " + name + " accessible.");
      return -1;
    }
  }

}

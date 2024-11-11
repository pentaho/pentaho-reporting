/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.pixie.wmf;

/**
 * The ROPConstants were defined in the Windows-API and are used do define the various RasterOperations (ROP). We don't
 * support RasterOperations...
 */
public interface ROPConstants {
  public static final int SRCCOPY = 0x00CC0020; /* dest = source                   */
  public static final int SRCPAINT = 0x00EE0086; /* dest = source OR dest           */
  public static final int SRCAND = 0x008800C6; /* dest = source AND dest          */
  public static final int SRCINVERT = 0x00660046; /* dest = source XOR dest          */
  public static final int SRCERASE = 0x00440328; /* dest = source AND (NOT dest )   */
  public static final int NOTSRCCOPY = 0x00330008; /* dest = (NOT source)             */
  public static final int NOTSRCERASE = 0x001100A6; /* dest = (NOT src) AND (NOT dest) */
  public static final int MERGECOPY = 0x00C000CA; /* dest = (source AND pattern)     */
  public static final int MERGEPAINT = 0x00BB0226; /* dest = (NOT source) OR dest     */
  public static final int PATCOPY = 0x00F00021; /* dest = pattern                  */
  public static final int PATPAINT = 0x00FB0A09; /* dest = DPSnoo                   */
  public static final int PATINVERT = 0x005A0049; /* dest = pattern XOR dest         */
  public static final int DSTINVERT = 0x00550009; /* dest = (NOT dest)               */
  public static final int BLACKNESS = 0x00000042; /* dest = BLACK                    */
  public static final int WHITENESS = 0x00FF0062; /* dest = WHITE                    */

  /* Binary raster ops */
  public static final int R2_BLACK = 1;   /*  0       */
  public static final int R2_NOTMERGEPEN = 2;   /* DPon     */
  public static final int R2_MASKNOTPEN = 3;   /* DPna     */
  public static final int R2_NOTCOPYPEN = 4;   /* PN       */
  public static final int R2_MASKPENNOT = 5;   /* PDna     */
  public static final int R2_NOT = 6;   /* Dn       */
  public static final int R2_XORPEN = 7;   /* DPx      */
  public static final int R2_NOTMASKPEN = 8;   /* DPan     */
  public static final int R2_MASKPEN = 9;   /* DPa      */
  public static final int R2_NOTXORPEN = 10;  /* DPxn     */
  public static final int R2_NOP = 11;  /* D        */
  public static final int R2_MERGENOTPEN = 12;  /* DPno     */
  public static final int R2_COPYPEN = 13;  /* P        */
  public static final int R2_MERGEPENNOT = 14;  /* PDno     */
  public static final int R2_MERGEPEN = 15;  /* DPo      */
  public static final int R2_WHITE = 16;  /*  1       */
  public static final int R2_LAST = 16;

}

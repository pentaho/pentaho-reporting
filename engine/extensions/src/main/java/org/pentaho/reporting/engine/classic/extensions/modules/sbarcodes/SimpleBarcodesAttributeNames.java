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

package org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes;

/**
 * This class holds the different attribute names used in SimpleBarcodes module.
 *
 * @author Thomas Morgner
 */
public class SimpleBarcodesAttributeNames {
  public static final String NAMESPACE =
      "http://reporting.pentaho.org/namespaces/engine/classic/extensions/sbarcodes/1.0";
  /**
   * This attribute represents the barcode symbology, it can be one of the following: 3of9, 3of9ext, code39, code39ext,
   * usd3, usd3ext, usd-3, usd-3ext, codabar, code27, usd4, 2of7, monarch, nw7, usd-4, nw-7, ean13, ean-13, upca, upc-a,
   * isbn, bookland, code128, code128a, code128b, code128c, uccean128, 2of5, std2of5, int2of5, postnet or pdf417.
   *
   * @see org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes.SimpleBarcodesUtility
   */
  public static final String TYPE_ATTRIBUTE = "type";
  /**
   * This attributes enables or disables the generation of a checksum, not all symbology support this attribute.
   */
  public static final String CHECKSUM_ATTRIBUTE = "checksum";
  /**
   * This attribute represents the width if the narrow bars.
   */
  public static final String BAR_WIDTH_ATTRIBUTE = "bar-width";
  /**
   * This attribute represents the height of the bars.<br/>
   * Bar height is often constrained by the barcode total width in order to ensure that barcode readers will read it
   * properly. It means that if this property is set too small it may be (silently) ignored by the barcode element.
   */
  public static final String BAR_HEIGHT_ATTRIBUTE = "bar-height";
  /**
   * This attribute enables or disables the printing of the barcode string representation located under the barcode.
   */
  public static final String SHOW_TEXT_ATTRIBUTE = "show-text";

  private SimpleBarcodesAttributeNames() {
  }
}

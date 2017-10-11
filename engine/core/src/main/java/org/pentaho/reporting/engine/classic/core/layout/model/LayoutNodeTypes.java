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

package org.pentaho.reporting.engine.classic.core.layout.model;

/**
 * Bits 0-3 define the general node type. Bits 4-7 define the first sub classification. Bits 8 to 16 define the second
 * level subtype. Bits 16 to 20 define the third level subtype.
 * <p/>
 * If bit0 is set, the node is not a box If bit1 is set, the node is a renderbox. Bit 2 and 3 are reserved.
 * <p/>
 * For boxes: If bit 4 is set if the box is a block box. If bit 5 is set if the box is a canvas box. If bit 6 is set if
 * the box is a inline box. If bit 7 is reserved.
 *
 * @author Thomas Morgner
 */
public final class LayoutNodeTypes {
  private LayoutNodeTypes() {
  }

  // Bit 0: 1 = Not a box
  // Bit 1: 1 = Definitely a box
  // Bit 2: <unused>
  // Bit 3: <unused>
  // Bit 4: Block
  // Bit 5: Canvas
  // Bit 6: Inline
  // Bit 7: Row
  // Bit 8-16: Subtype ID
  // Bit 11: Structural box

  public static final int MASK_NODE = 0x001;
  public static final int MASK_BOX = 0x002;
  public static final int MASK_BOX_BLOCK = 0x12;
  public static final int MASK_BOX_PAGEAREA = 0x812;
  public static final int MASK_BOX_CANVAS = 0x22;
  public static final int MASK_BOX_INLINE = 0x42;
  public static final int MASK_BOX_ROW = 0x82;
  public static final int MASK_BOX_TABLE = 0x40002;

  public static final int MASK_BASIC_BOX_TYPE = 0xFF0FE;

  public static final int TYPE_BOX_BLOCK = 0x0012;
  public static final int TYPE_BOX_PARAGRAPH = 0x0112;
  public static final int TYPE_BOX_BREAKMARK = 0x0212;
  public static final int TYPE_BOX_LOGICALPAGE = 0x0412;
  public static final int TYPE_BOX_PROGRESS_MARKER = 0x1000012;
  public static final int TYPE_BOX_PAGEAREA = 0x10812;
  public static final int TYPE_BOX_WATERMARK = 0x20812;
  public static final int TYPE_BOX_SECTION = 0x11012;

  public static final int TYPE_BOX_CANVAS = 0x0022;
  public static final int TYPE_BOX_INLINE = 0x0042;
  public static final int TYPE_BOX_LINEBOX = 0x0142;

  public static final int TYPE_BOX_ROWBOX = 0x0082;
  public static final int TYPE_BOX_CONTENT = 0x0102;

  public static final int TYPE_BOX_TABLE = 0x41002;
  public static final int TYPE_BOX_TABLE_SECTION = 0x42002;
  public static final int TYPE_BOX_TABLE_ROW = 0x44002;
  public static final int TYPE_BOX_TABLE_CELL = 0x48002;
  public static final int TYPE_BOX_TABLE_COL_GROUP = 0x50002;
  public static final int TYPE_BOX_TABLE_COL = 0x60002;

  public static final int TYPE_BOX_INLINE_PROGRESS_MARKER = 0x1000042;

  public static final int TYPE_NODE_TEXT = 0x11;
  public static final int TYPE_NODE_COMPLEX_TEXT = 0x111;
  public static final int TYPE_NODE_SPACER = 0x41;
  public static final int TYPE_NODE_FINISHEDNODE = 0x81;

  public static final int TYPE_BOX_AUTOLAYOUT = 0x80002;
}

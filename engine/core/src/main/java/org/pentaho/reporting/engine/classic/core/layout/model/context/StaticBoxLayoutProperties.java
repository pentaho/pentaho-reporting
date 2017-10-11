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

package org.pentaho.reporting.engine.classic.core.layout.model.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.layout.text.ExtendedBaselineInfo;

import java.io.Serializable;

/**
 * A static properties collection. That one is static; once computed it does not change anymore. It does not (under no
 * thinkable circumstances) depend on the given content. It may depend on static content of the parent.
 * <p/>
 * A box typically has two sets of margins. The first set is the declared margin set - it simply expresses the user's
 * definitions. The second set is the effective margin set, it is based on the context of the element in the document
 * tree and denotes the distance between the nodes edge and any oposite edge.
 *
 * @author Thomas Morgner
 */
public final class StaticBoxLayoutProperties implements Serializable {
  public enum PlaceholderType {
    NONE, SECTION, COMPLEX
  }

  private static final int FLAG_AVOID_PAGEBREAK = 0x1;
  private static final int FLAG_PRESERVE_SPACE = 0x2;
  private static final int FLAG_OVERFLOW_X = 0x4;
  private static final int FLAG_OVERFLOW_Y = 0x8;
  private static final int FLAG_INVISIBLE_CONSUMES_SPACE = 0x10;
  private static final int FLAG_VISIBLE = 0x20;
  private static final int FLAG_BREAK_AFTER = 0x40;
  private static final int FLAG_SECTION_CONTEXT = 0x80;
  private static final int FLAG_WIDOW_ORPHAN_OPT_OUT = 0x100;
  private static final int FLAG_DEFINED_WIDTH = 0x200;

  private static final Log logger = LogFactory.getLog( StaticBoxLayoutProperties.class );

  private long borderLeft;
  private long borderRight;
  private long borderTop;
  private long borderBottom;

  private int dominantBaseline;
  private ExtendedBaselineInfo nominalBaselineInfo;
  private int widows;
  private int orphans;
  private int flags;

  private String fontFamily;
  private long spaceWidth;

  private PlaceholderType placeholderBox;

  public StaticBoxLayoutProperties() {
    placeholderBox = PlaceholderType.NONE;
  }

  private void setFlag( final int flag, final boolean value ) {
    if ( value ) {
      flags = flags | flag;
    } else {
      flags = flags & ( ~flag );
    }
  }

  private boolean isFlag( final int flag ) {
    return ( flags & flag ) != 0;
  }

  public boolean isWidowOrphanOptOut() {
    return isFlag( FLAG_WIDOW_ORPHAN_OPT_OUT );
  }

  public void setWidowOrphanOptOut( final boolean widowOrphanOptOut ) {
    setFlag( FLAG_WIDOW_ORPHAN_OPT_OUT, widowOrphanOptOut );
  }

  public boolean isDefinedWidth() {
    return isFlag( FLAG_DEFINED_WIDTH );
  }

  /**
   * Indicates whether the box explicitly defines a width.
   *
   * @param definedWidth
   */
  public void setDefinedWidth( final boolean definedWidth ) {
    setFlag( FLAG_DEFINED_WIDTH, definedWidth );
  }

  public boolean isSectionContext() {
    return isFlag( FLAG_SECTION_CONTEXT );
  }

  public void setSectionContext( final boolean sectionContext ) {
    setFlag( FLAG_SECTION_CONTEXT, sectionContext );
  }

  public long getSpaceWidth() {
    return spaceWidth;
  }

  public void setSpaceWidth( final long spaceWidth ) {
    this.spaceWidth = spaceWidth;
  }

  public long getMarginLeft() {
    return 0;
  }

  public long getMarginRight() {
    return 0;
  }

  public long getMarginTop() {
    return 0;
  }

  public long getMarginBottom() {
    return 0;
  }

  public long getBorderLeft() {
    return borderLeft;
  }

  public void setBorderLeft( final long borderLeft ) {
    this.borderLeft = borderLeft;
  }

  public long getBorderRight() {
    return borderRight;
  }

  public void setBorderRight( final long borderRight ) {
    this.borderRight = borderRight;
  }

  public long getBorderTop() {
    return borderTop;
  }

  public void setBorderTop( final long borderTop ) {
    this.borderTop = borderTop;
  }

  public long getBorderBottom() {
    return borderBottom;
  }

  public void setBorderBottom( final long borderBottom ) {
    this.borderBottom = borderBottom;
  }

  public int getDominantBaseline() {
    return dominantBaseline;
  }

  public void setDominantBaseline( final int dominantBaseline ) {
    this.dominantBaseline = dominantBaseline;
  }

  public ExtendedBaselineInfo getNominalBaselineInfo() {
    return nominalBaselineInfo;
  }

  public boolean isBaselineCalculated() {
    return nominalBaselineInfo != null;
  }

  public void setNominalBaselineInfo( final ExtendedBaselineInfo nominalBaselineInfo ) {
    if ( nominalBaselineInfo == null ) {
      throw new NullPointerException();
    }
    this.nominalBaselineInfo = nominalBaselineInfo;
  }

  public String getFontFamily() {
    return fontFamily;
  }

  public void setFontFamily( final String fontFamily ) {
    this.fontFamily = fontFamily;
  }

  public int getWidows() {
    return widows;
  }

  public void setWidows( final int widows ) {
    this.widows = widows;
  }

  public int getOrphans() {
    return orphans;
  }

  public void setOrphans( final int orphans ) {
    this.orphans = orphans;
  }

  public boolean isAvoidPagebreakInside() {
    return isFlag( FLAG_AVOID_PAGEBREAK );
  }

  public void setAvoidPagebreakInside( final boolean avoidPagebreakInside ) {
    setFlag( FLAG_AVOID_PAGEBREAK, avoidPagebreakInside );
  }

  public boolean isPreserveSpace() {
    return isFlag( FLAG_PRESERVE_SPACE );
  }

  public void setPreserveSpace( final boolean preserveSpace ) {
    setFlag( FLAG_PRESERVE_SPACE, preserveSpace );
  }

  public boolean isBreakAfter() {
    return isFlag( FLAG_BREAK_AFTER );
  }

  public void setBreakAfter( final boolean breakAfter ) {
    setFlag( FLAG_BREAK_AFTER, breakAfter );
  }

  public boolean isOverflowX() {
    return isFlag( FLAG_OVERFLOW_X );
  }

  public void setOverflowX( final boolean overflowX ) {
    setFlag( FLAG_OVERFLOW_X, overflowX );
  }

  public boolean isOverflowY() {
    return isFlag( FLAG_OVERFLOW_Y );
  }

  public void setOverflowY( final boolean overflowY ) {
    setFlag( FLAG_OVERFLOW_Y, overflowY );
  }

  public boolean isInvisibleConsumesSpace() {
    return isFlag( FLAG_INVISIBLE_CONSUMES_SPACE );
  }

  public void setInvisibleConsumesSpace( final boolean invisibleConsumesSpace ) {
    setFlag( FLAG_INVISIBLE_CONSUMES_SPACE, invisibleConsumesSpace );
  }

  public boolean isVisible() {
    return isFlag( FLAG_VISIBLE );
  }

  public void setVisible( final boolean visible ) {
    setFlag( FLAG_VISIBLE, visible );
  }

  public boolean isPlaceholderBox() {
    return placeholderBox != PlaceholderType.NONE;
  }

  public PlaceholderType getPlaceholderBox() {
    return placeholderBox;
  }

  public void setPlaceholderBox( final PlaceholderType placeholderBox ) {
    if ( placeholderBox == null ) {
      throw new NullPointerException();
    }
    this.placeholderBox = placeholderBox;
  }

  public String toString() {
    return "StaticBoxLayoutProperties{" + "borderLeft=" + borderLeft + ", borderRight=" + borderRight + ", borderTop="
        + borderTop + ", borderBottom=" + borderBottom + ", dominantBaseline=" + dominantBaseline + ", widows="
        + widows + ", orphans=" + orphans + ", avoidPagebreakInside=" + isAvoidPagebreakInside() + ", preserveSpace="
        + isPreserveSpace() + ", visible=" + isVisible() + ", placeholderBox=" + placeholderBox
        + ", invisibleConsumesSpace=" + isInvisibleConsumesSpace() + '}';
  }
}

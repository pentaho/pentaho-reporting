/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.layout.model.table.rows;

import org.pentaho.reporting.engine.classic.core.layout.model.Border;
import org.pentaho.reporting.engine.classic.core.util.LongList;

public class TableRowImpl implements TableRow {
  // Borders will be needed for the combined column model ..
  private Border border;

  private long preferredSize;
  private long validateSize;

  private LongList preferredSizes;
  private long validatedLeadingSize;
  private LongList validatedTrailingSize;

  public TableRowImpl() {
    this( Border.EMPTY_BORDER );
  }

  public TableRowImpl( final Border border ) {
    this.border = border;
    this.preferredSizes = new LongList( 10 );
    this.validatedLeadingSize = 0;
    this.validatedTrailingSize = new LongList( 10 );
  }

  public long getPreferredSize() {
    return preferredSize;
  }

  public void setPreferredSize( final long preferredSize ) {
    this.preferredSize = preferredSize;
  }

  public long getPreferredSize( final int colspan ) {
    final int index = colspan - 1;
    if ( index < 0 ) {
      throw new IllegalArgumentException();
    }

    if ( preferredSizes.size() <= index ) {
      return 0;
    }
    return preferredSizes.get( index );
  }

  public void updateDefinedSize( final int rowSpan, final long preferredHeight ) {
    if ( rowSpan < 1 ) {
      throw new IllegalArgumentException();
    }
    final int idx = rowSpan - 1;

    if ( ( idx >= preferredSizes.size() ) || ( preferredSizes.get( idx ) < preferredHeight ) ) {
      preferredSizes.set( idx, preferredHeight );
    }
  }

  public long getValidatedLeadingSize() {
    return validatedLeadingSize;
  }

  public long getValidatedTrailingSize( final int rowSpan ) {
    if ( rowSpan > validatedTrailingSize.size() ) {
      return 0;
    }
    return validatedTrailingSize.get( rowSpan - 1 );
  }

  public int getMaxValidatedRowSpan() {
    return this.validatedTrailingSize.size();
  }

  public void updateValidatedSize( final int rowSpan, final long leading, final long trailing ) {
    final int idx = rowSpan - 1;
    if ( validatedLeadingSize < leading ) {
      validatedLeadingSize = leading;
    }

    if ( ( idx >= validatedTrailingSize.size() ) || ( validatedTrailingSize.get( idx ) < trailing ) ) {
      validatedTrailingSize.set( idx, trailing );
    }
  }

  public long getValidateSize() {
    return validateSize;
  }

  public void setValidateSize( final long validateSize ) {
    this.validateSize = validateSize;
  }

  public Border getBorder() {
    return border;
  }

  public void clear() {
    this.validatedTrailingSize.clear();
    this.validateSize = 0;
  }

  public int getMaximumRowSpan() {
    return preferredSizes.size();
  }

  public String toString() {
    return "TableRowImpl{" + "preferredSize=" + preferredSize + ", validateSize=" + validateSize + ", preferredSizes="
        + preferredSizes + ", validatedLeadingSize=" + validatedLeadingSize + ", validatedTrailingSize="
        + validatedTrailingSize + ", border=" + border + '}';
  }
}

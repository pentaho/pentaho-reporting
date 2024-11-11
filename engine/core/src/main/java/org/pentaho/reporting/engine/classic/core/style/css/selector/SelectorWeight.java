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


package org.pentaho.reporting.engine.classic.core.style.css.selector;

import java.io.Serializable;

/**
 * Creation-Date: 05.12.2005, 19:39:58
 *
 * @author Thomas Morgner
 */
public final class SelectorWeight implements Comparable, Serializable {
  private int styleAttribute;
  private int idCount;
  private int attributeCount; // and pseudo-formats!
  private int elementCount; // and pseudeo-elements

  public SelectorWeight( final int styleAttribute, final int idCount, final int attributeCount, final int elementCount ) {
    this( null, styleAttribute, idCount, attributeCount, elementCount );
  }

  public SelectorWeight( final SelectorWeight first, final SelectorWeight second ) {
    this.styleAttribute = first.styleAttribute + second.styleAttribute;
    this.idCount = first.idCount + second.idCount;
    this.attributeCount = first.attributeCount + second.attributeCount;
    this.elementCount = first.elementCount + second.attributeCount;
  }

  public SelectorWeight( final SelectorWeight parent, final int styleAttribute, final int idCount,
      final int attributeCount, final int elementCount ) {
    if ( parent == null ) {
      this.styleAttribute = styleAttribute;
      this.idCount = idCount;
      this.attributeCount = attributeCount;
      this.elementCount = elementCount;
    } else {
      this.styleAttribute = styleAttribute + parent.styleAttribute;
      this.idCount = idCount + parent.idCount;
      this.attributeCount = attributeCount + parent.attributeCount;
      this.elementCount = elementCount + parent.elementCount;
    }
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final SelectorWeight that = (SelectorWeight) o;

    if ( attributeCount != that.attributeCount ) {
      return false;
    }
    if ( elementCount != that.elementCount ) {
      return false;
    }
    if ( idCount != that.idCount ) {
      return false;
    }
    return styleAttribute == that.styleAttribute;

  }

  public int hashCode() {
    int result = styleAttribute;
    result = 29 * result + idCount;
    result = 29 * result + attributeCount;
    result = 29 * result + elementCount;
    return result;
  }

  public int compareTo( final Object o ) {
    SelectorWeight weight = (SelectorWeight) o;
    if ( styleAttribute < weight.styleAttribute ) {
      return -1;
    }
    if ( styleAttribute > weight.styleAttribute ) {
      return 1;
    }

    if ( idCount < weight.idCount ) {
      return -1;
    }
    if ( idCount > weight.idCount ) {
      return 1;
    }

    if ( attributeCount < weight.attributeCount ) {
      return -1;
    }
    if ( attributeCount > weight.attributeCount ) {
      return 1;
    }

    if ( elementCount < weight.elementCount ) {
      return -1;
    }
    if ( elementCount > weight.elementCount ) {
      return 1;
    }

    return 0;
  }

  public String toString() {
    return "SelectorWeight{" + "styleAttribute=" + styleAttribute + ", idCount=" + idCount + ", attributeCount="
        + attributeCount + ", elementCount=" + elementCount + '}';
  }
}

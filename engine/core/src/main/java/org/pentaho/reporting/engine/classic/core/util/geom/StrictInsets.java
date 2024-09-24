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

package org.pentaho.reporting.engine.classic.core.util.geom;

/**
 * Creation-Date: 09.07.2006, 20:22:06
 *
 * @author Thomas Morgner
 */
public class StrictInsets implements Cloneable {
  private long top;
  private long bottom;
  private long left;
  private long right;

  public StrictInsets() {
  }

  public StrictInsets( final long top, final long left, final long bottom, final long right ) {
    this.top = top;
    this.left = left;
    this.bottom = bottom;
    this.right = right;
  }

  public long getTop() {
    return top;
  }

  public void setTop( final long top ) {
    this.top = top;
  }

  public long getBottom() {
    return bottom;
  }

  public void setBottom( final long bottom ) {
    this.bottom = bottom;
  }

  public long getLeft() {
    return left;
  }

  public void setLeft( final long left ) {
    this.left = left;
  }

  public long getRight() {
    return right;
  }

  public void setRight( final long right ) {
    this.right = right;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final StrictInsets that = (StrictInsets) o;

    if ( bottom != that.bottom ) {
      return false;
    }
    if ( left != that.left ) {
      return false;
    }
    if ( right != that.right ) {
      return false;
    }
    if ( top != that.top ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = (int) ( top ^ ( top >>> 32 ) );
    result = 29 * result + (int) ( bottom ^ ( bottom >>> 32 ) );
    result = 29 * result + (int) ( left ^ ( left >>> 32 ) );
    result = 29 * result + (int) ( right ^ ( right >>> 32 ) );
    return result;
  }

  /**
   * Returns a string representation of the object. In general, the <code>toString</code> method returns a string that
   * "textually represents" this object. The result should be a concise but informative representation that is easy for
   * a person to read. It is recommended that all subclasses override this method.
   * <p/>
   * The <code>toString</code> method for class <code>Object</code> returns a string consisting of the name of the class
   * of which the object is an instance, the at-sign character `<code>@</code>', and the unsigned hexadecimal
   * representation of the hash code of the object. In other words, this method returns a string equal to the value of:
   * <blockquote>
   * 
   * <pre>
   * getClass().getName() + '@' + Integer.toHexString( hashCode() )
   * </pre>
   * 
   * </blockquote>
   *
   * @return a string representation of the object.
   */
  public String toString() {
    final StringBuffer b = new StringBuffer( 100 );
    b.append( getClass().getName() );
    b.append( "={top=" );
    b.append( top );
    b.append( ", left=" );
    b.append( left );
    b.append( ", bottom=" );
    b.append( bottom );
    b.append( ", right=" );
    b.append( right );
    b.append( '}' );
    return b.toString();
  }

  /**
   * Returns a copy of this bounds object. This method will never throw a 'CloneNotSupportedException'.
   *
   * @return the cloned instance.
   */
  public Object clone() {
    try {
      return super.clone();
    } catch ( CloneNotSupportedException e ) {
      throw new InternalError( "Clone must always be supported." );
    }
  }

}

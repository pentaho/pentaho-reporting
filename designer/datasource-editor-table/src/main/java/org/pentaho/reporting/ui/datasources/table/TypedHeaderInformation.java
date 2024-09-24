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

package org.pentaho.reporting.ui.datasources.table;

public class TypedHeaderInformation {
  private Class type;
  private String name;

  public TypedHeaderInformation( final Class type, final String name ) {
    this.type = type;
    this.name = name;
  }

  public Class getType() {
    return type;
  }

  public String getName() {
    return name;
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
   * <pre>
   * getClass().getName() + '@' + Integer.toHexString(hashCode())
   * </pre></blockquote>
   *
   * @return a string representation of the object.
   */
  public String toString() {
    return Messages.getString( "TypedHeaderInformation.Label", name, String.valueOf( type ) );
  }
}

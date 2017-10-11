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

package org.pentaho.reporting.engine.classic.core.modules.gui.base.internal;

import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingGuiContext;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

/**
 * Creation-Date: 01.12.2006, 18:49:32
 *
 * @author Thomas Morgner
 */
public class ActionCategory implements Comparable {
  private String resourceBase;
  private String resourcePrefix;
  private int position;
  private ResourceBundleSupport resources;
  private String name;
  private boolean userDefined;

  public ActionCategory() {
    name = ""; //$NON-NLS-1$
  }

  public void initialize( final SwingGuiContext context ) {
    resources =
        new ResourceBundleSupport( context.getLocale(), resourceBase, ObjectUtilities
            .getClassLoader( ActionCategory.class ) );
  }

  public String getResourceBase() {
    return resourceBase;
  }

  public void setResourceBase( final String resourceBase ) {
    this.resourceBase = resourceBase;
  }

  public String getResourcePrefix() {
    return resourcePrefix;
  }

  public void setResourcePrefix( final String resourcePrefix ) {
    this.resourcePrefix = resourcePrefix;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition( final int position ) {
    this.position = position;
  }

  public String getName() {
    return name;
  }

  public void setName( final String name ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    this.name = name;
  }

  /**
   * Returns the display name for the export action.
   *
   * @return The display name.
   */
  public String getDisplayName() {
    return resources.getString( resourcePrefix + "name" ); //$NON-NLS-1$
  }

  /**
   * Returns the short description for the export action.
   *
   * @return The short description.
   */
  public String getShortDescription() {
    return resources.getString( resourcePrefix + "description" ); //$NON-NLS-1$
  }

  /**
   * Returns the mnemonic key code.
   *
   * @return The code.
   */
  public Integer getMnemonicKey() {
    return resources.getOptionalMnemonic( resourcePrefix + "mnemonic" ); //$NON-NLS-1$
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final ActionCategory that = (ActionCategory) o;

    if ( position != that.position ) {
      return false;
    }
    if ( !name.equals( that.name ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = position;
    result = 29 * result + name.hashCode();
    return result;
  }

  /**
   * Compares this object with the specified object for order. Returns a negative integer, zero, or a positive integer
   * as this object is less than, equal to, or greater than the specified object.
   * <p>
   * <p/>
   *
   * @param o
   *          the Object to be compared.
   * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the
   *         specified object.
   * @throws ClassCastException
   *           if the specified object's type prevents it from being compared to this Object.
   */
  public int compareTo( final Object o ) {
    final ActionCategory other = (ActionCategory) o;
    if ( position < other.position ) {
      return -1;
    }
    if ( position > other.position ) {
      return 1;
    }
    return name.compareTo( other.name );
  }

  public String toString() {
    return "ActionCategory{" + //$NON-NLS-1$
        "name='" + name + '\'' + //$NON-NLS-1$
        ", position=" + position + //$NON-NLS-1$
        ", resourceBase='" + resourceBase + '\'' + //$NON-NLS-1$
        ", resourcePrefix='" + resourcePrefix + '\'' + //$NON-NLS-1$
        ", resources=" + resources + //$NON-NLS-1$
        '}';
  }

  public boolean isUserDefined() {
    return userDefined;
  }

  public void setUserDefined( final boolean userDefined ) {
    this.userDefined = userDefined;
  }
}

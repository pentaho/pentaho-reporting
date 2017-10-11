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

import java.util.ArrayList;

/**
 * Creation-Date: 01.12.2006, 20:01:32
 *
 * @author Thomas Morgner
 */
public class CategoryTreeItem implements Comparable {
  private CategoryTreeItem parent;
  private ActionCategory category;
  private ArrayList childs;
  private String name;
  private static final CategoryTreeItem[] EMPTY_CHILDS = new CategoryTreeItem[0];

  public CategoryTreeItem( final ActionCategory category ) {
    this.category = category;
    this.name = category.getName();
  }

  public String getName() {
    return name;
  }

  public CategoryTreeItem getParent() {
    return parent;
  }

  public void setParent( final CategoryTreeItem parent ) {
    this.parent = parent;
  }

  public ActionCategory getCategory() {
    return category;
  }

  public void add( final CategoryTreeItem item ) {
    if ( childs == null ) {
      childs = new ArrayList();
    }
    childs.add( item );
  }

  public CategoryTreeItem[] getChilds() {
    if ( childs == null ) {
      return CategoryTreeItem.EMPTY_CHILDS;
    }
    return (CategoryTreeItem[]) childs.toArray( new CategoryTreeItem[childs.size()] );
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
    final CategoryTreeItem other = (CategoryTreeItem) o;
    final int position = category.getPosition();
    final int otherPosition = other.getCategory().getPosition();
    if ( position < otherPosition ) {
      return -1;
    }
    if ( position > otherPosition ) {
      return 1;
    }
    return name.compareTo( other.name );
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( !( o instanceof CategoryTreeItem ) ) {
      return false;
    }

    final CategoryTreeItem that = (CategoryTreeItem) o;

    if ( !category.equals( that.category ) ) {
      return false;
    }
    if ( childs != null ? !childs.equals( that.childs ) : that.childs != null ) {
      return false;
    }
    if ( !name.equals( that.name ) ) {
      return false;
    }
    if ( parent != null ? !parent.equals( that.parent ) : that.parent != null ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result;
    result = ( parent != null ? parent.hashCode() : 0 );
    result = 31 * result + category.hashCode();
    result = 31 * result + ( childs != null ? childs.hashCode() : 0 );
    result = 31 * result + name.hashCode();
    return result;
  }

  public String toString() {
    return "CategoryTreeItem={name='" + name + "', category=" + category + '}'; // NON-NLS
  }
}

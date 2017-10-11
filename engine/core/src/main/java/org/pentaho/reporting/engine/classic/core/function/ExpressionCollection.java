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

package org.pentaho.reporting.engine.classic.core.function;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Collects all expressions used in the report. Unlike earlier versions of this class, now expressions can have a
 * null-name and more than one expression with the same name can exist in the collection. Expressions without a name
 * will not appear in the datarow and expressions with duplicate names will only appear once (the last expression added
 * will appear, according to the rules of duplicate entries in the datarow)
 *
 * @author Thomas Morgner
 */
public class ExpressionCollection implements Cloneable, Serializable {
  /**
   * Ordered storage for the Expressions.
   */
  private ArrayList<Expression> expressionList;

  /**
   * Creates a new expression collection (initially empty).
   */
  public ExpressionCollection() {
    expressionList = new ArrayList<Expression>();
  }

  /**
   * Creates a new expression collection, populated with the supplied expressions.
   *
   * @param expressions
   *          a collection of expressions.
   * @throws ClassCastException
   *           if the collection does not contain Expressions
   */
  public ExpressionCollection( final Collection expressions ) {
    this();
    addAll( expressions );
  }

  /**
   * Adds all expressions contained in the given collection to this expression collection. The expressions get
   * initialized during the adding process.
   *
   * @param expressions
   *          the expressions to be added.
   * @throws ClassCastException
   *           if the collection does not contain expressions
   */
  public void addAll( final Collection expressions ) {
    if ( expressions != null ) {
      final Iterator iterator = expressions.iterator();
      while ( iterator.hasNext() ) {
        final Expression f = (Expression) iterator.next();
        add( f );
      }
    }
  }

  /**
   * Returns the {@link Expression} with the specified name (or <code>null</code>).
   *
   * @param name
   *          the expression name (<code>null</code> not permitted).
   * @return The expression.
   */
  public Expression get( final String name ) {
    final int position = findExpressionByName( name );
    if ( position == -1 ) {
      return null;
    }
    return getExpression( position );
  }

  /**
   * Searches the list of expressions for an expression with the given name.
   *
   * @param name
   *          the name, never null.
   * @return the position of the expression with that name or -1 if no expression contains that name.
   */
  private int findExpressionByName( final String name ) {
    for ( int i = 0; i < expressionList.size(); i++ ) {
      final Expression expression = expressionList.get( i );
      if ( ObjectUtilities.equal( name, expression.getName() ) ) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Adds an expression to the collection. The expression is initialized before it is added to this collection.
   *
   * @param e
   *          the expression.
   */
  public void add( final Expression e ) {
    if ( e == null ) {
      throw new NullPointerException( "Expression is null" );
    }

    expressionList.add( e );
  }

  public void add( final int index, final Expression e ) {
    if ( e == null ) {
      throw new NullPointerException( "Expression is null" );
    }

    expressionList.add( index, e );
  }

  /**
   * Removes an expression from the collection.
   *
   * @param e
   *          the expression.
   * @return true if the expression can be removed from (was present in) the list
   * @throws NullPointerException
   *           if the given Expression is null.
   */
  public boolean removeExpression( final Expression e ) {
    if ( e == null ) {
      throw new NullPointerException();
    }
    return expressionList.remove( e );
  }

  /**
   * Removes an expression from the collection.
   *
   * @param index
   *          the index of the expression ro remove
   */
  public void removeExpression( final int index ) {
    expressionList.remove( index );
  }

  /**
   * Returns the number of active expressions in this collection.
   *
   * @return the number of expressions in this collection
   */
  public int size() {
    return expressionList.size();
  }

  /**
   * Returns the expression on the given position in the list.
   *
   * @param pos
   *          the position in the list.
   * @return the expression.
   * @throws IndexOutOfBoundsException
   *           if the given position is invalid
   */
  public Expression getExpression( final int pos ) {
    return expressionList.get( pos );
  }

  public int indexOf( final Expression element ) {
    if ( element == null ) {
      throw new NullPointerException();
    }
    return expressionList.indexOf( element );
  }

  public Expression set( final int index, final Expression element ) {
    if ( element == null ) {
      throw new NullPointerException();
    }
    return expressionList.set( index, element );
  }

  /**
   * Clones this expression collection and all expressions contained in the collection.
   *
   * @return The clone.
   */
  public ExpressionCollection clone() {
    try {
      final ExpressionCollection col = (ExpressionCollection) super.clone();
      col.expressionList = (ArrayList<Expression>) expressionList.clone();
      col.expressionList.clear();

      final Iterator it = expressionList.iterator();
      while ( it.hasNext() ) {
        final Expression ex = (Expression) it.next();
        col.expressionList.add( ex.getInstance() );
      }
      return col;
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException( "Unable to clone an expression: ", e );
    }
  }

  /**
   * Return all expressions contained in this collection as array.
   *
   * @return the expressions as array.
   */
  public Expression[] getExpressions() {
    return expressionList.toArray( new Expression[expressionList.size()] );
  }

  public boolean contains( final Expression expression ) {
    if ( expression == null ) {
      throw new NullPointerException();
    }
    return expressionList.contains( expression );
  }
}

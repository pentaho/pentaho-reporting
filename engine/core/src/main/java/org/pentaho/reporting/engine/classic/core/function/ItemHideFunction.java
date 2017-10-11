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

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.event.PageEventListener;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * The ItemHideFunction hides equal values in a group. Only the first changed value is printed. This function uses the
 * property <code>element</code> to define the name of the element in the ItemBand that should be made visible or
 * invisible by this function. The property <code>field</code> defines the field in the datasource or the expression
 * which should be used to determine the visibility.
 * <p/>
 * In simple cases, this function can be replaced with a style-formula on the visibly-stylekey:
 * <code>=NOT(ISCHANGED("fieldname"))</code>. In addition to the simple case, this function behaves special on
 * pagebreaks and intermediate group changes.
 *
 * @author Thomas Morgner
 */
public class ItemHideFunction extends AbstractFunction implements PageEventListener, LayoutProcessorFunction {
  /**
   * The last object. This is part of the internal state and therefore not serialized.
   */
  private transient Object lastObject;

  /**
   * The 'visible' flag. This is part of the internal state and therefore not serialized.
   */
  private transient boolean visible;

  /**
   * The 'first-in-group' flag. This is part of the internal state and therefore not serialized.
   */
  private transient boolean firstInGroup;

  /**
   * A flag indicating whether a group start resets the visiblity of the element.
   */
  private boolean ignoreGroupBreaks;
  /**
   * A flag indicating whether a page start resets the visiblity of the element.
   */
  private boolean ignorePageBreaks;

  /**
   * The name of the element that should be hidden.
   */
  private String element;
  /**
   * The data-row column that should be checked for changed values.
   */
  private String field;

  /**
   * Constructs an unnamed function.
   * <P>
   * Make sure to set the function name before it is used, or function initialisation will fail.
   */
  public ItemHideFunction() {
  }

  /**
   * Constructs a named function.
   * <P>
   * The field must be defined before using the function.
   *
   * @param name
   *          The function name.
   */
  public ItemHideFunction( final String name ) {
    this();
    setName( name );
  }

  /**
   * Returns whether a group start resets the visiblity of the element.
   *
   * @return false, if group breaks reset the visiblity, true otherwise.
   */
  public boolean isIgnoreGroupBreaks() {
    return ignoreGroupBreaks;
  }

  /**
   * Defines whether a group start resets the visiblity of the element.
   *
   * @param ignoreGroupBreaks
   *          false, if group breaks reset the visiblity, true otherwise.
   */
  public void setIgnoreGroupBreaks( final boolean ignoreGroupBreaks ) {
    this.ignoreGroupBreaks = ignoreGroupBreaks;
  }

  /**
   * Returns whether a page start resets the visiblity of the element.
   *
   * @return false, if page breaks reset the visiblity, true otherwise.
   */
  public boolean isIgnorePageBreaks() {
    return ignorePageBreaks;
  }

  /**
   * Returns whether a page start resets the visiblity of the element.
   *
   * @param ignorePageBreaks
   *          false, if page breaks reset the visiblity, true otherwise.
   */
  public void setIgnorePageBreaks( final boolean ignorePageBreaks ) {
    this.ignorePageBreaks = ignorePageBreaks;
  }

  /**
   * Returns the name of the element in the item band that should be set visible/invisible.
   *
   * @return The element name.
   */
  public String getElement() {
    return element;
  }

  /**
   * Sets the name of the element in the item band that should be set visible/invisible.
   *
   * @param name
   *          the element name (must not be null).
   */
  public void setElement( final String name ) {
    this.element = name;
  }

  /**
   * Returns the field used by the function. The field name corresponds to a column name in the report's data-row.
   *
   * @return The field name.
   */
  public String getField() {
    return field;
  }

  /**
   * Sets the field name for the function. The field name corresponds to a column name in the report's data-row.
   *
   * @param field
   *          the field name.
   */
  public void setField( final String field ) {
    this.field = field;
  }

  /**
   * Receives notification that a row of data is being processed. Reads the data from the field defined for this
   * function and hides the field if the value is equal to the last value and the this is not the first row of the item
   * group.
   *
   * @param event
   *          Information about the event.
   */
  public void itemsAdvanced( final ReportEvent event ) {
    final Object fieldValue = event.getDataRow().get( getField() );

    // is visible when last and current object are not equal
    // first element in group is always visible
    if ( firstInGroup == true ) {
      visible = true;
      firstInGroup = false;
    } else {
      visible = ( ObjectUtilities.equal( lastObject, fieldValue ) == false );
    }
    lastObject = fieldValue;
    applyVisible( event );
  }

  /**
   * Resets the state of the function when a new ItemGroup has started.
   *
   * @param event
   *          the report event.
   */
  public void itemsStarted( final ReportEvent event ) {
    if ( ignoreGroupBreaks == false ) {
      lastObject = null;
      firstInGroup = true;
    }
  }

  /**
   * Returns the function value, in this case the visibility of the defined element.
   *
   * @return The function value.
   */
  public Object getValue() {
    if ( visible ) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  /**
   * Receives notification that a page is completed.
   *
   * @param event
   *          The event.
   */
  public void pageFinished( final ReportEvent event ) {
  }

  /**
   * Receives notification that a new page is being started.
   *
   * @param event
   *          The event.
   */
  public void pageStarted( final ReportEvent event ) {
    if ( ignorePageBreaks ) {
      return;
    }

    final Object fieldValue = event.getDataRow().get( getField() );

    // is visible when last and current object are not equal
    // first element in group is always visible
    // after a page start, reset the function ...
    visible = true;
    firstInGroup = true;
    lastObject = fieldValue;

    applyVisible( event );
  }

  private void applyVisible( final ReportEvent event ) {
    final ItemBand itemBand = event.getReport().getItemBand();
    if ( itemBand == null ) {
      return;
    }

    final Element[] elements = FunctionUtilities.findAllElements( itemBand, getElement() );
    for ( int i = 0; i < elements.length; i++ ) {
      final Element e = elements[i];
      e.setVisible( visible );
    }
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final ItemHideFunction ih = (ItemHideFunction) super.getInstance();
    ih.lastObject = null;
    return ih;
  }

  /**
   * Receives notification that the report has started.
   *
   * @param event
   *          the event.
   */
  public void reportInitialized( final ReportEvent event ) {
    lastObject = null;
    firstInGroup = true;
  }
}

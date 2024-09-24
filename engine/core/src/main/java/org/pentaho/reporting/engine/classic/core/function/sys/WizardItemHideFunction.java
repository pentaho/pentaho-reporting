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

package org.pentaho.reporting.engine.classic.core.function.sys;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractElementFormatFunction;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;

public class WizardItemHideFunction extends AbstractElementFormatFunction implements StructureFunction {
  private static final Log logger = LogFactory.getLog( WizardItemHideFunction.class );
  private boolean pageStarted;

  public WizardItemHideFunction() {
  }

  public int getProcessingPriority() {
    // executed after the metadata has been applied, but before the style-expressions get applied.
    return 6000;
  }

  /**
   * Evaluates all defined style-expressions of the given element.
   *
   * @param e
   *          the element that should be updated.
   * @return true, if attributes or style were changed, false if no change was made.
   */
  protected boolean evaluateElement( final ReportElement e ) {
    if ( e == null ) {
      throw new NullPointerException();
    }

    boolean retval = false;

    final Object maybeShowChanging =
        e.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ONLY_SHOW_CHANGING_VALUES );
    if ( Boolean.TRUE.equals( maybeShowChanging ) ) {
      Object field = e.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.LABEL_FOR );
      if ( field == null ) {
        field = e.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD );
      }
      if ( field != null ) {
        final String fieldText = String.valueOf( field );
        if ( pageStarted || getDataRow().isChanged( fieldText ) ) {
          e.getStyle().setStyleProperty( ElementStyleKeys.VISIBLE, true );
        } else {
          e.getStyle().setStyleProperty( ElementStyleKeys.VISIBLE, false );
        }
        retval = true;
      }
    }

    return retval;
  }

  public void pageStarted( final ReportEvent event ) {
    pageStarted = true;
    super.pageStarted( event );
  }

  public void itemsStarted( final ReportEvent event ) {
    pageStarted = true;
    super.itemsStarted( event );
  }

  public void itemsAdvanced( final ReportEvent event ) {
    super.itemsAdvanced( event );
    pageStarted = false;
  }
}

/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package org.pentaho.reporting.designer.core.actions;

import org.pentaho.reporting.designer.core.actions.elements.MorphAction;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.engine.classic.core.Element;

import java.util.List;


public class ProtectedMethodHelper {
   public static void callUpdate( MorphAction action, ReportRenderContext oldContext, ReportRenderContext newContext ) {
     action.updateActiveContext( oldContext, newContext );
   }

  public static List<Element> getListElements( MorphAction action ) {
     return action.getSelectionModel().getSelectedElementsOfType( Element.class );
  }
}

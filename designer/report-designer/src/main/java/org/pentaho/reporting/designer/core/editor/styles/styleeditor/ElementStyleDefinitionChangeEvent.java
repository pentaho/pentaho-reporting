/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.designer.core.editor.styles.styleeditor;

import org.pentaho.reporting.engine.classic.core.style.css.ElementStyleDefinition;
import org.pentaho.reporting.engine.classic.core.style.css.ElementStyleRule;

import java.util.EventObject;

public class ElementStyleDefinitionChangeEvent extends EventObject {
  private ElementStyleDefinition styleDefinition;
  private ElementStyleRule styleRule;

  public ElementStyleDefinitionChangeEvent( final Object source,
                                            final ElementStyleDefinition styleDefinition,
                                            final ElementStyleRule styleRule ) {
    super( source );
    if ( styleDefinition == null ) {
      throw new IllegalStateException();
    }

    this.styleDefinition = styleDefinition;
    this.styleRule = styleRule;
  }

  public ElementStyleDefinition getStyleDefinition() {
    return styleDefinition;
  }

  public ElementStyleRule getStyleRule() {
    return styleRule;
  }
}

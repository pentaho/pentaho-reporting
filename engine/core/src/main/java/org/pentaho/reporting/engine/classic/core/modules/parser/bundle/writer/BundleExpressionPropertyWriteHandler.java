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



package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.ExpressionPropertyWriteHandler;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;

public interface BundleExpressionPropertyWriteHandler extends ExpressionPropertyWriteHandler {
  void initBundleContext( final WriteableDocumentBundle bundle, final BundleWriterState state );
}

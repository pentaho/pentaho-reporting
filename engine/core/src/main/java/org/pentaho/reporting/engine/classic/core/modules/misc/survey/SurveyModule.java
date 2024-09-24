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

package org.pentaho.reporting.engine.classic.core.modules.misc.survey;

import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaDataParser;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleElementRegistry;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterHandlerRegistry;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

import java.beans.PropertyEditorManager;

public class SurveyModule extends AbstractModule {
  public static final String NAMESPACE =
      "http://reporting.pentaho.org/namespaces/engine/classic/extensions/survey-scale/1.0";

  public static final String LOWEST = "lowest";
  public static final String HIGHEST = "highest";
  public static final String RANGE_LOWER_BOUND = "range-lower-bound";
  public static final String RANGE_UPPER_BOUND = "range-upper-bound";
  public static final String TICK_MARK_PAINT = "tick-mark-paint";
  public static final String LOWER_MARGIN = "lower-margin";
  public static final String UPPER_MARGIN = "upper-margin";
  public static final String DEFAULT_SHAPE = "default-shape";
  public static final String OUTLINE_STROKE = "outline-stroke";

  public SurveyModule() throws ModuleInitializeException {
    loadModuleInfo();
  }

  /**
   * Initializes the module. Use this method to perform all initial setup operations. This method is called only once in
   * a modules lifetime. If the initializing cannot be completed, throw a ModuleInitializeException to indicate the
   * error,. The module will not be available to the system.
   *
   * @throws ModuleInitializeException
   *           if an error ocurred while initializing the module.
   */
  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
    ElementTypeRegistry.getInstance().registerNamespacePrefix( NAMESPACE, "surveyscale" );
    BundleWriterHandlerRegistry.getInstance().setNamespaceHasCData( NAMESPACE, false );

    ElementMetaDataParser
        .initializeOptionalExpressionsMetaData( "org/pentaho/reporting/engine/classic/core/modules/misc/survey/meta-expressions.xml" );
    ElementMetaDataParser
        .initializeOptionalElementMetaData( "org/pentaho/reporting/engine/classic/core/modules/misc/survey/meta-elements.xml" );

    BundleElementRegistry.getInstance().registerGenericWriter( SurveyScaleType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericReader( SurveyScaleType.INSTANCE );
    PropertyEditorManager.registerEditor( SurveyScaleShapeType.class, SurveyScaleShapeTypePropertyEditor.class );
  }
}

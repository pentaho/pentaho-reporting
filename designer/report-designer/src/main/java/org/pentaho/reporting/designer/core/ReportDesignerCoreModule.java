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


package org.pentaho.reporting.designer.core;

import org.pentaho.reporting.designer.core.editor.drilldown.parser.ParameterDocumentResourceFactory;
import org.pentaho.reporting.designer.core.editor.drilldown.parser.ParameterDocumentXmlFactoryModule;
import org.pentaho.reporting.designer.core.inspections.InspectionsRegistry;
import org.pentaho.reporting.designer.core.inspections.impl.DeprecatedUsagesInspection;
import org.pentaho.reporting.designer.core.inspections.impl.DuplicateFieldInspection;
import org.pentaho.reporting.designer.core.inspections.impl.ExpressionsParameterInspection;
import org.pentaho.reporting.designer.core.inspections.impl.FontSizeInspection;
import org.pentaho.reporting.designer.core.inspections.impl.FormulaErrorInspection;
import org.pentaho.reporting.designer.core.inspections.impl.InvalidElementReferenceInspection;
import org.pentaho.reporting.designer.core.inspections.impl.InvalidFieldReferenceInspection;
import org.pentaho.reporting.designer.core.inspections.impl.InvalidFormatInspection;
import org.pentaho.reporting.designer.core.inspections.impl.InvalidGroupReferenceInspection;
import org.pentaho.reporting.designer.core.inspections.impl.InvalidQueryNameReferenceInspection;
import org.pentaho.reporting.designer.core.inspections.impl.MandatoryAttributeMissingInspection;
import org.pentaho.reporting.designer.core.inspections.impl.OverlappingElementsInspection;
import org.pentaho.reporting.designer.core.inspections.impl.ReportMigrationInspection;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

public class ReportDesignerCoreModule extends AbstractModule {
  public ReportDesignerCoreModule() throws ModuleInitializeException {
    loadModuleInfo();
  }

  /**
   * Initializes the module. Use this method to perform all initial setup operations. This method is called only once in
   * a modules lifetime. If the initializing cannot be completed, throw a ModuleInitializeException to indicate the
   * error,. The module will not be available to the system.
   *
   * @param subSystem the subSystem.
   * @throws ModuleInitializeException if an error ocurred while initializing the module.
   */
  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
    ParameterDocumentResourceFactory.register( ParameterDocumentXmlFactoryModule.class );
    InspectionsRegistry.getInstance().addInspection( new DeprecatedUsagesInspection() );
    InspectionsRegistry.getInstance().addInspection( new DuplicateFieldInspection() );
    InspectionsRegistry.getInstance().addInspection( new ExpressionsParameterInspection() );
    InspectionsRegistry.getInstance().addInspection( new FontSizeInspection() );
    InspectionsRegistry.getInstance().addInspection( new FormulaErrorInspection() );
    InspectionsRegistry.getInstance().addInspection( new InvalidElementReferenceInspection() );
    InspectionsRegistry.getInstance().addInspection( new InvalidFieldReferenceInspection() );
    InspectionsRegistry.getInstance().addInspection( new InvalidFormatInspection() );
    InspectionsRegistry.getInstance().addInspection( new InvalidGroupReferenceInspection() );
    InspectionsRegistry.getInstance().addInspection( new InvalidQueryNameReferenceInspection() );
    InspectionsRegistry.getInstance().addInspection( new MandatoryAttributeMissingInspection() );
    InspectionsRegistry.getInstance().addInspection( new OverlappingElementsInspection() );
    InspectionsRegistry.getInstance().addInspection( new ReportMigrationInspection() );
  }
}

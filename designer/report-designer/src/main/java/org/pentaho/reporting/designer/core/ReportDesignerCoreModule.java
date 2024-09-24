/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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

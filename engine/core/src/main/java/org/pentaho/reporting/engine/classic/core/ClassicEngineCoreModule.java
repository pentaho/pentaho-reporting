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

package org.pentaho.reporting.engine.classic.core;

import java.awt.BasicStroke;
import java.beans.PropertyEditorManager;
import java.io.InputStream;
import java.util.TimeZone;

import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaDataParser;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.parser.DataFactoryMetaDataResourceFactory;
import org.pentaho.reporting.engine.classic.core.metadata.parser.DataFactoryMetaDataXmlFactoryModule;
import org.pentaho.reporting.engine.classic.core.metadata.parser.ElementMetaDataResourceFactory;
import org.pentaho.reporting.engine.classic.core.metadata.parser.ElementMetaDataXmlFactoryModule;
import org.pentaho.reporting.engine.classic.core.metadata.parser.ExpressionMetaDataResourceFactory;
import org.pentaho.reporting.engine.classic.core.metadata.parser.ExpressionMetaDataXmlFactoryModule;
import org.pentaho.reporting.engine.classic.core.metadata.parser.GlobalMetaDefinitionResourceFactory;
import org.pentaho.reporting.engine.classic.core.metadata.parser.GlobalMetaDefinitionXmlFactoryModule;
import org.pentaho.reporting.engine.classic.core.metadata.parser.ReportPreProcessorMetaDataResourceFactory;
import org.pentaho.reporting.engine.classic.core.metadata.parser.ReportPreProcessorMetaDataXmlFactoryModule;
import org.pentaho.reporting.engine.classic.core.metadata.parser.ReportProcessTaskMetaDataResourceFactory;
import org.pentaho.reporting.engine.classic.core.metadata.parser.ReportProcessTaskMetaDataXmlFactoryModule;
import org.pentaho.reporting.engine.classic.core.metadata.propertyeditors.BasicStrokeEditor;
import org.pentaho.reporting.engine.classic.core.metadata.propertyeditors.BorderStylePropertyEditor;
import org.pentaho.reporting.engine.classic.core.metadata.propertyeditors.BoxSizingPropertyEditor;
import org.pentaho.reporting.engine.classic.core.metadata.propertyeditors.ElementTypePropertyEditor;
import org.pentaho.reporting.engine.classic.core.metadata.propertyeditors.StagingModePropertyEditor;
import org.pentaho.reporting.engine.classic.core.metadata.propertyeditors.TextDirectionPropertyEditor;
import org.pentaho.reporting.engine.classic.core.metadata.propertyeditors.TextWrapPropertyEditor;
import org.pentaho.reporting.engine.classic.core.metadata.propertyeditors.TimeZonePropertyEditor;
import org.pentaho.reporting.engine.classic.core.metadata.propertyeditors.VerticalTextAlignmentPropertyEditor;
import org.pentaho.reporting.engine.classic.core.metadata.propertyeditors.WhitespaceCollapsePropertyEditor;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.style.BoxSizing;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.TextDirection;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextWrap;
import org.pentaho.reporting.engine.classic.core.style.VerticalTextAlign;
import org.pentaho.reporting.engine.classic.core.style.WhitespaceCollapse;
import org.pentaho.reporting.engine.classic.core.util.StagingMode;
import org.pentaho.reporting.engine.classic.core.wizard.parser.DataSchemaXmlFactoryModule;
import org.pentaho.reporting.engine.classic.core.wizard.parser.DataSchemaXmlResourceFactory;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * The CoreModule is used to represent the base classes of JFreeReport in a PackageManager-compatible way. Modules may
 * request a certain core-version to be present by referencing to this module.
 * <p/>
 * This module is used to initialize the image and drawable factories. If the Pixie library is available, support for
 * WMF-files is added to the factories.
 *
 * @author Thomas Morgner
 */
public class ClassicEngineCoreModule extends AbstractModule {
  /**
   * The 'no-printer-available' property key.
   */
  public static final String NO_PRINTER_AVAILABLE_KEY = "org.pentaho.reporting.engine.classic.core.NoPrinterAvailable";

  /**
   * The G2 fontrenderer bug override configuration key.
   */
  public static final String FONTRENDERER_ISBUGGY_FRC_KEY =
      "org.pentaho.reporting.engine.classic.core.layout.fontrenderer.IsBuggyFRC";

  /**
   * The text aliasing configuration key.
   */
  public static final String FONTRENDERER_USEALIASING_KEY =
      "org.pentaho.reporting.engine.classic.core.layout.fontrenderer.UseAliasing";

  /**
   * A configuration key that defines, whether errors will abort the report processing. This defaults to true.
   */
  public static final String STRICT_ERROR_HANDLING_KEY =
      "org.pentaho.reporting.engine.classic.core.StrictErrorHandling";

  public static final String COMPLEX_TEXT_CONFIG_OVERRIDE_KEY =
      "org.pentaho.reporting.engine.classic.core.layout.fontrenderer.ComplexTextLayout";

  /**
   * Creates a new module definition based on the 'coremodule.properties' file of this package.
   *
   * @throws ModuleInitializeException
   *           if the file could not be loaded.
   */
  public ClassicEngineCoreModule() throws ModuleInitializeException {
    final InputStream in =
        ObjectUtilities.getResourceRelativeAsStream( "coremodule.properties", ClassicEngineCoreModule.class );
    if ( in == null ) {
      throw new ModuleInitializeException( "File 'coremodule.properties' not found in JFreeReport package." );
    }
    loadModuleInfo( in );
  }

  /**
   * Initializes the module. Use this method to perform all initial setup operations. This method is called only once in
   * a modules lifetime. If the initializing cannot be completed, throw a ModuleInitializeException to indicate the
   * error,. The module will not be available to the system.
   *
   * @param subSystem
   *          the subSystem.
   * @throws ModuleInitializeException
   *           if an error ocurred while initializing the module.
   */
  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
    DataSchemaXmlResourceFactory.register( DataSchemaXmlFactoryModule.class );
    DataFactoryMetaDataResourceFactory.register( DataFactoryMetaDataXmlFactoryModule.class );
    ElementMetaDataResourceFactory.register( ElementMetaDataXmlFactoryModule.class );
    ExpressionMetaDataResourceFactory.register( ExpressionMetaDataXmlFactoryModule.class );
    GlobalMetaDefinitionResourceFactory.register( GlobalMetaDefinitionXmlFactoryModule.class );
    ReportPreProcessorMetaDataResourceFactory.register( ReportPreProcessorMetaDataXmlFactoryModule.class );
    ReportProcessTaskMetaDataResourceFactory.register( ReportProcessTaskMetaDataXmlFactoryModule.class );

    StyleKey.registerClass( ElementStyleKeys.class );
    StyleKey.registerClass( TextStyleKeys.class );
    StyleKey.registerClass( BandStyleKeys.class );
    StyleKey.registerDefaults();

    ElementTypeRegistry.getInstance().registerNamespacePrefix( AttributeNames.Core.NAMESPACE, "core" );
    ElementTypeRegistry.getInstance().registerNamespacePrefix( AttributeNames.Crosstab.NAMESPACE, "crosstab" );
    ElementTypeRegistry.getInstance().registerNamespacePrefix( AttributeNames.Designtime.NAMESPACE, "report-designer" );
    ElementTypeRegistry.getInstance().registerNamespacePrefix( AttributeNames.Excel.NAMESPACE, "excel" );
    ElementTypeRegistry.getInstance().registerNamespacePrefix( AttributeNames.Html.NAMESPACE, "html" );
    ElementTypeRegistry.getInstance().registerNamespacePrefix( AttributeNames.Internal.NAMESPACE, "internal" );
    ElementTypeRegistry.getInstance().registerNamespacePrefix( AttributeNames.Pdf.NAMESPACE, "pdf" );
    ElementTypeRegistry.getInstance().registerNamespacePrefix( AttributeNames.Pentaho.NAMESPACE, "pentaho" );
    ElementTypeRegistry.getInstance().registerNamespacePrefix( AttributeNames.Swing.NAMESPACE, "action" );
    ElementTypeRegistry.getInstance().registerNamespacePrefix( AttributeNames.Table.NAMESPACE, "table" );
    ElementTypeRegistry.getInstance().registerNamespacePrefix( AttributeNames.Wizard.NAMESPACE, "wizard" );
    ElementTypeRegistry.getInstance().registerNamespacePrefix( AttributeNames.Xml.NAMESPACE, "xml" );

    PropertyEditorManager.registerEditor( BasicStroke.class, BasicStrokeEditor.class );
    PropertyEditorManager.registerEditor( BorderStyle.class, BorderStylePropertyEditor.class );
    PropertyEditorManager.registerEditor( BoxSizing.class, BoxSizingPropertyEditor.class );
    PropertyEditorManager.registerEditor( ElementType.class, ElementTypePropertyEditor.class );
    PropertyEditorManager.registerEditor( StagingMode.class, StagingModePropertyEditor.class );
    PropertyEditorManager.registerEditor( TextWrap.class, TextWrapPropertyEditor.class );
    PropertyEditorManager.registerEditor( TextDirection.class, TextDirectionPropertyEditor.class );
    PropertyEditorManager.registerEditor( TimeZone.class, TimeZonePropertyEditor.class );
    PropertyEditorManager.registerEditor( VerticalTextAlign.class, VerticalTextAlignmentPropertyEditor.class );
    PropertyEditorManager.registerEditor( WhitespaceCollapse.class, WhitespaceCollapsePropertyEditor.class );

    ElementMetaDataParser.registerNamespaces();
    ElementMetaDataParser
        .initializeOptionalElementMetaData( "org/pentaho/reporting/engine/classic/core/metadata/meta-elements.xml" );
    ElementMetaDataParser
        .initializeOptionalElementMetaData( "org/pentaho/reporting/engine/classic/core/metadata/meta-elements-data.xml" );
    ElementMetaDataParser
        .initializeOptionalElementMetaData( "org/pentaho/reporting/engine/classic/core/metadata/meta-elements-crosstab.xml" );

    ElementMetaDataParser
        .initializeOptionalExpressionsMetaData( "org/pentaho/reporting/engine/classic/core/function/meta-expressions.xml" );
    ElementMetaDataParser
        .initializeOptionalExpressionsMetaData( "org/pentaho/reporting/engine/classic/core/function/meta-expressions-deprecated.xml" );
    ElementMetaDataParser
        .initializeOptionalExpressionsMetaData( "org/pentaho/reporting/engine/classic/core/function/bool/meta-expressions.xml" );
    ElementMetaDataParser
        .initializeOptionalExpressionsMetaData( "org/pentaho/reporting/engine/classic/core/function/date/meta-expressions.xml" );
    ElementMetaDataParser
        .initializeOptionalExpressionsMetaData( "org/pentaho/reporting/engine/classic/core/function/numeric/meta-expressions.xml" );
    ElementMetaDataParser
        .initializeOptionalExpressionsMetaData( "org/pentaho/reporting/engine/classic/core/function/strings/meta-expressions.xml" );
    ElementMetaDataParser
        .initializeOptionalExpressionsMetaData( "org/pentaho/reporting/engine/classic/core/function/sys/meta-expressions.xml" );
    ElementMetaDataParser
        .initializeOptionalDataFactoryMetaData( "org/pentaho/reporting/engine/classic/core/metadata/meta-datafactory.xml" );
    ElementMetaDataParser
        .initializeOptionalReportPreProcessorMetaData( "org/pentaho/reporting/engine/classic/core/metadata/meta-report-preprocessors.xml" );
  }
}

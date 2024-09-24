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

package org.pentaho.reporting.engine.classic.core.designtime.datafactory;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DefaultReportEnvironment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportEnvironment;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeUtil;
import org.pentaho.reporting.engine.classic.core.util.LibLoaderResourceBundleFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import java.util.LinkedHashSet;
import java.util.List;

public class DataFactoryEditorSupport {
  public static final String SYNTAX_STYLE_NONE = "text/plain";
  public static final String SYNTAX_STYLE_ASSEMBLER_X86 = "text/asm";
  public static final String SYNTAX_STYLE_C = "text/c";
  public static final String SYNTAX_STYLE_CPLUSPLUS = "text/cpp";
  public static final String SYNTAX_STYLE_CSHARP = "text/cs";
  public static final String SYNTAX_STYLE_CSS = "text/css";
  public static final String SYNTAX_STYLE_FORTRAN = "text/fortran";
  public static final String SYNTAX_STYLE_GROOVY = "text/groovy";
  public static final String SYNTAX_STYLE_HTML = "text/html";
  public static final String SYNTAX_STYLE_JAVA = "text/java";
  public static final String SYNTAX_STYLE_JAVASCRIPT = "text/javascript";
  public static final String SYNTAX_STYLE_JSP = "text/jsp";
  public static final String SYNTAX_STYLE_LUA = "text/lua";
  public static final String SYNTAX_STYLE_MAKEFILE = "text/makefile";
  public static final String SYNTAX_STYLE_PERL = "text/perl";
  public static final String SYNTAX_STYLE_PHP = "text/php";
  public static final String SYNTAX_STYLE_PROPERTIES_FILE = "text/properties";
  public static final String SYNTAX_STYLE_PYTHON = "text/python";
  public static final String SYNTAX_STYLE_RUBY = "text/ruby";
  public static final String SYNTAX_STYLE_SAS = "text/sas";
  public static final String SYNTAX_STYLE_SQL = "text/sql";
  public static final String SYNTAX_STYLE_TCL = "text/tcl";
  public static final String SYNTAX_STYLE_UNIX_SHELL = "text/unix";
  public static final String SYNTAX_STYLE_WINDOWS_BATCH = "text/bat";
  public static final String SYNTAX_STYLE_XML = "text/xml";

  public static ScriptEngineFactory[] getScriptEngineLanguages() {
    final LinkedHashSet<ScriptEngineFactory> langSet = new LinkedHashSet<ScriptEngineFactory>();
    langSet.add( null );
    final List<ScriptEngineFactory> engineFactories = new ScriptEngineManager().getEngineFactories();
    for ( final ScriptEngineFactory engineFactory : engineFactories ) {
      langSet.add( engineFactory );
    }
    return langSet.toArray( new ScriptEngineFactory[langSet.size()] );
  }

  public static String mapLanguageToSyntaxHighlighting( final ScriptEngineFactory script ) {
    if ( script == null ) {
      return SYNTAX_STYLE_NONE;
    }

    final String language = script.getLanguageName();
    if ( "ECMAScript".equalsIgnoreCase( language ) || "js".equalsIgnoreCase( language )
        || "rhino".equalsIgnoreCase( language ) || "javascript".equalsIgnoreCase( language ) ) {
      return SYNTAX_STYLE_JAVASCRIPT;
    }
    if ( "groovy".equalsIgnoreCase( language ) ) {
      return SYNTAX_STYLE_GROOVY;
    }
    return SYNTAX_STYLE_NONE;
  }

  public static void configureDataFactoryForPreview( final DataFactory dataFactory, final DesignTimeContext context )
    throws ReportProcessingException {
    configureDataFactoryForPreview( dataFactory, context, new DataFactory[0] );
  }

  public static void configureDataFactoryForPreview( final DataFactory dataFactory, final DesignTimeContext context,
      final DataFactory[] additionalDataFactories ) throws ReportProcessingException {
    final AbstractReportDefinition report = context.getReport();
    final MasterReport masterReport = DesignTimeUtil.getMasterReport( report );
    final Configuration configuration;
    final ResourceKey contentBase;
    final ReportEnvironment reportEnvironment;
    final DataFactory reportDataFactory;
    final ResourceManager resourceManager;
    final ResourceBundleFactory resourceBundleFactory;

    if ( masterReport == null ) {
      contentBase = null;
      configuration = ClassicEngineBoot.getInstance().getGlobalConfig();
      reportEnvironment = new DefaultReportEnvironment( configuration );
      reportDataFactory = null;
      resourceManager = new ResourceManager();
      resourceBundleFactory = new LibLoaderResourceBundleFactory();
    } else {
      contentBase = masterReport.getContentBase();
      configuration = masterReport.getConfiguration();
      reportEnvironment = masterReport.getReportEnvironment();
      reportDataFactory = masterReport.getDataFactory();
      resourceManager = masterReport.getResourceManager();
      resourceBundleFactory = masterReport.getResourceBundleFactory();
    }

    final CompoundDataFactory compoundDataFactory = new CompoundDataFactory();
    compoundDataFactory.add( dataFactory );
    for ( int i = 0; i < additionalDataFactories.length; i++ ) {
      compoundDataFactory.add( additionalDataFactories[i] );
    }
    if ( reportDataFactory != null ) {
      compoundDataFactory.add( reportDataFactory );
    }

    final DesignTimeDataFactoryContext dataFactoryContext =
        new DesignTimeDataFactoryContext( configuration, resourceManager, contentBase, MasterReport
            .computeAndInitResourceBundleFactory( resourceBundleFactory, reportEnvironment ), compoundDataFactory );
    dataFactory.initialize( dataFactoryContext );
    compoundDataFactory.initialize( dataFactoryContext );
  }
}

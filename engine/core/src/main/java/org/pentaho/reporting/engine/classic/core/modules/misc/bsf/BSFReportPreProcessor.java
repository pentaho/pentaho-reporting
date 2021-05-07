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
 * Copyright (c) 2002-2021 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.misc.bsf;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.pentaho.reporting.engine.classic.core.AbstractReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.states.datarow.DefaultFlowController;
import org.pentaho.reporting.libraries.base.util.StringUtils;

public class BSFReportPreProcessor extends AbstractReportPreProcessor {
  private String preDataScript;
  private String script;
  private String language;

  public BSFReportPreProcessor() {
  }

  public String getPreDataScript() {
    return preDataScript;
  }

  public void setPreDataScript( final String preDataScript ) {
    this.preDataScript = preDataScript;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage( final String language ) {
    this.language = language;
  }

  public String getScript() {
    return script;
  }

  public void setScript( final String script ) {
    this.script = script;
  }

  public MasterReport performPreDataProcessing( final MasterReport definition,
      final DefaultFlowController flowController ) throws ReportProcessingException {
    if ( preDataScript == null || language == null || StringUtils.isEmpty( preDataScript, true ) ) {
      return definition;
    }
    checkScriptEvalAllowed();

    try {
      final BSFManager interpreter = new BSFManager();
      interpreter.declareBean( "definition", definition, MasterReport.class ); //$NON-NLS-1$
      interpreter.declareBean( "flowController", flowController, DefaultFlowController.class ); //$NON-NLS-1$
      final Object o = interpreter.eval( getLanguage(), "expression", 1, 1, preDataScript );
      if ( o instanceof MasterReport == false ) {
        throw new ReportDataFactoryException( "Not a MasterReport" );
      }
      return (MasterReport) o; //$NON-NLS-1$

    } catch ( BSFException e ) {
      throw new ReportDataFactoryException( "Failed to initialize the BSF-Framework", e );
    }
  }

  public MasterReport performPreProcessing( final MasterReport definition, final DefaultFlowController flowController )
    throws ReportProcessingException {
    if ( script == null || language == null || StringUtils.isEmpty( script, true ) ) {
      return definition;
    }
    checkScriptEvalAllowed();

    try {
      final BSFManager interpreter = new BSFManager();
      interpreter.declareBean( "definition", definition, MasterReport.class ); //$NON-NLS-1$
      interpreter.declareBean( "flowController", flowController, DefaultFlowController.class ); //$NON-NLS-1$
      final Object o = interpreter.eval( getLanguage(), "expression", 1, 1, script );
      if ( o instanceof MasterReport == false ) {
        throw new ReportDataFactoryException( "Not a MasterReport" );
      }
      return (MasterReport) o; //$NON-NLS-1$

    } catch ( BSFException e ) {
      throw new ReportDataFactoryException( "Failed to initialize the BSF-Framework", e );
    }
  }

  public SubReport performPreDataProcessing( final SubReport definition, final DefaultFlowController flowController )
    throws ReportProcessingException {
    if ( script == null || language == null ) {
      return definition;
    }
    checkScriptEvalAllowed();

    try {
      final BSFManager interpreter = new BSFManager();
      interpreter.declareBean( "definition", definition, MasterReport.class ); //$NON-NLS-1$
      interpreter.declareBean( "flowController", flowController, DefaultFlowController.class ); //$NON-NLS-1$
      final Object o = interpreter.eval( getLanguage(), "expression", 1, 1, preDataScript );
      if ( o instanceof SubReport == false ) {
        throw new ReportDataFactoryException( "Not a SubReport" );
      }
      return (SubReport) o; //$NON-NLS-1$

    } catch ( BSFException e ) {
      throw new ReportDataFactoryException( "Failed to initialize the BSF-Framework", e );
    }
  }

  public SubReport performPreProcessing( final SubReport definition, final DefaultFlowController flowController )
    throws ReportProcessingException {
    if ( script == null || language == null ) {
      return definition;
    }
    checkScriptEvalAllowed();

    try {
      final BSFManager interpreter = new BSFManager();
      interpreter.declareBean( "definition", definition, SubReport.class ); //$NON-NLS-1$
      interpreter.declareBean( "flowController", flowController, DefaultFlowController.class ); //$NON-NLS-1$
      final Object o = interpreter.eval( getLanguage(), "expression", 1, 1, script );
      if ( o instanceof SubReport == false ) {
        throw new ReportDataFactoryException( "Not a MasterReport" );
      }
      return (SubReport) o; //$NON-NLS-1$

    } catch ( BSFException e ) {
      throw new ReportDataFactoryException( "Failed to initialize the BSF-Framework", e );
    }
  }

  private void checkScriptEvalAllowed() throws ReportDataFactoryException {
    boolean allowScriptEval = ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(
      "org.pentaho.reporting.engine.classic.core.allowScriptEvaluation", "false" )
      .equalsIgnoreCase( "true" );

    if ( !allowScriptEval ) {
      throw new ReportDataFactoryException( "Scripts are prevented from running by default in order to avoid"
        + " potential remote code execution.  The system administrator must enable this capability." );
    }
  }
}

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

package org.pentaho.reporting.engine.classic.core.layout.output.crosstab;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.CrosstabOtherGroup;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.layout.Renderer;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultOutputFunction;
import org.pentaho.reporting.engine.classic.core.layout.output.RelationalGroupOutputHandler;

public class CrosstabOtherOutputHandler extends RelationalGroupOutputHandler {
  public CrosstabOtherOutputHandler() {
  }

  public void groupStarted( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    final int gidx = event.getState().getCurrentGroupIndex();
    final CrosstabOtherGroup group = (CrosstabOtherGroup) event.getReport().getGroup( gidx );
    final Band b = group.getHeader();
    final GroupBody groupBody = group.getBody();

    outputFunction.updateFooterArea( event );

    final Renderer renderer = outputFunction.getRenderer();
    renderer.startGroup( group, event.getState().getPredictedStateCount() );
    renderer.startSection( Renderer.SectionType.NORMALFLOW );
    renderer.add( b, outputFunction.getRuntime() );
    outputFunction.addSubReportMarkers( renderer.endSection() );

    renderer.startGroupBody( groupBody, event.getState().getPredictedStateCount() );
  }

  public void groupBodyFinished( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    CrosstabOutputHelper.closeCrosstabTable( outputFunction );

    final Renderer renderer = outputFunction.getRenderer();
    outputFunction.updateFooterArea( event );
    renderer.endGroupBody();
  }

  public void groupFinished( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    final int gidx = event.getState().getCurrentGroupIndex();
    final CrosstabOtherGroup g = (CrosstabOtherGroup) event.getReport().getGroup( gidx );
    final Band b = g.getFooter();

    final Renderer renderer = outputFunction.getRenderer();
    outputFunction.updateFooterArea( event );

    renderer.startSection( Renderer.SectionType.NORMALFLOW );
    renderer.add( b, outputFunction.getRuntime() );
    outputFunction.addSubReportMarkers( renderer.endSection() );
    renderer.endGroup();
  }

  public void itemsStarted( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    throw new ReportProcessingException( "A crosstab-other-group cannot contain a detail band. Never." );
  }

  public void itemsAdvanced( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    throw new ReportProcessingException( "A crosstab-other-group cannot contain a detail band. Never." );
  }

  public void itemsFinished( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    throw new ReportProcessingException( "A crosstab-other-group cannot contain a detail band. Never." );
  }

  public void summaryRowStart( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    throw new ReportProcessingException( "A crosstab-other-group cannot contain a summary band. Never." );
  }

  public void summaryRowEnd( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    throw new ReportProcessingException( "A crosstab-other-group cannot contain a summary band. Never." );
  }

  public void summaryRow( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    throw new ReportProcessingException( "A crosstab-other-group cannot contain a summary band. Never." );
  }
}

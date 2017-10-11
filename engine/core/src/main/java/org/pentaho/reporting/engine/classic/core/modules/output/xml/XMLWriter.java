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

package org.pentaho.reporting.engine.classic.core.modules.output.xml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.DetailsFooter;
import org.pentaho.reporting.engine.classic.core.DetailsHeader;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.NoDataBand;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;
import org.pentaho.reporting.engine.classic.core.function.OutputFunction;
import org.pentaho.reporting.engine.classic.core.layout.InlineSubreportMarker;
import org.pentaho.reporting.engine.classic.core.states.LayoutProcess;
import org.pentaho.reporting.engine.classic.core.states.ReportState;
import org.pentaho.reporting.engine.classic.core.states.process.SubReportProcessType;
import org.pentaho.reporting.libraries.xmlns.writer.CharacterEntityParser;

import java.io.IOException;
import java.io.Writer;

/**
 * The XMLWriter is the content creation function used to create the XML content. This implementation does no layouting,
 * the bands and elements are written in the defined order.
 * <p/>
 * The xml writer is intended as simple example on how to write OutputFunctions, the XML-code generated is very simple
 * and easy to understand. If you seek complexer XML-Outputs, have a look at the HTML-Writer, this implementation is
 * able to write XHTML output.
 *
 * @author Thomas Morgner
 * @deprecated The whole basic XML output is deprecated as it cannot handle inline subreports.
 */
public class XMLWriter extends AbstractFunction implements OutputFunction {
  private static final Log logger = LogFactory.getLog( XMLWriter.class );

  /**
   * the writer used to write the generated document.
   */
  private Writer w;

  /**
   * the dependency level.
   */
  private int depLevel;

  /**
   * the XMLEntity parser used to encode the xml characters.
   */
  private final CharacterEntityParser entityParser;
  private static final InlineSubreportMarker[] EMPTY_SUBREPORTS = new InlineSubreportMarker[0];

  /**
   * Creates a new XMLWriter function. The Writer gets a dependency level of -1.
   */
  public XMLWriter() {
    setDependencyLevel( LayoutProcess.LEVEL_PAGINATE );
    entityParser = CharacterEntityParser.createXMLEntityParser();
  }

  /**
   * returns the assigned writer for the output.
   *
   * @return the writer.
   */
  public Writer getWriter() {
    return w;
  }

  /**
   * Defines the writer for the XML-output.
   *
   * @param w
   *          the writer.
   */
  public void setWriter( final Writer w ) {
    this.w = w;
  }

  /**
   * Writes the band's elements into the assigned Writer.
   *
   * @param b
   *          the band that should be written.
   * @throws IOException
   *           if an IO-Error occurs.
   */
  private void writeBand( final Band b ) throws IOException {
    final Element[] elementBuffer = b.unsafeGetElementArray();
    final int elementCount = elementBuffer.length;
    for ( int i = 0; i < elementCount; i++ ) {
      final Element e = elementBuffer[i];
      if ( e instanceof Band ) {
        w.write( "<band>" );
        writeBand( (Band) e );
        w.write( "</band>" );
      } else {
        w.write( "<element name=\"" );
        w.write( entityParser.encodeEntities( e.getName() ) );
        w.write( "\">" );
        final String value = String.valueOf( e.getElementType().getValue( getRuntime(), e ) );
        w.write( entityParser.encodeEntities( value ) );
        w.write( "</element>" );
      }

    }
  }

  /**
   * Writes the report header.
   *
   * @param event
   *          the event.
   */
  public void reportStarted( final ReportEvent event ) {
    if ( event.getState().isPrepareRun() ) {
      return;
    }
    try {
      w.write( "<report>" );
      w.write( "<reportheader>" );
      writeBand( event.getReport().getReportHeader() );
      w.write( "</reportheader>" );
    } catch ( IOException ioe ) {
      XMLWriter.logger.error( "Error writing the band", ioe );
    }
  }

  /**
   * Writes the report footer.
   *
   * @param event
   *          the event.
   */
  public void reportFinished( final ReportEvent event ) {
    if ( event.getState().isPrepareRun() ) {
      return;
    }

    try {
      w.write( "<reportfooter>" );
      writeBand( event.getReport().getReportFooter() );
      w.write( "</reportfooter>" );
      w.write( "</report>" );
    } catch ( IOException ioe ) {
      XMLWriter.logger.error( "Error writing the band", ioe );
    }
  }

  /**
   * Writes the header of the current group.
   *
   * @param event
   *          the event.
   */
  public void groupStarted( final ReportEvent event ) {
    if ( event.getState().isPrepareRun() ) {
      return;
    }
    try {
      final Group g = event.getReport().getGroup( event.getState().getCurrentGroupIndex() );
      if ( g instanceof RelationalGroup ) {
        RelationalGroup rg = (RelationalGroup) g;
        w.write( "<groupheader name=\"" );
        w.write( entityParser.encodeEntities( g.getName() ) );
        w.write( "\">" );
        writeBand( rg.getHeader() );
        w.write( "</groupheader>" );
      }
    } catch ( IOException ioe ) {
      XMLWriter.logger.error( "Error writing the band", ioe );
    }
  }

  /**
   * Writes the footer of the current group.
   *
   * @param event
   *          the event.
   */
  public void groupFinished( final ReportEvent event ) {
    if ( event.getState().isPrepareRun() ) {
      return;
    }
    try {
      final Group g = event.getReport().getGroup( event.getState().getCurrentGroupIndex() );
      if ( g instanceof RelationalGroup ) {
        RelationalGroup rg = (RelationalGroup) g;
        w.write( "<groupfooter name=\"" );
        w.write( entityParser.encodeEntities( g.getName() ) );
        w.write( "\">" );
        writeBand( rg.getFooter() );
        w.write( "</groupfooter>" );
      }
    } catch ( IOException ioe ) {
      XMLWriter.logger.error( "Error writing the band", ioe );
    }
  }

  /**
   * Writes the itemband.
   *
   * @param event
   *          the event.
   */
  public void itemsAdvanced( final ReportEvent event ) {
    if ( event.getState().isPrepareRun() ) {
      return;
    }
    try {
      final ItemBand itemBand = event.getReport().getItemBand();
      if ( itemBand != null ) {
        w.write( "<itemband>" );
        writeBand( itemBand );
        w.write( "</itemband>" );
      }
    } catch ( IOException ioe ) {
      XMLWriter.logger.error( "Error writing the band", ioe );
    }
  }

  /**
   * Starts the itembands section.
   * <P>
   * The next events will be itemsAdvanced events until the itemsFinished event is raised.
   *
   * @param event
   *          The event.
   */
  public void itemsStarted( final ReportEvent event ) {
    if ( event.getState().isPrepareRun() ) {
      return;
    }
    try {
      if ( event.getState().getNumberOfRows() == 0 ) {
        final NoDataBand noDataBand = event.getReport().getNoDataBand();
        if ( noDataBand != null ) {
          w.write( "<nodata>" );
          writeBand( noDataBand );
          w.write( "</nodata>" );
        }
      }

      final DetailsHeader header = event.getReport().getDetailsHeader();
      if ( header != null ) {
        w.write( "<details-header>" );
        writeBand( header );
        w.write( "</details-header>" );
        w.write( "<items>" );
      }
    } catch ( IOException ioe ) {
      XMLWriter.logger.error( "Error writing the items tag", ioe );
    }
  }

  /**
   * Closes the itemband section.
   * <P>
   * The itemBand is finished, the report starts to close open groups.
   *
   * @param event
   *          The event.
   */
  public void itemsFinished( final ReportEvent event ) {
    if ( event.getState().isPrepareRun() ) {
      return;
    }
    try {
      final DetailsFooter header = event.getReport().getDetailsFooter();
      if ( header != null ) {
        w.write( "<details-footer>" );
        writeBand( header );
        w.write( "</details-footer>" );
        w.write( "</items>" );
      }
    } catch ( IOException ioe ) {
      XMLWriter.logger.error( "Error writing the items tag", ioe );
    }
  }

  /**
   * Return the self reference of this writer.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    return this;
  }

  /**
   * The dependency level defines the level of execution for this function. Higher dependency functions are executed
   * before lower dependency functions. For ordinary functions and expressions, the range for dependencies is defined to
   * start from 0 (lowest dependency possible) to 2^31 (upper limit of int).
   * <p/>
   * PageLayouter functions override the default behaviour an place them self at depency level -1, an so before any user
   * defined function.
   *
   * @return the level.
   */
  public int getDependencyLevel() {
    return depLevel;
  }

  /**
   * Overrides the depency level. Should be lower than any other function depency.
   *
   * @param deplevel
   *          the new depency level.
   */
  public void setDependencyLevel( final int deplevel ) {
    this.depLevel = deplevel;
  }

  /**
   * This method simply clones the function. The XMLWriter does not maintain large internal states and therefore need
   * not to be aware of any advanced optimizations.
   *
   * @return the derived function.
   */
  public OutputFunction deriveForStorage() {
    try {
      return (OutputFunction) clone();
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }

  /**
   * This method simply clones the function. The XMLWriter does not maintain large internal states and therefore need
   * not to be aware of any advanced optimizations.
   *
   * @return the derived function.
   */
  public OutputFunction deriveForPagebreak() {
    try {
      return (OutputFunction) clone();
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }

  /**
   * Clones the expression. The expression should be reinitialized after the cloning.
   * <P>
   * Expressions maintain no state, cloning is done at the beginning of the report processing to disconnect the
   * expression from any other object space.
   *
   * @return a clone of this expression.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public Object clone() throws CloneNotSupportedException {
    final XMLWriter o = (XMLWriter) super.clone();
    return o;
  }

  public InlineSubreportMarker[] getInlineSubreports() {
    return EMPTY_SUBREPORTS;
  }

  public void clearInlineSubreports( final SubReportProcessType inlineExecution ) {

  }

  public void restart( final ReportState state ) {

  }

  public void groupBodyFinished( final ReportEvent event ) {

  }

  public boolean createRollbackInformation() {
    return false;
  }
}

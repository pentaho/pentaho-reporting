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

package org.pentaho.reporting.libraries.xmlns.parser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * A SAX-Error handler that logs all incomming errors to the given log context.
 *
 * @author Thomas Morgner
 */
public class LoggingErrorHandler implements ErrorHandler {
  private static final Log defaultLogContext = LogFactory.getLog( LoggingErrorHandler.class );
  /**
   * @noinspection NonConstantLogger
   */
  private Log logContext;

  /**
   * Default-Constructor. Logs to a logger configured with this class name as category.
   */
  public LoggingErrorHandler() {
    logContext = defaultLogContext;
  }

  /**
   * Creates a new LoggingErrorHandler with the given (non-null) logcontext as log-target.
   *
   * @param logContext the logger that should receive the messages.
   */
  public LoggingErrorHandler( final Log logContext ) {
    if ( logContext == null ) {
      throw new NullPointerException();
    }
    this.logContext = logContext;
  }

  /**
   * Receive notification of a warning. <p/> <p>SAX parsers will use this method to report conditions that are not
   * errors or fatal errors as defined by the XML recommendation.  The default behaviour is to take no action.</p> <p/>
   * <p>The SAX parser must continue to provide normal parsing events after invoking this method: it should still be
   * possible for the application to process the document through to the end.</p> <p/> <p>Filters may use this method to
   * report other, non-XML warnings as well.</p>
   *
   * @param exception The warning information encapsulated in a SAX parse exception.
   * @throws org.xml.sax.SAXException Any SAX exception, possibly wrapping another exception.
   * @see org.xml.sax.SAXParseException
   */
  public void warning( final SAXParseException exception ) throws SAXException {
    if ( logContext.isDebugEnabled() ) {
      if ( exception.getMessage().startsWith( "URI was not reported to parser for entity" ) ) {
        // ignore that one. It is stupid! We do not use DTDs but old parsers like
        // the GNU thing complain about it ..
        return;
      }
      logContext.debug( "Parser-Warning", exception );
    }
  }

  /**
   * Receive notification of a recoverable error. <p/> <p>This corresponds to the definition of "error" in section 1.2
   * of the W3C XML 1.0 Recommendation.  For example, a validating parser would use this callback to report the
   * violation of a validity constraint.  The default behaviour is to take no action.</p> <p/> <p>The SAX parser must
   * continue to provide normal parsing events after invoking this method: it should still be possible for the
   * application to process the document through to the end. If the application cannot do so, then the parser should
   * report a fatal error even if the XML recommendation does not require it to do so.</p> <p/> <p>Filters may use this
   * method to report other, non-XML errors as well.</p>
   *
   * @param exception The error information encapsulated in a SAX parse exception.
   * @throws org.xml.sax.SAXException Any SAX exception, possibly wrapping another exception.
   * @see org.xml.sax.SAXParseException
   */
  public void error( final SAXParseException exception ) throws SAXException {
    if ( logContext.isWarnEnabled() ) {
      if ( logContext.isDebugEnabled() ) {
        logContext.warn( "Recoverable Parser-Error", exception );
      } else {
        logContext.warn( "Recoverable Parser-Error:" + exception.getMessage() );
      }
    }
  }

  /**
   * Receive notification of a non-recoverable error. <p/> <p><strong>There is an apparent contradiction between the
   * documentation for this method and the documentation for {@link org.xml.sax.ContentHandler#endDocument}. Until this
   * ambiguity is resolved in a future major release, clients should make no assumptions about whether endDocument()
   * will or will not be invoked when the parser has reported a fatalError() or thrown an exception.</strong></p> <p/>
   * <p>This corresponds to the definition of "fatal error" in section 1.2 of the W3C XML 1.0 Recommendation.  For
   * example, a parser would use this callback to report the violation of a well-formedness constraint.</p> <p/> <p>The
   * application must assume that the document is unusable after the parser has invoked this method, and should continue
   * (if at all) only for the sake of collecting additional error messages: in fact, SAX parsers are free to stop
   * reporting any other events once this method has been invoked.</p>
   *
   * @param exception The error information encapsulated in a SAX parse exception.
   * @throws org.xml.sax.SAXException Any SAX exception, possibly wrapping another exception.
   * @see org.xml.sax.SAXParseException
   */
  public void fatalError( final SAXParseException exception ) throws SAXException {
    if ( logContext.isErrorEnabled() ) {
      if ( logContext.isDebugEnabled() ) {
        logContext.error( "Fatal Parser-Error", exception );
      } else {
        logContext.error( "Fatal Parser-Error:" + exception.getMessage() );
      }
    }
  }
}

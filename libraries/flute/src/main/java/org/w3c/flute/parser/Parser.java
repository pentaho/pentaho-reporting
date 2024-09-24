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
 * Copyright (c) 2009 - 2017 Hitachi Vantara.  All rights reserved.
 */

package org.w3c.flute.parser;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionFactory;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.ErrorHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Locator;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorFactory;
import org.w3c.css.sac.SelectorList;
import org.w3c.css.sac.SimpleSelector;
import org.w3c.flute.parser.selectors.ConditionFactoryImpl;
import org.w3c.flute.parser.selectors.SelectorFactoryImpl;
import org.w3c.flute.util.Encoding;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Locale;

/**
 * A CSS2 parser
 *
 * @author Philippe Le Hegaret
 * @version $Revision$
 */
public class Parser implements org.w3c.css.sac.Parser, ParserConstants {

  // replaces all \t, \n, etc with this StringBuffer.
  static final StringBuffer SPACE = new StringBuffer( " " );

  // the document handler for the parser
  protected DocumentHandler documentHandler;
  // the error handler for the parser
  protected ErrorHandler errorHandler;
  // the input source for the parser
  protected InputSource source;

  protected ConditionFactory conditionFactory;
  protected SelectorFactory selectorFactory;

  // temporary place holder for pseudo-element ...
  protected String pseudoElt;

  /**
   * Creates a new Parser
   */
  public Parser() {
    this( (CharStream) null );
  }

  /**
   * @throws CSSException Not yet implemented
   * @@TODO
   */
  public void setLocale( Locale locale ) throws CSSException {
    throw new CSSException( CSSException.SAC_NOT_SUPPORTED_ERR );
  }

  /**
   * Set the document handler for this parser
   */
  public void setDocumentHandler( DocumentHandler handler ) {
    this.documentHandler = handler;
  }

  public void setSelectorFactory( SelectorFactory selectorFactory ) {
    this.selectorFactory = selectorFactory;
  }

  public void setConditionFactory( ConditionFactory conditionFactory ) {
    this.conditionFactory = conditionFactory;
  }

  /**
   * Set the error handler for this parser
   */
  public void setErrorHandler( ErrorHandler error ) {
    this.errorHandler = error;
  }

  /**
   * Main parse methods
   *
   * @param source the source of the style sheet.
   * @throws IOException  the source can't be parsed.
   * @throws CSSException the source is not CSS valid.
   */
  public void parseStyleSheet( InputSource source )
    throws CSSException, IOException {
    this.source = source;
    ReInit( getCharStreamWithLurk( source ) );
    if ( selectorFactory == null ) {
      selectorFactory = new SelectorFactoryImpl();
    }
    if ( conditionFactory == null ) {
      conditionFactory = new ConditionFactoryImpl();
    }

    parserUnit();
  }

  /**
   * Convenient method for URIs.
   *
   * @param systemId the fully resolved URI of the style sheet.
   * @throws IOException  the source can't be parsed.
   * @throws CSSException the source is not CSS valid.
   */
  public void parseStyleSheet( String systemId )
    throws CSSException, IOException {
    parseStyleSheet( new InputSource( systemId ) );
  }

  /**
   * This method parses only one rule (style rule or at-rule, except @charset).
   *
   * @param source the source of the rule.
   * @throws IOException  the source can't be parsed.
   * @throws CSSException the source is not CSS valid.
   */
  public void parseRule( InputSource source )
    throws CSSException, IOException {
    this.source = source;
    ReInit( getCharStreamWithLurk( source ) );

    if ( selectorFactory == null ) {
      selectorFactory = new SelectorFactoryImpl();
    }
    if ( conditionFactory == null ) {
      conditionFactory = new ConditionFactoryImpl();
    }
    _parseRule();
  }

  /**
   * This method parses a style declaration (including the surrounding curly braces).
   *
   * @param source the source of the style declaration.
   * @throws IOException  the source can't be parsed.
   * @throws CSSException the source is not CSS valid.
   */
  public void parseStyleDeclaration( InputSource source )
    throws CSSException, IOException {
    this.source = source;
    ReInit( getCharStreamWithLurk( source ) );

    if ( selectorFactory == null ) {
      selectorFactory = new SelectorFactoryImpl();
    }
    if ( conditionFactory == null ) {
      conditionFactory = new ConditionFactoryImpl();
    }
    _parseDeclarationBlock();
  }

  /**
   * This methods returns "http://www.w3.org/TR/REC-CSS2".
   *
   * @return the string "http://www.w3.org/TR/REC-CSS2".
   */
  public String getParserVersion() {
    return "http://www.w3.org/TR/REC-CSS2";
  }

  /**
   * Parse methods used by DOM Level 2 implementation.
   */
  public void parseImportRule( InputSource source )
    throws CSSException, IOException {
    this.source = source;
    ReInit( getCharStreamWithLurk( source ) );

    if ( selectorFactory == null ) {
      selectorFactory = new SelectorFactoryImpl();
    }
    if ( conditionFactory == null ) {
      conditionFactory = new ConditionFactoryImpl();
    }
    _parseImportRule();
  }

  public void parseMediaRule( InputSource source )
    throws CSSException, IOException {
    this.source = source;
    ReInit( getCharStreamWithLurk( source ) );

    if ( selectorFactory == null ) {
      selectorFactory = new SelectorFactoryImpl();
    }
    if ( conditionFactory == null ) {
      conditionFactory = new ConditionFactoryImpl();
    }
    _parseMediaRule();
  }

  public SelectorList parseSelectors( InputSource source )
    throws CSSException, IOException {
    this.source = source;
    ReInit( getCharStreamWithLurk( source ) );

    if ( selectorFactory == null ) {
      selectorFactory = new SelectorFactoryImpl();
    }
    if ( conditionFactory == null ) {
      conditionFactory = new ConditionFactoryImpl();
    }
    return _parseSelectors();
  }


  public String parseNamespaceToken( InputSource source )
    throws CSSException, IOException {
    this.source = source;
    ReInit( getCharStreamWithLurk( source ) );

    if ( selectorFactory == null ) {
      selectorFactory = new SelectorFactoryImpl();
    }
    if ( conditionFactory == null ) {
      conditionFactory = new ConditionFactoryImpl();
    }
    return _parseNamespaceToken();
  }

  public LexicalUnit parsePropertyValue( InputSource source )
    throws CSSException, IOException {
    this.source = source;
    ReInit( getCharStreamWithLurk( source ) );

    return expr();
  }

  public boolean parsePriority( InputSource source )
    throws CSSException, IOException {
    this.source = source;
    ReInit( getCharStreamWithLurk( source ) );

    return prio();
  }

  /**
   * Convert the source into a Reader. Used only by DOM Level 2 parser methods.
   */
  private Reader getReader( InputSource source ) throws IOException {
    if ( source.getCharacterStream() != null ) {
      return source.getCharacterStream();
    } else if ( source.getByteStream() != null ) {
      // My DOM level 2 implementation doesn't use this case.
      if ( source.getEncoding() == null ) {
        // unknown encoding, use ASCII as default.
        return new InputStreamReader( source.getByteStream(), "ASCII" );
      } else {
        return new InputStreamReader( source.getByteStream(),
          source.getEncoding() );
      }
    } else {
      // systemId
      // @@TODO
      throw new CSSException( "not yet implemented" );
    }
  }

  /**
   * Convert the source into a CharStream with encoding informations. The encoding can be found in the InputSource 
   * or in
   * the CSS document. Since this method marks the reader and make a reset after looking for the charset declaration,
   * you'll find the charset declaration into the stream.
   */
  private CharStream getCharStreamWithLurk( InputSource source )
    throws CSSException, IOException {
    if ( source.getCharacterStream() != null ) {
      // all encoding are supposed to be resolved by the user
      // return the reader
      return new Generic_CharStream( source.getCharacterStream(), 1, 1 );
    } else if ( source.getByteStream() == null ) {
      // @@CONTINUE ME. see also getReader() with systemId
      try {
        source.setByteStream( new URL( source.getURI() ).openStream() );
      } catch ( Exception e ) {
        try {
          source.setByteStream( new FileInputStream( source.getURI() ) );
        } catch ( IOException ex ) {
          throw new CSSException( "invalid url ?" );
        }
      }
    }
    String encoding = "ASCII";
    InputStream input = source.getByteStream();
    char c = ' ';

    if ( !input.markSupported() ) {
      input = new BufferedInputStream( input );
      source.setByteStream( input );
    }
    input.mark( 100 );
    c = (char) input.read();

    if ( c == '@' ) {
      // hum, is it a charset ?
      int size = 100;
      byte[] buf = new byte[ size ];
      input.read( buf, 0, 7 );
      String keyword = new String( buf, 0, 7 );
      if ( keyword.equals( "charset" ) ) {
        // Yes, this is the charset declaration !

        // here I don't use the right declaration : white space are ' '.
        while ( ( c = (char) input.read() ) == ' ' ) {
          // find the first quote
        }
        char endChar = c;
        int i = 0;

        if ( ( endChar != '"' ) && ( endChar != '\'' ) ) {
          // hum this is not a quote.
          throw new CSSException( "invalid charset declaration" );
        }

        while ( ( c = (char) input.read() ) != endChar ) {
          buf[ i++ ] = (byte) c;
          if ( i == size ) {
            byte[] old = buf;
            buf = new byte[ size + 100 ];
            System.arraycopy( old, 0, buf, 0, size );
            size += 100;
          }
        }
        while ( ( c = (char) input.read() ) == ' ' ) {
          // find the next relevant character
        }
        if ( c != ';' ) {
          // no semi colon at the end ?
          throw new CSSException( "invalid charset declaration: "
            + "missing semi colon" );
        }
        encoding = new String( buf, 0, i );
        if ( source.getEncoding() != null ) {
          // compare the two encoding informations.
          // For example, I don't accept to have ASCII and after UTF-8.
          // Is it really good ? That is the question.
          if ( !encoding.equals( source.getEncoding() ) ) {
            throw new CSSException( "invalid encoding information." );
          }
        }
      } // else no charset declaration available
    }
    // ok set the real encoding of this source.
    source.setEncoding( encoding );
    // set the real reader of this source.
    source.setCharacterStream( new InputStreamReader( source.getByteStream(),
      Encoding.getJavaEncoding( encoding ) ) );
    // reset the stream (leave the charset declaration in the stream).
    input.reset();

    return new Generic_CharStream( source.getCharacterStream(), 1, 1 );
  }

  private LocatorImpl currentLocator;

  private Locator getLocator() {
    if ( currentLocator == null ) {
      currentLocator = new LocatorImpl( this );
      return currentLocator;
    }
    return currentLocator.reInit( this );
  }

  private LocatorImpl getLocator( Token save ) {
    if ( currentLocator == null ) {
      currentLocator = new LocatorImpl( this, save );
      return currentLocator;
    }
    return currentLocator.reInit( this, save );
  }

  private void reportError( Locator l, Exception e ) {
    if ( errorHandler != null ) {
      if ( e instanceof ParseException ) {
        // construct a clean error message.
        ParseException pe = (ParseException) e;
        if ( pe.specialConstructor ) {
          StringBuffer errorM = new StringBuffer();
          if ( pe.currentToken != null ) {
            errorM.append( "encountered \"" )
              .append( pe.currentToken.next );
          }
          errorM.append( '"' );
          if ( pe.expectedTokenSequences.length != 0 ) {
            errorM.append( ". Was expecting one of: " );
            for ( int i = 0; i < pe.expectedTokenSequences.length; i++ ) {
              for ( int j = 0; j < pe.expectedTokenSequences[ i ].length; j++ ) {
                int kind = pe.expectedTokenSequences[ i ][ j ];
                if ( kind != S ) {
                  errorM.append( pe.tokenImage[ kind ] );
                  errorM.append( ' ' );
                }
              }
            }
          }
          errorHandler.error( new CSSParseException( errorM.toString(),
            l, e ) );
        } else {
          errorHandler.error( new CSSParseException( e.getMessage(),
            l, e ) );
        }
      } else if ( e == null ) {
        errorHandler.error( new CSSParseException( "error", l, null ) );
      } else {
        errorHandler.error( new CSSParseException( e.getMessage(), l, e ) );
      }
    }
  }

  private void reportWarningSkipText( Locator l, String text ) {
    if ( errorHandler != null && text != null ) {
      errorHandler.warning( new CSSParseException( "Skipping: " + text, l ) );
    }
  }

/*
 * The grammar of CSS2
 */

  /**
   * The main entry for the parser.
   *
   * @throws ParseException exception during the parse
   */
  final public void parserUnit() throws ParseException {
    try {
      documentHandler.startDocument( source );
      switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
        case CHARSET_SYM:
          charset();
          break;
        default:
          jj_la1[ 0 ] = jj_gen;
          ;
      }
      label_1:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case S:
          case CDO:
          case CDC:
          case ATKEYWORD:
            ;
            break;
          default:
            jj_la1[ 1 ] = jj_gen;
            break label_1;
        }
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case S:
            jj_consume_token( S );
            break;
          case CDO:
          case CDC:
          case ATKEYWORD:
            ignoreStatement();
            break;
          default:
            jj_la1[ 2 ] = jj_gen;
            jj_consume_token( -1 );
            throw new ParseException();
        }
      }
      label_2:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case IMPORT_SYM:
            ;
            break;
          default:
            jj_la1[ 3 ] = jj_gen;
            break label_2;
        }
        importDeclaration();
        label_3:
        while ( true ) {
          switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
            case CDO:
            case CDC:
            case ATKEYWORD:
              ;
              break;
            default:
              jj_la1[ 4 ] = jj_gen;
              break label_3;
          }
          ignoreStatement();
          label_4:
          while ( true ) {
            switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
              case S:
                ;
                break;
              default:
                jj_la1[ 5 ] = jj_gen;
                break label_4;
            }
            jj_consume_token( S );
          }
        }
      }
      label_5:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case NAMESPACE_SYM:
            ;
            break;
          default:
            jj_la1[ 6 ] = jj_gen;
            break label_5;
        }
        namespaceDeclaration();
        label_6:
        while ( true ) {
          switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
            case CDO:
            case CDC:
            case ATKEYWORD:
              ;
              break;
            default:
              jj_la1[ 7 ] = jj_gen;
              break label_6;
          }
          ignoreStatement();
          label_7:
          while ( true ) {
            switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
              case S:
                ;
                break;
              default:
                jj_la1[ 8 ] = jj_gen;
                break label_7;
            }
            jj_consume_token( S );
          }
        }
      }
      afterImportDeclaration();
      jj_consume_token( 0 );
    } finally {
      documentHandler.endDocument( source );
    }
  }

  final public void charset() throws ParseException {
    Token n;
    try {
      jj_consume_token( CHARSET_SYM );
      label_8:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case S:
            ;
            break;
          default:
            jj_la1[ 9 ] = jj_gen;
            break label_8;
        }
        jj_consume_token( S );
      }
      n = jj_consume_token( STRING );
      label_9:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case S:
            ;
            break;
          default:
            jj_la1[ 10 ] = jj_gen;
            break label_9;
        }
        jj_consume_token( S );
      }
      jj_consume_token( SEMICOLON );
    } catch ( ParseException e ) {
      reportError( getLocator( e.currentToken.next ), e );
      skipStatement();
      // reportWarningSkipText(getLocator(), skipStatement());

    } catch ( Exception e ) {
      reportError( getLocator(), e );
      skipStatement();
      // reportWarningSkipText(getLocator(), skipStatement());

    }
  }

  final public void afterImportDeclaration() throws ParseException {
    String ret;
    Locator l;
    label_10:
    while ( true ) {
      ;
      switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
        case LBRACKET:
        case ANY:
        case DOT:
        case COLON:
        case IDENT:
        case NAMESPACE_IDENT:
        case HASH:
          styleRule();
          break;
        case MEDIA_SYM:
          media();
          break;
        case PAGE_SYM:
          page();
          break;
        case FONT_FACE_SYM:
          fontFace();
          break;
        default:
          jj_la1[ 11 ] = jj_gen;
          l = getLocator();
          ret = skipStatement();
          if ( ( ret == null ) || ( ret.length() == 0 ) ) {
            {
              if ( true ) {
                return;
              }
            }
          }
          reportWarningSkipText( l, ret );
          if ( ret.charAt( 0 ) == '@' ) {
            documentHandler.ignorableAtRule( ret );
          }
      }
      label_11:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case CDO:
          case CDC:
          case ATKEYWORD:
            ;
            break;
          default:
            jj_la1[ 12 ] = jj_gen;
            break label_11;
        }
        ignoreStatement();
        label_12:
        while ( true ) {
          switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
            case S:
              ;
              break;
            default:
              jj_la1[ 13 ] = jj_gen;
              break label_12;
          }
          jj_consume_token( S );
        }
      }
    }
  }

  final public void ignoreStatement() throws ParseException {
    switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
      case CDO:
        jj_consume_token( CDO );
        break;
      case CDC:
        jj_consume_token( CDC );
        break;
      case ATKEYWORD:
        atRuleDeclaration();
        break;
      default:
        jj_la1[ 14 ] = jj_gen;
        jj_consume_token( -1 );
        throw new ParseException();
    }
  }

  /**
   * The import statement
   *
   * @throws ParseException exception during the parse
   */
  final public void importDeclaration() throws ParseException {
    Token n;
    String uri;
    MediaListImpl ml = new MediaListImpl();
    try {
      jj_consume_token( IMPORT_SYM );
      label_13:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case S:
            ;
            break;
          default:
            jj_la1[ 15 ] = jj_gen;
            break label_13;
        }
        jj_consume_token( S );
      }
      switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
        case STRING:
          n = jj_consume_token( STRING );
          uri = convertStringIndex( n.image, 1,
            n.image.length() - 1 );
          break;
        case URL:
          n = jj_consume_token( URL );
          uri = n.image.substring( 4, n.image.length() - 1 ).trim();
          if ( ( uri.charAt( 0 ) == '"' )
            || ( uri.charAt( 0 ) == '\'' ) ) {
            uri = uri.substring( 1, uri.length() - 1 );
          }
          break;
        default:
          jj_la1[ 16 ] = jj_gen;
          jj_consume_token( -1 );
          throw new ParseException();
      }
      label_14:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case S:
            ;
            break;
          default:
            jj_la1[ 17 ] = jj_gen;
            break label_14;
        }
        jj_consume_token( S );
      }
      switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
        case IDENT:
          mediaStatement( ml );
          break;
        default:
          jj_la1[ 18 ] = jj_gen;
          ;
      }
      jj_consume_token( SEMICOLON );
      label_15:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case S:
            ;
            break;
          default:
            jj_la1[ 19 ] = jj_gen;
            break label_15;
        }
        jj_consume_token( S );
      }
      if ( ml.getLength() == 0 ) {
        // see section 6.3 of the CSS2 recommandation.
        ml.addItem( "all" );
      }
      documentHandler.importStyle( uri, ml, null );
    } catch ( ParseException e ) {
      reportError( getLocator(), e );
      skipStatement();
      // reportWarningSkipText(getLocator(), skipStatement());

    }
  }

  /**
   * The namespace statement
   *
   * @throws ParseException exception during the parse
   */
  final public void namespaceDeclaration() throws ParseException {
    Token n;
    Token prefix = null;
    String uri;
    try {
      jj_consume_token( NAMESPACE_SYM );
      label_16:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case S:
            ;
            break;
          default:
            jj_la1[ 20 ] = jj_gen;
            break label_16;
        }
        jj_consume_token( S );
      }
      switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
        case IDENT:
          prefix = jj_consume_token( IDENT );
          label_17:
          while ( true ) {
            switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
              case S:
                ;
                break;
              default:
                jj_la1[ 21 ] = jj_gen;
                break label_17;
            }
            jj_consume_token( S );
          }
          break;
        default:
          jj_la1[ 22 ] = jj_gen;
          ;
      }
      n = jj_consume_token( URL );
      uri = n.image.substring( 4, n.image.length() - 1 ).trim();
      if ( ( uri.charAt( 0 ) == '"' ) || ( uri.charAt( 0 ) == '\'' ) ) {
        uri = uri.substring( 1, uri.length() - 1 );
      }
      jj_consume_token( SEMICOLON );
      label_18:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case S:
            ;
            break;
          default:
            jj_la1[ 23 ] = jj_gen;
            break label_18;
        }
        jj_consume_token( S );
      }
      if ( prefix == null ) {
        this.documentHandler.namespaceDeclaration( "", uri );
      } else {
        this.documentHandler.namespaceDeclaration( prefix.image, uri );
      }
    } catch ( ParseException e ) {
      reportError( getLocator(), e );
      skipStatement();
      // reportWarningSkipText(getLocator(), skipStatement());

    }
  }

  /**
   * @throws ParseException exception during the parse
   */
  final public void media() throws ParseException {
    boolean start = false;
    String ret;
    MediaListImpl ml = new MediaListImpl();
    try {
      jj_consume_token( MEDIA_SYM );
      label_19:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case S:
            ;
            break;
          default:
            jj_la1[ 24 ] = jj_gen;
            break label_19;
        }
        jj_consume_token( S );
      }
      mediaStatement( ml );
      start = true;
      documentHandler.startMedia( ml );
      jj_consume_token( LBRACE );
      label_20:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case S:
            ;
            break;
          default:
            jj_la1[ 25 ] = jj_gen;
            break label_20;
        }
        jj_consume_token( S );
      }
      label_21:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case CDO:
          case LBRACE:
          case DASHMATCH:
          case INCLUDES:
          case PLUS:
          case MINUS:
          case COMMA:
          case SEMICOLON:
          case PRECEDES:
          case LBRACKET:
          case ANY:
          case DOT:
          case COLON:
          case NONASCII:
          case STRING:
          case IDENT:
          case NUMBER:
          case URL:
          case NAMESPACE_IDENT:
          case PERCENTAGE:
          case HASH:
          case IMPORT_SYM:
          case MEDIA_SYM:
          case CHARSET_SYM:
          case PAGE_SYM:
          case FONT_FACE_SYM:
          case ATKEYWORD:
          case IMPORTANT_SYM:
          case UNICODERANGE:
          case FUNCTION:
          case UNKNOWN:
            ;
            break;
          default:
            jj_la1[ 26 ] = jj_gen;
            break label_21;
        }
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case LBRACKET:
          case ANY:
          case DOT:
          case COLON:
          case IDENT:
          case NAMESPACE_IDENT:
          case HASH:
            styleRule();
            break;
          case CDO:
          case LBRACE:
          case DASHMATCH:
          case INCLUDES:
          case PLUS:
          case MINUS:
          case COMMA:
          case SEMICOLON:
          case PRECEDES:
          case NONASCII:
          case STRING:
          case NUMBER:
          case URL:
          case PERCENTAGE:
          case IMPORT_SYM:
          case MEDIA_SYM:
          case CHARSET_SYM:
          case PAGE_SYM:
          case FONT_FACE_SYM:
          case ATKEYWORD:
          case IMPORTANT_SYM:
          case UNICODERANGE:
          case FUNCTION:
          case UNKNOWN:
            skipUnknownRule();
            break;
          default:
            jj_la1[ 27 ] = jj_gen;
            jj_consume_token( -1 );
            throw new ParseException();
        }
      }
      jj_consume_token( RBRACE );
      label_22:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case S:
            ;
            break;
          default:
            jj_la1[ 28 ] = jj_gen;
            break label_22;
        }
        jj_consume_token( S );
      }
    } catch ( ParseException e ) {
      reportError( getLocator(), e );
      skipStatement();
      // reportWarningSkipText(getLocator(), skipStatement());

    } finally {
      if ( start ) {
        documentHandler.endMedia( ml );
      }
    }
  }

  final public void mediaStatement( MediaListImpl ml ) throws ParseException {
    String m;
    m = medium();
    label_23:
    while ( true ) {
      switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[ 29 ] = jj_gen;
          break label_23;
      }
      jj_consume_token( COMMA );
      label_24:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case S:
            ;
            break;
          default:
            jj_la1[ 30 ] = jj_gen;
            break label_24;
        }
        jj_consume_token( S );
      }
      ml.addItem( m );
      m = medium();
    }
    ml.addItem( m );
  }

  /**
   * @throws ParseException exception during the parse
   */
  final public String medium() throws ParseException {
    Token n;
    n = jj_consume_token( IDENT );
    label_25:
    while ( true ) {
      switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
        case S:
          ;
          break;
        default:
          jj_la1[ 31 ] = jj_gen;
          break label_25;
      }
      jj_consume_token( S );
    }
    {
      if ( true ) {
        return convertIdent( n.image );
      }
    }
    throw new Error( "Missing return statement in function" );
  }

  /**
   * @throws ParseException exception during the parse
   */
  final public void page() throws ParseException {
    boolean start = false;
    Token n = null;
    String page = null;
    String pseudo = null;
    try {
      jj_consume_token( PAGE_SYM );
      label_26:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case S:
            ;
            break;
          default:
            jj_la1[ 32 ] = jj_gen;
            break label_26;
        }
        jj_consume_token( S );
      }
      switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
        case IDENT:
          n = jj_consume_token( IDENT );
          label_27:
          while ( true ) {
            switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
              case S:
                ;
                break;
              default:
                jj_la1[ 33 ] = jj_gen;
                break label_27;
            }
            jj_consume_token( S );
          }
          break;
        default:
          jj_la1[ 34 ] = jj_gen;
          ;
      }
      switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
        case COLON:
          pseudo = pseudo_page();
          break;
        default:
          jj_la1[ 35 ] = jj_gen;
          ;
      }
      if ( n != null ) {
        page = convertIdent( n.image );
      }
      jj_consume_token( LBRACE );
      label_28:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case S:
            ;
            break;
          default:
            jj_la1[ 36 ] = jj_gen;
            break label_28;
        }
        jj_consume_token( S );
      }
      start = true;
      documentHandler.startPage( page, pseudo );
      switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
        case IDENT:
          declaration();
          break;
        default:
          jj_la1[ 37 ] = jj_gen;
          ;
      }
      label_29:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case SEMICOLON:
            ;
            break;
          default:
            jj_la1[ 38 ] = jj_gen;
            break label_29;
        }
        jj_consume_token( SEMICOLON );
        label_30:
        while ( true ) {
          switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
            case S:
              ;
              break;
            default:
              jj_la1[ 39 ] = jj_gen;
              break label_30;
          }
          jj_consume_token( S );
        }
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case IDENT:
            declaration();
            break;
          default:
            jj_la1[ 40 ] = jj_gen;
            ;
        }
      }
      label_31:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case ATKEYWORD:
            ;
            break;
          default:
            jj_la1[ 41 ] = jj_gen;
            break label_31;
        }
        atRuleDeclaration();
      }
      jj_consume_token( RBRACE );
      label_32:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case S:
            ;
            break;
          default:
            jj_la1[ 42 ] = jj_gen;
            break label_32;
        }
        jj_consume_token( S );
      }
    } catch ( ParseException e ) {
      if ( errorHandler != null ) {
        LocatorImpl li = new LocatorImpl( this,
          e.currentToken.next.beginLine,
          e.currentToken.next.beginColumn - 1 );
        reportError( li, e );
        skipStatement();
        // reportWarningSkipText(li, skipStatement());
      } else {
        skipStatement();
      }
    } finally {
      if ( start ) {
        documentHandler.endPage( page, pseudo );
      }
    }
  }

  final public String pseudo_page() throws ParseException {
    Token n;
    jj_consume_token( COLON );
    n = jj_consume_token( IDENT );
    label_33:
    while ( true ) {
      switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
        case S:
          ;
          break;
        default:
          jj_la1[ 43 ] = jj_gen;
          break label_33;
      }
      jj_consume_token( S );
    }
    {
      if ( true ) {
        return convertIdent( n.image );
      }
    }
    throw new Error( "Missing return statement in function" );
  }

  final public void fontFace() throws ParseException {
    boolean start = false;
    try {
      jj_consume_token( FONT_FACE_SYM );
      label_34:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case S:
            ;
            break;
          default:
            jj_la1[ 44 ] = jj_gen;
            break label_34;
        }
        jj_consume_token( S );
      }
      jj_consume_token( LBRACE );
      label_35:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case S:
            ;
            break;
          default:
            jj_la1[ 45 ] = jj_gen;
            break label_35;
        }
        jj_consume_token( S );
      }
      start = true;
      documentHandler.startFontFace();
      switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
        case IDENT:
          declaration();
          break;
        default:
          jj_la1[ 46 ] = jj_gen;
          ;
      }
      label_36:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case SEMICOLON:
            ;
            break;
          default:
            jj_la1[ 47 ] = jj_gen;
            break label_36;
        }
        jj_consume_token( SEMICOLON );
        label_37:
        while ( true ) {
          switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
            case S:
              ;
              break;
            default:
              jj_la1[ 48 ] = jj_gen;
              break label_37;
          }
          jj_consume_token( S );
        }
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case IDENT:
            declaration();
            break;
          default:
            jj_la1[ 49 ] = jj_gen;
            ;
        }
      }
      jj_consume_token( RBRACE );
      label_38:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case S:
            ;
            break;
          default:
            jj_la1[ 50 ] = jj_gen;
            break label_38;
        }
        jj_consume_token( S );
      }
    } catch ( ParseException e ) {
      reportError( getLocator(), e );
      skipStatement();
      // reportWarningSkipText(getLocator(), skipStatement());

    } finally {
      if ( start ) {
        documentHandler.endFontFace();
      }
    }
  }

  /**
   * @throws ParseException exception during the parse
   */
  final public void atRuleDeclaration() throws ParseException {
    Token n;
    String ret;
    n = jj_consume_token( ATKEYWORD );
    ret = skipStatementNoSemicolon();
    reportWarningSkipText( getLocator(), ret );
    if ( ( ret != null ) && ( ret.charAt( 0 ) == '@' ) ) {
      documentHandler.ignorableAtRule( ret );
    }
  }

  final public void skipUnknownRule() throws ParseException {
    Token n;
    switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
      case ATKEYWORD:
        n = jj_consume_token( ATKEYWORD );
        break;
      case CDO:
        n = jj_consume_token( CDO );
        break;
      case CHARSET_SYM:
        n = jj_consume_token( CHARSET_SYM );
        break;
      case COMMA:
        n = jj_consume_token( COMMA );
        break;
      case DASHMATCH:
        n = jj_consume_token( DASHMATCH );
        break;
      case FONT_FACE_SYM:
        n = jj_consume_token( FONT_FACE_SYM );
        break;
      case FUNCTION:
        n = jj_consume_token( FUNCTION );
        break;
      case IMPORTANT_SYM:
        n = jj_consume_token( IMPORTANT_SYM );
        break;
      case IMPORT_SYM:
        n = jj_consume_token( IMPORT_SYM );
        break;
      case INCLUDES:
        n = jj_consume_token( INCLUDES );
        break;
      case LBRACE:
        n = jj_consume_token( LBRACE );
        break;
      case MEDIA_SYM:
        n = jj_consume_token( MEDIA_SYM );
        break;
      case NONASCII:
        n = jj_consume_token( NONASCII );
        break;
      case NUMBER:
        n = jj_consume_token( NUMBER );
        break;
      case PAGE_SYM:
        n = jj_consume_token( PAGE_SYM );
        break;
      case PERCENTAGE:
        n = jj_consume_token( PERCENTAGE );
        break;
      case STRING:
        n = jj_consume_token( STRING );
        break;
      case UNICODERANGE:
        n = jj_consume_token( UNICODERANGE );
        break;
      case URL:
        n = jj_consume_token( URL );
        break;
      case SEMICOLON:
        n = jj_consume_token( SEMICOLON );
        break;
      case PLUS:
        n = jj_consume_token( PLUS );
        break;
      case PRECEDES:
        n = jj_consume_token( PRECEDES );
        break;
      case MINUS:
        n = jj_consume_token( MINUS );
        break;
      case UNKNOWN:
        n = jj_consume_token( UNKNOWN );
        break;
      default:
        jj_la1[ 51 ] = jj_gen;
        jj_consume_token( -1 );
        throw new ParseException();
    }
    String ret;
    Locator loc = getLocator();
    ret = skipStatement();
    reportWarningSkipText( loc, ret );
    if ( ( ret != null ) && ( n.image.charAt( 0 ) == '@' ) ) {
      documentHandler.ignorableAtRule( ret );
    }
  }

  /**
   * @throws ParseException exception during the parse
   */
  final public char combinator() throws ParseException {
    char connector = ' ';
    switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
      case PLUS:
        jj_consume_token( PLUS );
        label_39:
        while ( true ) {
          switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
            case S:
              ;
              break;
            default:
              jj_la1[ 52 ] = jj_gen;
              break label_39;
          }
          jj_consume_token( S );
        }
      {
        if ( true ) {
          return '+';
        }
      }
      break;
      case PRECEDES:
        jj_consume_token( PRECEDES );
        label_40:
        while ( true ) {
          switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
            case S:
              ;
              break;
            default:
              jj_la1[ 53 ] = jj_gen;
              break label_40;
          }
          jj_consume_token( S );
        }
      {
        if ( true ) {
          return '>';
        }
      }
      break;
      case S:
        jj_consume_token( S );
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case PLUS:
          case PRECEDES:
            switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
              case PLUS:
                jj_consume_token( PLUS );
                connector = '+';
                break;
              case PRECEDES:
                jj_consume_token( PRECEDES );
                connector = '>';
                break;
              default:
                jj_la1[ 54 ] = jj_gen;
                jj_consume_token( -1 );
                throw new ParseException();
            }
            label_41:
            while ( true ) {
              switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
                case S:
                  ;
                  break;
                default:
                  jj_la1[ 55 ] = jj_gen;
                  break label_41;
              }
              jj_consume_token( S );
            }
            break;
          default:
            jj_la1[ 56 ] = jj_gen;
            ;
        }
      {
        if ( true ) {
          return connector;
        }
      }
      break;
      default:
        jj_la1[ 57 ] = jj_gen;
        jj_consume_token( -1 );
        throw new ParseException();
    }
    throw new Error( "Missing return statement in function" );
  }

  /**
   * @throws ParseException exception during the parse
   */
  final public String property() throws ParseException {
    Token n;
    n = jj_consume_token( IDENT );
    label_42:
    while ( true ) {
      switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
        case S:
          ;
          break;
        default:
          jj_la1[ 58 ] = jj_gen;
          break label_42;
      }
      jj_consume_token( S );
    }
    {
      if ( true ) {
        return convertIdent( n.image );
      }
    }
    throw new Error( "Missing return statement in function" );
  }

  /**
   * @throws ParseException exception during the parse
   */
  final public void styleRule() throws ParseException {
    boolean start = false;
    SelectorList l = null;
    Token save;
    Locator loc;
    try {
      l = selectorList();
      save = token;
      jj_consume_token( LBRACE );
      label_43:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case S:
            ;
            break;
          default:
            jj_la1[ 59 ] = jj_gen;
            break label_43;
        }
        jj_consume_token( S );
      }
      start = true;
      documentHandler.startSelector( l );
      switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
        case IDENT:
          declaration();
          break;
        default:
          jj_la1[ 60 ] = jj_gen;
          ;
      }
      label_44:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case SEMICOLON:
            ;
            break;
          default:
            jj_la1[ 61 ] = jj_gen;
            break label_44;
        }
        jj_consume_token( SEMICOLON );
        label_45:
        while ( true ) {
          switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
            case S:
              ;
              break;
            default:
              jj_la1[ 62 ] = jj_gen;
              break label_45;
          }
          jj_consume_token( S );
        }
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case IDENT:
            declaration();
            break;
          default:
            jj_la1[ 63 ] = jj_gen;
            ;
        }
      }
      jj_consume_token( RBRACE );
      label_46:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case S:
            ;
            break;
          default:
            jj_la1[ 64 ] = jj_gen;
            break label_46;
        }
        jj_consume_token( S );
      }
    } catch ( ThrowedParseException e ) {
      if ( errorHandler != null ) {
        LocatorImpl li = new LocatorImpl( this,
          e.e.currentToken.next.beginLine,
          e.e.currentToken.next.beginColumn - 1 );
        reportError( li, e.e );
      }
    } catch ( ParseException e ) {
      reportError( getLocator(), e );
      skipStatement();
      // reportWarningSkipText(getLocator(), skipStatement());

    } catch ( TokenMgrError e ) {
      reportWarningSkipText( getLocator(), skipStatement() );
    } finally {
      if ( start ) {
        documentHandler.endSelector( l );
      }
    }
  }

  final public SelectorList selectorList() throws ParseException {
    SelectorListImpl selectors = new SelectorListImpl();
    Selector selector;
    selector = selector();
    label_47:
    while ( true ) {
      switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[ 65 ] = jj_gen;
          break label_47;
      }
      jj_consume_token( COMMA );
      label_48:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case S:
            ;
            break;
          default:
            jj_la1[ 66 ] = jj_gen;
            break label_48;
        }
        jj_consume_token( S );
      }
      selectors.addSelector( selector );
      selector = selector();
    }
    selectors.addSelector( selector );
    {
      if ( true ) {
        return selectors;
      }
    }
    throw new Error( "Missing return statement in function" );
  }

  /**
   * @throws ParseException exception during the parse
   */
  final public Selector selector() throws ParseException {
    Selector selector;
    char comb;
    try {
      selector = simple_selector( null, ' ' );
      label_49:
      while ( true ) {
        if ( jj_2_1( 2 ) ) {
          ;
        } else {
          break label_49;
        }
        comb = combinator();
        selector = simple_selector( selector, comb );
      }
      label_50:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case S:
            ;
            break;
          default:
            jj_la1[ 67 ] = jj_gen;
            break label_50;
        }
        jj_consume_token( S );
      }
      {
        if ( true ) {
          return selector;
        }
      }
    } catch ( ParseException e ) {
     /*
     Token t = getToken(1);
     StringBuffer s = new StringBuffer();
     s.append(getToken(0).image);
     while ((t.kind != COMMA) && (t.kind != SEMICOLON) 
	    && (t.kind != LBRACE) && (t.kind != EOF)) {
	 s.append(t.image);
	 getNextToken();
	 t = getToken(1);
     }
     reportWarningSkipText(getLocator(), s.toString());
     */
      Token t = getToken( 1 );
      while ( ( t.kind != COMMA ) && ( t.kind != SEMICOLON )
        && ( t.kind != LBRACE ) && ( t.kind != EOF ) ) {
        getNextToken();
        t = getToken( 1 );
      }

      {
        if ( true ) {
          throw new ThrowedParseException( e );
        }
      }
    }
    throw new Error( "Missing return statement in function" );
  }

  /**
   * @throws ParseException exception during the parse
   */
  final public Selector simple_selector( Selector selector, char comb ) throws ParseException {
    SimpleSelector simple_current = null;
    Condition cond = null;

    pseudoElt = null;
    switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
      case ANY:
      case IDENT:
      case NAMESPACE_IDENT:
        simple_current = element_name();
        label_51:
        while ( true ) {
          switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
            case LBRACKET:
            case DOT:
            case COLON:
            case HASH:
              ;
              break;
            default:
              jj_la1[ 68 ] = jj_gen;
              break label_51;
          }
          switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
            case HASH:
              cond = hash( cond );
              break;
            case DOT:
              cond = _class( cond );
              break;
            case LBRACKET:
              cond = attrib( cond );
              break;
            case COLON:
              cond = pseudo( cond );
              break;
            default:
              jj_la1[ 69 ] = jj_gen;
              jj_consume_token( -1 );
              throw new ParseException();
          }
        }
        break;
      case HASH:
        cond = hash( cond );
        label_52:
        while ( true ) {
          switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
            case LBRACKET:
            case DOT:
            case COLON:
              ;
              break;
            default:
              jj_la1[ 70 ] = jj_gen;
              break label_52;
          }
          switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
            case DOT:
              cond = _class( cond );
              break;
            case LBRACKET:
              cond = attrib( cond );
              break;
            case COLON:
              cond = pseudo( cond );
              break;
            default:
              jj_la1[ 71 ] = jj_gen;
              jj_consume_token( -1 );
              throw new ParseException();
          }
        }
        break;
      case DOT:
        cond = _class( cond );
        label_53:
        while ( true ) {
          switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
            case LBRACKET:
            case DOT:
            case COLON:
            case HASH:
              ;
              break;
            default:
              jj_la1[ 72 ] = jj_gen;
              break label_53;
          }
          switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
            case HASH:
              cond = hash( cond );
              break;
            case DOT:
              cond = _class( cond );
              break;
            case LBRACKET:
              cond = attrib( cond );
              break;
            case COLON:
              cond = pseudo( cond );
              break;
            default:
              jj_la1[ 73 ] = jj_gen;
              jj_consume_token( -1 );
              throw new ParseException();
          }
        }
        break;
      case COLON:
        cond = pseudo( cond );
        label_54:
        while ( true ) {
          switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
            case LBRACKET:
            case DOT:
            case COLON:
            case HASH:
              ;
              break;
            default:
              jj_la1[ 74 ] = jj_gen;
              break label_54;
          }
          switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
            case HASH:
              cond = hash( cond );
              break;
            case DOT:
              cond = _class( cond );
              break;
            case LBRACKET:
              cond = attrib( cond );
              break;
            case COLON:
              cond = pseudo( cond );
              break;
            default:
              jj_la1[ 75 ] = jj_gen;
              jj_consume_token( -1 );
              throw new ParseException();
          }
        }
        break;
      case LBRACKET:
        cond = attrib( cond );
        label_55:
        while ( true ) {
          switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
            case LBRACKET:
            case DOT:
            case COLON:
            case HASH:
              ;
              break;
            default:
              jj_la1[ 76 ] = jj_gen;
              break label_55;
          }
          switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
            case HASH:
              cond = hash( cond );
              break;
            case DOT:
              cond = _class( cond );
              break;
            case LBRACKET:
              cond = attrib( cond );
              break;
            case COLON:
              cond = pseudo( cond );
              break;
            default:
              jj_la1[ 77 ] = jj_gen;
              jj_consume_token( -1 );
              throw new ParseException();
          }
        }
        break;
      default:
        jj_la1[ 78 ] = jj_gen;
        jj_consume_token( -1 );
        throw new ParseException();
    }
    if ( simple_current == null ) {
      simple_current = selectorFactory.createElementSelector( null, null );
    }
    if ( cond != null ) {
      simple_current = selectorFactory.createConditionalSelector( simple_current,
        cond );
    }
    if ( selector != null ) {
      switch( comb ) {
        case ' ':
          selector = selectorFactory.createDescendantSelector( selector,
            simple_current );
          break;
        case '+':
          selector =
            selectorFactory.createDirectAdjacentSelector( (short) 1,
              selector,
              simple_current );
          break;
        case '>':
          selector = selectorFactory.createChildSelector( selector,
            simple_current );
          break;
        default: {
          if ( true ) {
            throw new ParseException( "invalid state. send a bug report" );
          }
        }
      }
    } else {
      selector = simple_current;
    }
    if ( pseudoElt != null ) {
      selector = selectorFactory.createChildSelector( selector,
        selectorFactory.createPseudoElementSelector( null, pseudoElt ) );
    }
    {
      if ( true ) {
        return selector;
      }
    }
    throw new Error( "Missing return statement in function" );
  }

  /**
   * @throws ParseException exception during the parse
   */
  final public Condition _class( Condition pred ) throws ParseException {
    Token n;
    Condition c;
    jj_consume_token( DOT );
    n = jj_consume_token( IDENT );
    c = conditionFactory.createClassCondition( null, n.image );
    if ( pred == null ) {
      {
        if ( true ) {
          return c;
        }
      }
    } else {
      {
        if ( true ) {
          return conditionFactory.createAndCondition( pred, c );
        }
      }
    }
    throw new Error( "Missing return statement in function" );
  }

  /**
   * @throws ParseException exception during the parse
   */
  final public SimpleSelector element_name() throws ParseException {
    Token n;
    switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
      case IDENT:
        n = jj_consume_token( IDENT );
      {
        if ( true ) {
          return selectorFactory.createElementSelector( null, convertIdent( n.image ) );
        }
      }
      break;
      case NAMESPACE_IDENT:
        n = jj_consume_token( NAMESPACE_IDENT );
      {
        if ( true ) {
          return selectorFactory.createElementSelector( null, convertIdent( n.image ) );
        }
      }
      break;
      case ANY:
        jj_consume_token( ANY );
      {
        if ( true ) {
          return selectorFactory.createElementSelector( null, null );
        }
      }
      break;
      default:
        jj_la1[ 79 ] = jj_gen;
        jj_consume_token( -1 );
        throw new ParseException();
    }
    throw new Error( "Missing return statement in function" );
  }

  /**
   * @throws ParseException exception during the parse
   */
  final public Condition attrib( Condition pred ) throws ParseException {
    int cases = 0;
    Token att = null;
    Token val = null;
    String attValue = null;
    jj_consume_token( LBRACKET );
    label_56:
    while ( true ) {
      switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
        case S:
          ;
          break;
        default:
          jj_la1[ 80 ] = jj_gen;
          break label_56;
      }
      jj_consume_token( S );
    }
    switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
      case IDENT:
        att = jj_consume_token( IDENT );
        break;
      case NAMESPACE_IDENT:
        att = jj_consume_token( NAMESPACE_IDENT );
        break;
      default:
        jj_la1[ 81 ] = jj_gen;
        jj_consume_token( -1 );
        throw new ParseException();
    }
    label_57:
    while ( true ) {
      switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
        case S:
          ;
          break;
        default:
          jj_la1[ 82 ] = jj_gen;
          break label_57;
      }
      jj_consume_token( S );
    }
    switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
      case DASHMATCH:
      case INCLUDES:
      case EQ:
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case EQ:
            jj_consume_token( EQ );
            cases = 1;
            break;
          case INCLUDES:
            jj_consume_token( INCLUDES );
            cases = 2;
            break;
          case DASHMATCH:
            jj_consume_token( DASHMATCH );
            cases = 3;
            break;
          default:
            jj_la1[ 83 ] = jj_gen;
            jj_consume_token( -1 );
            throw new ParseException();
        }
        label_58:
        while ( true ) {
          switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
            case S:
              ;
              break;
            default:
              jj_la1[ 84 ] = jj_gen;
              break label_58;
          }
          jj_consume_token( S );
        }
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case IDENT:
            val = jj_consume_token( IDENT );
            attValue = val.image;
            break;
          case STRING:
            val = jj_consume_token( STRING );
            attValue = convertStringIndex( val.image, 1, val.image.length() - 1 );
            break;
          default:
            jj_la1[ 85 ] = jj_gen;
            jj_consume_token( -1 );
            throw new ParseException();
        }
        label_59:
        while ( true ) {
          switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
            case S:
              ;
              break;
            default:
              jj_la1[ 86 ] = jj_gen;
              break label_59;
          }
          jj_consume_token( S );
        }
        break;
      default:
        jj_la1[ 87 ] = jj_gen;
        ;
    }
    jj_consume_token( RBRACKET );
    String name = convertIdent( att.image );
    Condition c;
    switch( cases ) {
      case 0:
        c = conditionFactory.createAttributeCondition( name, null, false, null );
        break;
      case 1:
        c = conditionFactory.createAttributeCondition( name, null, false,
          attValue );
        break;
      case 2:
        c = conditionFactory.createOneOfAttributeCondition( name, null, false,
          attValue );
        break;
      case 3:
        c = conditionFactory.createBeginHyphenAttributeCondition( name, null,
          false,
          attValue );
        break;
      default:
        // never reached.
        c = null;
    }
    if ( pred == null ) {
      {
        if ( true ) {
          return c;
        }
      }
    } else {
      {
        if ( true ) {
          return conditionFactory.createAndCondition( pred, c );
        }
      }
    }
    throw new Error( "Missing return statement in function" );
  }

  /**
   * @throws ParseException exception during the parse
   */
  final public Condition pseudo( Condition pred ) throws ParseException {
    Token n;
    Token language;
    jj_consume_token( COLON );
    switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
      case IDENT:
        n = jj_consume_token( IDENT );
        String s = convertIdent( n.image );
        if ( s.equals( "first-letter" ) || s.equals( "first-line" ) ) {
          if ( pseudoElt != null ) {
            {
              if ( true ) {
                throw new CSSParseException( "duplicate pseudo element definition "
                  + s, getLocator() );
              }
            }
          } else {
            pseudoElt = s;
            {
              if ( true ) {
                return pred;
              }
            }
          }
        } else {
          Condition c =
            conditionFactory.createPseudoClassCondition( null, s );
          if ( pred == null ) {
            {
              if ( true ) {
                return c;
              }
            }
          } else {
            {
              if ( true ) {
                return conditionFactory.createAndCondition( pred, c );
              }
            }
          }
        }
        break;
      case FUNCTION:
        n = jj_consume_token( FUNCTION );
        label_60:
        while ( true ) {
          switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
            case S:
              ;
              break;
            default:
              jj_la1[ 88 ] = jj_gen;
              break label_60;
          }
          jj_consume_token( S );
        }
        language = jj_consume_token( IDENT );
        label_61:
        while ( true ) {
          switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
            case S:
              ;
              break;
            default:
              jj_la1[ 89 ] = jj_gen;
              break label_61;
          }
          jj_consume_token( S );
        }
        jj_consume_token( LPARAN );
        String f = convertIdent( n.image );
        if ( f.equals( "lang(" ) ) {
          Condition d =
            conditionFactory.createLangCondition( convertIdent( language.image ) );
          if ( pred == null ) {
            {
              if ( true ) {
                return d;
              }
            }
          } else {
            {
              if ( true ) {
                return conditionFactory.createAndCondition( pred, d );
              }
            }
          }
        } else {
          {
            if ( true ) {
              throw new CSSParseException( "invalid pseudo function name "
                + f, getLocator() );
            }
          }
        }
        break;
      default:
        jj_la1[ 90 ] = jj_gen;
        jj_consume_token( -1 );
        throw new ParseException();
    }
    throw new Error( "Missing return statement in function" );
  }

  /**
   * @throws ParseException exception during the parse
   */
  final public Condition hash( Condition pred ) throws ParseException {
    Token n;
    n = jj_consume_token( HASH );
    Condition d =
      conditionFactory.createIdCondition( n.image.substring( 1 ) );
    if ( pred == null ) {
      {
        if ( true ) {
          return d;
        }
      }
    } else {
      {
        if ( true ) {
          return conditionFactory.createAndCondition( pred, d );
        }
      }
    }
    throw new Error( "Missing return statement in function" );
  }

  /**
   * @throws ParseException exception during the parse
   */
  final public void declaration() throws ParseException {
    boolean important = false;
    String name;
    LexicalUnit exp;
    Token save;
    try {
      name = property();
      save = token;
      jj_consume_token( COLON );
      label_62:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case S:
            ;
            break;
          default:
            jj_la1[ 91 ] = jj_gen;
            break label_62;
        }
        jj_consume_token( S );
      }
      exp = expr();
      switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
        case IMPORTANT_SYM:
          important = prio();
          break;
        default:
          jj_la1[ 92 ] = jj_gen;
          ;
      }
      documentHandler.property( name, exp, important );
    } catch ( JumpException e ) {
      skipAfterExpression();
      // reportWarningSkipText(getLocator(), skipAfterExpression());

    } catch ( NumberFormatException e ) {
      if ( errorHandler != null ) {
        errorHandler.error( new CSSParseException( "Invalid number "
          + e.getMessage(),
          getLocator(),
          e ) );
      }
      reportWarningSkipText( getLocator(), skipAfterExpression() );
    } catch ( ParseException e ) {
      if ( errorHandler != null ) {
        if ( e.currentToken != null ) {
          LocatorImpl li = new LocatorImpl( this,
            e.currentToken.next.beginLine,
            e.currentToken.next.beginColumn - 1 );
          reportError( li, e );
        } else {
          reportError( getLocator(), e );
        }
        skipAfterExpression();
         /*
   LocatorImpl loc = (LocatorImpl) getLocator();
	 loc.column--;
	 reportWarningSkipText(loc, skipAfterExpression());
	 */
      } else {
        skipAfterExpression();
      }
    }
  }

  /**
   * @throws ParseException exception during the parse
   */
  final public boolean prio() throws ParseException {
    jj_consume_token( IMPORTANT_SYM );
    label_63:
    while ( true ) {
      switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
        case S:
          ;
          break;
        default:
          jj_la1[ 93 ] = jj_gen;
          break label_63;
      }
      jj_consume_token( S );
    }
    {
      if ( true ) {
        return true;
      }
    }
    throw new Error( "Missing return statement in function" );
  }

  /**
   * @throws ParseException exception during the parse
   */
  final public LexicalUnitImpl operator( LexicalUnitImpl prev ) throws ParseException {
    Token n;
    switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
      case DIV:
        n = jj_consume_token( DIV );
        label_64:
        while ( true ) {
          switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
            case S:
              ;
              break;
            default:
              jj_la1[ 94 ] = jj_gen;
              break label_64;
          }
          jj_consume_token( S );
        }
      {
        if ( true ) {
          return LexicalUnitImpl.createSlash( n.beginLine,
            n.beginColumn,
            prev );
        }
      }
      break;
      case COMMA:
        n = jj_consume_token( COMMA );
        label_65:
        while ( true ) {
          switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
            case S:
              ;
              break;
            default:
              jj_la1[ 95 ] = jj_gen;
              break label_65;
          }
          jj_consume_token( S );
        }
      {
        if ( true ) {
          return LexicalUnitImpl.createComma( n.beginLine,
            n.beginColumn,
            prev );
        }
      }
      break;
      default:
        jj_la1[ 96 ] = jj_gen;
        jj_consume_token( -1 );
        throw new ParseException();
    }
    throw new Error( "Missing return statement in function" );
  }

  /**
   * @throws ParseException exception during the parse
   */
  final public LexicalUnit expr() throws ParseException {
    LexicalUnitImpl first, res;
    char op;
    first = term( null );
    res = first;
    label_66:
    while ( true ) {
      switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
        case PLUS:
        case MINUS:
        case COMMA:
        case DIV:
        case STRING:
        case IDENT:
        case NUMBER:
        case URL:
        case NAMESPACE_IDENT:
        case PERCENTAGE:
        case PT:
        case MM:
        case CM:
        case PC:
        case IN:
        case PX:
        case EMS:
        case EXS:
        case DEG:
        case RAD:
        case GRAD:
        case MS:
        case SECOND:
        case HZ:
        case KHZ:
        case DIMEN:
        case HASH:
        case UNICODERANGE:
        case FUNCTION:
          ;
          break;
        default:
          jj_la1[ 97 ] = jj_gen;
          break label_66;
      }
      switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
        case COMMA:
        case DIV:
          res = operator( res );
          break;
        default:
          jj_la1[ 98 ] = jj_gen;
          ;
      }
      res = term( res );
    }
    {
      if ( true ) {
        return first;
      }
    }
    throw new Error( "Missing return statement in function" );
  }

  /**
   * @throws ParseException exception during the parse
   */
  final public char unaryOperator() throws ParseException {
    switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
      case MINUS:
        jj_consume_token( MINUS );
      {
        if ( true ) {
          return '-';
        }
      }
      break;
      case PLUS:
        jj_consume_token( PLUS );
      {
        if ( true ) {
          return '+';
        }
      }
      break;
      default:
        jj_la1[ 99 ] = jj_gen;
        jj_consume_token( -1 );
        throw new ParseException();
    }
    throw new Error( "Missing return statement in function" );
  }

  /**
   * @throws ParseException exception during the parse
   */
  final public LexicalUnitImpl term( LexicalUnitImpl prev ) throws ParseException {
    LexicalUnitImpl result = null;
    Token n = null;
    char op = ' ';
    switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
      case PLUS:
      case MINUS:
      case NUMBER:
      case PERCENTAGE:
      case PT:
      case MM:
      case CM:
      case PC:
      case IN:
      case PX:
      case EMS:
      case EXS:
      case DEG:
      case RAD:
      case GRAD:
      case MS:
      case SECOND:
      case HZ:
      case KHZ:
      case DIMEN:
      case FUNCTION:
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case PLUS:
          case MINUS:
            op = unaryOperator();
            break;
          default:
            jj_la1[ 100 ] = jj_gen;
            ;
        }
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case NUMBER:
            n = jj_consume_token( NUMBER );
            result = LexicalUnitImpl.createNumber( n.beginLine, n.beginColumn,
              prev, number( op, n, 0 ) );
            break;
          case PERCENTAGE:
            n = jj_consume_token( PERCENTAGE );
            result = LexicalUnitImpl.createPercentage( n.beginLine, n.beginColumn,
              prev, number( op, n, 1 ) );
            break;
          case PT:
            n = jj_consume_token( PT );
            result = LexicalUnitImpl.createPT( n.beginLine, n.beginColumn,
              prev, number( op, n, 2 ) );
            break;
          case CM:
            n = jj_consume_token( CM );
            result = LexicalUnitImpl.createCM( n.beginLine, n.beginColumn,
              prev, number( op, n, 2 ) );
            break;
          case MM:
            n = jj_consume_token( MM );
            result = LexicalUnitImpl.createMM( n.beginLine, n.beginColumn,
              prev, number( op, n, 2 ) );
            break;
          case PC:
            n = jj_consume_token( PC );
            result = LexicalUnitImpl.createPC( n.beginLine, n.beginColumn,
              prev, number( op, n, 2 ) );
            break;
          case IN:
            n = jj_consume_token( IN );
            result = LexicalUnitImpl.createIN( n.beginLine, n.beginColumn,
              prev, number( op, n, 2 ) );
            break;
          case PX:
            n = jj_consume_token( PX );
            result = LexicalUnitImpl.createPX( n.beginLine, n.beginColumn,
              prev, number( op, n, 2 ) );
            break;
          case EMS:
            n = jj_consume_token( EMS );
            result = LexicalUnitImpl.createEMS( n.beginLine, n.beginColumn,
              prev, number( op, n, 2 ) );
            break;
          case EXS:
            n = jj_consume_token( EXS );
            result = LexicalUnitImpl.createEXS( n.beginLine, n.beginColumn,
              prev, number( op, n, 2 ) );
            break;
          case DEG:
            n = jj_consume_token( DEG );
            result = LexicalUnitImpl.createDEG( n.beginLine, n.beginColumn,
              prev, number( op, n, 3 ) );
            break;
          case RAD:
            n = jj_consume_token( RAD );
            result = LexicalUnitImpl.createRAD( n.beginLine, n.beginColumn,
              prev, number( op, n, 3 ) );
            break;
          case GRAD:
            n = jj_consume_token( GRAD );
            result = LexicalUnitImpl.createGRAD( n.beginLine, n.beginColumn,
              prev, number( op, n, 3 ) );
            break;
          case SECOND:
            n = jj_consume_token( SECOND );
            result = LexicalUnitImpl.createS( n.beginLine, n.beginColumn,
              prev, number( op, n, 1 ) );
            break;
          case MS:
            n = jj_consume_token( MS );
            result = LexicalUnitImpl.createMS( n.beginLine, n.beginColumn,
              prev, number( op, n, 2 ) );
            break;
          case HZ:
            n = jj_consume_token( HZ );
            result = LexicalUnitImpl.createHZ( n.beginLine, n.beginColumn,
              prev, number( op, n, 2 ) );
            break;
          case KHZ:
            n = jj_consume_token( KHZ );
            result = LexicalUnitImpl.createKHZ( n.beginLine, n.beginColumn,
              prev, number( op, n, 3 ) );
            break;
          case DIMEN:
            n = jj_consume_token( DIMEN );
            String s = n.image;
            int i = 0;
            while ( i < s.length()
              && ( Character.isDigit( s.charAt( i ) ) || ( s.charAt( i ) == '.' ) ) ) {
              i++;
            }
            result = LexicalUnitImpl.createDimen( n.beginLine, n.beginColumn, prev,
              Float.valueOf( s.substring( 0, i ) ).floatValue(),
              s.substring( i ) );
            break;
          case FUNCTION:
            result = function( op, prev );
            break;
          default:
            jj_la1[ 101 ] = jj_gen;
            jj_consume_token( -1 );
            throw new ParseException();
        }
        break;
      case STRING:
      case IDENT:
      case URL:
      case NAMESPACE_IDENT:
      case HASH:
      case UNICODERANGE:
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case STRING:
            n = jj_consume_token( STRING );
            result = LexicalUnitImpl.createString
              ( n.beginLine, n.beginColumn, prev,
                convertStringIndex( n.image, 1,
                  n.image.length() - 1 ) );
            break;
          case NAMESPACE_IDENT:
            n = jj_consume_token( NAMESPACE_IDENT );
            result = LexicalUnitImpl.createIdent
              ( n.beginLine, n.beginColumn, prev, convertIdent( n.image ) );
            break;
          case IDENT:
            n = jj_consume_token( IDENT );
            String s = convertIdent( n.image );
            if ( "inherit".equals( s ) ) {
              result = LexicalUnitImpl.createInherit
                ( n.beginLine, n.beginColumn, prev );
            } else {
              result = LexicalUnitImpl.createIdent
                ( n.beginLine, n.beginColumn, prev, s );
            }

          /* /
         Auto correction code used in the CSS Validator but must not
          be used by a conformant CSS2 parser.
	 * Common error :
	 * H1 {
	 *   color : black
	 *   background : white
	 * }
	 *
	Token t = getToken(1);
	Token semicolon = new Token();
	semicolon.kind = SEMICOLON;
	semicolon.image = ";";
	if (t.kind == COLON) {
	    // @@SEEME. (generate a warning?)
	    // @@SEEME if expression is a single ident, 
	       generate an error ?
	    rejectToken(semicolon);
	    
	    result = prev;
	}
	/ */

            break;
          case HASH:
            result = hexcolor( prev );
            break;
          case URL:
            result = url( prev );
            break;
          case UNICODERANGE:
            result = unicode( prev );
            break;
          default:
            jj_la1[ 102 ] = jj_gen;
            jj_consume_token( -1 );
            throw new ParseException();
        }
        break;
      default:
        jj_la1[ 103 ] = jj_gen;
        jj_consume_token( -1 );
        throw new ParseException();
    }
    label_67:
    while ( true ) {
      switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
        case S:
          ;
          break;
        default:
          jj_la1[ 104 ] = jj_gen;
          break label_67;
      }
      jj_consume_token( S );
    }
    {
      if ( true ) {
        return result;
      }
    }
    throw new Error( "Missing return statement in function" );
  }

  /**
   * Handle all CSS2 functions.
   *
   * @throws ParseException exception during the parse
   */
  final public LexicalUnitImpl function( char operator, LexicalUnitImpl prev ) throws ParseException {
    Token n;
    LexicalUnit params = null;
    n = jj_consume_token( FUNCTION );
    label_68:
    while ( true ) {
      switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
        case S:
          ;
          break;
        default:
          jj_la1[ 105 ] = jj_gen;
          break label_68;
      }
      jj_consume_token( S );
    }
    switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
      case PLUS:
      case MINUS:
      case STRING:
      case IDENT:
      case NUMBER:
      case URL:
      case NAMESPACE_IDENT:
      case PERCENTAGE:
      case PT:
      case MM:
      case CM:
      case PC:
      case IN:
      case PX:
      case EMS:
      case EXS:
      case DEG:
      case RAD:
      case GRAD:
      case MS:
      case SECOND:
      case HZ:
      case KHZ:
      case DIMEN:
      case HASH:
      case UNICODERANGE:
      case FUNCTION:
        params = expr();
        break;
      default:
        jj_la1[ 106 ] = jj_gen;
        ;
    }
    jj_consume_token( LPARAN );
    if ( operator != ' ' ) {
      {
        if ( true ) {
          throw new CSSParseException( "invalid operator before a function.",
            getLocator() );
        }
      }
    }
    String f = convertIdent( n.image );
    LexicalUnitImpl l = (LexicalUnitImpl) params;
    boolean loop = true;
    if ( "rgb(".equals( f ) ) {
      // this is a RGB declaration (e.g. rgb(255, 50%, 0) )
      int i = 0;
      while ( loop && l != null && i < 5 ) {
        switch( i ) {
          case 0:
          case 2:
          case 4:
            if ( ( l.getLexicalUnitType() != LexicalUnit.SAC_INTEGER )
              && ( l.getLexicalUnitType() != LexicalUnit.SAC_PERCENTAGE ) ) {
              loop = false;
            }
            break;
          case 1:
          case 3:
            if ( l.getLexicalUnitType() != LexicalUnit.SAC_OPERATOR_COMMA ) {
              loop = false;
            }
            break;
          default: {
            if ( true ) {
              throw new ParseException( "implementation error" );
            }
          }
        }
        if ( loop ) {
          l = (LexicalUnitImpl) l.getNextLexicalUnit();
          i++;
        }
      }
      if ( ( i == 5 ) && loop && ( l == null ) ) {
        {
          if ( true ) {
            return LexicalUnitImpl.createRGBColor( n.beginLine,
              n.beginColumn,
              prev, params );
          }
        }
      } else {
        if ( errorHandler != null ) {
          String errorText;
          Locator loc;
          if ( i < 5 ) {
            if ( params == null ) {
              loc = new LocatorImpl( this, n.beginLine,
                n.beginColumn - 1 );
              errorText = "not enough parameters.";
            } else if ( l == null ) {
              loc = new LocatorImpl( this, n.beginLine,
                n.beginColumn - 1 );
              errorText = "not enough parameters: "
                + params.toString();
            } else {
              loc = new LocatorImpl( this, l.getLineNumber(),
                l.getColumnNumber() );
              errorText = "invalid parameter: "
                + l.toString();
            }
          } else {
            loc = new LocatorImpl( this, l.getLineNumber(),
              l.getColumnNumber() );
            errorText = "too many parameters: "
              + l.toString();
          }
          errorHandler.error( new CSSParseException( errorText, loc ) );
        }

        {
          if ( true ) {
            throw new JumpException();
          }
        }
      }
    } else if ( "counter".equals( f ) ) {
      int i = 0;
      while ( loop && l != null && i < 3 ) {
        switch( i ) {
          case 0:
          case 2:
            if ( l.getLexicalUnitType() != LexicalUnit.SAC_IDENT ) {
              loop = false;
            }
            break;
          case 1:
            if ( l.getLexicalUnitType() != LexicalUnit.SAC_OPERATOR_COMMA ) {
              loop = false;
            }
            break;
          default: {
            if ( true ) {
              throw new ParseException( "implementation error" );
            }
          }
        }
        l = (LexicalUnitImpl) l.getNextLexicalUnit();
        i++;
      }
      if ( ( ( i == 1 ) || ( i == 3 ) ) && loop && ( l == null ) ) {
        {
          if ( true ) {
            return LexicalUnitImpl.createCounter( n.beginLine, n.beginColumn,
              prev, params );
          }
        }
      }

    } else if ( "counters(".equals( f ) ) {

      int i = 0;
      while ( loop && l != null && i < 5 ) {
        switch( i ) {
          case 0:
          case 4:
            if ( l.getLexicalUnitType() != LexicalUnit.SAC_IDENT ) {
              loop = false;
            }
            break;
          case 2:
            if ( l.getLexicalUnitType() != LexicalUnit.SAC_STRING_VALUE ) {
              loop = false;
            }
            break;
          case 1:
          case 3:
            if ( l.getLexicalUnitType() != LexicalUnit.SAC_OPERATOR_COMMA ) {
              loop = false;
            }
            break;
          default: {
            if ( true ) {
              throw new ParseException( "implementation error" );
            }
          }
        }
        l = (LexicalUnitImpl) l.getNextLexicalUnit();
        i++;
      }
      if ( ( ( i == 3 ) || ( i == 5 ) ) && loop && ( l == null ) ) {
        {
          if ( true ) {
            return LexicalUnitImpl.createCounters( n.beginLine, n.beginColumn,
              prev, params );
          }
        }
      }
    } else if ( "attr(".equals( f ) ) {
      if ( ( l != null )
        && ( l.getNextLexicalUnit() == null )
        && ( l.getLexicalUnitType() == LexicalUnit.SAC_IDENT ) ) {
        {
          if ( true ) {
            return LexicalUnitImpl.createAttr( l.getLineNumber(),
              l.getColumnNumber(),
              prev, l.getStringValue() );
          }
        }
      }
    } else if ( "rect(".equals( f ) ) {
      int i = 0;
      while ( loop && l != null && i < 7 ) {
        switch( i ) {
          case 0:
          case 2:
          case 4:
          case 6:
            switch( l.getLexicalUnitType() ) {
              case LexicalUnit.SAC_INTEGER:
                if ( l.getIntegerValue() != 0 ) {
                  loop = false;
                }
                break;
              case LexicalUnit.SAC_IDENT:
                if ( !l.getStringValue().equals( "auto" ) ) {
                  loop = false;
                }
                break;
              case LexicalUnit.SAC_EM:
              case LexicalUnit.SAC_EX:
              case LexicalUnit.SAC_PIXEL:
              case LexicalUnit.SAC_CENTIMETER:
              case LexicalUnit.SAC_MILLIMETER:
              case LexicalUnit.SAC_INCH:
              case LexicalUnit.SAC_POINT:
              case LexicalUnit.SAC_PICA:
                // nothing
                break;
              default:
                loop = false;
            }
            break;
          case 1:
          case 3:
          case 5:
            if ( l.getLexicalUnitType() != LexicalUnit.SAC_OPERATOR_COMMA ) {
              loop = false;
            }
            break;
          default: {
            if ( true ) {
              throw new ParseException( "implementation error" );
            }
          }
        }
        l = (LexicalUnitImpl) l.getNextLexicalUnit();
        i++;
      }
      if ( ( i == 7 ) && loop && ( l == null ) ) {
        {
          if ( true ) {
            return LexicalUnitImpl.createRect( n.beginLine, n.beginColumn,
              prev, params );
          }
        }
      }
    }
    {
      if ( true ) {
        return LexicalUnitImpl.createFunction( n.beginLine, n.beginColumn, prev,
          f.substring( 0,
            f.length() - 1 ),
          params );
      }
    }
    throw new Error( "Missing return statement in function" );
  }

  final public LexicalUnitImpl unicode( LexicalUnitImpl prev ) throws ParseException {
    Token n;
    n = jj_consume_token( UNICODERANGE );
    LexicalUnitImpl params = null;
    String s = n.image.substring( 2 );
    int index = s.indexOf( '-' );
    if ( index == -1 ) {
      params = LexicalUnitImpl.createInteger( n.beginLine, n.beginColumn,
        params, Integer.parseInt( s, 16 ) );
    } else {
      String s1 = s.substring( 0, index );
      String s2 = s.substring( index );

      params = LexicalUnitImpl.createInteger( n.beginLine, n.beginColumn,
        params, Integer.parseInt( s1, 16 ) );
      params = LexicalUnitImpl.createInteger( n.beginLine, n.beginColumn,
        params, Integer.parseInt( s2, 16 ) );
    }

    {
      if ( true ) {
        return LexicalUnitImpl.createUnicodeRange( n.beginLine, n.beginColumn,
          prev, params );
      }
    }
    throw new Error( "Missing return statement in function" );
  }

  final public LexicalUnitImpl url( LexicalUnitImpl prev ) throws ParseException {
    Token n;
    n = jj_consume_token( URL );
    String urlname = n.image.substring( 4, n.image.length() - 1 ).trim();
    if ( urlname.charAt( 0 ) == '"'
      || urlname.charAt( 0 ) == '\'' ) {
      urlname = urlname.substring( 1, urlname.length() - 1 );
    }
    {
      if ( true ) {
        return LexicalUnitImpl.createURL( n.beginLine, n.beginColumn, prev, urlname );
      }
    }
    throw new Error( "Missing return statement in function" );
  }

  /**
   * @throws ParseException exception during the parse
   */
  final public LexicalUnitImpl hexcolor( LexicalUnitImpl prev ) throws ParseException {
    Token n;
    n = jj_consume_token( HASH );
    int r;
    LexicalUnitImpl first, params = null;
    String s = n.image.substring( 1 );

    if ( s.length() == 3 ) {
      String sh = s.substring( 0, 1 );
      r = Integer.parseInt( sh + sh, 16 );
      first = params = LexicalUnitImpl.createInteger( n.beginLine, n.beginColumn,
        params, r );
      params = LexicalUnitImpl.createComma( n.beginLine, n.beginColumn,
        params );
      sh = s.substring( 1, 2 );
      r = Integer.parseInt( sh + sh, 16 );
      params = LexicalUnitImpl.createInteger( n.beginLine, n.beginColumn,
        params, r );
      params = LexicalUnitImpl.createComma( n.beginLine, n.beginColumn,
        params );
      sh = s.substring( 2, 3 );
      r = Integer.parseInt( sh + sh, 16 );
      params = LexicalUnitImpl.createInteger( n.beginLine, n.beginColumn,
        params, r );
    } else if ( s.length() == 6 ) {
      r = Integer.parseInt( s.substring( 0, 2 ), 16 );
      first = params = LexicalUnitImpl.createInteger( n.beginLine,
        n.beginColumn,
        params, r );
      params = LexicalUnitImpl.createComma( n.beginLine, n.beginColumn,
        params );
      r = Integer.parseInt( s.substring( 2, 4 ), 16 );
      params = LexicalUnitImpl.createInteger( n.beginLine, n.beginColumn,
        params, r );
      params = LexicalUnitImpl.createComma( n.beginLine, n.beginColumn,
        params );
      r = Integer.parseInt( s.substring( 4, 6 ), 16 );
      params = LexicalUnitImpl.createInteger( n.beginLine, n.beginColumn,
        params, r );
    } else {
      first = null;
      {
        if ( true ) {
          throw new CSSParseException( "invalid hexadecimal notation for RGB: " + s,
            getLocator() );
        }
      }
    }
    {
      if ( true ) {
        return LexicalUnitImpl.createRGBColor( n.beginLine, n.beginColumn,
          prev, first );
      }
    }
    throw new Error( "Missing return statement in function" );
  }

  float number( char operator, Token n, int lengthUnit ) throws ParseException {
    String image = n.image;
    float f = 0;

    if ( lengthUnit != 0 ) {
      image = image.substring( 0, image.length() - lengthUnit );
    }
    f = Float.valueOf( image ).floatValue();
    return ( operator == '-' ) ? -f : f;
  }

  String skipStatementNoSemicolon() throws ParseException {
    StringBuffer s = new StringBuffer();
    Token tok = getToken( 0 );
    if ( tok.image != null ) {
      s.append( tok.image );
    }
    while ( true ) {
      tok = getToken( 1 );
      if ( tok.kind == EOF ) {
        return null;
      }
      s.append( tok.image );
      if ( tok.kind == LBRACE ) {
        getNextToken();
        s.append( skip_to_matching_brace() );
        getNextToken();
        tok = getToken( 1 );
        break;
      }
      getNextToken();
    }

    // skip white space
    while ( true ) {
      if ( tok.kind != S ) {
        break;
      }
      tok = getNextToken();
      tok = getToken( 1 );
    }

    return s.toString().trim();
  }

  String skipStatement() throws ParseException {
    StringBuffer s = new StringBuffer();
    Token tok = getToken( 0 );
    if ( tok.image != null ) {
      s.append( tok.image );
    }
    while ( true ) {
      tok = getToken( 1 );
      if ( tok.kind == EOF ) {
        return null;
      }
      s.append( tok.image );
      if ( tok.kind == LBRACE ) {
        getNextToken();
        s.append( skip_to_matching_brace() );
        getNextToken();
        tok = getToken( 1 );
        break;
      } else if ( tok.kind == RBRACE ) {
        getNextToken();
        tok = getToken( 1 );
        break;
      } else if ( tok.kind == SEMICOLON ) {
        getNextToken();
        tok = getToken( 1 );
        break;
      }
      getNextToken();
    }

    // skip white space
    while ( true ) {
      if ( tok.kind != S ) {
        break;
      }
      tok = getNextToken();
      tok = getToken( 1 );
    }

    return s.toString().trim();
  }

  String skip_to_matching_brace() throws ParseException {
    StringBuffer s = new StringBuffer();
    Token tok;
    int nesting = 1;
    while ( true ) {
      tok = getToken( 1 );
      if ( tok.kind == EOF ) {
        break;
      }
      s.append( tok.image );
      if ( tok.kind == LBRACE ) {
        nesting++;
      } else if ( tok.kind == RBRACE ) {
        nesting--;
        if ( nesting == 0 ) {
          break;
        }
      }
      getNextToken();
    }
    return s.toString();
  }

  String convertStringIndex( String s, int start, int len ) throws ParseException {
    StringBuffer buf = new StringBuffer( len );
    int index = start;

    while ( index < len ) {
      char c = s.charAt( index );
      if ( c == '\\' ) {
        if ( ++index < len ) {
          c = s.charAt( index );
          switch( c ) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
              int numValue = Character.digit( c, 16 );
              int count = 0;
              int p = 16;

              while ( index + 1 < len && count < 6 ) {
                c = s.charAt( index + 1 );

                if ( Character.digit( c, 16 ) != -1 ) {
                  numValue = ( numValue * 16 ) + Character.digit( c, 16 );
                  p *= 16;
                  index++;
                } else {
                  if ( c == ' ' ) {
                    // skip the latest white space
                    index++;
                  }
                  break;
                }
              }
              buf.append( (char) numValue );
              break;
            case '\n':
            case '\f':
              break;
            case '\r':
              if ( index + 1 < len ) {
                if ( s.charAt( index + 1 ) == '\n' ) {
                  index++;
                }
              }
              break;
            default:
              buf.append( c );
          }
        } else {
          throw new CSSParseException( "invalid string " + s, getLocator() );
        }
      } else {
        buf.append( c );
      }
      index++;
    }

    return buf.toString();
  }

  String convertIdent( String s ) throws ParseException {
    return convertStringIndex( s, 0, s.length() );
  }

  String convertString( String s ) throws ParseException {
    return convertStringIndex( s, 0, s.length() );
  }

  void rejectToken( Token t ) throws ParseException {
    Token fakeToken = new Token();
    t.next = token;
    fakeToken.next = t;
    token = fakeToken;
  }

  String skipAfterExpression() throws ParseException {
    Token t = getToken( 1 );
    StringBuffer s = new StringBuffer();
    s.append( getToken( 0 ).image );

    while ( ( t.kind != RBRACE ) && ( t.kind != SEMICOLON ) && ( t.kind != EOF ) ) {
      s.append( t.image );
      getNextToken();
      t = getToken( 1 );
    }

    return s.toString();
  }

  /**
   * The following functions are useful for a DOM CSS implementation only and are not part of the general CSS2 parser.
   */
  final public void _parseRule() throws ParseException {
    String ret = null;
    label_69:
    while ( true ) {
      switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
        case S:
          ;
          break;
        default:
          jj_la1[ 107 ] = jj_gen;
          break label_69;
      }
      jj_consume_token( S );
    }
    switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
      case IMPORT_SYM:
        importDeclaration();
        break;
      case LBRACKET:
      case ANY:
      case DOT:
      case COLON:
      case IDENT:
      case NAMESPACE_IDENT:
      case HASH:
        styleRule();
        break;
      case MEDIA_SYM:
        media();
        break;
      case PAGE_SYM:
        page();
        break;
      case FONT_FACE_SYM:
        fontFace();
        break;
      default:
        jj_la1[ 108 ] = jj_gen;
        ret = skipStatement();
        if ( ( ret == null ) || ( ret.length() == 0 ) ) {
          {
            if ( true ) {
              return;
            }
          }
        }
        if ( ret.charAt( 0 ) == '@' ) {
          documentHandler.ignorableAtRule( ret );
        } else {
          {
            if ( true ) {
              throw new CSSParseException( "unrecognize rule: " + ret,
                getLocator() );
            }
          }
        }
    }
  }

  final public void _parseImportRule() throws ParseException {
    label_70:
    while ( true ) {
      switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
        case S:
          ;
          break;
        default:
          jj_la1[ 109 ] = jj_gen;
          break label_70;
      }
      jj_consume_token( S );
    }
    importDeclaration();
  }

  final public void _parseMediaRule() throws ParseException {
    label_71:
    while ( true ) {
      switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
        case S:
          ;
          break;
        default:
          jj_la1[ 110 ] = jj_gen;
          break label_71;
      }
      jj_consume_token( S );
    }
    media();
  }

  final public void _parseDeclarationBlock() throws ParseException {
    label_72:
    while ( true ) {
      switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
        case S:
          ;
          break;
        default:
          jj_la1[ 111 ] = jj_gen;
          break label_72;
      }
      jj_consume_token( S );
    }
    switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
      case IDENT:
        declaration();
        break;
      default:
        jj_la1[ 112 ] = jj_gen;
        ;
    }
    label_73:
    while ( true ) {
      switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
        case SEMICOLON:
          ;
          break;
        default:
          jj_la1[ 113 ] = jj_gen;
          break label_73;
      }
      jj_consume_token( SEMICOLON );
      label_74:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case S:
            ;
            break;
          default:
            jj_la1[ 114 ] = jj_gen;
            break label_74;
        }
        jj_consume_token( S );
      }
      switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
        case IDENT:
          declaration();
          break;
        default:
          jj_la1[ 115 ] = jj_gen;
          ;
      }
    }
  }

  final public SelectorList _parseSelectors() throws ParseException {
    SelectorList p = null;
    try {
      label_75:
      while ( true ) {
        switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
          case S:
            ;
            break;
          default:
            jj_la1[ 116 ] = jj_gen;
            break label_75;
        }
        jj_consume_token( S );
      }
      p = selectorList();
      {
        if ( true ) {
          return p;
        }
      }
    } catch ( ThrowedParseException e ) {
      {
        if ( true ) {
          throw (ParseException) e.e;
        }
      }//.fillInStackTrace();

    }
    throw new Error( "Missing return statement in function" );
  }

  final public String _parseNamespaceToken() throws ParseException {
    Token retval = null;
    switch( ( jj_ntk == -1 ) ? jj_ntk() : jj_ntk ) {
      case NAMESPACE_IDENT:
        retval = jj_consume_token( NAMESPACE_IDENT );
      {
        if ( true ) {
          return retval.image;
        }
      }
      break;
      case IDENT:
        retval = jj_consume_token( IDENT );
      {
        if ( true ) {
          return retval.image;
        }
      }
      break;
      default:
        jj_la1[ 117 ] = jj_gen;
        jj_consume_token( -1 );
        throw new ParseException();
    }
    throw new Error( "Missing return statement in function" );
  }

  final private boolean jj_2_1( int xla ) {
    jj_la = xla;
    jj_lastpos = jj_scanpos = token;
    try {
      return !jj_3_1();
    } catch ( LookaheadSuccess ls ) {
      return true;
    } finally {
      jj_save( 0, xla );
    }
  }

  final private boolean jj_3R_92() {
    if ( jj_scan_token( PLUS ) ) {
      return true;
    }
    return false;
  }

  final private boolean jj_3R_86() {
    Token xsp;
    xsp = jj_scanpos;
    if ( jj_3R_92() ) {
      jj_scanpos = xsp;
      if ( jj_3R_93() ) {
        return true;
      }
    }
    return false;
  }

  final private boolean jj_3R_85() {
    if ( jj_3R_91() ) {
      return true;
    }
    return false;
  }

  final private boolean jj_3R_91() {
    if ( jj_scan_token( LBRACKET ) ) {
      return true;
    }
    return false;
  }

  final private boolean jj_3R_79() {
    if ( jj_scan_token( PRECEDES ) ) {
      return true;
    }
    Token xsp;
    while ( true ) {
      xsp = jj_scanpos;
      if ( jj_scan_token( 1 ) ) {
        jj_scanpos = xsp;
        break;
      }
    }
    return false;
  }

  final private boolean jj_3R_80() {
    if ( jj_scan_token( S ) ) {
      return true;
    }
    Token xsp;
    xsp = jj_scanpos;
    if ( jj_3R_86() ) {
      jj_scanpos = xsp;
    }
    return false;
  }

  final private boolean jj_3R_78() {
    if ( jj_scan_token( PLUS ) ) {
      return true;
    }
    Token xsp;
    while ( true ) {
      xsp = jj_scanpos;
      if ( jj_scan_token( 1 ) ) {
        jj_scanpos = xsp;
        break;
      }
    }
    return false;
  }

  final private boolean jj_3R_76() {
    Token xsp;
    xsp = jj_scanpos;
    if ( jj_3R_78() ) {
      jj_scanpos = xsp;
      if ( jj_3R_79() ) {
        jj_scanpos = xsp;
        if ( jj_3R_80() ) {
          return true;
        }
      }
    }
    return false;
  }

  final private boolean jj_3R_84() {
    if ( jj_3R_90() ) {
      return true;
    }
    return false;
  }

  final private boolean jj_3R_83() {
    if ( jj_3R_89() ) {
      return true;
    }
    return false;
  }

  final private boolean jj_3R_96() {
    if ( jj_scan_token( ANY ) ) {
      return true;
    }
    return false;
  }

  final private boolean jj_3_1() {
    if ( jj_3R_76() ) {
      return true;
    }
    if ( jj_3R_77() ) {
      return true;
    }
    return false;
  }

  final private boolean jj_3R_95() {
    if ( jj_scan_token( NAMESPACE_IDENT ) ) {
      return true;
    }
    return false;
  }

  final private boolean jj_3R_90() {
    if ( jj_scan_token( COLON ) ) {
      return true;
    }
    return false;
  }

  final private boolean jj_3R_82() {
    if ( jj_3R_88() ) {
      return true;
    }
    return false;
  }

  final private boolean jj_3R_88() {
    if ( jj_scan_token( HASH ) ) {
      return true;
    }
    return false;
  }

  final private boolean jj_3R_94() {
    if ( jj_scan_token( IDENT ) ) {
      return true;
    }
    return false;
  }

  final private boolean jj_3R_87() {
    Token xsp;
    xsp = jj_scanpos;
    if ( jj_3R_94() ) {
      jj_scanpos = xsp;
      if ( jj_3R_95() ) {
        jj_scanpos = xsp;
        if ( jj_3R_96() ) {
          return true;
        }
      }
    }
    return false;
  }

  final private boolean jj_3R_81() {
    if ( jj_3R_87() ) {
      return true;
    }
    return false;
  }

  final private boolean jj_3R_77() {
    Token xsp;
    xsp = jj_scanpos;
    if ( jj_3R_81() ) {
      jj_scanpos = xsp;
      if ( jj_3R_82() ) {
        jj_scanpos = xsp;
        if ( jj_3R_83() ) {
          jj_scanpos = xsp;
          if ( jj_3R_84() ) {
            jj_scanpos = xsp;
            if ( jj_3R_85() ) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  final private boolean jj_3R_93() {
    if ( jj_scan_token( PRECEDES ) ) {
      return true;
    }
    return false;
  }

  final private boolean jj_3R_89() {
    if ( jj_scan_token( DOT ) ) {
      return true;
    }
    return false;
  }

  public ParserTokenManager token_source;
  public Token token, jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  public boolean lookingAhead = false;
  private boolean jj_semLA;
  private int jj_gen;
  final private int[] jj_la1 = new int[ 118 ];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static private int[] jj_la1_2;

  static {
    jj_la1_0();
    jj_la1_1();
    jj_la1_2();
  }

  private static void jj_la1_0() {
    jj_la1_0 =
      new int[] { 0x0, 0x62, 0x62, 0x0, 0x60, 0x2, 0x0, 0x60, 0x2, 0x2, 0x2, 0x1340000, 0x60, 0x2, 0x60, 0x2, 0x0, 0x2,
        0x0, 0x2, 0x2, 0x2, 0x0, 0x2, 0x2, 0x2, 0x335f6a0, 0x335f6a0, 0x2, 0x4000, 0x2, 0x2, 0x2, 0x2, 0x0, 0x1000000,
        0x2, 0x0, 0x8000, 0x2, 0x0, 0x0, 0x2, 0x2, 0x2, 0x2, 0x0, 0x8000, 0x2, 0x0, 0x2, 0x201f6a0, 0x2, 0x2, 0x11000,
        0x2, 0x11000, 0x11002, 0x2, 0x2, 0x0, 0x8000, 0x2, 0x0, 0x2, 0x4000, 0x2, 0x2, 0x1240000, 0x1240000, 0x1240000,
        0x1240000, 0x1240000, 0x1240000, 0x1240000, 0x1240000, 0x1240000, 0x1240000, 0x1340000, 0x100000, 0x2, 0x0, 0x2,
        0xe00, 0x2, 0x0, 0x2, 0xe00, 0x2, 0x2, 0x0, 0x2, 0x0, 0x2, 0x2, 0x2, 0x24000, 0x27000, 0x24000, 0x3000, 0x3000,
        0x0, 0x0, 0x3000, 0x2, 0x2, 0x3000, 0x2, 0x1340000, 0x2, 0x2, 0x2, 0x0, 0x8000, 0x2, 0x0, 0x2, 0x0, };
  }

  private static void jj_la1_1() {
    jj_la1_1 =
      new int[] { 0x20000000, 0x0, 0x0, 0x8000000, 0x0, 0x0, 0x80000000, 0x0, 0x0, 0x0, 0x0, 0x54000110, 0x0, 0x0, 0x0,
        0x0, 0x88, 0x0, 0x10, 0x0, 0x0, 0x0, 0x10, 0x0, 0x0, 0x0, 0x7c0003b8, 0x7c0003b8, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
        0x10, 0x0, 0x0, 0x10, 0x0, 0x0, 0x10, 0x0, 0x0, 0x0, 0x0, 0x0, 0x10, 0x0, 0x0, 0x10, 0x0, 0x780002a8, 0x0, 0x0,
        0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x10, 0x0, 0x0, 0x10, 0x0, 0x0, 0x0, 0x0, 0x4000000, 0x4000000, 0x0, 0x0,
        0x4000000, 0x4000000, 0x4000000, 0x4000000, 0x4000000, 0x4000000, 0x4000110, 0x110, 0x0, 0x110, 0x0, 0x0, 0x0,
        0x18, 0x0, 0x0, 0x0, 0x0, 0x10, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x7ffffb8, 0x0, 0x0, 0x0, 0x3fffe20, 0x4000198,
        0x7ffffb8, 0x0, 0x0, 0x7ffffb8, 0x0, 0x5c000110, 0x0, 0x0, 0x0, 0x10, 0x0, 0x0, 0x10, 0x0, 0x110, };
  }

  private static void jj_la1_2() {
    jj_la1_2 =
      new int[] { 0x0, 0x2, 0x2, 0x0, 0x2, 0x0, 0x0, 0x2, 0x0, 0x0, 0x0, 0x1, 0x2, 0x0, 0x2, 0x0, 0x0, 0x0, 0x0, 0x0,
        0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x7007, 0x7007, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
        0x2, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x7007, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
        0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
        0x0, 0x0, 0x0, 0x0, 0x0, 0x2000, 0x0, 0x4, 0x0, 0x0, 0x0, 0x0, 0x3000, 0x0, 0x0, 0x0, 0x2000, 0x1000, 0x3000,
        0x0, 0x0, 0x3000, 0x0, 0x1, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, };
  }

  final private JJCalls[] jj_2_rtns = new JJCalls[ 1 ];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  public Parser( CharStream stream ) {
    token_source = new ParserTokenManager( stream );
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for ( int i = 0; i < 118; i++ ) {
      jj_la1[ i ] = -1;
    }
    for ( int i = 0; i < jj_2_rtns.length; i++ ) {
      jj_2_rtns[ i ] = new JJCalls();
    }
  }

  public void ReInit( CharStream stream ) {
    token_source.ReInit( stream );
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for ( int i = 0; i < 118; i++ ) {
      jj_la1[ i ] = -1;
    }
    for ( int i = 0; i < jj_2_rtns.length; i++ ) {
      jj_2_rtns[ i ] = new JJCalls();
    }
  }

  public Parser( ParserTokenManager tm ) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for ( int i = 0; i < 118; i++ ) {
      jj_la1[ i ] = -1;
    }
    for ( int i = 0; i < jj_2_rtns.length; i++ ) {
      jj_2_rtns[ i ] = new JJCalls();
    }
  }

  public void ReInit( ParserTokenManager tm ) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for ( int i = 0; i < 118; i++ ) {
      jj_la1[ i ] = -1;
    }
    for ( int i = 0; i < jj_2_rtns.length; i++ ) {
      jj_2_rtns[ i ] = new JJCalls();
    }
  }

  final private Token jj_consume_token( int kind ) throws ParseException {
    Token oldToken;
    if ( ( oldToken = token ).next != null ) {
      token = token.next;
    } else {
      token = token.next = token_source.getNextToken();
    }
    jj_ntk = -1;
    if ( token.kind == kind ) {
      jj_gen++;
      if ( ++jj_gc > 100 ) {
        jj_gc = 0;
        for ( int i = 0; i < jj_2_rtns.length; i++ ) {
          JJCalls c = jj_2_rtns[ i ];
          while ( c != null ) {
            if ( c.gen < jj_gen ) {
              c.first = null;
            }
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error {
  }

  final private LookaheadSuccess jj_ls = new LookaheadSuccess();

  final private boolean jj_scan_token( int kind ) {
    if ( jj_scanpos == jj_lastpos ) {
      jj_la--;
      if ( jj_scanpos.next == null ) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if ( jj_rescan ) {
      int i = 0;
      Token tok = token;
      while ( tok != null && tok != jj_scanpos ) {
        i++;
        tok = tok.next;
      }
      if ( tok != null ) {
        jj_add_error_token( kind, i );
      }
    }
    if ( jj_scanpos.kind != kind ) {
      return true;
    }
    if ( jj_la == 0 && jj_scanpos == jj_lastpos ) {
      throw jj_ls;
    }
    return false;
  }

  final public Token getNextToken() {
    if ( token.next != null ) {
      token = token.next;
    } else {
      token = token.next = token_source.getNextToken();
    }
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

  final public Token getToken( int index ) {
    Token t = lookingAhead ? jj_scanpos : token;
    for ( int i = 0; i < index; i++ ) {
      if ( t.next != null ) {
        t = t.next;
      } else {
        t = t.next = token_source.getNextToken();
      }
    }
    return t;
  }

  final private int jj_ntk() {
    if ( ( jj_nt = token.next ) == null ) {
      return ( jj_ntk = ( token.next = token_source.getNextToken() ).kind );
    } else {
      return ( jj_ntk = jj_nt.kind );
    }
  }

  private java.util.Vector jj_expentries = new java.util.Vector();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[ 100 ];
  private int jj_endpos;

  private void jj_add_error_token( int kind, int pos ) {
    if ( pos >= 100 ) {
      return;
    }
    if ( pos == jj_endpos + 1 ) {
      jj_lasttokens[ jj_endpos++ ] = kind;
    } else if ( jj_endpos != 0 ) {
      jj_expentry = new int[ jj_endpos ];
      for ( int i = 0; i < jj_endpos; i++ ) {
        jj_expentry[ i ] = jj_lasttokens[ i ];
      }
      boolean exists = false;
      for ( java.util.Enumeration e = jj_expentries.elements(); e.hasMoreElements(); ) {
        int[] oldentry = (int[]) ( e.nextElement() );
        if ( oldentry.length == jj_expentry.length ) {
          exists = true;
          for ( int i = 0; i < jj_expentry.length; i++ ) {
            if ( oldentry[ i ] != jj_expentry[ i ] ) {
              exists = false;
              break;
            }
          }
          if ( exists ) {
            break;
          }
        }
      }
      if ( !exists ) {
        jj_expentries.addElement( jj_expentry );
      }
      if ( pos != 0 ) {
        jj_lasttokens[ ( jj_endpos = pos ) - 1 ] = kind;
      }
    }
  }

  public ParseException generateParseException() {
    jj_expentries.removeAllElements();
    boolean[] la1tokens = new boolean[ 79 ];
    for ( int i = 0; i < 79; i++ ) {
      la1tokens[ i ] = false;
    }
    if ( jj_kind >= 0 ) {
      la1tokens[ jj_kind ] = true;
      jj_kind = -1;
    }
    for ( int i = 0; i < 118; i++ ) {
      if ( jj_la1[ i ] == jj_gen ) {
        for ( int j = 0; j < 32; j++ ) {
          if ( ( jj_la1_0[ i ] & ( 1 << j ) ) != 0 ) {
            la1tokens[ j ] = true;
          }
          if ( ( jj_la1_1[ i ] & ( 1 << j ) ) != 0 ) {
            la1tokens[ 32 + j ] = true;
          }
          if ( ( jj_la1_2[ i ] & ( 1 << j ) ) != 0 ) {
            la1tokens[ 64 + j ] = true;
          }
        }
      }
    }
    for ( int i = 0; i < 79; i++ ) {
      if ( la1tokens[ i ] ) {
        jj_expentry = new int[ 1 ];
        jj_expentry[ 0 ] = i;
        jj_expentries.addElement( jj_expentry );
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token( 0, 0 );
    int[][] exptokseq = new int[ jj_expentries.size() ][];
    for ( int i = 0; i < jj_expentries.size(); i++ ) {
      exptokseq[ i ] = (int[]) jj_expentries.elementAt( i );
    }
    return new ParseException( token, exptokseq, tokenImage );
  }

  final public void enable_tracing() {
  }

  final public void disable_tracing() {
  }

  final private void jj_rescan_token() {
    jj_rescan = true;
    for ( int i = 0; i < 1; i++ ) {
      JJCalls p = jj_2_rtns[ i ];
      do {
        if ( p.gen > jj_gen ) {
          jj_la = p.arg;
          jj_lastpos = jj_scanpos = p.first;
          switch( i ) {
            case 0:
              jj_3_1();
              break;
          }
        }
        p = p.next;
      } while ( p != null );
    }
    jj_rescan = false;
  }

  final private void jj_save( int index, int xla ) {
    JJCalls p = jj_2_rtns[ index ];
    while ( p.gen > jj_gen ) {
      if ( p.next == null ) {
        p = p.next = new JJCalls();
        break;
      }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la;
    p.first = token;
    p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}

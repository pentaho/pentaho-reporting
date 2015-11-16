package org.pentaho.reporting.engine.classic.core.layout.text;

import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableComplexText;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ComplexTextFactory implements RenderableTextFactory {
  public ComplexTextFactory() {
  }

  public RenderNode[] createText( final int[] text, final int offset, final int length, final StyleSheet layoutContext,
      final ElementType elementType, final InstanceID instanceId, final ReportAttributeMap<Object> attributeMap ) {
    List<String> strings = processText( text, offset, length );
    ArrayList<RenderableComplexText> result = new ArrayList<RenderableComplexText>();
    Iterator<String> lines = strings.iterator();
    while ( lines.hasNext() ) {
      String next = lines.next();
      if ( next.isEmpty() && lines.hasNext() == false ) {
        // ignore the last line, it acted as indicator for a forced linebreak
        continue;
      }

      RenderableComplexText rct =
          new RenderableComplexText( layoutContext, instanceId, elementType, attributeMap, next );
      rct.setForceLinebreak( lines.hasNext() );
      result.add( rct );
    }

    return result.toArray( new RenderNode[result.size()] );
  }

  public RenderNode[] finishText() {
    return new RenderNode[0];
  }

  public void startText() {
  }

  private enum State {
    None, LF, CR
  }

  /**
   * This method breaks text into lines in a strict manor. It accepts CR+LF (windows), CR (old Mac) and LF (Unix) as
   * line endings and correctly handles empty lines formed by multiple line-breaks.
   *
   * @param text
   * @param offset
   * @param length
   * @return
   */
  public static List<String> processText( final int[] text, final int offset, final int length ) {
    final ArrayList<String> result = new ArrayList<String>();
    final int end = offset + length;
    int start = offset;
    State state = State.None;
    for ( int i = offset; i < end; i += 1 ) {
      State oldState = state;
      int cp = text[i];
      switch ( cp ) {
        case '\n': {
          state = State.LF;
          switch ( oldState ) {
            case CR: {
              // LF+CR causes linebreaks
              String txt = new String( text, start, i - start - 1 );
              result.add( txt );
              start = i + 1;
              break;
            }
            case LF: {
              result.add( "" );
              start = i + 1;
              break;
            }
            default: {
              String txt = new String( text, start, i - start );
              result.add( txt );
              start = i + 1;
              break;
            }
          }

          break;
        }
        case '\r': {
          state = State.CR;
          if ( oldState == State.CR ) {
            // double CR causes a new line
            String txt = new String( text, start, i - start - 1 );
            result.add( txt );
            start = i + 1;
          }

          break;
        }
        default: {
          state = State.None;
          if ( oldState == State.CR ) {
            // A CR causes a delayed new line
            String txt = new String( text, start, i - start );
            result.add( txt );
            start = i;
          }

          break;
        }
      }
    }

    switch ( state ) {
      case None:
      case LF: {
        result.add( new String( text, start, offset + length - start ) );
        break;
      }
      case CR: {
        result.add( new String( text, start, offset + length - start - 1 ) );
        result.add( "" );
        break;
      }
    }
    return result;
  }

}

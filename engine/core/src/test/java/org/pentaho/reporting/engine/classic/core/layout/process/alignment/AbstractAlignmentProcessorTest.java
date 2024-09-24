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

package org.pentaho.reporting.engine.classic.core.layout.process.alignment;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableTextTest;
import org.pentaho.reporting.engine.classic.core.layout.model.SpacerRenderNode;

import static org.junit.Assert.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.pentaho.reporting.engine.classic.core.layout.process.alignment.AbstractAlignmentProcessor.shiftArray;

/**
 * @author Andrey Khayrutdinov
 */
public class AbstractAlignmentProcessorTest {

  private static final int SAMPLE_ARRAY_SIZE = 10;

  @BeforeClass
  public static void initEngine() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  private static String[] createArray( String... array ) {
    String[] result = new String[SAMPLE_ARRAY_SIZE];
    System.arraycopy( array, 0, result, 0, array.length );
    return result;
  }

  @Test
  public void shiftArray_InTheBeginning() {
    String[] array = createArray( "a", "b", "c" );
    shiftArray( array, 0, 3, 3 );
    assertArrayEquals( createArray( "a", "b", "c", "a", "b", "c" ), array );
  }

  @Test
  public void shiftArray_InTheMiddle() {
    String[] array = createArray( "a", "b", "c" );
    shiftArray( array, 1, 2, 3 );
    assertArrayEquals( createArray( "a", "b", "c", null, "b", "c" ), array );
  }

  @Test
  public void shiftArray_InTheEnd() {
    String[] array = createArray( "a", "b", "c" );
    shiftArray( array, 3, 3, 3 );
    assertArrayEquals( createArray( "a", "b", "c" ), array );
  }

  @Test
  public void shiftArray_ShiftNothing() {
    String[] array = createArray( "a", "b", "c" );
    shiftArray( array, 1, 0, 3 );
    assertArrayEquals( createArray( "a", "b", "c" ), array );
  }

  @Test
  public void shiftArray_NoOffset() {
    String[] array = createArray( "a", "b", "c" );
    shiftArray( array, 1, 2, 0 );
    assertArrayEquals( createArray( "a", "b", "c" ), array );
  }

  @Test
  public void splitBreakableIfPossible_BreakableIndexIsLessThanNil() throws Exception {
    AbstractAlignmentProcessor processor = mockProcessorForSplitTrying();
    processor.setBreakableIndex( -1 );
    assertFalse( processor.splitBreakableIfPossible() );
  }

  @Test
  public void splitBreakableIfPossible_BreakableNodeIsNotSplittable() throws Exception {
    AbstractAlignmentProcessor processor = mockProcessorForSplitTrying();
    processor.setBreakableIndex( 0 );
    // (SpacerRenderNode instanceof SplittableNode) == false
    // noinspection deprecation
    processor.setNodes( new RenderNode[] { new SpacerRenderNode() } );

    assertFalse( processor.splitBreakableIfPossible() );
  }

  @Test
  public void splitBreakableIfPossible_BreakableMaxAllowedWidthIsNil() throws Exception {
    AbstractAlignmentProcessor processor = mockProcessorForSplitTrying();
    processor.setBreakableIndex( 0 );
    // noinspection deprecation
    processor.setNodes( new RenderNode[] { RenderableTextTest.createText( "txt" ) } );
    processor.setBreakableMaxAllowedWidth( 0 );

    assertFalse( processor.splitBreakableIfPossible() );
  }

  @SuppressWarnings( "deprecation" )
  @Test
  public void splitBreakableIfPossible_NodeRefusedToBreak() throws Exception {
    RenderableText node = RenderableTextTest.createText( "txt" );
    // verify node will refuse breaking
    assertNull( node.splitBy( 1 ) );

    AbstractAlignmentProcessor processor = mockProcessorForSplitTrying();
    processor.setBreakableIndex( 0 );
    processor.setNodes( new RenderNode[] { node } );
    processor.setBreakableMaxAllowedWidth( 1 );
    processor.setElementDimensions( new long[] { 2 } );

    assertFalse( processor.splitBreakableIfPossible() );
  }

  @SuppressWarnings( "deprecation" )
  @Test
  public void splitBreakableIfPossible_SplitWasSuccessful() throws Exception {
    RenderableText node = RenderableTextTest.createText( "txt" );
    // verify node will do breaking
    assertNotNull( node.getMinimumWidth() / 2 );

    AbstractAlignmentProcessor processor = mockProcessorForSplitTrying();
    processor.setBreakableIndex( 0 );
    processor.setNodes( new RenderNode[] { node } );
    processor.setBreakableMaxAllowedWidth( node.getMinimumWidth() / 2 );
    processor.setElementDimensions( new long[] { 0 } );

    assertTrue( processor.splitBreakableIfPossible() );
  }

  @SuppressWarnings( "deprecation" )
  private AbstractAlignmentProcessor mockProcessorForSplitTrying() {
    AbstractAlignmentProcessor processor = mock( AbstractAlignmentProcessor.class );

    doCallRealMethod().when( processor ).splitBreakableIfPossible();

    doCallRealMethod().when( processor ).getBreakableIndex();
    doCallRealMethod().when( processor ).setBreakableIndex( anyInt() );

    doCallRealMethod().when( processor ).setNodes( any( RenderNode[].class ) );

    doCallRealMethod().when( processor ).getBreakableMaxAllowedWidth();
    doCallRealMethod().when( processor ).setBreakableMaxAllowedWidth( anyLong() );

    doCallRealMethod().when( processor ).getElementDimensions();
    doCallRealMethod().when( processor ).setElementDimensions( any( long[].class ) );
    return processor;
  }
}

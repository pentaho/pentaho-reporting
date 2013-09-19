package org.pentaho.reporting.engine.classic.core.layout;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.layout.process.util.BlockLevelPaginationShiftState;
import org.pentaho.reporting.engine.classic.core.layout.process.util.InitialPaginationShiftState;
import org.pentaho.reporting.engine.classic.core.layout.process.util.RowLevelPaginationShiftState;

public class PaginationShiftStateTest extends TestCase
{
  // Fictional example run. Block-level boxes contained in each other:
  // [ INIT [ CH1 ] [ CH2 ] [ CH3 [ CC1 ][ CC2 ]] [ CH4 ] ]
  // each box is assumed to have a size of 1 px, but shall be aligned to 5 px positions, starting at 5
  public void testBlockExample()
  {
    // represents outside box.
    InitialPaginationShiftState init = new InitialPaginationShiftState();

    BlockLevelPaginationShiftState stateCH1 = new BlockLevelPaginationShiftState(init);
    stateCH1.increaseShift(5);
    stateCH1.pop();

    assertEquals(5, init.getShiftForNextChild());

    BlockLevelPaginationShiftState stateCH2 = new BlockLevelPaginationShiftState(init);

    assertEquals(5, stateCH2.getShiftForNextChild());
    stateCH2.increaseShift(5);
    assertEquals(10, stateCH2.getShiftForNextChild());
    stateCH2.pop();

    assertEquals(10, init.getShiftForNextChild());

    BlockLevelPaginationShiftState stateCH3 = new BlockLevelPaginationShiftState(init);
    BlockLevelPaginationShiftState stateCC1 = new BlockLevelPaginationShiftState(stateCH3);
    stateCC1.increaseShift(5);
    stateCC1.pop();

    assertEquals(15, stateCH3.getShiftForNextChild());

    BlockLevelPaginationShiftState stateCC2 = new BlockLevelPaginationShiftState(stateCH3);
    stateCC2.increaseShift(5);
    stateCC2.pop();

    stateCH3.pop();
    assertEquals(20, init.getShiftForNextChild());

  }

  public void testRowShifting ()
  {
    InitialPaginationShiftState init = new InitialPaginationShiftState();
    RowLevelPaginationShiftState stateR1 = new RowLevelPaginationShiftState(init);
    stateR1.increaseShift(10);
    stateR1.pop();

    assertEquals(10, init.getShiftForNextChild());

    RowLevelPaginationShiftState stateR2 = new RowLevelPaginationShiftState(init);
    stateR2.increaseShift(10);

    BlockLevelPaginationShiftState stateCC1 = new BlockLevelPaginationShiftState(stateR2);
    assertEquals(20, stateCC1.getShiftForNextChild());
    stateCC1.increaseShift(3);
    stateCC1.pop();

    assertEquals(20, stateR2.getShiftForNextChild());

    BlockLevelPaginationShiftState stateCC2 = new BlockLevelPaginationShiftState(stateR2);
    assertEquals(20, stateCC2.getShiftForNextChild());
    stateCC2.increaseShift(7);
    stateCC2.pop();

    assertEquals(20, stateR2.getShiftForNextChild());

    stateR2.pop();
    assertEquals(27, init.getShiftForNextChild());
  }
}

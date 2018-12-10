package model;

import java.util.*;


/**
 * {@link Grid} instances represent the grid in <i>The Game of Life</i>.
 */
public class Grid implements Iterable<Cell> {

    private final int numberOfRows;
    private final int numberOfColumns;
    private final Cell[][] cells;

    /**
     * Creates a new {@code Grid} instance given the number of rows and columns.
     *
     * @param numberOfRows    the number of rows
     * @param numberOfColumns the number of columns
     * @throws IllegalArgumentException if {@code numberOfRows} or {@code numberOfColumns} are
     *                                  less than or equal to 0
     */
    public Grid(int numberOfRows, int numberOfColumns) {
        this.numberOfRows = numberOfRows;
        this.numberOfColumns = numberOfColumns;
        this.cells = createCells();
    }

    /**
     * Returns an iterator over the cells in this {@code Grid}.
     *
     * @return an iterator over the cells in this {@code Grid}
     */

    @Override
    public Iterator<Cell> iterator() {
        return new GridIterator(this);
    }

    private Cell[][] createCells() {
        Cell[][] cells = new Cell[getNumberOfRows()][getNumberOfColumns()];
        for (int rowIndex = 0; rowIndex < getNumberOfRows(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < getNumberOfColumns(); columnIndex++) {
                cells[rowIndex][columnIndex] = new Cell();
            }
        }
        return cells;
    }

    /**
     * Returns the {@link Cell} at the given index.
     *
     * <p>Note that the index is wrapped around so that a {@link Cell} is always returned.
     *
     * @param rowIndex    the row index of the {@link Cell}
     * @param columnIndex the column index of the {@link Cell}
     * @return the {@link Cell} at the given row and column index
     */
    public Cell getCell(int rowIndex, int columnIndex) {
        return cells[getWrappedRowIndex(rowIndex)][getWrappedColumnIndex(columnIndex)];
    }

    private int getWrappedRowIndex(int rowIndex) {
        return (rowIndex + getNumberOfRows()) % getNumberOfRows();
    }

    private int getWrappedColumnIndex(int columnIndex) {
        return (columnIndex + getNumberOfColumns()) % getNumberOfColumns();
    }

    /**
     * Returns the number of rows in this {@code Grid}.
     *
     * @return the number of rows in this {@code Grid}
     */
    public int getNumberOfRows() {
        return numberOfRows;
    }

    /**
     * Returns the number of columns in this {@code Grid}.
     *
     * @return the number of columns in this {@code Grid}
     */
    public int getNumberOfColumns() {
        return numberOfColumns;
    }


    private List<Cell> getNeighbours(int rowIndex, int columnIndex) {
        List<Cell> neighbours = new ArrayList<>();
        for(int i = rowIndex-1 ; i<=rowIndex+1 ; i++)
        {
            for(int j = columnIndex-1; j<=columnIndex+1 ; j++)
            {
                if(i != rowIndex && j !=columnIndex)
                    neighbours.add(this.getCell(i, j));
            }
        }
        return neighbours;
    }

    private int countAliveNeighbours(int rowIndex, int columnIndex) {
        int count = 0;
        List<Cell> listCell = this.getNeighbours(rowIndex, columnIndex);
        for(Cell cell : listCell)
            if(cell.getState()==CellState.RED || cell.getState() == CellState.BLUE)
                count++;
        return count;
    }

    private CellState determinateAliveNeighbours(int rowIndex, int columnIndex){
        int countR = 0;
        int countB = 0;
        int count = 0;
        List<Cell> listCell = this.getNeighbours(rowIndex, columnIndex);
        for(Cell cell : listCell)
        {
            count ++;
            if(cell.getState() == CellState.BLUE)
                countB = countB+1;
            else if(cell.getState() == CellState.RED)
                countR = countR+1;
        }

        if(countB>=countR && count==3)
            return CellState.BLUE;
        else if(countR>=countB && count==3)
            return CellState.RED;
        else
            return CellState.DEAD;
    }

    private CellState calculateNextState(int rowIndex, int columnIndex) {
        int count = this.countAliveNeighbours(rowIndex, columnIndex);
        if(this.getCell(rowIndex, columnIndex).getState()==CellState.DEAD)
        {
            return determinateAliveNeighbours(rowIndex, columnIndex);
        }
        else
            if(count>=2 && count <= 3)
                return this.getCell(rowIndex, columnIndex).getState();
            else
                return CellState.DEAD;
    }



    private CellState[][] calculateNextStates() {
        CellState[][] nextCellState = new CellState[getNumberOfRows()][getNumberOfColumns()];
        for(int i = 0; i<getNumberOfRows();i++)
        {
            for(int j = 0 ; j<getNumberOfColumns() ; j++){
                nextCellState[i][j] = this.calculateNextState(i, j);
            }
        }
        return nextCellState;
    }

    private void updateStates(CellState[][] nextState) {
        for(int i = 0 ; i<getNumberOfRows();i++)
        {
            for(int j = 0 ; j<getNumberOfColumns();j++)
            {
                this.cells[i][j].setState(nextState[i][j]);
            }
        }
    }

    /**
     * Transitions all {@link Cell}s in this {@code Grid} to the next generation.
     *
     * <p>The following rules are applied:
     * <ul>
     * <li>Any live {@link Cell} with fewer than two live neighbours dies, i.e. underpopulation.</li>
     * <li>Any live {@link Cell} with two or three live neighbours lives on to the next
     * generation.</li>
     * <li>Any live {@link Cell} with more than three live neighbours dies, i.e. overpopulation.</li>
     * <li>Any dead {@link Cell} with exactly three live neighbours becomes a live cell, i.e.
     * reproduction.</li>
     * </ul>
     */
    void updateToNextGeneration() {
        CellState[][] cellState = this.calculateNextStates();
        this.updateStates(cellState);
    }

    /**
     * Sets all {@link Cell}s in this {@code Grid} as dead.
     */
    void clear() {

        for(int i=0;i<getNumberOfRows();i++)
        {
            for(int j=0;j<getNumberOfColumns();j++)
            {
                this.getCell(i, j).setState(CellState.DEAD);
            }
        }

    }

    /**
     * Goes through each {@link Cell} in this {@code Grid} and randomly sets its state as ALIVE or DEAD.
     *
     * @param random {@link Random} instance used to decide if each {@link Cell} is ALIVE or DEAD.
     * @throws NullPointerException if {@code random} is {@code null}.
     */
    void randomGeneration(Random random) {
        for(int i=0;i<getNumberOfRows();i++)
        {
            for(int j=0;j<getNumberOfColumns();j++)
            {
                if(CellState.DEAD.isAlive == random.nextBoolean())
                    this.getCell(i, j).setState(CellState.DEAD);
                else {
                    if (CellState.BLUE.isAlive == random.nextBoolean())
                        this.getCell(i, j).setState(CellState.BLUE);
                    else
                        this.getCell(i, j).setState(CellState.RED);
                }
            }
        }
    }
}

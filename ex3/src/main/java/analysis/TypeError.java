package analysis;

import frontend.SourcePosition;
import minijava.ast.MJElement;

/**
 * This class deals with error messages.
 * Has methods to return position, error message.
 */
public class TypeError extends RuntimeException {
    private SourcePosition source;

    /**
     * sets position of error
     * @param message(@code String)
     * @param line(@code int)
     * @param column(@code int)
     */
    public TypeError(String message, int line, int column) {
        super(message);
        this.source = new SourcePosition("", line, column, line, column);
    }

    /**
     * gets position and parent element
     * @param element(@code MJElement)
     * @param message(@code String)
     */
    public TypeError(MJElement element, String message) {
        super(message);
        while (element != null) {
        this.source = element.getSourcePosition();
            if (this.source != null) {
                break;
            }
            element = element.getParent();
        }
    }

     /**
     * Gets the Line Number
     * @return the value (@code source.getLine())
     */
    public int getLine()
    {
        return source.getLine();
    }

    /**
     * Gets the Column
     * @return the value(@code source.getColumn())
     */
    public int getColumn()
    {
        return source.getColumn();
    }

     /**
     * returns error statement
     * @return the Value ("Error in line"+ @code getLine()+ ":" + getColumn() + ": " + getMessage())
     */
    @Override
    public String toString()
    {
        return "Error in line " + getLine() + ":" + getColumn() + ": " + getMessage();
    }

    /**
     * returns length of column
     * @return the value (@code source.getLine() == source.getEndLine())?(@code source.getEndColumn() - source.getColumn():5)
     *
     */
    public int getLength() {
        if (source.getLine() == source.getEndLine()) {
            return source.getEndColumn() - source.getColumn();
        }
        return 5;
    }

    /**
     * Gets sourcePosition
     * @return (@code source)
     */
    public SourcePosition getSource()
    {
        return source;
    }
}
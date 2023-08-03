package kalah.view;

import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.Serial;

/**
 * Represents a slot on {@link GridPanel}..
 */
public class SlotPanel extends JPanel {
    @Serial
    private static final long serialVersionUID = 7224703144103831000L;
    private SlotLabel[][] labels;
    private final int gridY;
    private final boolean isStorePit;

    /**
     * Creates a slot on the grid.
     *
     * @param gridY Column's number.
     * @param numOfSeeds Number of seeds to be read.
     * @param isStorePit Value to be read.
     */
    public SlotPanel(int gridY, int numOfSeeds,
                     boolean isStorePit) {
        this.isStorePit = isStorePit;
        this.gridY = gridY;
        labels = new SlotLabel[2][1];
        createBoard(numOfSeeds);


    }

    private void createBoard(int numOfSeeds) {
        if (!isStorePit) {
            setLayout(new GridLayout(2, 1));
            labels[0][0] = new SlotLabel(0, gridY, numOfSeeds);
            labels[1][0] = new SlotLabel(1, gridY, numOfSeeds);
        } else {
            setLayout(new BorderLayout());
            labels[0][0] = new SlotLabel(0, gridY, 0);
            labels[1][0] = new SlotLabel(1, gridY, 0);
        }
        add(labels[0][0]);
        add(labels[1][0]);
    }

    /**
     * Returns number of column in the Grid.
     *
     * @return number of column in the Grid.
     */
    public int getGridY() {
        return gridY;
    }

    /**
     * Returns labels.
     *
     * @return labels.
     */
    public SlotLabel[][] getLabels() {
        return labels;
    }
}

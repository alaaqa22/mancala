package kalah.view;

import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.Serial;
import java.awt.Color;

/**
 * Represent the game field.
 */
public class GridPanel extends JPanel {
    @Serial
    private static final long serialVersionUID = -2022742022192439560L;
    private SlotPanel[][] slotsGrid;
    private final JPanel upperNumber;
    private final JPanel downNumber;
    private JPanel field;
    private int numY;
    private int numOfSeeds;

    /**
     * Creates the field components.
     *
     * @param numY Number of columns in the field.
     * @param numOfSeeds Number of seeds to be read
     */
    public GridPanel(int numY, int numOfSeeds) {
        this.numOfSeeds = numOfSeeds;
        this.numY = numY;
        setLayout(new BorderLayout());
        upperNumber = new JPanel(new GridLayout(1, numY));
        downNumber = new JPanel(new GridLayout(1, numY));
        slotsGrid = new SlotPanel[1][numY + 2];
        field = new JPanel(new GridLayout(1, numY + 2));

        createGrid();
        //upper panel.
        for (int i = 2 * numY + 2; i > numY; i--) {
            JLabel l1 = (new JLabel(String.valueOf(i)));
            l1.setForeground(Color.WHITE);
            l1.setHorizontalAlignment(JLabel.CENTER);
            upperNumber.add(l1);
        }

        //down panel.

        JLabel l;
        l = new JLabel(String.valueOf(2 * numY + 2));
        l.setForeground(Color.WHITE);
        l.setHorizontalAlignment(JLabel.CENTER);
        downNumber.add(l);
        for (int i = 0; i < numY + 1; i++) {
            l = (new JLabel(String.valueOf(i + 1)));
            l.setForeground(Color.WHITE);
            l.setHorizontalAlignment(JLabel.CENTER);
            downNumber.add(l);
        }
        downNumber.setBackground(SlotLabel.BROWN_COLOR);
        add(field, BorderLayout.CENTER);
        add(upperNumber, BorderLayout.NORTH);
        add(downNumber, BorderLayout.SOUTH);
        setOpaque(false);
    }
    private void createGrid() {
        for (int i = 0; i < numY + 2; i++) {
            SlotPanel slotPanel;
            if (i > 0 && i < numY + 1) {
                slotPanel = new SlotPanel(i, numOfSeeds, false);
            } else {
                slotPanel = new SlotPanel(i, numOfSeeds, true);
            }
            slotsGrid[0][i] = slotPanel;
            field.add(slotPanel);
        }        upperNumber.setBackground(SlotLabel.BROWN_COLOR);

        }

    /**
     * Returns slots on grid
     *
     * @return Slots on grid.
     */
    public SlotPanel[][] getSlotsGrid() {
        return slotsGrid;
    }

    /**
     * Returns field on Grid.
     *
     * @return Field.
     */
    public JPanel getField() {
        return field;
    }
}

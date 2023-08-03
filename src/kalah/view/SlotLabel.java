package kalah.view;

import javax.swing.JLabel;
import javax.swing.border.LineBorder;
import javax.swing.SwingConstants;

import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.io.Serial;

/**
 * Represent a label on the {@link SlotPanel}.
 */
public class SlotLabel extends JLabel {
    /**
     *
     */
    public static final Color BROWN_COLOR = new Color(153, 102, 0);
    private static final Color SLOTS_COLOR =
            Color.getHSBColor(0.08f, 0.3f, 0.97f);
    @Serial
    private static final long serialVersionUID = -1300903333790655792L;
    private final int seeds;
    private final int pit;
    private final int row;
    private boolean isHighlighted;

    /**
     * Creates a label with the given row,pit and seeds.
     *
     * @param row The row to be read.
     * @param pit The pit to be read.
     * @param seeds The seeds to be read.
     */
    public SlotLabel(int row, int pit, int seeds) {
        this.pit = pit;
        this.row = row;
        this.seeds = seeds;
        setBorder(new LineBorder(BROWN_COLOR));
        setHorizontalAlignment(SwingConstants.CENTER);
        setFont(new Font("Arial", Font.BOLD, 30));
        setText(String.valueOf(seeds));
        setHighlighted(false);
    }

    /**
     * Repaint components.
     *
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(SLOTS_COLOR);
        g.fillRect(0, 0, getWidth(), getHeight());
        if (isHighlighted) {
            g.setColor(Color.BLUE);
            g.fillRect(getWidth() - getWidth() / 3, 0, getWidth(), getHeight());
        }
        super.paintComponent(g);
    }

    /**
     * Set highlighted.
     *
     * @param highlighted The flag to be read.
     */
    public void setHighlighted(boolean highlighted) {
        isHighlighted = highlighted;
    }
}

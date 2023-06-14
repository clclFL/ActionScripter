package club.pineclone.gui.swing;

import club.pineclone.api.CallBack;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

public class SliderToggleButton extends JToggleButton {

    public static final int WHEN_ON = 0;
    public static final int WHEN_OFF = 1;

    private int posX;
    private Timer timer;
    private boolean isPainting;

    private final int sliderWidth;
    private final int sliderHeight;
    private final Color sliderColor;

    private final int maxX;
    private final int minX;

    private final List<CallBack<Void>> onSubscribers = new LinkedList<>();
    private final List<CallBack<Void>> offSubscribers = new LinkedList<>();

    public SliderToggleButton(int width, int height) {
        this(width, height, Color.darkGray);
    }

    public void addListener(CallBack<Void> subscriber, int when) {
        switch (when) {
            case WHEN_ON: {
                onSubscribers.add(subscriber);
                break;
            }
            case WHEN_OFF: {
                offSubscribers.add(subscriber);
            }
        }
    }

    public SliderToggleButton(int width, int height, Color sliderColor) {
        setPreferredSize(new Dimension(width, height));
        sliderWidth = width / 2;
        sliderHeight = height;
        this.sliderColor = sliderColor;
        isPainting = false;

        maxX = width - sliderWidth;
        minX = 0;

        setModel(new ToggleButtonModel());
        setUI(new ToggleButtonUI());

        timer = new Timer(1, e -> {
            if (!isSelected()) {
                if (posX < maxX) {
                    posX += 1;
                } else {
                    posX = maxX;
                    timer.stop();
                    setSelected(true);
                    onSubscribers.forEach(c -> c.callBack(null));
                    isPainting = false;
                    return;
                }
            }

            if (isSelected()) {
                if (posX > minX) {
                    posX -= 1;
                } else {
                    posX = minX;
                    timer.stop();
                    setSelected(false);
                    offSubscribers.forEach(c -> c.callBack(null));
                    isPainting = false;
                    return;
                }
            }
            repaint();
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isPainting) return;
                timer.start();
                isPainting = true;
                super.mousePressed(e);
            }
        });
    }

    @Override
    public void setSelected(boolean flag) {
        posX = flag ? maxX : 0;
        repaint();
        super.setSelected(flag);
    }

    private static class ToggleButtonModel extends DefaultButtonModel {
        @Override
        public boolean isRollover() {
            return super.isRollover() || isPressed();
        }
    }

    private class ToggleButtonUI extends BasicButtonUI {
        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(sliderColor);
            g2.fillRoundRect(posX, 0, sliderWidth, sliderHeight, 0, 0);
            super.paint(g, c);
        }
    }

/*    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test");
            SliderToggleButton button = new SliderToggleButton(40, 20);
            JPanel buttonPanel = new JPanel();
            button.setSelected(true);

            button.addListener(v -> {
                System.out.println("button on");
            } , SliderToggleButton.WHEN_ON);

            button.addListener(v -> {
                System.out.println("button off");
            } , SliderToggleButton.WHEN_OFF);

            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER , 5 , 5));
            buttonPanel.add(new JLabel("open auto saving"));
            buttonPanel.add(button);

            frame.add(buttonPanel, BorderLayout.CENTER);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        })};*/

}

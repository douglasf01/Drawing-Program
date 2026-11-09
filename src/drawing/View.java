package drawing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Everything inside the main window: the toolbar, the canvas and the status
 * bar. The menu bar is built here too and handed to the frame by
 * {@link #getMenuBar()}. The view listens to the model and updates itself
 * whenever the model changes.
 */
public final class View extends JPanel {
    private static final long serialVersionUID = 1L;

    /** Stroke widths offered in the toolbar, in pixels. */
    public static final List<Integer> STROKES = List.of(1, 2, 4, 8);

    private static final int CTRL = InputEvent.CTRL_DOWN_MASK;

    private final transient Model model;
    private final DrawingPanel canvas;
    private final JMenuBar menuBar = new JMenuBar();

    private final transient Action newAction = action("New", KeyEvent.VK_N, CTRL, this::newDrawing);
    private final transient Action saveAction = action("Save as PNG...", KeyEvent.VK_S, CTRL, this::saveAsPng);
    private final transient Action exitAction = action("Exit", KeyEvent.VK_Q, CTRL, this::exit);
    private final transient Action undoAction;
    private final transient Action redoAction;
    private final transient Action clearAction;

    private final transient Map<Shape.Kind, JToggleButton> toolButtons = new EnumMap<>(Shape.Kind.class);
    private final JToggleButton outlineButton = new JToggleButton();
    private final JToggleButton fillButton = new JToggleButton();
    private final transient List<JToggleButton> strokeButtons = new ArrayList<>();
    private final transient List<SwatchButton> swatches = new ArrayList<>();

    private final JLabel toolStatus = statusLabel();
    private final JLabel colorStatus = statusLabel();
    private final JLabel strokeStatus = statusLabel();
    private final JLabel fillStatus = statusLabel();
    private final JLabel cursorStatus = statusLabel();
    private final JLabel countStatus = statusLabel();

    public View(Model model) {
        super(new BorderLayout());
        this.model = model;
        this.canvas = new DrawingPanel(model);
        undoAction = action("Undo", KeyEvent.VK_Z, CTRL, model::undo);
        redoAction = action("Redo", KeyEvent.VK_Y, CTRL, model::redo);
        clearAction = action("Clear", KeyEvent.VK_DELETE, CTRL, model::clear);

        Controller controller = new Controller(model, this);
        canvas.addMouseListener(controller);
        canvas.addMouseMotionListener(controller);

        setBackground(Theme.BACKGROUND);
        buildMenuBar();
        add(buildToolBar(), BorderLayout.NORTH);
        add(buildWorkspace(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
        bindKeys();
        colorStatus.setIconTextGap(2);

        model.addChangeListener(this::refresh);
        hideCursor();
        refresh();
    }

    /** The menu bar, for the frame to show above this panel. */
    public JMenuBar getMenuBar() {
        return menuBar;
    }

    public DrawingPanel getCanvas() {
        return canvas;
    }

    // ----- Building the parts -----

    private void buildMenuBar() {
        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        file.add(new JMenuItem(newAction));
        file.add(new JMenuItem(saveAction));
        file.addSeparator();
        file.add(new JMenuItem(exitAction));

        JMenu edit = new JMenu("Edit");
        edit.setMnemonic(KeyEvent.VK_E);
        edit.add(new JMenuItem(undoAction));
        edit.add(new JMenuItem(redoAction));
        edit.addSeparator();
        edit.add(new JMenuItem(clearAction));

        menuBar.add(file);
        menuBar.add(edit);
    }

    private JToolBar buildToolBar() {
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);
        bar.setBackground(Theme.BACKGROUND);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        for (Shape.Kind kind : Shape.Kind.values()) {
            JToggleButton button = iconToggle(c -> Icons.tool(kind, c),
                    kind + " (" + shortcutKey(kind) + ")");
            button.addActionListener(e -> model.setTool(kind));
            toolButtons.put(kind, button);
            bar.add(button);
        }

        bar.add(divider());
        styleIconToggle(outlineButton, c -> Icons.fill(false, c), "Outline");
        styleIconToggle(fillButton, c -> Icons.fill(true, c), "Fill (F)");
        outlineButton.addActionListener(e -> model.setFilled(false));
        fillButton.addActionListener(e -> model.setFilled(true));
        bar.add(outlineButton);
        bar.add(fillButton);

        bar.add(divider());
        for (int width : STROKES) {
            JToggleButton button = iconToggle(c -> Icons.stroke(width, c), "Stroke " + width + " px");
            button.addActionListener(e -> model.setStroke(width));
            strokeButtons.add(button);
            bar.add(button);
        }

        bar.add(divider());
        for (Palette.Swatch swatch : Palette.SWATCHES) {
            SwatchButton button = new SwatchButton(swatch.color(), swatch.name());
            button.addActionListener(e -> model.setColor(swatch.color()));
            swatches.add(button);
            bar.add(button);
        }
        bar.add(Box.createHorizontalStrut(4));
        JButton more = FlatButtonUI.style(new JButton("More..."));
        more.setToolTipText("Pick any colour");
        more.addActionListener(e -> chooseColor());
        bar.add(more);

        bar.add(Box.createHorizontalGlue());
        bar.add(iconButton(undoAction, c -> Icons.history(false, c), "Undo (Ctrl+Z)"));
        bar.add(iconButton(redoAction, c -> Icons.history(true, c), "Redo (Ctrl+Y)"));
        return bar;
    }

    private JComponent buildWorkspace() {
        JPanel paper = new JPanel(new BorderLayout());
        paper.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        paper.add(canvas);

        JPanel workspace = new JPanel(new BorderLayout()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintShadow(g, paper);
            }
        };
        workspace.setBackground(Theme.WORKSPACE);
        workspace.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        workspace.add(paper);
        return workspace;
    }

    /** A soft shadow below the paper, so it lifts off the workspace. */
    private static void paintShadow(Graphics g, Component paper) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int i = 1; i <= 4; i++) {
            g2.setColor(new Color(16, 24, 40, 14 - i * 3));
            g2.fillRoundRect(paper.getX() - i, paper.getY() - i + 2,
                    paper.getWidth() + 2 * i, paper.getHeight() + 2 * i, 2 * i + 2, 2 * i + 2);
        }
        g2.dispose();
    }

    private JComponent buildStatusBar() {
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        addWithDividers(left, toolStatus, colorStatus, strokeStatus, fillStatus);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        addWithDividers(right, cursorStatus, countStatus);

        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Theme.BACKGROUND);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private static void addWithDividers(JPanel panel, JLabel... labels) {
        for (int i = 0; i < labels.length; i++) {
            if (i > 0) {
                JSeparator line = new JSeparator(SwingConstants.VERTICAL);
                line.setPreferredSize(new Dimension(1, 14));
                panel.add(Box.createHorizontalStrut(12));
                panel.add(line);
                panel.add(Box.createHorizontalStrut(12));
            }
            panel.add(labels[i]);
        }
    }

    /** Single key shortcuts for the tools, and Ctrl+Shift+Z as a second redo key. */
    private void bindKeys() {
        for (Shape.Kind kind : Shape.Kind.values()) {
            bindKey(KeyStroke.getKeyStroke(shortcutKey(kind), 0), "tool-" + kind.name(),
                    () -> model.setTool(kind));
        }
        bindKey(KeyStroke.getKeyStroke(KeyEvent.VK_F, 0), "toggle-fill",
                () -> model.setFilled(!model.isFilled()));
        bindKey(KeyStroke.getKeyStroke(KeyEvent.VK_Z, CTRL | InputEvent.SHIFT_DOWN_MASK), "redo",
                model::redo);
    }

    private void bindKey(KeyStroke key, String name, Runnable task) {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(key, name);
        getActionMap().put(name, action(name, 0, 0, task));
    }

    private static char shortcutKey(Shape.Kind kind) {
        return kind.toString().charAt(0);
    }

    // ----- Small component helpers -----

    /** An action with an optional keyboard shortcut (key 0 means none). */
    private static Action action(String name, int key, int modifiers, Runnable task) {
        Action action = new AbstractAction(name) {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                task.run();
            }
        };
        if (key != 0) {
            action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(key, modifiers));
        }
        return action;
    }

    private static JToggleButton iconToggle(Function<Color, Icon> icon, String tip) {
        JToggleButton button = new JToggleButton();
        styleIconToggle(button, icon, tip);
        return button;
    }

    private static void styleIconToggle(JToggleButton button, Function<Color, Icon> icon, String tip) {
        FlatButtonUI.style(button);
        setIcons(button, icon);
        button.setToolTipText(tip);
        button.getAccessibleContext().setAccessibleName(tip);
    }

    private static JButton iconButton(Action action, Function<Color, Icon> icon, String tip) {
        JButton button = new JButton(action);
        button.setHideActionText(true);
        FlatButtonUI.style(button);
        setIcons(button, icon);
        button.setToolTipText(tip);
        return button;
    }

    private static void setIcons(AbstractButton button, Function<Color, Icon> icon) {
        button.setIcon(icon.apply(Theme.ICON));
        button.setSelectedIcon(icon.apply(Theme.ACCENT));
        button.setDisabledIcon(icon.apply(Theme.ICON_DISABLED));
        button.setDisabledSelectedIcon(icon.apply(Theme.ICON_DISABLED));
    }

    private static JComponent divider() {
        JPanel holder = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        holder.setOpaque(false);
        holder.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        JSeparator line = new JSeparator(SwingConstants.VERTICAL);
        line.setPreferredSize(new Dimension(1, 20));
        holder.add(line);
        holder.setMaximumSize(new Dimension(17, 32));
        return holder;
    }

    private static JLabel statusLabel() {
        JLabel label = new JLabel();
        label.setFont(Theme.FONT_SMALL);
        label.setForeground(Theme.TEXT_MUTED);
        return label;
    }

    // ----- Updating -----

    /** Brings the toolbar, menus and status bar in line with the model, and repaints. */
    private void refresh() {
        Shape.Kind tool = model.getTool();
        toolButtons.forEach((kind, button) -> button.setSelected(kind == tool));

        outlineButton.setSelected(!model.isFilled());
        fillButton.setSelected(model.isFilled());
        outlineButton.setEnabled(tool.canFill());
        fillButton.setEnabled(tool.canFill());

        for (int i = 0; i < STROKES.size(); i++) {
            strokeButtons.get(i).setSelected(STROKES.get(i) == model.getStroke());
        }
        for (SwatchButton swatch : swatches) {
            swatch.setSelected(swatch.getColor().equals(model.getColor()));
        }

        undoAction.setEnabled(model.canUndo());
        redoAction.setEnabled(model.canRedo());
        clearAction.setEnabled(!model.getShapes().isEmpty());

        toolStatus.setText("Tool: " + tool);
        colorStatus.setIcon(Icons.colorDot(model.getColor()));
        colorStatus.setText(Palette.nameOf(model.getColor()));
        strokeStatus.setText("Stroke: " + model.getStroke() + " px");
        fillStatus.setText("Style: " + styleName(tool, model.isFilled()));
        int count = model.getShapes().size();
        countStatus.setText(count + (count == 1 ? " shape" : " shapes"));
        canvas.repaint();
    }

    private static String styleName(Shape.Kind tool, boolean filled) {
        if (tool == Shape.Kind.DOT) {
            return "Filled";
        }
        if (!tool.canFill()) {
            return "Line";
        }
        return filled ? "Filled" : "Outline";
    }

    /** Shows the cursor position on the canvas in the status bar. */
    public void showCursor(int x, int y) {
        cursorStatus.setText(x + ", " + y + " px");
    }

    /** Clears the cursor position when the mouse leaves the canvas. */
    public void hideCursor() {
        cursorStatus.setText("Off canvas");
    }

    // ----- Menu commands -----

    private void newDrawing() {
        if (!model.getShapes().isEmpty()) {
            int answer = JOptionPane.showConfirmDialog(this,
                    "Start a new drawing? The current drawing and its undo history will be lost.",
                    "New drawing", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (answer != JOptionPane.OK_OPTION) {
                return;
            }
        }
        model.newDrawing();
    }

    private void saveAsPng() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save as PNG");
        chooser.setFileFilter(new FileNameExtensionFilter("PNG images", "png"));
        chooser.setSelectedFile(new File("drawing.png"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".png")) {
            file = new File(file.getParentFile(), file.getName() + ".png");
        }
        if (file.exists() && JOptionPane.showConfirmDialog(this,
                file.getName() + " already exists. Replace it?", "Save as PNG",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            ImageIO.write(canvas.toImage(), "png", file);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not save the image:\n" + e.getMessage(),
                    "Save as PNG", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void chooseColor() {
        Color chosen = JColorChooser.showDialog(this, "Pick a colour", model.getColor());
        if (chosen != null) {
            model.setColor(chosen);
        }
    }

    private void exit() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
        }
    }
}

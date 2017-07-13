package com.mczal.nb;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Created by mczal on 6/11/17.
 */
@SuppressWarnings("serial")
public class TextAreaOutputStreamWrapper extends JPanel {

  private JTextArea textArea = new JTextArea(30, 100);

  private TextAreaOutputStream taOutputStream = new TextAreaOutputStream(
      textArea, "> ", 100);

  public TextAreaOutputStreamWrapper() {
    textArea.setEditable(false);
    textArea.setFont(textArea.getFont().deriveFont(12f));
    setLayout(new BorderLayout());
    add(new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
    System.setOut(new PrintStream(taOutputStream));
  }

  private static void createAndShowGui() {
    JFrame frame = new JFrame("Output");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(new TextAreaOutputStreamWrapper());
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  public static void mainRunner(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        createAndShowGui();
      }
    });
  }
}

class TextAreaOutputStream extends OutputStream {

  private final JTextArea textArea;

  private final StringBuilder sb = new StringBuilder();
  private String title;
  //  private int counter;
  private int column;

  TextAreaOutputStream(final JTextArea textArea, String title, int column) {
//    counter = 0;
    this.column = column;
    this.textArea = textArea;
    this.title = title;
    sb.append(title);
  }

  @Override
  public void flush() {
  }

  @Override
  public void close() {
  }

  @Override
  public void write(int b) throws IOException {

    if (b == '\r') {
//      counter = 0;
      return;
    }

    if (b == '\n') {
      final String text = sb.toString() + "\n";
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          textArea.append(text);
        }
      });
      sb.setLength(0);
      sb.append(title);
//      counter = 0;
      return;
    }

//    if (counter >= (column * 2) - (column / 2)) {
//      sb.append("\n");
//      counter = 0;
//    }
//    counter++;

    sb.append((char) b);

  }
}

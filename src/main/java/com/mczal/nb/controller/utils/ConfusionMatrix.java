package com.mczal.nb.controller.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Gl552 on 3/5/2017.
 */
public class ConfusionMatrix {

  /**
   * <ClassValue,Index>
   */
  private HashMap<String, Integer> info = new HashMap<>();

  /**
   * [actual][predicted]
   */
  private int[][] matrix;

  public ConfusionMatrix(int length) {
    matrix = new int[length][length];
    Arrays.stream(matrix).forEach(ints -> {
      Arrays.stream(ints).forEach(value -> {
        value = 0;
      });
    });
  }

  public HashMap<String, Integer> getInfo() {
    return info;
  }

  public void setInfo(HashMap<String, Integer> info) {
    this.info = info;
  }

  public int[][] getMatrix() {
    return matrix;
  }

  public void setMatrix(int[][] matrix) {
    this.matrix = matrix;
  }

  private String sAddLessSpace(int size, String res, int lengthCurrentPrint) {
    int length = size - lengthCurrentPrint;
    for (int i = 0; i < length; i++) {
      res += " ";
    }
    return res;
  }

  private int sFindDigitLength(int digit) {
    int length = 0;
    int tmpDigit = digit;
    //    System.out.println("For Digit: " + digit);
    while (tmpDigit > 0) {
      //      System.out.println("tmpDigit: " + tmpDigit);
      length++;
      tmpDigit /= 10;
    }
    //    System.out.println("length: " + length);
    //    System.out.println(length == 0 ? 1 : length);
    return (length == 0 ? 1 : length);
  }

  private String sNewLine(int horizontalLength, String res) {
    res += "\n";
    for (int i = 0; i < horizontalLength; i++) {
      res += "-";
    }
    res += "\n";
    return res;
  }

  //  private String sNewLine(int horizontalLength, String res, int flag) {
  //    res += "\n";
  //    for (int i = 0; i < horizontalLength; i++) {
  //      res += "-";
  //    }
  //    res += "\n";
  //    return res;
  //  }

  public String stringPrintedMatrix() {
    HashMap<Integer, String> infoFromIdx = new HashMap<>();
    int mostLongLength = 0;
    for (Iterator<Map.Entry<String, Integer>> itr = info.entrySet().iterator(); itr.hasNext(); ) {
      Map.Entry<String, Integer> entry = itr.next();
      infoFromIdx.put(entry.getValue(), entry.getKey());
      if (mostLongLength < entry.getKey().length()) {
        mostLongLength = entry.getKey().length();
      }
    }
    mostLongLength++;
    String res = "|";
    res = sAddLessSpace(mostLongLength, res, 0);
    res += " ";
    for (int i = 0; i < infoFromIdx.size(); i++) {
      String s = infoFromIdx.get(i);
      if (s == null) {
        throw new IllegalArgumentException("String infoFromIdx is null");
      }
      //      res += s + "  ";
      res += "| " + s;
      res = sAddLessSpace(mostLongLength, res, s.length());
    }
    res += "|";
    res.trim();
    int horizontalLength = res.length();
    res = sNewLine(horizontalLength, new String()) + res;
    res = sNewLine(horizontalLength, res);
    for (int i = 0; i < matrix.length; i++) {

      res += "| " + infoFromIdx.get(i);
      res = sAddLessSpace(mostLongLength, res, infoFromIdx.get(i).length());
      for (int j = 0; j < matrix[0].length; j++) {
        res += "| " + matrix[i][j];
        res = sAddLessSpace(mostLongLength, res, sFindDigitLength(matrix[i][j]));
      }
      res += "|";
      res.trim();
      res += System.lineSeparator();
    }

    return sNewLine(horizontalLength, res.trim()).trim();
  }

  @Override
  public String toString() {
    return "ConfusionMatrix{" + "info=" + info + ", matrix=" + Arrays.toString(matrix) + '}';
  }
}

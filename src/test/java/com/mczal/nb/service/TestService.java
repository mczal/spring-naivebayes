package com.mczal.nb.service;

import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Gl552 on 3/5/2017.
 */
public class TestService {

  @Test
  public void testSplitLength() {
    String tst = "abcd/asdlj/asdl";
    String[] splitter = tst.split("dataset");
    System.out.println(splitter.length);
    System.out.println(Arrays.toString(splitter));
  }

  @Test
//  @Ignore
  public void testStreamForEachArray() {
    int length = 1000000;
    int[] arr = new int[length];
    for (int i = 0; i < length; i++) {
      arr[i] = i;
    }
    Arrays.stream(arr).forEach(value -> {
      System.out.println(value);
    });
  }

  @Test
//  @Ignore
  public void testStreamForEachList() {
    int length = 1000000;
    List<Integer> arr = new ArrayList<>();
    for (int i = 0; i < length; i++) {
      arr.add(i);
    }
    arr.stream().forEach(integer -> {
      System.out.println(integer);
    });
  }

}


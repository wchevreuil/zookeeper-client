package com.cloudera.zookeeper.client;

import java.util.ArrayList;
import java.util.List;

public class Result<T> {

  private List<T> results;

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

    Result<String> results = new Result<String>();
    results.results = new ArrayList<String>();

  }


}

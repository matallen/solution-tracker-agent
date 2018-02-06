package com.redhat.sso.solutiontracker.requests.integration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

public class StringUtils {
  public static String toString(List<String> list){
    return Joiner.on(", ").skipNulls().join(list);
  }
  public static List<String> toList(String commaSeparatedString){
//    return Arrays.asList(Iterables.toArray(Splitter.on(",").trimResults().split(commaSeparatedString), String.class));
    if (null==commaSeparatedString) return null;
    List<String> result=new ArrayList<String>();
    for(String v:Splitter.on(",").trimResults().split(commaSeparatedString))
      result.add(v);
    return result;
  }
}

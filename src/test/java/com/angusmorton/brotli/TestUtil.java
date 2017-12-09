package com.angusmorton.brotli;

import okio.BufferedSource;
import okio.Okio;

import java.io.File;
import java.io.IOException;

public final class TestUtil {

  public static BufferedSource resource(String name) {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    File file = new File(classLoader.getResource(name).getFile());
    try {
      return Okio.buffer(Okio.source(file));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}

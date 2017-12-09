package com.angusmorton.brotli.internal;

import okio.Buffer;
import okio.Okio;
import okio.Source;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.*;

public class BrotliSourceTest {

  @Test
  public void decode() throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource("100kilobytes.br").getFile());
    Source brotliSource = BrotliSource.create(Okio.buffer(Okio.source(file)));

    // Decode!
    Buffer brotliDecoded = new Buffer();
    while (brotliSource.read(brotliDecoded, Integer.MAX_VALUE) != -1) {
    }
    // BrotliDecoded now contains all of the decoded
    String decoded = brotliDecoded.readUtf8();

    File expectedFile = new File(classLoader.getResource("100kilobytes.txt").getFile());
    String expected = Okio.buffer(Okio.source(expectedFile)).readUtf8();

    Assert.assertEquals(decoded, expected);
  }

  @Test
  public void decodeErrorWhenNotBrotliEncoded() throws IOException {
    try {
      ClassLoader classLoader = getClass().getClassLoader();
      File file = new File(classLoader.getResource("100kilobytes.txt").getFile());
      Source brotliSource = BrotliSource.create(Okio.buffer(Okio.source(file)));

      // Decode!
      Buffer brotliDecoded = new Buffer();
      while (brotliSource.read(brotliDecoded, Integer.MAX_VALUE) != -1) {
      }
      fail();
    } catch (IOException expected) {
    }
  }
}
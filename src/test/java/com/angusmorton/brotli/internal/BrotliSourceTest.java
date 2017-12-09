package com.angusmorton.brotli.internal;

import com.angusmorton.brotli.TestUtil;
import okio.Buffer;
import okio.Okio;
import okio.Source;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.angusmorton.brotli.TestUtil.resource;
import static org.junit.Assert.*;

public class BrotliSourceTest {

  @Test
  public void decode() throws IOException {

    Source brotliSource = BrotliSource.create(resource("100kilobytes.br"));

    // Decode!
    Buffer brotliDecoded = new Buffer();
    while (brotliSource.read(brotliDecoded, Integer.MAX_VALUE) != -1) {
    }

    // BrotliDecoded now contains all of the decoded
    String decoded = brotliDecoded.readUtf8();
    String expected = resource("100kilobytes.txt").readUtf8();

    Assert.assertEquals(expected, decoded);
  }

  @Test
  public void decodeErrorWhenNotBrotliEncoded() throws IOException {
    try {
      Source brotliSource = BrotliSource.create(resource("100kilobytes.txt"));

      // Decode!
      Buffer brotliDecoded = new Buffer();
      while (brotliSource.read(brotliDecoded, Integer.MAX_VALUE) != -1) {
      }
      fail();
    } catch (IOException expected) {
    }
  }
}
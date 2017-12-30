package com.angusmorton.brotli.internal;

import okio.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.angusmorton.brotli.TestUtil.resource;
import static org.junit.Assert.*;

public class BrotliSourceTest {

  @Test
  public void decode() throws IOException {
    InputStream inputStream = new ByteArrayInputStream(ByteString.decodeHex("8b0f8049742773206120554e49582073797374656d212049206b6e6f7720746869732103").toByteArray());
    Source brotliSource = BrotliSource.create(Okio.buffer(Okio.source(inputStream)));

    String decoded = Okio.buffer(brotliSource).readUtf8();
    String expected = "It's a UNIX system! I know this!";

    Assert.assertEquals(expected, decoded);
  }

  @Test
  public void decodeWithBuffer() throws IOException {
    Buffer brotliBuffer = new Buffer()
        .write(ByteString.decodeHex("8b0f8049742773206120554e49582073797374656d212049206b6e6f7720746869732103"));
    Source brotliSource = BrotliSource.create(brotliBuffer);

    String decoded = Okio.buffer(brotliSource).readUtf8();

    Assert.assertEquals("It's a UNIX system! I know this!", decoded);
  }

  @Test
  public void decodeErrorWhenNotBrotliEncoded() throws IOException {
    try {
      Source brotliSource = BrotliSource.create(resource("100kilobytes.txt"));
      Okio.buffer(brotliSource).readUtf8();
      fail();
    } catch (IOException expected) {
    }
  }

  @Test
  public void brotliDecodesShortString() throws Exception {
    Buffer brotliBuffer = new Buffer().write(ByteString.decodeHex("0b018061626303")); // abc
    BufferedSource brotliSource = Okio.buffer(BrotliSource.create(brotliBuffer));
    Assert.assertEquals("abc", brotliSource.readUtf8());
  }
}
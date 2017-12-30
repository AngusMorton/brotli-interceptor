package com.angusmorton.brotli;

import okhttp3.*;
import okhttp3.internal.http.RealResponseBody;
import okhttp3.internal.tls.SslClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;
import okio.ByteString;
import okio.Okio;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static com.angusmorton.brotli.TestUtil.resource;
import static org.junit.Assert.*;

public final class BrotliResponseInterceptorTest {

  @Rule
  public MockWebServer server = new MockWebServer();

  private SslClient sslClient = SslClient.localhost();
  private OkHttpClient client;
  private String host;
  private HttpUrl url;

  @Before
  public void setUp() {
    client = new OkHttpClient.Builder()
        .addNetworkInterceptor(new BrotliResponseInterceptor())
        .sslSocketFactory(sslClient.socketFactory, sslClient.trustManager)
        .build();

    host = server.getHostName() + ":" + server.getPort();
    url = server.url("/");
  }

  @Test
  public void interceptorDecodeEncodedResponse() throws Exception {
    Buffer brotliSource = new Buffer()
        .write(ByteString.decodeHex("0b018061626303")); // 'abc'
    MockResponse mockResponse = new MockResponse()
        .setBody(brotliSource)
        .addHeader("Content-Encoding: br");

    server.enqueue(mockResponse);

    Request request = request().build();

    client = client.newBuilder()
        .addNetworkInterceptor(new BrotliResponseInterceptor())
        .build();

    // No extra headers in the application's request.
    assertNull(request.header("User-Agent"));
    assertNull(request.header("Host"));
    assertNull(request.header("Accept-Encoding"));

    Response response = client.newCall(request).execute();
    Assert.assertNull(response.header("Content-Encoding"));

    String actualResponse = response.body().string();
    assertEquals("abc", actualResponse);
  }

  private Request.Builder request() {
    return new Request.Builder().url(url);
  }
}
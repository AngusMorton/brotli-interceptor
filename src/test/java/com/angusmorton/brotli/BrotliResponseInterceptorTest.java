package com.angusmorton.brotli;

import okhttp3.*;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static com.angusmorton.brotli.TestUtil.resource;
import static org.junit.Assert.*;

public final class BrotliResponseInterceptorTest {
  private static final ConnectionPool connectionPool = new ConnectionPool();
  private static final Dispatcher dispatcher = new Dispatcher();

  public static OkHttpClient defaultClient() {
    return new OkHttpClient.Builder()
        .connectionPool(connectionPool)
        .dispatcher(dispatcher)
        .dns(new SingleInetAddressDns()) // Prevent unexpected fallback addresses.
        .build();
  }

  @Rule
  public MockWebServer server = new MockWebServer();

  private OkHttpClient client = defaultClient();

  @Test
  public void applicationInterceptorsCanShortCircuitResponses() throws Exception {
    server.enqueue(new MockResponse()
        .setBody(resource("100kilobytes.br").buffer())
        .addHeader("Content-Encoding: br"));

    Request request = new Request.Builder()
        .url(server.url("/"))
        .build();

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
    String expectedResponse = resource("100kilobytes.txt").readUtf8();
    assertEquals(expectedResponse, actualResponse);
  }
}
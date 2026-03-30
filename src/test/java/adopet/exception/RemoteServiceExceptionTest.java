package adopet.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RemoteServiceExceptionTest {

  @Test
  void shouldExposeMessageStatusCodeAndResponseBody() {
    RemoteServiceException exception = new RemoteServiceException("boom", 502, "{\"error\":\"upstream\"}");

    assertEquals("boom", exception.getMessage());
    assertEquals(502, exception.getStatusCode());
    assertEquals("{\"error\":\"upstream\"}", exception.getResponseBody());
  }
}

package pl.kubie.fluentmocks.http.api;

public interface HttpVerification {

  HttpVerification never();

  HttpVerification once();

  HttpVerification exactly(int times);

  HttpVerification atLeast(int times);

  HttpVerification atMost(int times);

  HttpVerification between(int atLeast, int atMost);

}

package uk.gov.hmcts.reform.dev.models;

public record ApiError(
    int status,
    String error,
    String message
) {
}

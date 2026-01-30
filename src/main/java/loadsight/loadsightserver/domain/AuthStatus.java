package loadsight.loadsightserver.domain;

public enum AuthStatus {
    none,
    bearer,
    basic,
    apikey_header,
    apikey_query,
    custom
}
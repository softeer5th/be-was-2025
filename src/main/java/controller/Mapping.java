package controller;

import webserver.httpserver.HttpMethod;

public @interface Mapping {
    String path();
    HttpMethod method();
}

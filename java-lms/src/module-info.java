module com.library {
    requires java.base;
    requires java.desktop;
    requires jdk.httpserver;

    exports com.library;
    exports com.library.enums;
    exports com.library.interfaces;
    exports com.library.models;
    exports com.library.services;
    exports com.library.strategies;
    exports com.library.ui;
}

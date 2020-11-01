package com.github.pgagala

import groovy.transform.CompileStatic

@CompileStatic
class OrderedSpockProperties {

    private final Properties properties = new Properties()

    OrderedSpockProperties() {
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("ordered-spock-app.properties"))
    }

    String version() {
        return properties.version
    }

    String artifactId() {
        return properties.artifactId
    }

    String groupId() {
        return properties.groupId
    }

    String id() {
        return properties.id
    }
}

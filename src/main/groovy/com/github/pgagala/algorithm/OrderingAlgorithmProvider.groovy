package com.github.pgagala.algorithm

import groovy.transform.CompileStatic


/**
 * Provides single ordering algorithm - default or predefined by client.
 */
@CompileStatic
class OrderingAlgorithmProvider {

    private final OrderingAlgorithmLoader orderingAlgorithmLoader

    OrderingAlgorithmProvider(OrderingAlgorithmLoader orderingAlgorithmLoader) {
        this.orderingAlgorithmLoader = orderingAlgorithmLoader
    }

    OrderingAlgorithmProvider() {
        this.orderingAlgorithmLoader = {ServiceLoader.load(OrderingAlgorithm.class).toList()}
    }

    OrderingAlgorithm getOrderingAlgorithm() {
        List<OrderingAlgorithm> randomizationAlgorithms = orderingAlgorithmLoader.load()
        if (randomizationAlgorithms.size() > 1) {
            throw new IllegalStateException("Cannot define more than 1 randomization algorithm. Currently defined: " + randomizationAlgorithms)
        }
        return randomizationAlgorithms.isEmpty() ? new RandomOrderingAlgorithm() : randomizationAlgorithms.get(0)
    }
}

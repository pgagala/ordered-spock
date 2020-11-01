package com.github.pgagala.algorithm

import org.junit.platform.engine.UniqueId
import org.spockframework.runtime.SpecNode
import org.spockframework.runtime.model.SpecInfo
import spock.lang.Shared
import spock.lang.Specification

class RandomOrderingAlgorithmSpec extends Specification {

    @Shared
    RandomOrderingAlgorithm algorithm = new RandomOrderingAlgorithm()

    def "Spec nodes should be randomly shuffled"() {
        given: "3 specifications with predefined order"
            List<SpecNode> unshuffledSpecNodes = []
            (0..2).forEach({ unshuffledSpecNodes.add(specNode(it)) })

        when: "Specifications are shuffled"
            List<SpecNode> shuffledSpecNodes = algorithm.shuffle(unshuffledSpecNodes)

        then: "Their order changed"
            shuffledSpecNodes.stream()
                    .anyMatch(node -> {
                        int indexOfShuffledNode = shuffledSpecNodes.indexOf(node)
                        node.uniqueId != unshuffledSpecNodes[indexOfShuffledNode].uniqueId
                    })
    }

    private SpecNode specNode(int order) {
        def specInfo = new SpecInfo(name: "name", reflection: this.getClass())
        return new SpecNode(UniqueId.parse("[id:" + order.toString() + "]"), specInfo)
    }
}

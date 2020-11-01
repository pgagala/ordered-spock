package com.github.pgagala.engine

import com.github.pgagala.OrderedSpockProperties
import com.github.pgagala.algorithm.OrderingAlgorithmProvider
import com.github.pgagala.algorithm.RandomOrderingAlgorithm
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.spockframework.runtime.RunContext
import org.spockframework.runtime.SpecInfoBuilder
import org.spockframework.runtime.SpecNode
import org.spockframework.runtime.SpockEngineDescriptor
import spock.lang.Specification

class OrderedSpockEngineSpec extends Specification {

    def "Engine's discover invocation should shuffle specifications"() {
        given: "Ordering algorithm which shuffling specifications in random way"
            OrderingAlgorithmProvider algorithmProvider = new OrderingAlgorithmProvider({ [new RandomOrderingAlgorithm()] })

        and: "Engine's descriptor with predefined specifications"
            SpockEngineDescriptor engineDescriptor = new SpockEngineDescriptor(UniqueId.parse("[engine:spock]"), Mock(RunContext))
            addTestSpecifications(engineDescriptor)

        and: "Defined ordered spock engine"
            OrderedSpockEngine orderedSpockEngine = new OrderedSpockEngine(new OrderedSpockProperties(), algorithmProvider,
                    (x, y) -> { engineDescriptor },
                    new OrderedSpockEngineDiscoveryPostProcessor())

        when: "Discover is invoked"
            TestDescriptor specNode = orderedSpockEngine.discover(LauncherDiscoveryRequestBuilder.request().build(), uniqueId(EngineDiscoveryRequest.class))

        then: "Specifications are shuffled in random order"
            def unshuffledSpecifications = engineDescriptor.getChildren()
            def shuffledSpecifications = specNode.getChildren()
            shuffledSpecifications.stream().anyMatch(node -> {
                int indexOfShuffledSpec = shuffledSpecifications.findIndexOf({ it.uniqueId == node.uniqueId })
                node.uniqueId != unshuffledSpecifications[indexOfShuffledSpec].uniqueId
            })
    }

    SpecNode specNode(Class<Specification> specificationClass) {
        return new SpecNode(uniqueId(specificationClass), new SpecInfoBuilder(specificationClass).build())
    }

    UniqueId uniqueId(Class specificationClass) {
        return UniqueId.parse("[engine:spock]/[spec:${specificationClass.name}]")
    }

    def addTestSpecifications(SpockEngineDescriptor engineDescriptor) {
        engineDescriptor.addChild(specNode(TestSpec1.class))
        engineDescriptor.addChild(specNode(TestSpec2.class))
        engineDescriptor.addChild(specNode(TestSpec3.class))
    }

    class TestSpec1 extends Specification {
    }

    class TestSpec2 extends Specification {
    }

    class TestSpec3 extends Specification {
    }
}


# Purpose of library

Possibility to run Spock's specifications in custom order.
Order is customized by `OrderingAlgorithm`.
Default algorithm included in library `RandomOrderingAlgorithm` provides random order. 

#Usage 

##Gradle

1) Build jar

2) Add a built jar accordingly to `build.gradle`
```groovy
    testImplementation 'com.github.pgagala:ordered-spock:1.0-SNAPSHOT'
```

3) Define test engine 
```groovy
test {
     useJUnitPlatform() {
        includeEngines 'ordered-spock'
    }
}
```

# Use own `OrderingAlgorithm`
1) Implement Your own algorithm. For example below implementation will shuffle specifications in alphabetical order.
Create that class in test scope (default `src/test/groovy`).

```groovy
package com.app

import com.github.pgagala.algorithm.OrderingAlgorithm
import org.junit.platform.engine.TestDescriptor
import java.util.stream.Collectors

class AlphabeticalOrderAlgorithm implements OrderingAlgorithm {

    @Override
    <T extends TestDescriptor> List<T> shuffle(List<T> list) {
        return list.stream()
                .sorted((t1, t2) -> {
                    t1.getDisplayName() <=> t2.getDisplayName()
                })
                .collect(Collectors.toUnmodifiableList());
    }
}
```

2) Add a file named `META-INF/services/com.github.pgagala.algorithm.OrderingAlgorithm` to test resources (default is
`src/test/resources`). Create folders `META-INF` and `services` if they don't exist.

3) Add content to file `META-INF/services/com.github.pgagala.algorithm.OrderingAlgorithm`.
Content should be full package path to that file. For above example that will be: 
`com.app.AlphabeticalOrderAlgorithm`

 
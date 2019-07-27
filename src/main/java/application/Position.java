package application;

import org.immutables.value.Value;

@Value.Immutable
interface Position {
    Integer x();

    Integer y();
}

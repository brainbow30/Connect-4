package application.game;

import org.immutables.value.Value;

import java.io.Serializable;

@Value.Immutable
public interface Position extends Serializable {
    Integer x();

    Integer y();
}

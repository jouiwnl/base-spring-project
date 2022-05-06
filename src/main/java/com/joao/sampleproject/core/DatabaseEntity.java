package com.joao.sampleproject.core;

import java.io.Serializable;

public interface DatabaseEntity<PK> extends Serializable {
    PK getId();
}

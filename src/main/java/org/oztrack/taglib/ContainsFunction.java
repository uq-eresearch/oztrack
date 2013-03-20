package org.oztrack.taglib;

import java.util.Collection;

public class ContainsFunction {
    public static boolean contains(Collection<?> c, Object o) {
        return (c != null) && c.contains(o);
    }
}

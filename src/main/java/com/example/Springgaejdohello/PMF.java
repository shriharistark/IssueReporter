package com.example.Springgaejdohello;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

public final class PMF {
    private static final PersistenceManagerFactory pmfInstance =
        JDOHelper.getPersistenceManagerFactory("xxtransactions-optionalxx");

    private PMF() {}

    public static PersistenceManagerFactory get() {
        return pmfInstance;
    }
}
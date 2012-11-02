package edu.psu.citeseerx.loaders;

import java.io.IOException;

import org.springframework.beans.factory.ListableBeanFactory;

import edu.psu.citeseerx.fixers.FixAbs;

/**
 * Throwaway fixer.
 *
 * @author Isaac Councill
 * @version $Rev: 729 $ $Date: 2008-10-10 05:14:55 -0400 (Fri, 10 Oct 2008) $
 * @deprecated
 */
public class FixAbsLoader {

    public static void main(String[] args) throws IOException {
        ListableBeanFactory factory = ContextReader.loadContext();
        FixAbs fixAbs =
            (FixAbs)factory.getBean("fixAbs");
        try {
            fixAbs.fixAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

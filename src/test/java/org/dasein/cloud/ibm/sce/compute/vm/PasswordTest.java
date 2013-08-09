/**
 * Copyright (C) 2009-2013 Dell, Inc.
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.dasein.cloud.ibm.sce.compute.vm;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collection;

public class PasswordTest extends TestCase {

    private static Collection<String> invalidSubstrings = new ArrayList<String>();
    static {
        final String[] invalidStrings = {"root", "admin", "Administrator", "idcadmin"};
        for (String invalidString : invalidStrings) {
            invalidSubstrings.addAll(SCEVirtualMachine.generateThreeCharSubstrings(invalidString));
        }
    }

    public void testSubstrings() throws Exception {
        final Collection<String> expected = new ArrayList<String>();
        expected.add("roo");
        expected.add("oot");
        final Collection<String> substrings = SCEVirtualMachine.generateThreeCharSubstrings("root");
        assertTrue("does not contain expected substrings", substrings.containsAll(expected));

        final Collection<String> expected2 = new ArrayList<String>();
        expected2.add("Adm");
        expected2.add("dmi");
        expected2.add("min");
        expected2.add("ini");
        expected2.add("nis");
        expected2.add("ist");
        expected2.add("str");
        expected2.add("tra");
        expected2.add("rat");
        expected2.add("ato");
        expected2.add("tor");
        final Collection<String> substrings2 = SCEVirtualMachine.generateThreeCharSubstrings("Administrator");
        assertTrue("does not contain expected substrings", substrings2.containsAll(expected2));
    }

    public void testGetPassword() throws Exception {
        for (int i=0; i < 100000; i++) {
            throwIfInvalid(SCEVirtualMachine.getRandomWindowsPassword("root"));
        }
    }

    private static void throwIfInvalid(String password) throws Exception {
        if (password.length() < 8) {
            throw new Exception("password not long enough: " + password);
        }
        for (int i=0; i < password.length()-2; i++) {
            for (String invalidSubstring : invalidSubstrings) {
                if (invalidSubstring.equals(password.substring(i,i+3))) {
                    throw new Exception("password contains bad substring '" + password.substring(i,i+3) + "': " + password);
                }
            }
        }
        int categoryCount = 0;
        char[] chars = password.toCharArray();

        for (char c : chars) {
            if (SCEVirtualMachine.uppercaseAlphabet.contains(String.valueOf(c))) {
                categoryCount += 1;
                break;
            }
        }
        for (char c : chars) {
            if (SCEVirtualMachine.lowercaseAlphabet.contains(String.valueOf(c))) {
                categoryCount += 1;
                break;
            }
        }
        for (char c : chars) {
            if (SCEVirtualMachine.symbols.contains(String.valueOf(c))) {
                categoryCount += 1;
                break;
            }
        }
        for (char c : chars) {
            if (SCEVirtualMachine.numbers.contains(String.valueOf(c))) {
                categoryCount += 1;
                break;
            }
        }

        if (categoryCount < 3) {
            throw new Exception("password does not contain characters from at least three of the categories: " + password);
        }
    }
}

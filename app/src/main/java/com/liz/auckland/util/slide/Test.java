package com.liz.auckland.util.slide;

import com.liz.auckland.model.Address;
import com.liz.auckland.model.Rubbish;

public class Test {
    void test() {

        Address address = generateAddress();
        if (address != null) {
            address.setName("Ha Noi");
            // Do something
        }

    }

    Address generateAddress() {
        return new Address();
    }
}
